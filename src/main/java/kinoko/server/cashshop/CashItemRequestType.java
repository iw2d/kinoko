package kinoko.server.cashshop;

public enum CashItemRequestType {
    // CashItemReq
    WebShopOrderGetList(0),
    LoadLocker(1),
    LoadWIsh(2),
    Buy(3),
    Gift(4),
    SetWish(5),
    IncSlotCount(6),
    IncTrunkCount(7),
    IncCharSlotCount(8),
    IncBuyCharCount(9),
    EnableEquipSlotExt(10),
    CancelPurchase(11),
    ConfirmPurchase(12),
    Destroy(13),
    MoveLtoS(14),
    MoveStoL(15),
    Expire(16),
    Use(17),
    StatChange(18),
    SkillChange(19),
    SkillReset(20),
    DestroyPetItem(21),
    SetPetName(22),
    SetPetLife(23),
    SetPetSKill(24),
    SetItemName(25),
    SendMemo(26),
    GetMaplePoint(27),
    Rebate(28),
    UseCoupon(29),
    GiftCoupon(30),
    Couple(31),
    BuyPackage(32),
    GiftPackage(33),
    BuyNormal(34),
    ApplyWishListEvent(35),
    MovePetStat(36),
    Friendship(37),
    ShopScan(38),
    LoadPetExceptionList(39),
    UpdatePetExceptionList(40),
    FreeCashItem(41),
    LoadFreeCashItem(42),
    Script(43),
    PurchaseRecord(44),
    TradeDone(45),
    BuyDone(46),
    TradeSave(47),
    TradeLog(48),
    EvolPet(49),
    BuyNameChange(50),
    CancelChangeName(51),
    CancelNameChangeFail(52),
    BuyTransferWorld(53),
    CancelTransferWorld(54),

    CharacterSale(55),

    ItemUpgrade(60),
    ItemUpgradeFail(62), // CashItemReq_ItemUpgradeFail
    ItemUpgradeReq(63),
    ItemUpgradeDone(64),

    Vega(67),

    CashItemGachapon(74),
    CashGachaponOpen(75),
    CashGachaponCopy(76),

    ChangeMaplePoint(77),

    CheckFreeCashItemTable(78),

    SetFreeCashItemTable(81),

    Give(189);


    private final int value;

    CashItemRequestType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static CashItemRequestType getByValue(int value) {
        for (CashItemRequestType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
