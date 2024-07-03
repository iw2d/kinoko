package kinoko.server.event;

import kinoko.server.field.FieldStorage;

public final class ContiMoveLeafre extends ContiMoveEvent {
    // Orbis -> Leafre
    public static final int ORBIS_STATION_TO_LEAFRE = 200000131;
    public static final int ORBIS_CABIN_TO_LEAFRE = 200000132;
    public static final int DURING_THE_RIDE_TO_LEAFRE = 200090200;
    public static final int LEAFRE_STATION_ENTRANCE = 240000100;
    // Leafre -> Orbis
    public static final int LEAFRE_STATION = 240000110;
    public static final int BEFORE_TAKEOFF_TO_ORBIS = 240000111;
    public static final int DURING_THE_RIDE_TO_ORBIS = 200090210;
    public static final int ORBIS_STATION_ENTRANCE = 200000100;


    public ContiMoveLeafre(FieldStorage fieldStorage) {
        super(
                fieldStorage,
                ORBIS_STATION_TO_LEAFRE,    // boardingField1
                LEAFRE_STATION,             // boardingField2
                ORBIS_CABIN_TO_LEAFRE,      // waitingField1
                BEFORE_TAKEOFF_TO_ORBIS,    // waitingField2
                DURING_THE_RIDE_TO_LEAFRE,  // insideField1
                DURING_THE_RIDE_TO_ORBIS,   // insideField2
                LEAFRE_STATION_ENTRANCE,    // arriveField1
                ORBIS_STATION_ENTRANCE      // arriveField2
        );
    }

    @Override
    public EventType getType() {
        return EventType.CM_LEAFRE;
    }
}
