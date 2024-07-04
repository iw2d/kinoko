package kinoko.server.dialog.shop;

public enum ShopRequestType {
    // ShopReq
    Buy(0),
    Sell(1),
    Recharge(2),
    Close(3);

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
