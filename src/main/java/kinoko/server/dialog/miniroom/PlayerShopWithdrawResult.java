package kinoko.server.dialog.miniroom;

public enum PlayerShopWithdrawResult {
    // ESWithdraw
    Success(0),
    OverPrice(1),
    OnlyItem(2),
    NoSlot(3),
    Unknown(4),
    Nothing(5);

    private final int value;

    PlayerShopWithdrawResult(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
