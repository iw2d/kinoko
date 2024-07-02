package kinoko.server.event;

public enum EventState {
    // Ludibrium Elevator
    ELEVATOR_GOING_DOWN,    // minute % 4 == 0
    ELEVATOR_2ND_FLOOR,     // minute % 4 == 1
    ELEVATOR_GOING_UP,      // minute % 4 == 2
    ELEVATOR_99TH_FLOOR,    // minute % 4 == 3

    // NLC Subway
    SUBWAY_BOARDING,        // minute % 10 == 5
    SUBWAY_WAITING,         // minute % 10 == 9
    SUBWAY_INSIDE           // minute % 10 == 0
}
