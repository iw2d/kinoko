package kinoko.server.event;

import kinoko.server.field.FieldStorage;
import kinoko.server.node.ServerExecutor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public final class Airport extends Event {
    // Kerning City -> CBD
    public static final int KERNING_CITY = 103000000;
    public static final int KERNING_AIRPORT = 540010100;
    public static final int ON_THE_WAY_TO_CBD = 540010101;
    // CBD -> Kerning City
    public static final int CHANGI_AIRPORT = 540010000;
    public static final int BEFORE_DEPARTURE_TO_KERNING_CITY = 540010001;
    public static final int ON_THE_WAY_TO_KERNING_CITY = 540010002;


    public Airport(FieldStorage fieldStorage) {
        super(fieldStorage);
    }

    @Override
    public EventType getType() {
        return EventType.CM_AIRPORT;
    }

    @Override
    public void initialize() {
        // Initialize current state
        final LocalDateTime now = LocalDateTime.now();
        final int minute = now.getMinute() % 5;
        if (minute >= 1 && minute < 4) {
            currentState = EventState.AIRPORT_BOARDING;
        } else if (minute == 4) {
            currentState = EventState.AIRPORT_WAITING;
        } else {
            currentState = EventState.AIRPORT_INSIDE;
        }
        // Schedule event - run every minute
        final LocalDateTime nextStateTime = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        eventFuture = ServerExecutor.scheduleWithFixedDelay(this, this::nextState, now.until(nextStateTime, ChronoUnit.MILLIS), 60 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void nextState() {
        final int minute = getNearestMinute() % 5;
        if (minute == 1) {
            handleBoarding();
        } else if (minute == 4) {
            handleWaiting();
        } else if (minute == 0) {
            handleInside();
        }
    }

    private void handleBoarding() {
        warp(ON_THE_WAY_TO_CBD, CHANGI_AIRPORT, "sp");
        warp(ON_THE_WAY_TO_KERNING_CITY, KERNING_CITY, "sp");
        reset(ON_THE_WAY_TO_CBD);
        reset(ON_THE_WAY_TO_KERNING_CITY);
        currentState = EventState.AIRPORT_BOARDING;
    }

    private void handleWaiting() {
        currentState = EventState.AIRPORT_WAITING;
    }

    private void handleInside() {
        warp(KERNING_AIRPORT, ON_THE_WAY_TO_CBD, "sp");
        warp(BEFORE_DEPARTURE_TO_KERNING_CITY, ON_THE_WAY_TO_KERNING_CITY, "sp");
        reset(KERNING_AIRPORT);
        reset(BEFORE_DEPARTURE_TO_KERNING_CITY);
        currentState = EventState.AIRPORT_INSIDE;
    }
}
