package kinoko.server.cashshop;

public enum CashItemRequestType {
    // CashItemReq
    WEB_SHOP_ORDER_GET_LIST(0),
    LOAD_LOCKER(1),
    LOAD_WISH(2),
    BUY(3),
    GIFT(4),
    SET_WISH(5),
    INC_SLOT_COUNT(6),
    INC_TRUNK_COUNT(7),
    INC_CHAR_SLOT_COUNT(8),
    INC_BUY_CHAR_COUNT(9),
    ENABLE_EQUIP_SLOT_EXT(10),
    CANCEL_PURCHASE(11),
    CONFIRM_PURCHASE(12),
    DESTROY(13),
    MOVE_L_TO_S(14),
    MOVE_S_TO_L(15),
    EXPIRE(16),
    USE(17),
    STAT_CHANGE(18),
    SKILL_CHANGE(19),
    SKILL_RESET(20),
    DESTROY_PET_ITEM(21),
    SET_PET_NAME(22),
    SET_PET_LIFE(23),
    SET_PET_SKILL(24),
    SET_ITEM_NAME(25),
    SEND_MEMO(26),
    GET_MAPLE_POINT(27),
    REBASE(28),
    USE_COUPON(29),
    GIFT_COUPON(30),
    COUPLE(31),
    BUY_PACKAGE(32),
    GIFT_PACKAGE(33),
    BUY_NORMAL(34),
    APPLY_WISHLIST_EVENT(35),
    MOVE_PET_STAT(36),
    FRIENDSHIP(37),
    SHOP_SCAN(38),
    LOAD_PET_EXCEPTION_LIST(39),
    UPDATE_PET_EXCEPTION_LIST(40),
    FREE_CASH_ITEM(41),
    LOAD_FREE_CASH_ITEM(42),
    SCRIPT(43),
    PURCHASE_RECORD(44),
    TRADE_DONE(45),
    BUY_DONE(46),
    TRADE_SAVE(47),
    TRADE_LOG(48),
    EVOL_PET(49),
    BUY_NAME_CHANGE(50),
    CANCEL_CHANGE_NAME(51),
    CANCEL_NAME_CHANGE_FAIL(52),
    BUY_TRANSFER_WORLD(53),
    CANCEL_TRANSFER_WORLD(54),

    CHARACTER_SALE(55),

    ITEM_UPGRADE(60),
    ITEM_UPGRADE_FAIL(62), // CashItemReq_ItemUpgradeFail
    ITEM_UPGRADE_REQ(63),
    ITEM_UPGRADE_DONE(64),

    VEGA(67),

    CASH_ITEM_GACHAPON(74),
    CASH_GACHAPON_OPEN(75),
    CASH_GACHAPON_COPY(76),

    CHANGE_MAPLE_POINT(77),

    CHECK_FREE_CASH_ITEM_TABLE(78),

    SET_FREE_CASH_ITEM_TABLE(81),

    GIVE(189);


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
