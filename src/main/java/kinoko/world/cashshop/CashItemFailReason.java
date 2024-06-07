package kinoko.world.cashshop;

public enum CashItemFailReason {
    // CashItemFailReason
    Unknown(0),
    Timeout(1),
    CashDaemonDBError(2),
    NORemainCash(3),
    GiftUnderAge(4),
    GiftLimitOver(5),
    GiftSameAccount(6),
    GiftUnknownRecipient(7),
    GiftRecipientGenderMismatch(8),
    GiftRecipientLockerFull(9),
    BuyStoredProcFailed(10),
    GiftStoredProcFailed(11),
    GiftNoReceiveCharacter(12),
    GiftNoSenderCharacter(13),
    InvalidCoupon(14),
    InvalidCoupon_UserBan(15),
    ExpiredCoupon(16),
    UsedCoupon(17),
    CouponForCafeOnly(18),
    CouponForCafeOnly_Used(19),
    CouponForCafeOnly_Expired(20),
    NotAvailableCoupon(21),
    GenderMismatch(22),
    GiftNormalItem(23),
    GiftMaplePoint(24),
    NoEmptyPos(25),
    ForPremiumUserOnly(26),
    BuyCoupleStoredProcFailed(27),
    BuyFriendshipStoredProcFailed(28),
    NotAvailableTime(29),
    NoStock(30),
    PurchaseLimitOver(31),
    NoRemainMeso(32),
    NotAuthorizedUser(33),
    InvalidBirthDate(34), // Check your PIC password and\r\nplease try again
    InvalidPassportID(35),
    IncorrectSSN2(36),
    ForNoPurchaseExpUsersOnly(37),
    AlreadyApplied(38),
    WebShopUnknown(39),
    WebShopInventoryCount(40),
    WebShopBuyStoredProcFailed(41),
    WebShopInvalidOrder(42),
    GachaponLimitOver(43),
    NoUser(44),
    WrongCommoditySN(45),
    CouponLimitError(46),
    BridgeNotConnected(47),
    UnderConstruction(48),
    Account_Age_limit(49),
    GiftNoMoney(50),
    DBError(51),
    AgeLimit(52),
    RestrictSender(53),
    RestrictReceiver(54),
    ExceedLimit(55),
    UnknownError(56),
    LevelLimit_20(57),
    TransferWorldFailed_InvalidWorld_SameWorld(58),
    TransferWorldFailed_InvalidWorld_NewWorld(59),
    TransferWorldFailed_InvalidWorld_FromNewWorld(60),
    TransferWorldFailed_MaxCharacter(61),
    EventError(62),
    OnlyNXCash(63),
    TryAgainRandomBox(64),
    CannotBuyOneADayItem(65),
    TooYoungToBuy(66),
    GiftTooYoungToRecv(67),
    LimitOverTheItem(68),
    CashLock(69);

    private final int value;

    CashItemFailReason(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
