package kinoko.world.field;

import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.ReactorProvider;
import kinoko.provider.map.*;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.server.event.EventScheduler;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.world.GameConstants;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Field {
    private static final AtomicInteger fieldKeyCounter = new AtomicInteger(1);
    private final AtomicInteger fieldObjectCounter = new AtomicInteger(1);
    private final AtomicBoolean firstEnterScript = new AtomicBoolean(false);
    private final UserPool userPool = new UserPool(this);
    private final MobPool mobPool = new MobPool(this);
    private final NpcPool npcPool = new NpcPool(this);
    private final DropPool dropPool = new DropPool(this);
    private final ReactorPool reactorPool = new ReactorPool(this);

    private final MapInfo mapInfo;
    private final byte fieldKey;
    private ScheduledFuture<?> mobRespawnFuture = null;
    private ScheduledFuture<?> reactorResetFuture = null;

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
        this.fieldKey = (byte) (fieldKeyCounter.getAndIncrement() % 0xFF);
    }

    public int getFieldId() {
        return mapInfo.getMapId();
    }

    public Set<FieldOption> getFieldOptions() {
        return mapInfo.getFieldOptions();
    }

    public boolean hasFieldOption(FieldOption fieldOption) {
        return mapInfo.getFieldOptions().contains(fieldOption);
    }

    public FieldType getFieldType() {
        return mapInfo.getFieldType();
    }

    public Optional<PortalInfo> getPortalById(int portalId) {
        return mapInfo.getPortalById(portalId);
    }

    public Optional<PortalInfo> getPortalByName(String portalName) {
        return mapInfo.getPortalByName(portalName);
    }

    public Optional<Foothold> getFootholdById(int footholdId) {
        return mapInfo.getFootholdById(footholdId);
    }

    public Optional<Foothold> getFootholdBelow(int x, int y) {
        return mapInfo.getFootholdBelow(x, y);
    }

    public int getReturnMap() {
        return mapInfo.getReturnMap();
    }

    public UserPool getUserPool() {
        return userPool;
    }

    public MobPool getMobPool() {
        return mobPool;
    }

    public NpcPool getNpcPool() {
        return npcPool;
    }

    public DropPool getDropPool() {
        return dropPool;
    }

    public ReactorPool getReactorPool() {
        return reactorPool;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public byte getFieldKey() {
        return fieldKey;
    }

    public ScheduledFuture<?> getMobRespawnFuture() {
        return mobRespawnFuture;
    }

    public void setMobRespawnFuture(ScheduledFuture<?> mobRespawnFuture) {
        this.mobRespawnFuture = mobRespawnFuture;
    }

    public ScheduledFuture<?> getReactorResetFuture() {
        return reactorResetFuture;
    }

    public void setReactorResetFuture(ScheduledFuture<?> reactorResetFuture) {
        this.reactorResetFuture = reactorResetFuture;
    }

    public int getNewObjectId() {
        return fieldObjectCounter.getAndIncrement();
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void broadcastPacket(OutPacket outPacket) {
        broadcastPacket(outPacket, null);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        userPool.forEach((user) -> {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                return;
            }
            user.write(outPacket);
        });
    }

    public void addUser(User user) {
        userPool.addUser(user);
        // Execute field enter scripts
        if (mapInfo.hasOnFirstUserEnter()) {
            if (firstEnterScript.compareAndSet(false, true)) {
                ScriptDispatcher.startFirstUserEnterScript(user, mapInfo.getOnFirstUserEnter());
            }
        }
        if (mapInfo.hasOnUserEnter()) {
            ScriptDispatcher.startUserEnterScript(user, mapInfo.getOnUserEnter());
        }
    }

    public void removeUser(User user) {
        userPool.removeUser(user);
    }

    public static Field from(MapInfo mapInfo) {
        final Field field = new Field(mapInfo);
        // Populate object pools
        for (LifeInfo lifeInfo : mapInfo.getLifeInfos()) {
            switch (lifeInfo.getLifeType()) {
                case NPC -> {
                    final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(lifeInfo.getTemplateId());
                    if (npcTemplateResult.isEmpty()) {
                        continue;
                    }
                    final Npc npc = Npc.from(npcTemplateResult.get(), lifeInfo);
                    field.getNpcPool().addNpc(npc);
                }
                case MOB -> {
                    final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(lifeInfo.getTemplateId());
                    if (mobTemplateResult.isEmpty()) {
                        continue;
                    }
                    final Mob mob = Mob.from(mobTemplateResult.get(), lifeInfo);
                    field.getMobPool().addMob(mob);
                }
            }
        }
        for (ReactorInfo reactorInfo : mapInfo.getReactorInfos()) {
            final Optional<ReactorTemplate> reactorTemplateResult = ReactorProvider.getReactorTemplate(reactorInfo.getTemplateId());
            if (reactorTemplateResult.isEmpty()) {
                continue;
            }
            final Reactor reactor = Reactor.from(reactorTemplateResult.get(), reactorInfo);
            field.getReactorPool().addReactor(reactor);
        }
        // Schedule mob and reactor respawns
        if (!field.getMobPool().isEmpty()) {
            final ScheduledFuture<?> respawnFuture = EventScheduler.addFixedDelayEvent(() -> {
                field.getMobPool().respawnMobs();
            }, GameConstants.MOB_RESPAWN_TIME, GameConstants.MOB_RESPAWN_TIME, TimeUnit.SECONDS);
            field.setMobRespawnFuture(respawnFuture);
        }
        if (!field.getReactorPool().isEmpty()) {
            final ScheduledFuture<?> resetFuture = EventScheduler.addFixedDelayEvent(() -> {
                field.getReactorPool().resetReactors();
            }, GameConstants.REACTOR_RESET_INTERVAL, GameConstants.REACTOR_RESET_INTERVAL, TimeUnit.SECONDS);
            field.setReactorResetFuture(resetFuture);
        }
        return field;
    }
}
