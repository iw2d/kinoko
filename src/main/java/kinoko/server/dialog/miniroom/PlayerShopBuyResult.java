package kinoko.server.dialog.miniroom;

public enum PlayerShopBuyResult {
    // PSBuy
    Success(0),
    NoStock(1),
    NoMoney(2),
    OverPrice(3),
    HostTooMuchMoney(4),
    NoSlot(5),
    OnlyItem(6),
    GenderMismatch(7),
    Under7Age(8),
    ItemExpired(9),
    Denied(10),
    DeniedUser(11),
    ItemCRCFailed(12),
    Unknown(13),
    MoneyLimit(14);

    private final int value;

    PlayerShopBuyResult(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
