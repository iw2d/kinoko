package kinoko.server.event;

import kinoko.server.field.FieldStorage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class Subway extends Event {
    // Kerning City -> NLC
    public static final int SUBWAY_TICKETING_BOOTH = 103020000;
    public static final int WAITING_ROOM_FROM_KC_TO_NLC = 600010004;
    public static final int INSIDE_SUBWAY_FROM_KC_TO_NLC = 600010005;
    // NLC -> Kerning City
    public static final int NLC_SUBWAY_STATION = 600010001;
    public static final int WAITING_ROOM_FROM_NLC_TO_KC = 600010002;
    public static final int INSIDE_SUBWAY_FROM_NLC_TO_KC = 600010003;


    public Subway(FieldStorage fieldStorage) {
        super(fieldStorage);
    }

    @Override
    public EventType getType() {
        return EventType.CM_SUBWAY;
    }

    @Override
    public void initialize() {
        // Initialize current state
        final LocalDateTime now = LocalDateTime.now();
        final int minute = now.getMinute() % 10;
        if (minute >= 5 && minute < 9) {
            currentState = EventState.SUBWAY_BOARDING;
        } else if (minute == 9) {
            currentState = EventState.SUBWAY_WAITING;
        } else {
            currentState = EventState.SUBWAY_INSIDE;
        }
        // Schedule event - run every minute
        final LocalDateTime nextStateTime = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        eventFuture = EventScheduler.addFixedDelayEvent(this::nextState, now.until(nextStateTime, ChronoUnit.MILLIS), 60 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void nextState() {
        final int minute = getNearestMinute() % 10;
        if (minute == 5) {
            handleBoarding();
        } else if (minute == 9) {
            handleWaiting();
        } else if (minute == 0) {
            handleInside();
        }
    }

    private void handleBoarding() {
        warp(INSIDE_SUBWAY_FROM_KC_TO_NLC, NLC_SUBWAY_STATION, "sp");
        warp(INSIDE_SUBWAY_FROM_NLC_TO_KC, SUBWAY_TICKETING_BOOTH, "sp");
        reset(INSIDE_SUBWAY_FROM_KC_TO_NLC);
        reset(INSIDE_SUBWAY_FROM_NLC_TO_KC);
        currentState = EventState.SUBWAY_BOARDING;
    }

    private void handleWaiting() {
        currentState = EventState.SUBWAY_WAITING;
    }

    private void handleInside() {
        warp(WAITING_ROOM_FROM_KC_TO_NLC, INSIDE_SUBWAY_FROM_KC_TO_NLC, "st00");
        warp(WAITING_ROOM_FROM_NLC_TO_KC, INSIDE_SUBWAY_FROM_NLC_TO_KC, "st00");
        reset(WAITING_ROOM_FROM_KC_TO_NLC);
        reset(WAITING_ROOM_FROM_NLC_TO_KC);
        currentState = EventState.SUBWAY_INSIDE;
    }
}
