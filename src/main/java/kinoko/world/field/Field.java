package kinoko.world.field;

import kinoko.provider.NpcProvider;
import kinoko.provider.ReactorProvider;
import kinoko.provider.map.*;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.server.ServerConfig;
import kinoko.server.event.EventScheduler;
import kinoko.server.node.FieldStorage;
import kinoko.server.packet.OutPacket;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.dialog.miniroom.MiniGameRoom;
import kinoko.world.dialog.miniroom.TradingRoom;
import kinoko.world.field.npc.Npc;
import kinoko.world.field.reactor.Reactor;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Field {
    private static final AtomicInteger fieldKeyCounter = new AtomicInteger(1);
    private final AtomicInteger fieldObjectCounter = new AtomicInteger(1);
    private final AtomicBoolean firstEnterScript = new AtomicBoolean(false);

    private final FieldStorage fieldStorage;
    private final MapInfo mapInfo;
    private final byte fieldKey;
    private final ScheduledFuture<?> fieldEventFuture;

    private final UserPool userPool;
    private final MobPool mobPool;
    private final NpcPool npcPool;
    private final DropPool dropPool;
    private final ReactorPool reactorPool;
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
        // Initialize field object pools
        this.userPool = new UserPool(this);
        this.mobPool = new MobPool(this);
        this.npcPool = new NpcPool(this);
        this.dropPool = new DropPool(this);
        this.reactorPool = new ReactorPool(this);
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
        affectedAreaPool.updateAffectedAreas(now);
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
        if (user.getDialog() instanceof TradingRoom tradingRoom) {
            tradingRoom.cancelTradeUnsafe(user);
        } else if (user.getDialog() instanceof MiniGameRoom miniGameRoom) {
            miniGameRoom.leaveUnsafe(user);
        }
    }

    public static Field from(FieldStorage fieldStorage, MapInfo mapInfo) {
        final Field field = new Field(fieldStorage, mapInfo);
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
        return field;
    }
}
