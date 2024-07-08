package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.provider.MapProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.ReactorProvider;
import kinoko.provider.map.*;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.script.common.ScriptDispatcher;
import kinoko.server.ServerConfig;
import kinoko.server.event.EventScheduler;
import kinoko.server.field.FieldStorage;
import kinoko.server.field.Instance;
import kinoko.server.field.InstanceFieldStorage;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.npc.Npc;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class Field {
    private static final AtomicInteger fieldKeyCounter = new AtomicInteger(1);
    private final AtomicInteger fieldObjectCounter = new AtomicInteger(1);
    private final AtomicBoolean firstEnterScript = new AtomicBoolean(false);

    private final FieldStorage fieldStorage;
    private final MapInfo mapInfo;
    private final byte fieldKey;
    private final ScheduledFuture<?> fieldEventFuture;
    private final Map<Integer, Consumer<Mob>> mobSpawnModifiers;

    private final UserPool userPool;
    private final MobPool mobPool;
    private final NpcPool npcPool;
    private final DropPool dropPool;
    private final ReactorPool reactorPool;
    private final SummonedPool summonedPool;
    private final MiniRoomPool miniRoomPool;
    private final TownPortalPool townPortalPool;
    private final AffectedAreaPool affectedAreaPool;

    private Instant nextMobRespawn = Instant.now();
    private Instant nextDropExpire = Instant.now();
    private Instant nextReactorExpire = Instant.now();

    public Field(FieldStorage fieldStorage, MapInfo mapInfo) {
        this.fieldStorage = fieldStorage;
        this.mapInfo = mapInfo;
        this.fieldKey = (byte) (fieldKeyCounter.getAndIncrement() % 0xFF);
        this.fieldEventFuture = EventScheduler.addFixedDelayEvent(this::update, ServerConfig.FIELD_TICK_INTERVAL, ServerConfig.FIELD_TICK_INTERVAL);
        this.mobSpawnModifiers = new ConcurrentHashMap<>();
        // Initialize field object pools
        this.userPool = new UserPool(this);
        this.mobPool = new MobPool(this);
        this.npcPool = new NpcPool(this);
        this.dropPool = new DropPool(this);
        this.reactorPool = new ReactorPool(this);
        this.summonedPool = new SummonedPool(this);
        this.miniRoomPool = new MiniRoomPool(this);
        this.townPortalPool = new TownPortalPool(this);
        this.affectedAreaPool = new AffectedAreaPool(this);
    }

    public FieldStorage getFieldStorage() {
        return fieldStorage;
    }

    public int getFieldId() {
        return mapInfo.getMapId();
    }

    public Set<FieldOption> getFieldOptions() {
        return mapInfo.getFieldOptions();
    }

    public boolean hasFieldOption(FieldOption fieldOption) {
        return mapInfo.hasFieldOption(fieldOption);
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

    public Optional<PortalInfo> getRandomStartPoint() {
        final List<PortalInfo> startPoints = mapInfo.getPortalInfos().stream().filter((pi) -> pi.getPortalType() == PortalType.STARTPOINT).toList();
        final Optional<PortalInfo> randomStartPoint = Util.getRandomFromCollection(startPoints);
        return randomStartPoint.or(() -> getPortalById(0));
    }

    public Optional<PortalInfo> getNearestStartPoint(int x, int y) {
        double nearestDistance = Double.MAX_VALUE;
        PortalInfo nearestPortal = null;
        for (PortalInfo portalInfo : mapInfo.getPortalInfos()) {
            if (portalInfo.getPortalType() != PortalType.STARTPOINT) {
                continue;
            }
            final double distance = Util.distance(x, y, portalInfo.getX(), portalInfo.getY());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPortal = portalInfo;
            }
        }
        if (nearestPortal != null) {
            return Optional.of(nearestPortal);
        }
        return getPortalById(0);
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

    public int getForcedReturn() {
        return mapInfo.getForcedReturn();
    }

    public boolean hasForcedReturn() {
        return getForcedReturn() != GameConstants.UNDEFINED_FIELD_ID;
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

    public SummonedPool getSummonedPool() {
        return summonedPool;
    }

    public MiniRoomPool getMiniRoomPool() {
        return miniRoomPool;
    }

    public TownPortalPool getTownPortalPool() {
        return townPortalPool;
    }

    public AffectedAreaPool getAffectedAreaPool() {
        return affectedAreaPool;
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

    public Map<Integer, Consumer<Mob>> getMobSpawnModifiers() {
        return mobSpawnModifiers;
    }

    public int getNewObjectId() {
        return fieldObjectCounter.getAndIncrement();
    }

    public void update() {
        // Handle field updates
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
        affectedAreaPool.updateAffectedAreas(now);
        // Handle instance
        if (fieldStorage instanceof InstanceFieldStorage instanceFieldStorage) {
            final Instance instance = instanceFieldStorage.getInstance();
            if (now.isAfter(instance.getExpireTime())) {
                // Remove instance
                final Field returnField = instance.getChannelServerNode().getFieldById(instance.getReturnMap()).orElseThrow();
                final PortalInfo defaultPortal = returnField.getPortalById(0).orElseThrow();
                userPool.forEach((user) -> {
                    try (var locked = user.acquire()) {
                        final PortalInfo portalInfo = returnField.getRandomStartPoint().orElse(defaultPortal);
                        locked.get().warp(returnField, portalInfo, false, false);
                    }
                });
                instance.getChannelServerNode().removeInstance(instance);
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public boolean isConnected(int targetFieldId) {
        return MapProvider.isConnected(getFieldId(), targetFieldId);
    }

    public boolean isSameContinent(int targetFieldId) {
        return getFieldId() / 100000000 == targetFieldId / 100000000;
    }

    public boolean isUpgradeTombUsable() {
        switch (getMapInfo().getFieldType()) {
            case SNOWBALL:
            case TOURNAMENT:
            case COCONUT:
            case OXQUIZ:
            case WAITINGROOM:
            case MONSTERCARNIVAL:
            case MONSTERCARNIVALREVIVE:
            case MONSTERCARNIVAL_S2:
                return false;
            default:
                return getFieldId() / 100000000 != 9 && getFieldId() / 1000 != 200090 && getFieldId() / 1000000 != 390;
        }
    }

    public boolean isMapTransferLimit() {
        return GameConstants.isEventMap(getFieldId()) ||
                getMapInfo().hasFieldOption(FieldOption.SKILLLIMIT) ||
                getMapInfo().hasFieldOption(FieldOption.TELEPORTITEMLIMIT);
    }

    public void broadcastPacket(OutPacket outPacket) {
        userPool.broadcastPacket(outPacket);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        userPool.broadcastPacket(outPacket, except);
    }

    public boolean hasUser() {
        return !userPool.isEmpty();
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
        // Handle clock
        if (mapInfo.isClock()) {
            final LocalDateTime now = LocalDateTime.now();
            user.write(FieldPacket.clock(now.getHour(), now.getMinute(), now.getSecond()));
        }
        // Handle instance
        if (fieldStorage instanceof InstanceFieldStorage instanceFieldStorage) {
            try (var lockedInstance = instanceFieldStorage.getInstance().acquire()) {
                lockedInstance.get().addUser(user);
            }
        }
    }

    public void removeUser(User user) {
        userPool.removeUser(user);
        // Handle dialogs
        user.closeDialog();
        // Handle instance
        if (fieldStorage instanceof InstanceFieldStorage instanceFieldStorage) {
            try (var lockedInstance = instanceFieldStorage.getInstance().acquire()) {
                lockedInstance.get().removeUser(user);
            }
        }
    }

    public void reset() {
        assert !hasUser();
        userPool.clear();
        mobPool.clear();
        npcPool.clear();
        dropPool.clear();
        reactorPool.clear();
        summonedPool.clear();
        miniRoomPool.clear();
        townPortalPool.clear();
        affectedAreaPool.clear();
        populateField(this, mapInfo);
    }

    public static Field from(FieldStorage fieldStorage, MapInfo mapInfo) {
        final Field field = new Field(fieldStorage, mapInfo);
        populateField(field, mapInfo);
        return field;
    }

    private static void populateField(Field field, MapInfo mapInfo) {
        // Populate npc pool
        for (LifeInfo lifeInfo : mapInfo.getLifeInfos()) {
            if (lifeInfo.getLifeType() != LifeType.NPC) {
                continue;
            }
            final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(lifeInfo.getTemplateId());
            if (npcTemplateResult.isEmpty()) {
                continue;
            }
            final Npc npc = Npc.from(npcTemplateResult.get(), lifeInfo);
            field.getNpcPool().addNpc(npc);
        }
        // Populate reactor pool
        for (ReactorInfo reactorInfo : mapInfo.getReactorInfos()) {
            final Optional<ReactorTemplate> reactorTemplateResult = ReactorProvider.getReactorTemplate(reactorInfo.getTemplateId());
            if (reactorTemplateResult.isEmpty()) {
                continue;
            }
            final Reactor reactor = Reactor.from(reactorTemplateResult.get(), reactorInfo);
            field.getReactorPool().addReactor(reactor);
        }
    }
}
