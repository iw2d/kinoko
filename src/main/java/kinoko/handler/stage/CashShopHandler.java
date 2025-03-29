package kinoko.handler.stage;

import kinoko.database.CharacterInfo;
import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.world.MemoPacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.server.cashshop.*;
import kinoko.server.header.InHeader;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.user.Account;
import kinoko.world.user.Locker;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CashShopHandler {
    private static final Logger log = LogManager.getLogger(CashShopHandler.class);

    @Handler(InHeader.CashShopQueryCashRequest)
    public static void handleQueryCashRequest(User user, InPacket inPacket) {
        user.write(CashShopPacket.queryCashResult(user.getAccount()));
    }

    @Handler(InHeader.CashShopCashItemRequest)
    public static void handleCashItemRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final CashItemRequestType requestType = CashItemRequestType.getByValue(type);
        switch (requestType) {
            case Buy -> {
                // CCashShop::OnBuy
                // CCashShop::SendBuyAvatarPacket
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN
                // These two encodes are swapped for OnBuy and SendBuyAvatarPacket - ignore
                inPacket.decodeByte(); // bRequestBuyOneADay
                inPacket.decodeInt(); // nEventSN

                // Resolve Commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Buy_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (!commodity.isOnSale()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Buy_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to buy commodity ID : {}, which is not available for purchase", commodity.getCommodityId());
                    return;
                }

                // Check account locker
                final Account account = user.getAccount();
                if (account.getLocker().getRemaining() < 1) {
                    user.write(CashShopPacket.fail(CashItemResultType.Buy_Failed, CashItemFailReason.BuyStoredProcFailed)); // Please check and see if you have exceeded\r\nthe number of cash items you can have.
                    return;
                }

                // Check balance for selected payment type
                if (!checkPrice(account, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.Buy_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }

                // Create CashItemInfo and deduct price
                final Optional<CashItemInfo> cashItemInfoResult = commodity.createCashItemInfo(user);
                if (cashItemInfoResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Buy_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not create cash item info for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final CashItemInfo cashItemInfo = cashItemInfoResult.get();
                if (!deductPrice(account, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                    throw new IllegalStateException("Could not deduct price for cash item");
                }

                // Add to locker and update client
                account.getLocker().addCashItem(cashItemInfo);
                user.write(CashShopPacket.queryCashResult(account));
                user.write(CashShopPacket.buyDone(cashItemInfo));
            }
            case Gift, GiftPackage -> {
                // CCashShop::SendGiftsPacket
                // CCashShop::GiftWishItem
                // CCashShop::OnGiftPackage
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
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.InvalidBirthDate)); // Check your PIC password and\r\nplease try again
                    return;
                }

                // Check gift receiver
                final Optional<CharacterInfo> characterInfoResult = DatabaseManager.characterAccessor().getCharacterInfoByName(receiverName);
                if (characterInfoResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.GiftUnknownRecipient)); // Please confirm whether\r\nthe character's name is correct.
                    return;
                }
                final int receiverAccountId = characterInfoResult.get().getAccountId();
                final int receiverCharacterId = characterInfoResult.get().getCharacterId();
                if (receiverAccountId == user.getAccountId()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.GiftSameAccount)); // You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
                    return;
                }

                // Resolve Commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (!commodity.isOnSale()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to gift commodity ID : {}, which is not available for purchase", commodity.getCommodityId());
                    return;
                }

                // Check account has enough nx prepaid (gifts are prepaid only)
                final Account account = user.getAccount();
                if (!checkPrice(account, PaymentType.NX_PREPAID, commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }

                // Create Gift, Record in DB and deduct price
                final Optional<Gift> giftResult = commodity.createGift(user, giftMessage);
                if (giftResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not create gift for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final Gift gift = giftResult.get();
                if (!DatabaseManager.giftAccessor().newGift(gift, receiverCharacterId)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                if (!deductPrice(account, PaymentType.NX_PREPAID, commodity.getPrice())) {
                    throw new IllegalStateException("Could not deduct price for gift item");
                }

                // Update client
                if (requestType == CashItemRequestType.GiftPackage) {
                    user.write(CashShopPacket.giftPackageDone(receiverName, commodity));
                } else {
                    user.write(CashShopPacket.giftDone(receiverName, commodity));
                }
                user.write(CashShopPacket.queryCashResult(account)); // the order is important here

                // Send memo
                if (!sendGiftMemo(user, receiverCharacterId)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                }
            }
            case SetWish -> {
                // CCashShop::OnSetWish
                // CCashShop::OnRemoveWish
                final List<Integer> wishlist = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    wishlist.add(inPacket.decodeInt()); // nCommSN
                }
                final Account account = user.getAccount();
                account.setWishlist(Collections.unmodifiableList(wishlist));
                user.write(CashShopPacket.setWishDone(account.getWishlist()));
            }
            case IncSlotCount, IncTrunkCount -> {
                // CCashShop::OnExItemSlot - (0x6) : byte, int (dwOption), byte (0), byte (nTI)
                // CCashShop::OnIncTrunkCount - (0x7) : byte, int (dwOption), byte (0)
                // CCashShop::OnBuySlotInc - (0x6 + (is_trunkcount_inc_item)) : byte, int (dwOption), byte (1), int (nCommSN)
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final boolean isAdd4Slots = inPacket.decodeByte() == 0;

                // Add by UI (4 slots) or by commodity (CCashShop::OnBuySlotInc)
                if (isAdd4Slots) {
                    if (requestType == CashItemRequestType.IncSlotCount) {
                        // Check inventory type
                        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI
                        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                            user.write(CashShopPacket.fail(CashItemResultType.IncSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
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
                        user.write(CashShopPacket.fail(CashItemResultType.IncSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
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
                            user.write(CashShopPacket.fail(CashItemResultType.IncSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                            log.error("Tried to increase slot count with item ID : {}", commodity.getItemId());
                        }
                    }
                }
            }
            case IncCharSlotCount -> {
                // CCashShop::OnIncCharacterSlotCount
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN

                // Resolve and verify commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.IncCharSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (commodity.getItemId() != CashShop.ADD_CHAR_SLOTS) {
                    user.write(CashShopPacket.fail(CashItemResultType.IncCharSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to increase char slot count with item ID : {}", commodity.getItemId());
                    return;
                }
                // Check current character slot count
                final Account account = user.getAccount();
                final int newSize = account.getSlotCount() + 1;
                if (newSize > GameConstants.CHARACTER_SLOT_MAX) {
                    user.write(CashShopPacket.fail(CashItemResultType.IncCharSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to increase char slot count over maximum number");
                    return;
                }
                // Check payment type and deduct price
                if (!deductPrice(account, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.IncCharSlotCount_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }
                // Increase slot count and update client
                account.setSlotCount(newSize);
                user.write(CashShopPacket.queryCashResult(account));
                user.write(CashShopPacket.incCharSlotCountDone(newSize));
            }
            case EnableEquipSlotExt -> {
                // CCashShop::OnEnableEquipSlotExt
                final boolean isMaplePoint = inPacket.decodeBoolean(); // nx credit or maple point only
                final int commodityId = inPacket.decodeInt(); // nCommSN

                // Resolve and verify commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.EnableEquipSlotExt_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (commodity.getItemId() != CashShop.EQUIP_SLOT_EXT_30_DAYS && commodity.getItemId() != CashShop.EQUIP_SLOT_EXT_7_days) {
                    user.write(CashShopPacket.fail(CashItemResultType.EnableEquipSlotExt_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to enable equip slot ext with item ID : {}", commodity.getItemId());
                }

                // Deduct price
                if (!deductPrice(user.getAccount(), isMaplePoint ? PaymentType.MAPLE_POINT : PaymentType.NX_CREDIT, commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.EnableEquipSlotExt_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }

                // Extend ext slot expiry and update client
                final InventoryManager im = user.getInventoryManager();
                final int addDays = commodity.getItemId() == CashShop.EQUIP_SLOT_EXT_30_DAYS ? 30 : 7;
                if (im.getExtSlotExpire().isBefore(Instant.now())) {
                    im.setExtSlotExpire(Instant.now().plus(addDays, ChronoUnit.DAYS));
                } else {
                    im.setExtSlotExpire(im.getExtSlotExpire().plus(addDays, ChronoUnit.DAYS));
                }
                user.write(CashShopPacket.enableEquipSlotExtDone(addDays));
            }
            case MoveLtoS -> {
                // CCashShop::OnMoveCashItemLtoS
                final long itemSn = inPacket.decodeLong(); // liSN
                final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI
                final int position = inPacket.decodeShort(); // nPOS

                // Verify inventory type
                if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                    user.write(CashShopPacket.fail(CashItemResultType.MoveLtoS_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                // Verify inventory position
                final InventoryManager im = user.getInventoryManager();
                final Inventory inventory = im.getInventoryByType(inventoryType);
                if (inventory.getItem(position) != null) {
                    user.write(CashShopPacket.fail(CashItemResultType.MoveLtoS_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to move locker item to {} inventory position {} which was not empty", inventoryType.name(), position);
                    return;
                }
                // Resolve and remove item in locker
                final Locker locker = user.getAccount().getLocker();
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
                    user.write(CashShopPacket.fail(CashItemResultType.MoveLtoS_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve cash item in locker with sn : {}", itemSn);
                    return;
                }
                // Move item to specified position and update client
                inventory.putItem(position, removed.getItem());
                user.write(CashShopPacket.moveLtoSDone(position, removed.getItem()));
            }
            case MoveStoL -> {
                // CCashShop::OnMoveCashItemStoL
                final long itemSn = inPacket.decodeLong(); // liSN
                final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nTI

                // Verify inventory type
                if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
                    user.write(CashShopPacket.fail(CashItemResultType.MoveStoL_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                // Verify locker size
                final Locker locker = user.getAccount().getLocker();
                if (locker.getRemaining() < 1) {
                    user.write(CashShopPacket.fail(CashItemResultType.MoveStoL_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
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
                    user.write(CashShopPacket.fail(CashItemResultType.MoveStoL_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve cash item in {} inventory with sn : {}", inventoryType.name(), itemSn);
                    return;
                }
                // Move item to locker and update client
                final CashItemInfo cashItemInfo = CashItemInfo.from(removed, user);
                locker.addCashItem(cashItemInfo);
                user.write(CashShopPacket.moveStoLDone(cashItemInfo));
            }
            case BuyPackage -> {
                // CCashShop::OnBuyPackage
                inPacket.decodeByte(); // dwOption == 2
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN

                // Resolve package commodity and create CashItemInfos
                final Optional<Tuple<Commodity, List<Commodity>>> cashPackageResult = CashShop.getCashPackage(commodityId);
                if (cashPackageResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyPackage_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve cash package commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity packageCommodity = cashPackageResult.get().getLeft();
                if (!packageCommodity.isOnSale()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyPackage_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to buy package commodity ID : {}, which is not available for purchase", packageCommodity.getCommodityId());
                    return;
                }
                final List<Commodity> packageContents = cashPackageResult.get().getRight();

                // Check account locker
                final Account account = user.getAccount();
                if (account.getLocker().getRemaining() < packageContents.size()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyPackage_Failed, CashItemFailReason.BuyStoredProcFailed)); // Please check and see if you have exceeded\r\nthe number of cash items you can have.
                    return;
                }

                // Check balance for selected payment type
                if (!checkPrice(account, PaymentType.getByValue(paymentType), packageCommodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyPackage_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }

                // Create items and deduct price
                final List<CashItemInfo> packageCashItemInfos = new ArrayList<>();
                for (Commodity commodity : packageContents) {
                    final Optional<CashItemInfo> cashItemInfoResult = commodity.createCashItemInfo(user);
                    if (cashItemInfoResult.isEmpty()) {
                        user.write(CashShopPacket.fail(CashItemResultType.BuyPackage_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                        log.error("Could not create cash item info for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                        return;
                    }
                    packageCashItemInfos.add(cashItemInfoResult.get());
                }
                if (!deductPrice(account, PaymentType.getByValue(paymentType), packageCommodity.getPrice())) {
                    throw new IllegalStateException("Could not deduct price for cash package item");
                }

                // Add to locker and update client
                for (CashItemInfo cashItemInfo : packageCashItemInfos) {
                    account.getLocker().addCashItem(cashItemInfo);
                }
                user.write(CashShopPacket.queryCashResult(account));
                user.write(CashShopPacket.buyPackageDone(packageCashItemInfos));
            }
            case BuyNormal -> {
                // CCashShop::OnBuyNormal
                final int commodityId = inPacket.decodeInt(); // nCommoditySn
                if (commodityId / 10000000 != 8) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to buy commodity ID {} with mesos", commodityId);
                    return;
                }

                // Resolve Commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (!commodity.isOnSale()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to buy commodity ID : {}, which is not available for purchase", commodity.getCommodityId());
                    return;
                }

                // Check if user has enough money
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddMoney(-commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.NoRemainMeso)); // You do not have enough mesos.
                    return;
                }

                // Check if user can add item to inventory
                if (!im.canAddItem(commodity.getItemId(), 1)) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.NoEmptyPos)); // Please check if your inventory is full or not.
                    return;
                }

                // Create item
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(commodity.getItemId());
                if (itemInfoResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.BuyNormal_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                final Item boughtItem = itemInfoResult.get().createItem(user.getNextItemSn(), 1);

                // Deduct money and add item to inventory
                if (!im.addMoney(-commodity.getPrice())) {
                    throw new IllegalStateException("Could not deduct price from user");
                }
                final Optional<List<InventoryOperation>> addResult = im.addItem(boughtItem);
                if (addResult.isEmpty()) {
                    throw new IllegalStateException("Could not add bought item to inventory");
                }

                // Update client
                final List<Tuple<Integer, Integer>> updates = addResult.get().stream()
                        .map((op) -> Tuple.of(op.getPosition(), commodity.getItemId()))
                        .toList();
                user.write(WvsContext.inventoryOperation(addResult.get(), false));
                user.write(CashShopPacket.buyNormalDone(updates));
            }
            case Couple, Friendship -> {
                // CCashShop::OnBuyCouple
                // CCashShop::OnBuyFriendship
                final String secondaryPassword = inPacket.decodeString();
                final int paymentType = inPacket.decodeInt(); // dwOption
                final int commodityId = inPacket.decodeInt(); // nCommSN
                final String receiverName = inPacket.decodeString();
                final String giftMessage = inPacket.decodeString();

                // Check secondary password
                if (!DatabaseManager.accountAccessor().checkPassword(user.getAccount(), secondaryPassword, true)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.InvalidBirthDate)); // Check your PIC password and\r\nplease try again
                    return;
                }

                // Check gift receiver
                final Optional<CharacterInfo> characterInfoResult = DatabaseManager.characterAccessor().getCharacterInfoByName(receiverName);
                if (characterInfoResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.GiftUnknownRecipient)); // Please confirm whether\r\nthe character's name is correct.
                    return;
                }
                final int receiverAccountId = characterInfoResult.get().getAccountId();
                final int receiverCharacterId = characterInfoResult.get().getCharacterId();
                final String receiverCharacterName = characterInfoResult.get().getCharacterName();
                if (receiverAccountId == user.getAccountId()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.GiftSameAccount)); // You cannot send a gift to your own account.\r\nPlease purchase it after logging\r\nin with the related character.
                    return;
                }

                // Resolve Commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(commodityId);
                if (commodityResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not resolve commodity with ID : {}", commodityId);
                    return;
                }
                final Commodity commodity = commodityResult.get();
                if (!commodity.isOnSale()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Tried to gift commodity ID : {}, which is not available for purchase", commodity.getCommodityId());
                    return;
                }

                // Check account locker
                final Account account = user.getAccount();
                if (account.getLocker().getRemaining() < 1) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.BuyStoredProcFailed)); // Please check and see if you have exceeded\r\nthe number of cash items you can have.
                    return;
                }

                // Check balance for selected payment type
                if (!checkPrice(account, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
                    return;
                }

                // Generate item SN for both rings
                final long selfItemSn = user.getNextItemSn();
                final long pairItemSn = user.getNextItemSn();

                // Create CashItemInfo and set RingData
                final Optional<CashItemInfo> cashItemInfoResult = commodity.createCashItemInfo(selfItemSn, user.getAccountId(), user.getCharacterId(), "");
                if (cashItemInfoResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not create cash item info for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final CashItemInfo cashItemInfo = cashItemInfoResult.get();
                final RingData selfRingData = new RingData();
                selfRingData.setPairCharacterId(receiverCharacterId);
                selfRingData.setPairCharacterName(receiverCharacterName);
                selfRingData.setPairItemSn(pairItemSn);
                cashItemInfo.getItem().setRingData(selfRingData);

                // Create Gift with pairItemSn
                final Optional<Gift> giftResult = commodity.createGift(pairItemSn, user.getCharacterId(), user.getCharacterName(), giftMessage, selfItemSn);
                if (giftResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    log.error("Could not create gift for commodity ID : {}, item ID : {}", commodityId, commodity.getItemId());
                    return;
                }
                final Gift gift = giftResult.get();

                // Record gift in DB and deduct price
                if (!DatabaseManager.giftAccessor().newGift(gift, receiverCharacterId)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                if (!deductPrice(account, PaymentType.getByValue(paymentType), commodity.getPrice())) {
                    throw new IllegalStateException("Could not deduct price for couple item");
                }

                // Add to locker and update client
                account.getLocker().addCashItem(cashItemInfo);
                user.write(CashShopPacket.queryCashResult(account));
                user.write(CashShopPacket.coupleDone(cashItemInfo, receiverCharacterName, commodity.getItemId()));

                // Send memo
                if (!sendGiftMemo(user, receiverCharacterId)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Couple_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                }
            }
            case PurchaseRecord -> {
                // CCashShop::RequestCashPurchaseRecord
                final int commodityId = inPacket.decodeInt(); // nCommoditySn
                user.write(CashShopPacket.purchaseRecord(commodityId, false));
            }
            case null -> {
                log.error("Unknown cash item request type : {}", type);
            }
            default -> {
                log.error("Unhandled cash item request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.CashItemGachaponRequest)
    public static void handleCashItemGachaponRequest(User user, InPacket inPacket) {
        // CUICashItemGachapon::OnButtonClicked
        final long itemSn = inPacket.decodeLong();
        // Check account locker
        final Account account = user.getAccount();
        final Locker locker = account.getLocker();
        if (locker.getRemaining() < 1) {
            user.write(CashShopPacket.cashItemGachaponResultFailed());
            return;
        }
        // Create CashItemInfo
        final Optional<Commodity> commodityResult = CashShop.getCommodity(10000000); // TODO : pick from random
        if (commodityResult.isEmpty()) {
            user.write(CashShopPacket.cashItemGachaponResultFailed());
            return;
        }
        final Optional<CashItemInfo> cashItemInfoResult = commodityResult.get().createCashItemInfo(user);
        if (cashItemInfoResult.isEmpty()) {
            user.write(CashShopPacket.cashItemGachaponResultFailed());
            return;
        }
        final CashItemInfo added = cashItemInfoResult.get();
        // Consume surprise box
        final var iter = locker.getCashItems().iterator();
        CashItemInfo removed = null;
        while (iter.hasNext()) {
            final CashItemInfo cii = iter.next();
            final Item item = cii.getItem();
            if (item.getItemSn() == itemSn && item.getItemId() == 5222000) { // Premium Cash Shop Surprise
                removed = cii;
                item.setQuantity((short) (item.getQuantity() - 1));
                if (item.getQuantity() <= 0) {
                    iter.remove();
                }
                break;
            }
        }
        if (removed == null) {
            user.write(CashShopPacket.cashItemGachaponResultFailed());
            log.error("Could not resolve cash item gachapon item in locker with sn : {}", itemSn);
            return;
        }
        // Add CashItemInfo to locker
        locker.addCashItem(added);
        user.write(CashShopPacket.cashItemGachaponResultSuccess(itemSn, removed.getItem().getQuantity(), added));
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private static void handleIncSlotCount(User user, InventoryType inventoryType, int incSlots, int paymentType, int price) {
        // Check current inventory size
        final InventoryManager im = user.getInventoryManager();
        final Inventory inventory = im.getInventoryByType(inventoryType);
        final int newSize = inventory.getSize() + incSlots;
        if (newSize > GameConstants.INVENTORY_SLOT_MAX) {
            user.write(CashShopPacket.fail(CashItemResultType.IncSlotCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
            return;
        }
        final Account account = user.getAccount();
        // Check payment type and deduct price
        if (!deductPrice(account, PaymentType.getByValue(paymentType), price)) {
            user.write(CashShopPacket.fail(CashItemResultType.IncSlotCount_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
            return;
        }
        // Increase inventory size and update client
        inventory.setSize(newSize);
        user.write(CashShopPacket.queryCashResult(account));
        user.write(CashShopPacket.incSlotCountDone(inventoryType, newSize));
    }

    private static void handleIncTrunkCount(User user, int incSlots, int paymentType, int price) {
        // Check current trunk size
        final Account account = user.getAccount();
        final Trunk trunk = account.getTrunk();
        final int newSize = trunk.getSize() + incSlots;
        if (newSize > GameConstants.TRUNK_SLOT_MAX) {
            user.write(CashShopPacket.fail(CashItemResultType.IncTrunkCount_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
            return;
        }
        // Check payment type and deduct price
        if (!deductPrice(account, PaymentType.getByValue(paymentType), price)) {
            user.write(CashShopPacket.fail(CashItemResultType.IncTrunkCount_Failed, CashItemFailReason.NoRemainCash)); // You don't have enough cash.
            return;
        }
        // Increase trunk size and update client
        trunk.setSize(newSize);
        user.write(CashShopPacket.queryCashResult(account));
        user.write(CashShopPacket.incTrunkCountDone(newSize));
    }

    private static boolean checkPrice(Account account, PaymentType paymentType, int price) {
        switch (paymentType) {
            case NX_CREDIT -> {
                return account.getNxCredit() >= price;
            }
            case NX_PREPAID -> {
                return account.getNxPrepaid() >= price;
            }
            case MAPLE_POINT -> {
                return account.getMaplePoint() >= price;
            }
            case null, default -> {
                return false;
            }
        }
    }

    private static boolean deductPrice(Account account, PaymentType paymentType, int price) {
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
                account.setNxPrepaid(account.getNxPrepaid() - price);
            }
            case MAPLE_POINT -> {
                if (account.getMaplePoint() < price) {
                    return false;
                }
                account.setMaplePoint(account.getMaplePoint() - price);
            }
            case null -> {
                return false;
            }
        }
        return true;
    }

    private static boolean sendGiftMemo(User user, int receiverCharacterId) {
        // Create memo
        final Optional<Integer> memoIdResult = DatabaseManager.idAccessor().nextMemoId();
        if (memoIdResult.isEmpty()) {
            return false;
        }
        final Memo memo = new Memo(
                MemoType.DEFAULT,
                memoIdResult.get(),
                user.getCharacterName(),
                user.getCharacterName() + " has sent you a gift! Go check out the Cash Shop.",
                Instant.now()
        );
        // Save memo and update client
        if (!DatabaseManager.memoAccessor().newMemo(memo, receiverCharacterId)) {
            return false;
        }
        // Notify memo recipient
        user.getConnectedServer().submitUserPacketReceive(receiverCharacterId, MemoPacket.receive());
        return true;
    }
}
