package kinoko.server.event;

import kinoko.packet.field.ContiMovePacket;
import kinoko.server.field.FieldStorage;
import kinoko.util.Util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class ContiMoveVictoria extends ContiMoveEvent {
    // Orbis -> Victoria
    public static final int ORBIS_STATION_VICTORIA_BOUND = 200000111;
    public static final int PRE_DEPARTURE_VICTORIA_BOUND = 200000112;
    public static final int DURING_THE_RIDE_VICTORIA_BOUND = 200090000;
    public static final int DURING_THE_RIDE_CABIN_VICTORIA_BOUND = 200090001;
    // Victoria -> Orbis
    public static final int STATION_TO_ORBIS = 104020110;
    public static final int PRE_DEPARTURE_TO_ORBIS = 104020111; // Port Road : To Orbis <Before Starting>
    public static final int DURING_THE_RIDE_TO_ORBIS = 200090010;
    public static final int DURING_THE_RIDE_CABIN_TO_ORBIS = 200090011;
    public static final int ORBIS_STATION_ENTRANCE = 200000100;

    public static final int CRIMSON_BALROG = 8150000;

    public ContiMoveVictoria(FieldStorage fieldStorage) {
        super(
                fieldStorage,
                ORBIS_STATION_VICTORIA_BOUND,   // boardingField1
                STATION_TO_ORBIS,               // boardingField2
                PRE_DEPARTURE_VICTORIA_BOUND,   // waitingField1
                PRE_DEPARTURE_TO_ORBIS,         // waitingField2
                DURING_THE_RIDE_VICTORIA_BOUND, // insideField1
                DURING_THE_RIDE_TO_ORBIS,       // insideField2
                STATION_TO_ORBIS,               // arriveField1
                ORBIS_STATION_ENTRANCE          // arriveField2
        );
    }

    @Override
    public EventType getType() {
        return EventType.CM_VICTORIA;
    }

    @Override
    public void initialize() {
        // Initialize current state
        final LocalDateTime now = LocalDateTime.now();
        final int minute = now.getMinute() % 15;
        if (minute >= 10 && minute < 14) {
            currentState = EventState.CONTIMOVE_BOARDING;
        } else if (minute == 14) {
            currentState = EventState.CONTIMOVE_WAITING;
        } else {
            currentState = EventState.CONTIMOVE_INSIDE;
        }
        // Schedule event - run every minute
        final LocalDateTime nextStateTime = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        eventFuture = EventScheduler.addFixedDelayEvent(this::nextState, now.until(nextStateTime, ChronoUnit.MILLIS), 60 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void nextState() {
        final int minute = getNearestMinute() % 15;
        if (minute == 10) {
            handleBoarding();
        } else if (minute == 14) {
            handleWaiting();
        } else if (minute == 0) {
            handleInside();
        } else if (minute >= 3 && minute <= 5) {
            if (currentState == EventState.CONTIMOVE_INSIDE && Util.succeedProp(30)) { // chance = 1 - (1 - x)^3
                handleMobGen();
            }
        }
    }

    protected void handleBoarding() {
        super.handleBoarding();
        warp(DURING_THE_RIDE_CABIN_VICTORIA_BOUND, STATION_TO_ORBIS, "sp");
        warp(DURING_THE_RIDE_CABIN_TO_ORBIS, ORBIS_STATION_ENTRANCE, "sp");
        reset(DURING_THE_RIDE_CABIN_VICTORIA_BOUND);
        reset(DURING_THE_RIDE_CABIN_TO_ORBIS);
    }

    private void handleMobGen() {
        currentState = EventState.CONTIMOVE_MOBGEN;
        broadcastPacket(DURING_THE_RIDE_VICTORIA_BOUND, ContiMovePacket.mobGen());
        broadcastPacket(DURING_THE_RIDE_TO_ORBIS, ContiMovePacket.mobGen());
        for (int i = 0; i < 2; i++) {
            spawnMob(DURING_THE_RIDE_VICTORIA_BOUND, CRIMSON_BALROG, -590, -221); // 200090000 -> shipObj -> x, y
            spawnMob(DURING_THE_RIDE_TO_ORBIS, CRIMSON_BALROG, 485, -221); // 200090010 -> shipObj -> x, y
        }
        // should send ContiMovePacket.mobDestroy when all mobs are dead
    }
}
