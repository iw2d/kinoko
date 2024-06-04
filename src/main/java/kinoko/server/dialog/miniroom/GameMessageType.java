package kinoko.server.dialog.miniroom;

public enum GameMessageType {
    // MiniGame
    USER_BAN(0),
    USER_TURN(1),
    USER_GIVE_UP(2),
    USER_RETREAT_SUCCESS(3),
    USER_LEAVE(4),
    USER_LEAVE_ENGAGE(5),
    USER_LEAVE_ENGAGE_CANCEL(6),
    USER_ENTER(7),
    USER_NOT_ENOUGH_MONEY(8),
    USER_MATCH_CARD(9),
    TEN_SEC_ALERT(101),
    GAME_START(102),
    TOURNAMENT_MATCH_END(103);

    private final int value;

    GameMessageType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
