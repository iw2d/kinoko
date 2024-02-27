package kinoko.world.dialog.trunk;

public enum TrunkResultType {
    GET_SUCCESS(9),
    GET_UNKNOWN(10),
    GET_NO_MONEY(11),
    GET_HAVING_ONLY_ITEM(12),
    PUT_SUCCESS(13),
    PUT_INCORRECT_REQUEST(14),
    SORT_ITEM(15),
    PUT_NO_MONEY(16),
    PUT_NO_SPACE(17),
    PUT_UNKNOWN(18),
    MONEY_SUCCESS(19),
    MONEY_UNKNOWN(20),
    TRUNK_CHECK_SSN2(21),
    OPEN_TRUNK_DLG(22),
    TRADE_BLOCKED(23),
    SERVER_MSG(24);

    private final int value;

    TrunkResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
