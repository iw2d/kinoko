package kinoko.server.event;

import kinoko.server.field.FieldStorage;

public final class ContiMoveLudibrium extends ContiMoveEvent {
    // Orbis -> Ludibrium
    public static final int ORBIS_STATION_LUDIBRIUM = 200000121;
    public static final int BEFORE_THE_DEPARTURE_TO_LUDIBRIUM = 200000122;
    public static final int ON_A_VOYAGE_TO_LUDIBRIUM = 200090100;
    public static final int LUDIBRIUM_TICKETING_PLACE = 220000100;
    // Ludibrium -> Orbis
    public static final int LUDIBRIUM_STATION_ORBIS = 220000110;
    public static final int BEFORE_THE_DEPARTURE_TO_ORBIS = 220000111;
    public static final int ON_A_VOYAGE_TO_ORBIS = 200090110;
    public static final int ORBIS_STATION_ENTRANCE = 200000100;


    public ContiMoveLudibrium(FieldStorage fieldStorage) {
        super(
                fieldStorage,
                ORBIS_STATION_LUDIBRIUM,            // boardingField1
                LUDIBRIUM_STATION_ORBIS,            // boardingField2
                BEFORE_THE_DEPARTURE_TO_LUDIBRIUM,  // waitingField1
                BEFORE_THE_DEPARTURE_TO_ORBIS,      // waitingField2
                ON_A_VOYAGE_TO_LUDIBRIUM,           // insideField1
                ON_A_VOYAGE_TO_ORBIS,               // insideField2
                LUDIBRIUM_TICKETING_PLACE,          // arriveField1
                ORBIS_STATION_ENTRANCE              // arriveField2
        );
    }

    @Override
    public EventType getType() {
        return EventType.CM_LUDIBRIUM;
    }
}
