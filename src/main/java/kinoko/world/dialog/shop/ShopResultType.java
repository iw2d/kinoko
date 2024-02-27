package kinoko.world.dialog.shop;

public enum ShopResultType {
    // ShopRes
    BUY_SUCCESS(0),
    BUY_NO_STOCK(1),
    BUY_NO_MONEY(2),
    BUY_UNKNOWN(3),
    SELL_SUCCESS(4),
    SELL_NO_STOCK(5),
    SELL_INCORRECT_REQUEST(6),
    SELL_UNKNOWN(7),
    RECHARGE_SUCCESS(8),
    RECHARGE_NO_STOCK(9),
    RECHARGE_NO_MONEY(10),
    RECHARGE_INCORRECT_REQUEST(11),
    RECHARGE_UNKNOWN(12),
    BUY_NO_TOKEN(13),
    LIMIT_LEVEL_LESS(14),
    LIMIT_LEVEL_MORE(15),
    CANT_BUY_ANYMORE(16),
    TRADE_BLOCKED(17),
    BUY_LIMIT(18),
    SERVER_MSG(19);

    private final int value;

    ShopResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
