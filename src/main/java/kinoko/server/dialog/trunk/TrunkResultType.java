package kinoko.server.dialog.trunk;

public enum TrunkResultType {
    // TrunkRes
    GetSuccess(9),
    GetUnknown(10),
    GetNoMoney(11),
    GetHavingOnlyItem(12),
    PutSuccess(13),
    PutIncorrectRequest(14),
    SortItem(15),
    PutNoMoney(16),
    PutNoSpace(17),
    PutUnknown(18),
    MoneySuccess(19),
    MoneyUnknown(20),
    TrunkCheckSSN2(21),
    OpenTrunkDlg(22),
    TradeBlocked(23),
    ServerMsg(24);

    private final int value;

    TrunkResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
