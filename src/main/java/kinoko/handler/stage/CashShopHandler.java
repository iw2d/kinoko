package kinoko.handler.stage;

import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.stage.CashShopPacket;
import kinoko.server.cashshop.*;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.world.Account;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CashShopHandler {
    private static final Logger log = LogManager.getLogger(CashShopHandler.class);

    @Handler(InHeader.CASHSHOP_QUERY_CASH_REQUEST)
    public static void handleQueryCashRequest(User user, InPacket inPacket) {
        try (var lockedAccount = user.getAccount().acquire()) {
            user.write(CashShopPacket.queryCashResult(lockedAccount.get()));
        }
    }

    @Handler(InHeader.CASHSHOP_CASH_ITEM_REQUEST)
    public static void handleCashItemRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final CashItemRequestType requestType = CashItemRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown cash item request type : {}", type);
            return;
        }
        switch (requestType) {
            case BUY -> {
                // CCashShop::OnBuy
                // CCashShop::SendBuyAvatarPacket
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN
                // These two encodes are swapped for OnBuy and SendBuyAvatarPacket - ignore
                inPacket.decodeByte(); // bRequestBuyOneADay
                inPacket.decodeInt(); // nEventSN

                // Resolve Commodity and create CashItemInfo
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.BUY_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                final Optional<CashItemInfo> cashItemInfoResult = commodity.createCashItemInfo(user);
                if (cashItemInfoResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.BUY_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not create cash item info for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final CashItemInfo cashItemInfo = cashItemInfoResult.get();
                try (var lockedAccount = user.getAccount().acquire()) {
                    // Check account locker
                    final Account account = lockedAccount.get();
                    if (account.getLocker().getRemaining() < 1) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.BUY_FAILED, CashItemFailReason.BUY_STORED_PROC_FAILED))); // Please check and see if you have exceeded\r\nthe number of cash items you can have.
                        return;
                    }
                    // Check payment type and deduct price
                    if (!deductPrice(lockedAccount, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.BUY_FAILED, CashItemFailReason.NO_REMAIN_CASH))); // You don't have enough cash.
                        return;
                    }
                    // Add to locker and update client
                    account.getLocker().addCashItem(cashItemInfo);
                    user.write(CashShopPacket.queryCashResult(account));
                    user.write(CashShopPacket.cashItemResult(CashItemResult.buyDone(cashItemInfo)));
                }
            }
            case GIFT -> {
                // CCashShop::SendGiftsPacket
                // CCashShop::GiftWishItem
                final String secondaryPassword = inPacket.decodeString();
                final int commodityId = inPacket.decodeInt(); // nCommSN
                // This byte is only encoded in CCashShop::SendGiftsPacket, and not CCashShop::GiftWishItem
                // Should be able to differentiate them by checking if 0
                if (inPacket.peekByte() == 0) {
                    inPacket.decodeByte(); // bRequestBuyOneADay
                }
                final String receiverName = inPacket.decodeString();
                final String giftMessage = inPacket.decodeString();

                // Check secondary password
                if (!DatabaseManager.accountAccessor().checkPassword(user.getAccount(), secondaryPassword, true)) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.INVALID_BIRTH_DATE))); // Check your PIC password and\r\nplease try again
                    return;
                }
                // Check gift receiver
                final Optional<Integer> receiverAccountIdResult = DatabaseManager.characterAccessor().getAccountIdByName(receiverName);
                if (receiverAccountIdResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.GIFT_UNKNOWN_RECIPIENT))); // Please confirm whether\r\nthe character's name is correct.
                    return;
                }
                final int receiverAccountId = receiverAccountIdResult.get();
                if (receiverAccountId == user.getAccountId()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.GIFT_SAME_ACCOUNT))); // You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
                    return;
                }
                // Resolve Commodity and create Gift
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                final Optional<Gift> giftResult = commodity.createGift(user, giftMessage);
                if (giftResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not create gift for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final Gift gift = giftResult.get();
                try (var lockedAccount = user.getAccount().acquire()) {
                    // Check account has enough nx prepaid (gifts are prepaid only
                    final Account account = lockedAccount.get();
                    if (account.getNxPrepaid() < commodity.getPrice()) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.NO_REMAIN_CASH))); // You don't have enough cash.
                        return;
                    }
                    // Record gift in DB and deduct nx credit
                    if (!DatabaseManager.giftAccessor().newGift(gift, receiverAccountId)) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.GIFT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                        return;
                    }
                    account.setNxPrepaid(account.getNxPrepaid() - commodity.getPrice());
                    // Update client
                    user.write(CashShopPacket.queryCashResult(account));
                    user.write(CashShopPacket.cashItemResult(CashItemResult.giftDone(receiverName, commodity)));
                }

                // TODO create memo for receiver
            }
            case SET_WISH -> {
                // CCashShop::OnSetWish
                // CCashShop::OnRemoveWish
                final List<Integer> wishlist = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    wishlist.add(inPacket.decodeInt()); // nCommSN
                }
                try (var lockedAccount = user.getAccount().acquire()) {
                    final Account account = lockedAccount.get();
                    account.setWishlist(Collections.unmodifiableList(wishlist));
                    user.write(CashShopPacket.cashItemResult(CashItemResult.setWishDone(account.getWishlist())));
                }
            }
            case INC_SLOT_COUNT, INC_TRUNK_COUNT -> {
                // CCashShop::OnExItemSlot - (0x6) : byte, int (dwOption), byte (0), byte (nTI)
                // CCashShop::OnIncTrunkCount - (0x7) : byte, int (dwOption), byte (0)
                // CCashShop::OnBuySlotInc - (0x6 + (is_trunkcount_inc_item)) : byte, int (dwOption), byte (1), int (nCommSN)
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final boolean isAdd4Slots = inPacket.decodeInt() == 0;

                // Add by UI (4 slots) or by commodity (CCashShop::OnBuySlotInc)
                if (isAdd4Slots) {
                    if (requestType == CashItemRequestType.INC_SLOT_COUNT) {
                        // Check inventory type
                        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI
                        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                            user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                            return;
                        }
                        handleIncSlotCount(user, inventoryType, 4, paymentType, CashShop.ADD_4_SLOTS_PRICE);
                    } else {
                        handleIncTrunkCount(user, 4, paymentType, CashShop.ADD_4_SLOTS_PRICE);
                    }
                } else {
                    // Resolve commodity
                    final int commodityId = inPacket.decodeInt(); // nCommSN
                    final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                    if (commodityResult.isEmpty()) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                        log.error("Could not resolve commodity with ID : {}", commodityId);
                        return;
                    }
                    final Commodity commodity = commodityResult.get();
                    switch (commodity.getItemId()) {
                        case CashShop.ADD_STORAGE_SLOTS -> {
                            handleIncTrunkCount(user, 8, paymentType, commodity.getPrice());
                        }
                        case CashShop.ADD_EQUIP_SLOTS -> {
                            handleIncSlotCount(user, InventoryType.EQUIP, 8, paymentType, commodity.getPrice());
                        }
                        case CashShop.ADD_USE_SLOTS -> {
                            handleIncSlotCount(user, InventoryType.CONSUME, 8, paymentType, commodity.getPrice());
                        }
                        case CashShop.ADD_SETUP_SLOTS -> {
                            handleIncSlotCount(user, InventoryType.INSTALL, 8, paymentType, commodity.getPrice());
                        }
                        case CashShop.ADD_ETC_SLOTS -> {
                            handleIncSlotCount(user, InventoryType.ETC, 8, paymentType, commodity.getPrice());
                        }
                        default -> {
                            user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                            log.error("Tried to increase slot count with item ID : {}", commodity.getItemId());
                        }
                    }
                }
            }
            case INC_CHAR_SLOT_COUNT -> {
                // CCashShop::OnIncCharacterSlotCount
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN

                // Resolve and verify commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_CHAR_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (commodity.getItemId() != CashShop.ADD_CHAR_SLOTS) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_CHAR_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Tried to increase char slot count with item ID : {}", commodity.getItemId());
                    return;
                }
                try (var lockedAccount = user.getAccount().acquire()) {
                    // Check current character slot count
                    final Account account = lockedAccount.get();
                    final int newSize = account.getSlotCount() + 1;
                    if (newSize > GameConstants.CHARACTER_SLOT_MAX) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_CHAR_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                        log.error("Tried to increase char slot count over maximum number");
                        return;
                    }
                    // Check payment type and deduct price
                    if (!deductPrice(lockedAccount, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_CHAR_SLOT_COUNT_FAILED, CashItemFailReason.NO_REMAIN_CASH))); // You don't have enough cash.
                        return;
                    }
                    // Increase slot count and update client
                    account.setSlotCount(newSize);
                    user.write(CashShopPacket.queryCashResult(account));
                    user.write(CashShopPacket.cashItemResult(CashItemResult.incCharSlotCountDone(newSize)));
                }
            }
            case ENABLE_EQUIP_SLOT_EXT -> {
                // CCashShop::OnEnableEquipSlotExt
                final boolean isMaplePoint = inPacket.decodeBoolean(); // nx credit or maple point only
                final int commodityId = inPacket.decodeInt(); // nCommSN

                // Resolve and verify commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.ENABLE_EQUIP_SLOT_EXT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (commodity.getItemId() != CashShop.EQUIP_SLOT_EXT_30_DAYS && commodity.getItemId() != CashShop.EQUIP_SLOT_EXT_7_days) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.ENABLE_EQUIP_SLOT_EXT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    log.error("Tried to enable equip slot ext with item ID : {}", commodity.getItemId());
                }
                // TODO
            }
            case MOVE_L_TO_S -> {
                // CCashShop::OnMoveCashItemLtoS
                final long itemSn = inPacket.decodeLong(); // liSN
                final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI
                final int position = inPacket.decodeShort(); // nPOS

                // Verify inventory type
                if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_L_TO_S_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    return;
                }
                try (var locked = user.acquire()) {
                    // Verify inventory position
                    final InventoryManager im = user.getInventoryManager();
                    final Inventory inventory = im.getInventoryByType(inventoryType);
                    if (inventory.getItem(position) != null) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_L_TO_S_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                        log.error("Tried to move locker item to {} inventory position {} which was not empty", inventoryType.name(), position);
                        return;
                    }
                    try (var lockedAccount = user.getAccount().acquire()) {
                        // Resolve and remove item in locker
                        final Locker locker = lockedAccount.get().getLocker();
                        final var iter = locker.getCashItems().iterator();
                        CashItemInfo removed = null;
                        while (iter.hasNext()) {
                            final CashItemInfo cii = iter.next();
                            if (cii.getItem().getItemSn() == itemSn) {
                                removed = cii;
                                iter.remove();
                                break;
                            }
                        }
                        if (removed == null) {
                            user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_L_TO_S_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                            log.error("Could not resolve cash item in locker with sn : {}", itemSn);
                            return;
                        }
                        // Move item to specified position and update client
                        inventory.putItem(position, removed.getItem());
                        user.write(CashShopPacket.cashItemResult(CashItemResult.moveLtoSDone(position, removed.getItem())));
                    }
                }
            }
            case MOVE_S_TO_L -> {
                // CCashShop::OnMoveCashItemStoL
                final long itemSn = inPacket.decodeLong(); // liSN
                final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI

                // Verify inventory type
                if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_S_TO_L_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                    return;
                }
                try (var lockedAccount = user.getAccount().acquire()) {
                    // Verify locker size
                    final Locker locker = lockedAccount.get().getLocker();
                    if (locker.getRemaining() < 1) {
                        user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_S_TO_L_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                        return;
                    }
                    try (var locked = user.acquire()) {
                        // Resolve and remove item in inventory
                        final InventoryManager im = user.getInventoryManager();
                        final Inventory inventory = im.getInventoryByType(inventoryType);
                        Item removed = null;
                        final var iter = inventory.getItems().values().iterator();
                        while (iter.hasNext()) {
                            final Item item = iter.next();
                            if (item.getItemSn() == itemSn) {
                                removed = item;
                                iter.remove();
                                break;
                            }
                        }
                        if (removed == null) {
                            user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.MOVE_S_TO_L_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                            log.error("Could not resolve cash item in {} inventory with sn : {}", inventoryType.name(), itemSn);
                            return;
                        }
                        // Move item to locker and update client
                        final CashItemInfo cashItemInfo = CashItemInfo.from(removed, user);
                        locker.addCashItem(cashItemInfo);
                        user.write(CashShopPacket.cashItemResult(CashItemResult.moveStoLDone(cashItemInfo)));
                    }
                }
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private static void handleIncSlotCount(User user, InventoryType inventoryType, int incSlots, int paymentType, int price) {
        try (var locked = user.acquire()) {
            // Check current inventory size
            final InventoryManager im = locked.get().getInventoryManager();
            final Inventory inventory = im.getInventoryByType(inventoryType);
            final int newSize = inventory.getSize() + incSlots;
            if (newSize > GameConstants.INVENTORY_SLOT_MAX) {
                user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_SLOT_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                return;
            }
            try (var lockedAccount = user.getAccount().acquire()) {
                // Check payment type and deduct price
                if (!deductPrice(lockedAccount, PaymentType.getByValue(paymentType), price)) {
                    user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_SLOT_COUNT_FAILED, CashItemFailReason.NO_REMAIN_CASH))); // You don't have enough cash.
                    return;
                }
                // Increase inventory size and update client
                inventory.setSize(newSize);
                user.write(CashShopPacket.queryCashResult(lockedAccount.get()));
                user.write(CashShopPacket.cashItemResult(CashItemResult.incSlotCountDone(inventoryType, newSize)));
            }
        }
    }

    private static void handleIncTrunkCount(User user, int incSlots, int paymentType, int price) {
        try (var lockedAccount = user.getAccount().acquire()) {
            // Check current trunk size
            final Account account = lockedAccount.get();
            final Trunk trunk = account.getTrunk();
            final int newSize = trunk.getSize() + incSlots;
            if (newSize > GameConstants.TRUNK_SLOT_MAX) {
                user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_TRUNK_COUNT_FAILED, CashItemFailReason.UNKNOWN))); // Due to an unknown error%2C\r\nthe request for Cash Shop has failed.
                return;
            }
            // Check payment type and deduct price
            if (!deductPrice(lockedAccount, PaymentType.getByValue(paymentType), price)) {
                user.write(CashShopPacket.cashItemResult(CashItemResult.fail(CashItemResultType.INC_TRUNK_COUNT_FAILED, CashItemFailReason.NO_REMAIN_CASH))); // You don't have enough cash.
                return;
            }
            // Increase trunk size and update client
            trunk.setSize(newSize);
            user.write(CashShopPacket.queryCashResult(account));
            user.write(CashShopPacket.cashItemResult(CashItemResult.incTrunkCountDone(newSize)));
        }
    }

    private static boolean deductPrice(Locked<Account> locked, PaymentType paymentType, int price) {
        final Account account = locked.get();
        switch (paymentType) {
            case NX_CREDIT -> {
                if (account.getNxCredit() < price) {
                    return false;
                }
                account.setNxCredit(account.getNxCredit() - price);
            }
            case NX_PREPAID -> {
                if (account.getNxPrepaid() < price) {
                    return false;

                }
                account.setNxPrepaid(account.getNxCredit() - price);
            }
            case MAPLE_POINT -> {
                if (account.getMaplePoint() < price) {
                    return false;
                }
                account.setMaplePoint(account.getNxCredit() - price);
            }
            case null -> {
                return false;
            }
        }
        return true;
    }
}
