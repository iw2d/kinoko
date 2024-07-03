package kinoko.server.event;

import kinoko.server.field.FieldStorage;

public final class ContiMoveAriant extends ContiMoveEvent {
    // Orbis -> Ariant
    public static final int ORBIS_STATION_TO_ARIANT = 200000151;
    public static final int BEFORE_TAKEOFF_TO_ARIANT = 200000152; // Orbis : Station <To Ariant>
    public static final int CRUISING_TO_ARIANT = 200090400;
    // Ariant -> Orbis
    public static final int ARIANT_STATION_PLATFORM = 260000100;
    public static final int BEFORE_TAKEOFF_TO_ORBIS = 260000110;
    public static final int CRUISING_TO_ORBIS = 200090410;
    public static final int ORBIS_STATION_ENTRANCE = 200000100;


    public ContiMoveAriant(FieldStorage fieldStorage) {
        super(
                fieldStorage,
                ORBIS_STATION_TO_ARIANT,    // boardingField1
                ARIANT_STATION_PLATFORM,    // boardingField2
                BEFORE_TAKEOFF_TO_ARIANT,   // waitingField1
                BEFORE_TAKEOFF_TO_ORBIS,    // waitingField2
                CRUISING_TO_ARIANT,         // insideField1
                CRUISING_TO_ORBIS,          // insideField2
                ARIANT_STATION_PLATFORM,    // arriveField1
                ORBIS_STATION_ENTRANCE      // arriveField2
        );
    }

    @Override
    public EventType getType() {
        return EventType.CM_ARIANT;
    }
}
