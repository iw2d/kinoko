package kinoko.world.field;

import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.map.*;
import kinoko.provider.mob.MobInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.event.EventScheduler;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.world.GameConstants;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.field.life.npc.Npc;
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

    private final MapInfo mapInfo;
    private final byte fieldKey;
    private ScheduledFuture<?> respawnFuture = null;

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
        this.fieldKey = (byte) (fieldKeyCounter.getAndIncrement() % 0xFF);
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

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public byte getFieldKey() {
        return fieldKey;
    }

    public ScheduledFuture<?> getRespawnFuture() {
        return respawnFuture;
    }

    public void setRespawnFuture(ScheduledFuture<?> respawnFuture) {
        this.respawnFuture = respawnFuture;
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

    public int getNewObjectId() {
        return fieldObjectCounter.getAndIncrement();
    }

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

    public static Field from(MapInfo mapInfo) {
        final Field field = new Field(mapInfo);
        // Populate life pools
        for (LifeInfo lifeInfo : mapInfo.getLifeInfos()) {
            switch (lifeInfo.getLifeType()) {
                case NPC -> {
                    final Optional<NpcInfo> npcInfoResult = NpcProvider.getNpcInfo(lifeInfo.getTemplateId());
                    final Npc npc = Npc.from(npcInfoResult.orElseThrow(), lifeInfo);
                    field.getNpcPool().addNpc(npc);
                }
                case MOB -> {
                    final Optional<MobInfo> mobInfoResult = MobProvider.getMobInfo(lifeInfo.getTemplateId());
                    final Mob mob = Mob.from(mobInfoResult.orElseThrow(), lifeInfo);
                    field.getMobPool().addMob(mob);
                }
            }
        }
        // Schedule mob respawns
        if (!field.getMobPool().isEmpty()) {
            final ScheduledFuture<?> respawnFuture = EventScheduler.addFixedDelayEvent(() -> {
                field.getMobPool().respawnMobs();
            }, GameConstants.MOB_RESPAWN_TIME, GameConstants.MOB_RESPAWN_TIME, TimeUnit.SECONDS);
            field.setRespawnFuture(respawnFuture);
        }
        return field;
    }
}
