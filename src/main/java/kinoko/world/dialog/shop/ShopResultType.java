package kinoko.world.dialog.shop;

public enum ShopResultType {
    // ShopRes
    BuySuccess(0),
    BuyNoStock(1),
    BuyNoMoney(2),
    BuyUnknown(3),
    SellSuccess(4),
    SellNoStock(5),
    SellIncorrectRequest(6),
    SellUnknown(7),
    RechargeSuccess(8),
    RechargeNoStock(9),
    RechargeNoMoney(10),
    RechargeIncorrectRequest(11),
    RechargeUnknown(12),
    BuyNoToken(13),
    LimitLevel_Less(14),
    LimitLevel_More(15),
    CantBuyAnymore(16),
    TradeBlocked(17),
    BuyLimit(18),
    ServerMsg(19);

    private final int value;

    ShopResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
