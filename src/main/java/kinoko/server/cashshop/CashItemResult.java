package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.item.Item;

import java.util.List;

public final class CashItemResult implements Encodable {
    private final CashItemResultType type;
    private long long1;
    private int int1;
    private int int2;
    private int int3;
    private int int4;
    private boolean bool1;
    private boolean bool2;
    private String string1;

    private long[] longArray;
    private int[] intArray;

    private List<CashItemInfo> cashItemInfos;
    private CashItemInfo cashItemInfo;
    private List<GiftList> giftLists;
    private Item item;

    private CashItemFailReason reason;

    private CashItemResult(CashItemResultType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case LIMIT_GOODS_COUNT_CHANGED -> {
                // CCashShop::OnCashItemResLimitGoodsCountChanged
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeInt(int2); // nSN
                outPacket.encodeInt(int3); // nRemainCount
            }
            case LOAD_LOCKER_DONE -> {
                outPacket.encodeShort(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeShort(int1); // nTrunkCount
                outPacket.encodeShort(int2); // nCharacterSlotCount
                outPacket.encodeShort(int3); // nBuyCharacterCount
                outPacket.encodeShort(int4); // nCharacterCount
            }
            case LOAD_GIFT_DONE -> {
                outPacket.encodeShort(giftLists.size());
                for (GiftList giftList : giftLists) {
                    giftList.encode(outPacket); // GW_GiftList (98)
                }
            }
            case LOAD_WISH_DONE, SET_WISH_DONE -> {
                for (int i = 0; i < 10; i++) {
                    if (i < intArray.length) {
                        outPacket.encodeInt(intArray[i]); // sn
                    } else {
                        outPacket.encodeInt(0);
                    }
                }
            }
            case BUY_DONE, FREE_CASH_ITEM_DONE, NAME_CHANGE_BUY_DONE, TRANSFER_WORLD_DONE -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
            }
            case BUY_FAILED -> {
                outPacket.encodeByte(reason.getValue());
                if (reason == CashItemFailReason.NO_STOCK || reason == CashItemFailReason.NOT_AVAILABLE_TIME) {
                    outPacket.encodeInt(int1); // nCommSN
                } else if (reason == CashItemFailReason.LIMIT_OVER_THE_ITEM) {
                    outPacket.encodeByte(int2); // You cannot make any more purchases in %d.\r\nPlease try again in %d.
                }
            }
            case USE_COUPON_DONE -> {
                outPacket.encodeByte(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeInt(int1);
                outPacket.encodeInt(longArray.length);
                for (int i = 0; i < longArray.length; i++) {
                    outPacket.encodeLong(longArray[i]);
                }
                outPacket.encodeInt(int2);
            }
            case GIFT_COUPON_DONE -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeByte(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeInt(int1);
            }
            case GIFT_DONE -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeShort(int2); // nCount
                outPacket.encodeInt(int3); // nSpentNXCash
            }
            case INC_SLOT_COUNT_DONE -> {
                outPacket.encodeByte(int1); // inventory type
                outPacket.encodeShort(int2); // new size
            }
            case INC_TRUNK_COUNT_DONE, INC_CHAR_SLOT_COUNT_DONE, INC_BUY_CHAR_COUNT_DONE -> {
                outPacket.encodeShort(int1); // nTrunkCount
            }
            case ENABLE_EQUIP_SLOT_EXT_DONE -> {
                outPacket.encodeShort(int1); // not sure
                outPacket.encodeShort(int2); // days?
            }
            case MOVE_L_TO_S_DONE -> {
                outPacket.encodeShort(int1); // nPOS
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case MOVE_S_TO_L_DONE -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
            }
            case DESTROY_DONE, EXPIRE_DONE -> {
                outPacket.encodeLong(long1); // liSN
            }
            case REBATE_DONE -> {
                outPacket.encodeLong(long1); // liSN
                outPacket.encodeInt(int1); // Cash item has been deleted.\r\n(%d accumulated as MaplePoints)
            }
            case COUPLE_DONE, FRIENDSHIP_DONE -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeInt(int2); // nCount
            }
            case BUY_PACKAGE_DONE -> {
                outPacket.encodeShort(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeShort(int1); // maple points
            }
            case GIFT_PACKAGE_DONE -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeShort(0); // unused
                outPacket.encodeShort(0); // ^
                outPacket.encodeInt(int2); // [ %s ] \r\nwas sent to %s. \r\n%d NX Prepaid \r\nwere spent in the process.
            }
            case BUY_NORMAL_DONE -> {
                outPacket.encodeInt(longArray.length);
                for (int i = 0; i < longArray.length; i++) {
                    outPacket.encodeLong(longArray[i]);
                }
            }
            case PURCHASE_RECORD_DONE -> {
                outPacket.encodeInt(int1);
                outPacket.encodeByte(int2); // nPurchaseRecord
            }
            case CASH_GACHAPON_OPEN_DONE -> {
                outPacket.encodeLong(long1); // liSN
                outPacket.encodeInt(int1); // nRemain
                outPacket.encodeByte(bool1); // bIsCashItem
                if (bool1) {
                    cashItemInfo.encode(outPacket);
                }
                outPacket.encodeInt(int2); // nItemID
                outPacket.encodeInt(int3); // nCount
            }
            case CASH_GACHAPON_COPY_DONE -> {
                outPacket.encodeByte(bool1); // bRandomItemCopy
                outPacket.encodeByte(bool2);
                outPacket.encodeInt(0); // unused
                outPacket.encodeInt(0); // ^
                outPacket.encodeInt(int1); // nRandomItemLostItemID
                outPacket.encodeInt(int1); // nRandomItemLostNumber
                if (bool1 && bool2) {
                    cashItemInfo.encode(outPacket);
                }
            }
            case CHANGE_MAPLE_POINT_DONE -> {
                outPacket.encodeLong(long1); // liSN to remove?
                outPacket.encodeInt(int1); // You've received %d Maple Points.
            }
            case CHANGE_MAPLE_POINT_FAILED -> {
                // You've failed in using a Maple Point Chip.
            }
            case LOAD_LOCKER_FAILED, LOAD_GIFT_FAILED, LOAD_WISH_FAILED, SET_WISH_FAILED, USE_COUPON_FAILED,
                    GIFT_FAILED, INC_SLOT_COUNT_FAILED, INC_TRUNK_COUNT_FAILED, INC_CHAR_SLOT_COUNT_FAILED,
                    INC_BUY_CHAR_COUNT_FAILED, ENABLE_EQUIP_SLOT_EXT_FAILED, MOVE_L_TO_S_FAILED, MOVE_S_TO_L_FAILED,
                    DESTROY_FAILED, EXPIRE_FAILED, REBATE_FAILED, PURCHASE_RECORD_FAILED, TRANSFER_WORLD_FAILED,
                    CASH_GACHAPON_OPEN_FAILED, CASH_GACHAPON_COPY_FAILED -> {
                outPacket.encodeByte(reason.getValue()); // nReason
            }
            case COUPLE_FAILED, BUY_PACKAGE_FAILED, GIFT_PACKAGE_FAILED, BUY_NORMAL_FAILED, FRIENDSHIP_FAILED -> {
                outPacket.encodeByte(reason.getValue());
                if (reason == CashItemFailReason.NO_STOCK || reason == CashItemFailReason.NOT_AVAILABLE_TIME) {
                    outPacket.encodeInt(int1); // nCommSN
                }
            }
            default -> {
                throw new IllegalArgumentException("Unsupported cash item result type : " + type.name());
            }
        }
    }

    public static CashItemResult of(CashItemResultType type) {
        return new CashItemResult(type);
    }
}
