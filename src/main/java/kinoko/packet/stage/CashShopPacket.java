package kinoko.packet.stage;

import kinoko.server.cashshop.*;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;
import kinoko.world.item.InventoryType;
import kinoko.world.item.Item;
import kinoko.world.user.Account;

import java.util.List;

public final class CashShopPacket {
    // CCashShop::OnPacket ---------------------------------------------------------------------------------------------

    public static OutPacket queryCashResult(Account account) {
        return queryCashResult(
                account.getNxCredit(),
                account.getMaplePoint(),
                account.getNxPrepaid()
        );
    }

    public static OutPacket queryCashResult(int nxCredit, int maplePoint, int nxPrepaid) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopQueryCashResult);
        outPacket.encodeInt(nxCredit); // nNexonCash
        outPacket.encodeInt(maplePoint); // nMaplePoint
        outPacket.encodeInt(nxPrepaid); // nPrepaidNXCash
        return outPacket;
    }

    public static OutPacket cashItemGachaponResultSuccess(long itemSn, int remain, CashItemInfo cashItemInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemGachaponResult);
        outPacket.encodeByte(CashItemResultType.CashItemGachapon_Done.getValue());
        outPacket.encodeLong(itemSn);
        outPacket.encodeInt(remain);
        cashItemInfo.encode(outPacket);
        // CUICashItemGachapon::OnCashItemGachaponResult
        final Item item = cashItemInfo.getItem();
        outPacket.encodeInt(item.getItemId()); // nSelectedItemID
        outPacket.encodeByte(item.getQuantity()); // nSelectedItemCount
        outPacket.encodeByte(0); // bJackpot
        return outPacket;
    }

    public static OutPacket cashItemGachaponResultFailed() {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemGachaponResult);
        outPacket.encodeByte(CashItemResultType.GashItemGachapon_Failed.getValue());
        return outPacket;
    }


    // CCashShop::OnCashItemResult ---------------------------------------------------------------------------------------------

    public static OutPacket fail(CashItemResultType resultType, CashItemFailReason failReason) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(resultType.getValue());
        outPacket.encodeByte(failReason.getValue()); // nReason
        if (failReason == CashItemFailReason.NoStock || failReason == CashItemFailReason.NotAvailableTime) {
            outPacket.encodeInt(0); // nCommSN
        } else if (resultType == CashItemResultType.Buy_Failed && failReason == CashItemFailReason.LimitOverTheItem) {
            outPacket.encodeByte(0); // You cannot make any more purchases in %d.\r\nPlease try again in %d.
        }
        return outPacket;
    }

    public static OutPacket loadLockerDone(Account account) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.LoadLocker_Done.getValue());
        // aCashItemInfo
        final List<CashItemInfo> cashItemInfos = account.getLocker().getCashItems();
        outPacket.encodeShort(cashItemInfos.size());
        for (CashItemInfo cii : cashItemInfos) {
            cii.encode(outPacket); // GW_CashItemInfo (55)
        }
        outPacket.encodeShort(account.getTrunk().getSize()); // nTrunkCount
        outPacket.encodeShort(account.getSlotCount()); // nCharacterSlotCount
        outPacket.encodeShort(-1); // nBuyCharacterCount
        outPacket.encodeShort(-1); // nCharacterCount
        return outPacket;
    }

    public static OutPacket buyDone(CashItemInfo cashItemInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.Buy_Done.getValue());
        cashItemInfo.encode(outPacket); // GW_CashItemInfo
        return outPacket;
    }

    public static OutPacket buyPackageDone(List<CashItemInfo> cashItemInfos) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.BuyPackage_Done.getValue());
        outPacket.encodeByte(cashItemInfos.size());
        for (CashItemInfo cii : cashItemInfos) {
            cii.encode(outPacket); // GW_CashItemInfo (55)
        }
        outPacket.encodeInt(0); // bonus maple points
        return outPacket;
    }

    public static OutPacket giftDone(String receiverName, Commodity commodity) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.Gift_Done.getValue());
        outPacket.encodeString(receiverName); // sRcvCharacterName
        outPacket.encodeInt(commodity.getItemId()); // nItemID
        outPacket.encodeShort(commodity.getCount()); // nCount
        outPacket.encodeInt(commodity.getPrice()); // nSpentNXCash
        return outPacket;
    }

    public static OutPacket giftPackageDone(String receiverName, Commodity commodity) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.GiftPackage_Done.getValue());
        outPacket.encodeString(receiverName); // sRcvCharacterName
        outPacket.encodeInt(commodity.getItemId()); // nItemID
        outPacket.encodeShort(0); // ignored
        outPacket.encodeShort(0); // ignored
        outPacket.encodeInt(commodity.getPrice()); // nSpentNXCash
        return outPacket;
    }

    public static OutPacket loadGiftDone(List<Gift> gifts) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.LoadGift_Done.getValue());
        outPacket.encodeShort(gifts.size());
        for (Gift gift : gifts) {
            gift.encode(outPacket); // GW_GiftList (98)
        }
        return outPacket;
    }

    public static OutPacket loadWishDone(List<Integer> wishlist) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.LoadWish_Done.getValue());
        for (int i = 0; i < 10; i++) {
            if (wishlist != null && i < wishlist.size()) {
                outPacket.encodeInt(wishlist.get(i)); // sn
            } else {
                outPacket.encodeInt(0);
            }
        }
        return outPacket;
    }

    public static OutPacket setWishDone(List<Integer> wishlist) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.SetWish_Done.getValue());
        for (int sn : wishlist) {
            outPacket.encodeInt(sn);
        }
        return outPacket;
    }

    public static OutPacket incSlotCountDone(InventoryType inventoryType, int newSize) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.IncSlotCount_Done.getValue());
        outPacket.encodeByte(inventoryType.getValue()); // nTI
        outPacket.encodeShort(newSize); // new size
        return outPacket;
    }

    public static OutPacket incTrunkCountDone(int newSize) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.IncTrunkCount_Done.getValue());
        outPacket.encodeShort(newSize); // nTrunkCount
        return outPacket;
    }

    public static OutPacket incCharSlotCountDone(int newSize) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.IncCharSlotCount_Done.getValue());
        outPacket.encodeShort(newSize); // nCharacterSlotCount
        return outPacket;
    }

    public static OutPacket enableEquipSlotExtDone(int addDays) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.EnableEquipSlotExt_Done.getValue());
        outPacket.encodeShort(0); // ext slot type?
        outPacket.encodeShort(addDays); // short -> Util::FTAddDay
        return outPacket;
    }

    public static OutPacket moveLtoSDone(int position, Item item) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.MoveLtoS_Done.getValue());
        outPacket.encodeShort(position); // nPOS
        item.encode(outPacket); // GW_ItemSlotBase::Decode
        return outPacket;
    }

    public static OutPacket moveStoLDone(CashItemInfo cashItemInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.MoveStoL_Done.getValue());
        cashItemInfo.encode(outPacket); // GW_CashItemInfo (55)
        return outPacket;
    }

    public static OutPacket buyNormalDone(List<Tuple<Integer, Integer>> updates) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.BuyNormal_Done.getValue());
        outPacket.encodeInt(updates.size());
        for (var tuple : updates) {
            outPacket.encodeShort(0);
            outPacket.encodeShort(tuple.getLeft()); // nPos
            outPacket.encodeInt(tuple.getRight()); // nItemID
        }
        return outPacket;
    }

    public static OutPacket coupleDone(CashItemInfo cashItemInfo, String receiverName, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.Couple_Done.getValue());
        cashItemInfo.encode(outPacket); // GW_CashItemInfo
        outPacket.encodeString(receiverName);
        outPacket.encodeInt(itemId);
        outPacket.encodeShort(1); // count
        return outPacket;
    }

    public static OutPacket purchaseRecord(int commodityId, boolean purchaseRecord) {
        final OutPacket outPacket = OutPacket.of(OutHeader.CashShopCashItemResult);
        outPacket.encodeByte(CashItemResultType.PurchaseRecord_Done.getValue());
        outPacket.encodeInt(commodityId);
        outPacket.encodeByte(purchaseRecord); // nPurchaseRecord
        return outPacket;
    }
}
