package kinoko.world.dialog.miniroom;

public enum EnterResultType {
    // MREnterResult
    SUCCESS(0),
    NO_ROOM(1),
    FULL(2),
    BUSY(3),
    DEAD(4),
    EVENT(5),
    PERMISSION_DENIED(6),
    NO_TRADING(7),
    ETC(8),
    ONLY_IN_SAME_FIELD(9),
    NEAR_PORTAL(10),
    CREATE_COUNT_OVER(11),
    CREATE_IP_COUNT_OVER(12),
    EXIST_MINIROOM(13),
    NOT_AVAILABLE_FIELD_GAME(14),
    NOT_AVAILABLE_FIELD_PERSONAL_SHOP(15),
    NOT_AVAILABLE_FIELD_ENTRUSTED_SHOP(16),
    ON_BLOCKED_LIST(17),
    IS_MANAGING(18),
    TOURNAMENT(19),
    ALREADY_PLAYING(20),
    NOT_ENOUGH_MONEY(21),
    INVALID_PASSWORD(22),
    NOT_AVAILABLE_FIELD_SHOP_SCANNER(23),
    EXPIRED(24),
    TOO_SHORT_TIME_INTERVAL(25);

    private final int value;

    EnterResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
