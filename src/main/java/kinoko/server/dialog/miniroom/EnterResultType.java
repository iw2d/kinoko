package kinoko.server.dialog.miniroom;

public enum EnterResultType {
    // MREnterResult
    Success(0),
    NoRoom(1),
    Full(2),
    Busy(3),
    Dead(4),
    Event(5),
    PermissionDenied(6),
    NOTrading(7),
    Etc(8),
    OnlyInSameField(9),
    NearPortal(10),
    CreateCountOver(11),
    CreateIPCountOver(12),
    ExistMiniRoom(13),
    NotAvailableField_Game(14),
    NotAvailableField_PersonalShop(15),
    NotAvailableField_EntrustedShop(16),
    OnBlockedList(17),
    IsManaging(18),
    Tournament(19),
    AlreadyPlaying(20),
    NOtEnoughMoney(21),
    InvalidPassword(22),
    NotAvailableField_ShopScanner(23),
    Expired(24),
    TooShortTimeInterval(25);

    private final int value;

    EnterResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
