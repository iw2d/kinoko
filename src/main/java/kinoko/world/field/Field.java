package kinoko.world.field;

import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.ReactorProvider;
import kinoko.provider.map.*;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.server.ServerConfig;
import kinoko.server.event.EventScheduler;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.world.GameConstants;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.npc.Npc;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Field {
    private static final AtomicInteger fieldKeyCounter = new AtomicInteger(1);
    private final AtomicInteger fieldObjectCounter = new AtomicInteger(1);
    private final AtomicBoolean firstEnterScript = new AtomicBoolean(false);

    private final UserPool userPool = new UserPool(this);
    private final SummonedPool summonedPool = new SummonedPool(this);
    private final AffectedAreaPool affectedAreaPool = new AffectedAreaPool(this);

    private final MobPool mobPool = new MobPool(this);
    private final NpcPool npcPool = new NpcPool(this);
    private final DropPool dropPool = new DropPool(this);
    private final ReactorPool reactorPool = new ReactorPool(this);

    private final MapInfo mapInfo;
    private final byte fieldKey;
    private final ScheduledFuture<?> fieldEventFuture;
    private Instant nextMobRespawn = Instant.now();
    private Instant nextDropExpire = Instant.now();
    private Instant nextReactorExpire = Instant.now();

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
        this.fieldKey = (byte) (fieldKeyCounter.getAndIncrement() % 0xFF);
        this.fieldEventFuture = EventScheduler.addFixedDelayEvent(this::update, ServerConfig.FIELD_TICK_INTERVAL, ServerConfig.FIELD_TICK_INTERVAL);
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

    public SummonedPool getSummonedPool() {
        return summonedPool;
    }

    public AffectedAreaPool getAffectedAreaPool() {
        return affectedAreaPool;
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

    public ScheduledFuture<?> getFieldEventFuture() {
        return fieldEventFuture;
    }

    public int getNewObjectId() {
        return fieldObjectCounter.getAndIncrement();
    }

    public void update() {
        final Instant now = Instant.now();
        if (nextMobRespawn.isBefore(now)) {
            nextMobRespawn = now.plus(GameConstants.MOB_RESPAWN_TIME, ChronoUnit.SECONDS);
            mobPool.respawnMobs(now);
        }
        if (nextDropExpire.isBefore(now)) {
            nextDropExpire = now.plus(GameConstants.DROP_EXPIRE_INTERVAL, ChronoUnit.SECONDS);
            dropPool.expireDrops(now);
        }
        if (nextReactorExpire.isBefore(now)) {
            nextReactorExpire = now.plus(GameConstants.REACTOR_EXPIRE_INTERVAL, ChronoUnit.SECONDS);
            reactorPool.expireReactors(now);
        }
        userPool.updateUsers(now);
        mobPool.updateMobs(now);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public void broadcastPacket(OutPacket outPacket) {
        userPool.broadcastPacket(outPacket);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        userPool.broadcastPacket(outPacket, except);

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
        return field;
    }
}
