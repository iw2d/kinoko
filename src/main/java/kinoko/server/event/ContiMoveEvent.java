package kinoko.server.event;

import kinoko.packet.field.ContiMovePacket;
import kinoko.server.field.FieldStorage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public abstract class ContiMoveEvent extends Event {
    protected final int boardingField1;
    protected final int boardingField2;
    protected final int waitingField1;
    protected final int waitingField2;
    protected final int insideField1;
    protected final int insideField2;
    protected final int arriveField1;
    protected final int arriveField2;

    public ContiMoveEvent(FieldStorage fieldStorage, int boardingField1, int boardingField2, int waitingField1, int waitingField2, int insideField1, int insideField2, int arriveField1, int arriveField2) {
        super(fieldStorage);
        this.boardingField1 = boardingField1;
        this.boardingField2 = boardingField2;
        this.waitingField1 = waitingField1;
        this.waitingField2 = waitingField2;
        this.insideField1 = insideField1;
        this.insideField2 = insideField2;
        this.arriveField1 = arriveField1;
        this.arriveField2 = arriveField2;
    }

    @Override
    public void initialize() {
        // Initialize current state
        final LocalDateTime now = LocalDateTime.now();
        final int minute = now.getMinute() % 10;
        if (minute >= 5 && minute < 9) {
            currentState = EventState.CONTIMOVE_BOARDING;
        } else if (minute == 9) {
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
        final int minute = getNearestMinute() % 10;
        if (minute == 5) {
            handleBoarding();
        } else if (minute == 9) {
            handleWaiting();
        } else if (minute == 0) {
            handleInside();
        }
    }

    protected void handleBoarding() {
        currentState = EventState.CONTIMOVE_BOARDING;
        broadcastPacket(boardingField1, ContiMovePacket.enterShipMove());
        broadcastPacket(boardingField2, ContiMovePacket.enterShipMove());
        warp(insideField1, arriveField1, "sp");
        warp(insideField2, arriveField2, "sp");
        reset(insideField1);
        reset(insideField2);
    }

    protected void handleWaiting() {
        currentState = EventState.CONTIMOVE_WAITING;
    }

    protected void handleInside() {
        currentState = EventState.CONTIMOVE_INSIDE;
        broadcastPacket(boardingField1, ContiMovePacket.leaveShipMove());
        broadcastPacket(boardingField2, ContiMovePacket.leaveShipMove());
        warp(waitingField1, insideField1, "sp");
        warp(waitingField2, insideField2, "sp");
        reset(waitingField1);
        reset(waitingField2);
    }
}
