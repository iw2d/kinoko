package kinoko.server.dialog.shop;

public enum ShopRequestType {
    // ShopReq
    BUY(0),
    SELL(1),
    RECHARGE(2),
    CLOSE(3);

    private final int value;

    ShopRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static ShopRequestType getByValue(int value) {
        for (ShopRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
