package kinoko.world.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.Account;

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

    private List<Long> longList;
    private List<Integer> intList;

    private List<CashItemInfo> cashItemInfos;
    private CashItemInfo cashItemInfo;
    private List<Gift> gifts;
    private Item item;

    private CashItemFailReason reason;

    private CashItemResult(CashItemResultType type) {
        this.type = type;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(type.getValue());
        switch (type) {
            case LimitGoodsCount_Changed -> {
                // CCashShop::OnCashItemResLimitGoodsCountChanged
                outPacket.encodeInt(int1); // nItemId
                outPacket.encodeInt(int2); // nSN
                outPacket.encodeInt(int3); // nRemainCount
            }
            case LoadLocker_Done -> {
                outPacket.encodeShort(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeShort(int1); // nTrunkCount
                outPacket.encodeShort(int2); // nCharacterSlotCount
                outPacket.encodeShort(int3); // nBuyCharacterCount
                outPacket.encodeShort(int4); // nCharacterCount
            }
            case LoadGift_Done -> {
                outPacket.encodeShort(gifts.size());
                for (Gift gift : gifts) {
                    gift.encode(outPacket); // GW_GiftList (98)
                }
            }
            case LoadWish_Done, SetWish_Done -> {
                for (int i = 0; i < 10; i++) {
                    if (i < intList.size()) {
                        outPacket.encodeInt(intList.get(i)); // sn
                    } else {
                        outPacket.encodeInt(0);
                    }
                }
            }
            case Buy_Done, FreeCashItem_Done, NameChangeBuy_Done, TransferWorld_Done -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
            }
            case Buy_Failed -> {
                outPacket.encodeByte(reason.getValue());
                if (reason == CashItemFailReason.NoStock || reason == CashItemFailReason.NotAvailableTime) {
                    outPacket.encodeInt(int1); // nCommSN
                } else if (reason == CashItemFailReason.LimitOverTheItem) {
                    outPacket.encodeByte(int2); // You cannot make any more purchases in %d.\r\nPlease try again in %d.
                }
            }
            case UseCoupon_Done -> {
                outPacket.encodeByte(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeInt(int1);
                outPacket.encodeInt(longList.size());
                for (long longItem : longList) {
                    outPacket.encodeLong(longItem);
                }
                outPacket.encodeInt(int2);
            }
            case GiftCoupon_Done -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeByte(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeInt(int1);
            }
            case Gift_Done -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeShort(int2); // nCount
                outPacket.encodeInt(int3); // nSpentNXCash
            }
            case IncSlotCount_Done -> {
                outPacket.encodeByte(int1); // inventory type
                outPacket.encodeShort(int2); // new size
            }
            case IncTrunkCount_Done, IncCharSlotCount_Done, IncBuyCharCount_Done -> {
                outPacket.encodeShort(int1);
            }
            case EnableEquipSlotExt_Done -> {
                outPacket.encodeShort(int1);
                outPacket.encodeShort(int2); // short -> Util::FTAddDay
            }
            case MoveLtoS_Done -> {
                outPacket.encodeShort(int1); // nPOS
                item.encode(outPacket); // GW_ItemSlotBase::Decode
            }
            case MoveStoL_Done -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
            }
            case Destroy_Done, Expire_Done -> {
                outPacket.encodeLong(long1); // liSN
            }
            case Rebate_Done -> {
                outPacket.encodeLong(long1); // liSN
                outPacket.encodeInt(int1); // Cash item has been deleted.\r\n(%d accumulated as MaplePoints)
            }
            case Couple_Done, Friendship_Done -> {
                cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeInt(int2); // nCount
            }
            case BuyPackage_Done -> {
                outPacket.encodeByte(cashItemInfos.size());
                for (CashItemInfo cii : cashItemInfos) {
                    cii.encode(outPacket); // GW_CashItemInfo (55)
                }
                outPacket.encodeShort(int1); // bonus maple points
            }
            case GiftPackage_Done -> {
                outPacket.encodeString(string1); // sRcvCharacterName
                outPacket.encodeInt(int1); // nItemID
                outPacket.encodeShort(0); // unused
                outPacket.encodeShort(0); // ^
                outPacket.encodeInt(int2); // [ %s ] \r\nwas sent to %s. \r\n%d NX Prepaid \r\nwere spent in the process.
            }
            case BuyNormal_Done -> {
                outPacket.encodeInt(longList.size());
                for (long longItem : longList) {
                    outPacket.encodeLong(longItem);
                }
            }
            case PurchaseRecord_Done -> {
                outPacket.encodeInt(int1);
                outPacket.encodeByte(bool1); // nPurchaseRecord
            }
            case CashGachaponOpen_Done -> {
                outPacket.encodeLong(long1); // liSN
                outPacket.encodeInt(int1); // nRemain
                outPacket.encodeByte(bool1); // bIsCashItem
                if (bool1) {
                    cashItemInfo.encode(outPacket);
                }
                outPacket.encodeInt(int2); // nItemID
                outPacket.encodeInt(int3); // nCount
            }
            case CashGachaponCopy_Done -> {
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
            case ChangeMaplePoint_Done -> {
                outPacket.encodeLong(long1); // liSN to remove?
                outPacket.encodeInt(int1); // You've received %d Maple Points.
            }
            case ChangeMaplePoint_Failed -> {
                // You've failed in using a Maple Point Chip.
            }
            case LoadLocker_Failed, LoadGift_Failed, LoadWish_Failed, SetWish_Failed, UseCoupon_Failed,
                    Gift_Failed, IncSlotCount_Failed, IncTrunkCount_Failed, IncCharSlotCount_Failed,
                    IncBuyCharCount_Failed, EnableEquipSlotExt_Failed, MoveLtoS_Failed, MoveStoL_Failed,
                    DestroyFailed, Expire_Failed, Rebate_Failed, PurchaseRecord_Failed, TransferWorld_Failed,
                    CashGachaponOpen_Failed, CashGachapon_Copy_Failed -> {
                outPacket.encodeByte(reason.getValue()); // nReason
            }
            case Couple_Failed, BuyPackage_Failed, GiftPackage_Failed, BuyNormal_Failed, Friendship_Failed -> {
                outPacket.encodeByte(reason.getValue());
                if (reason == CashItemFailReason.NoStock || reason == CashItemFailReason.NotAvailableTime) {
                    outPacket.encodeInt(int1); // nCommSN
                }
            }
            default -> {
                throw new IllegalArgumentException("Unsupported cash item result type : " + type.name());
            }
        }
    }

    public static CashItemResult fail(CashItemResultType type, CashItemFailReason reason) {
        final CashItemResult result = new CashItemResult(type);
        result.reason = reason;
        return result;
    }

    public static CashItemResult loadLockerDone(Account account) {
        final CashItemResult result = new CashItemResult(CashItemResultType.LoadLocker_Done);
        result.cashItemInfos = account.getLocker().getCashItems(); // aCashItemInfo
        result.int1 = account.getTrunk().getSize(); // nTrunkCount
        result.int2 = account.getSlotCount(); // nCharacterSlotCount
        result.int3 = -1; // nBuyCharacterCount
        result.int4 = -1; // nCharacterCount
        return result;
    }

    public static CashItemResult buyDone(CashItemInfo cashItemInfo) {
        final CashItemResult result = new CashItemResult(CashItemResultType.Buy_Done);
        result.cashItemInfo = cashItemInfo; // GW_CashItemInfo
        return result;
    }

    public static CashItemResult buyPackageDone(List<CashItemInfo> cashItemInfos) {
        final CashItemResult result = new CashItemResult(CashItemResultType.BuyPackage_Done);
        result.cashItemInfos = cashItemInfos;
        result.int1 = 0; // bonus maple points
        return result;
    }

    public static CashItemResult giftDone(String receiverName, Commodity commodity) {
        final CashItemResult result = new CashItemResult(CashItemResultType.Gift_Done);
        result.string1 = receiverName; // sRcvCharacterName
        result.int1 = commodity.getItemId(); // nItemID
        result.int2 = commodity.getCount(); // nCount
        result.int3 = commodity.getPrice(); // nSpentNXCash
        return result;
    }

    public static CashItemResult loadGiftDone(List<Gift> gifts) {
        final CashItemResult result = new CashItemResult(CashItemResultType.LoadGift_Done);
        result.gifts = gifts; // GW_GiftList
        return result;
    }

    public static CashItemResult loadWishDone(List<Integer> wishlist) {
        final CashItemResult result = new CashItemResult(CashItemResultType.LoadWish_Done);
        result.intList = wishlist; // nWishList
        return result;
    }

    public static CashItemResult setWishDone(List<Integer> wishlist) {
        final CashItemResult result = new CashItemResult(CashItemResultType.SetWish_Done);
        result.intList = wishlist; // nWishList
        return result;
    }

    public static CashItemResult incSlotCountDone(InventoryType inventoryType, int newSize) {
        final CashItemResult result = new CashItemResult(CashItemResultType.IncSlotCount_Done);
        result.int1 = inventoryType.getValue(); // nTI
        result.int2 = newSize;
        return result;
    }

    public static CashItemResult incTrunkCountDone(int newSize) {
        final CashItemResult result = new CashItemResult(CashItemResultType.IncTrunkCount_Done);
        result.int1 = newSize; // nTrunkCount
        return result;
    }

    public static CashItemResult incCharSlotCountDone(int newSize) {
        final CashItemResult result = new CashItemResult(CashItemResultType.IncCharSlotCount_Done);
        result.int1 = newSize; // nCharacterSlotCount
        return result;
    }

    public static CashItemResult enableEquipSlotExtDone(int addDays) {
        final CashItemResult result = new CashItemResult(CashItemResultType.EnableEquipSlotExt_Done);
        result.int1 = 0; // ext slot type?
        result.int2 = addDays;
        return result;
    }

    public static CashItemResult moveLtoSDone(int position, Item item) {
        final CashItemResult result = new CashItemResult(CashItemResultType.MoveLtoS_Done);
        result.int1 = position; // nPOS
        result.item = item; // GW_ItemSlotBase
        return result;
    }

    public static CashItemResult moveStoLDone(CashItemInfo cashItemInfo) {
        final CashItemResult result = new CashItemResult(CashItemResultType.MoveStoL_Done);
        result.cashItemInfo = cashItemInfo; // GW_CashItemInfo
        return result;
    }

    public static CashItemResult purchaseRecord(int commodityId, boolean purchaseRecord) {
        final CashItemResult result = new CashItemResult(CashItemResultType.PurchaseRecord_Done);
        result.int1 = commodityId;
        result.bool1 = purchaseRecord;
        return result;
    }
}
