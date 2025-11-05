package kinoko.handler.user.item;

import kinoko.handler.Handler;
import kinoko.packet.user.UserPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemOptionInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.item.*;
import kinoko.world.user.User;

import java.util.*;

public final class UpgradeItemHandler extends ItemHandler {

    @Handler(InHeader.UserUpgradeItemUseRequest)
    public static void handleUserUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean whiteScroll = inPacket.decodeShort() > 1; // bWhiteScroll
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        // Resolve upgrade item
        final InventoryManager im = user.getInventoryManager();
        final Item upgradeItem = im.getInventoryByType(InventoryType.CONSUME).getItem(upgradeItemPosition);
        if (upgradeItem == null) {
            log.error("Received UserUpgradeItemUseRequest with upgrade item position {}", upgradeItemPosition);
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }
        final Optional<ItemInfo> upgradeItemInfoResult = ItemProvider.getItemInfo(upgradeItem.getItemId());
        if (upgradeItemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for upgrade item ID : {}", upgradeItem.getItemId());
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }
        final ItemInfo upgradeItemInfo = upgradeItemInfoResult.get();

        // Resolve equip item
        final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
        final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
        if (equipItem == null) {
            log.error("Received UserUpgradeItemUseRequest with equip item position {}", equipItemPosition);
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }
        final boolean recoverSlotItem = upgradeItemInfo.getInfo(ItemInfoType.recover) != 0;
        final boolean requireUpgradeCount = !recoverSlotItem && !ItemConstants.isUpgradeScrollNoConsumeWhiteScroll(upgradeItem.getItemId());
        final EquipData equipData = equipItem.getEquipData();
        if (equipData == null || (requireUpgradeCount && equipData.getRuc() <= 0) || !ItemConstants.isCorrectUpgradeEquip(upgradeItem.getItemId(), equipItem.getItemId())) {
            log.error("Tried to upgrade equip item {} with upgrade item {}", equipItem.getItemId(), upgradeItem.getItemId());
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }
        final Optional<ItemInfo> equipItemInfoResult = ItemProvider.getItemInfo(equipItem.getItemId());
        if (equipItemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for equip item ID : {}", equipItem.getItemId());
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }
        final ItemInfo equipItemInfo = equipItemInfoResult.get();
        if (recoverSlotItem && equipData.getRuc() + equipData.getCuc() >= equipItemInfo.getInfo(ItemInfoType.tuc) + equipData.getIuc()) {
            // log.error("Tried to use recover slot item {} on item {} in position {}", upgradeItem.getItemId(), equipItem.getItemId(), equipItemPosition);
            // This is not checked by client
            itemUpgradeEffectError(user, enchantSkill);
            return;
        }

        // Consume white scroll and upgrade scroll
        if (whiteScroll && requireUpgradeCount) {
            final Optional<List<InventoryOperation>> removeWhiteScrollResult = im.removeItem(ItemConstants.WHITE_SCROLL, 1);
            if (removeWhiteScrollResult.isEmpty()) {
                log.error("Failed to consume a white scroll while upgrading equip item {}", equipItem.getItemId());
                itemUpgradeEffectError(user, enchantSkill);
                return;
            }
            user.write(WvsContext.inventoryOperation(removeWhiteScrollResult.get(), false));
        }
        final Optional<InventoryOperation> removeUpgradeItemResult = im.removeItem(upgradeItemPosition, upgradeItem, 1);
        if (removeUpgradeItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not remove upgrade item %d in position %d", upgradeItem.getItemId(), upgradeItemPosition));
        }
        user.write(WvsContext.inventoryOperation(removeUpgradeItemResult.get(), false));

        // Upgrade item
        final boolean success = Util.succeedProp(upgradeItemInfo.getInfo(ItemInfoType.success, 100));
        if (success) {
            if (recoverSlotItem) {
                // Clean Slate Scroll
                equipData.setRuc((byte) (equipData.getRuc() + 1));
            } else if (upgradeItemInfo.getInfo(ItemInfoType.preventslip) != 0) {
                // Scroll for Spikes on Shoes
                equipItem.addAttribute(ItemAttribute.EQUIP_PREVENT_SLIP);
                equipData.setCuc((byte) (equipData.getCuc() + 1));
            } else if (upgradeItemInfo.getInfo(ItemInfoType.warmsupport) != 0) {
                // Scroll for Cape for Cold Protection
                equipItem.addAttribute(ItemAttribute.EQUIP_SUPPORT_WARM);
                equipData.setCuc((byte) (equipData.getCuc() + 1));
            } else if (upgradeItemInfo.getInfo(ItemInfoType.randstat) != 0) {
                // Chaos scroll
                final int randMax = upgradeItemInfo.getInfo(ItemInfoType.incRandVol) != 0 ? 10 : 5; // miraculous chaos scroll
                final Map<ItemInfoType, Object> randStats = new EnumMap<>(ItemInfoType.class);
                if (equipData.getIncStr() > 0) {
                    randStats.put(ItemInfoType.incSTR, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncDex() > 0) {
                    randStats.put(ItemInfoType.incDEX, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncInt() > 0) {
                    randStats.put(ItemInfoType.incINT, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncLuk() > 0) {
                    randStats.put(ItemInfoType.incLUK, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncMaxHp() > 0) {
                    randStats.put(ItemInfoType.incMHP, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncMaxMp() > 0) {
                    randStats.put(ItemInfoType.incMMP, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncPad() > 0) {
                    randStats.put(ItemInfoType.incPAD, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncMad() > 0) {
                    randStats.put(ItemInfoType.incMAD, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncPdd() > 0) {
                    randStats.put(ItemInfoType.incPDD, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncMdd() > 0) {
                    randStats.put(ItemInfoType.incMDD, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncAcc() > 0) {
                    randStats.put(ItemInfoType.incACC, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncEva() > 0) {
                    randStats.put(ItemInfoType.incEVA, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncCraft() > 0) {
                    randStats.put(ItemInfoType.incCraft, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncSpeed() > 0) {
                    randStats.put(ItemInfoType.incSpeed, Util.getRandom(-randMax, randMax));
                }
                if (equipData.getIncJump() > 0) {
                    randStats.put(ItemInfoType.incJump, Util.getRandom(-randMax, randMax));
                }
                equipData.applyScrollStats(randStats);
                equipData.setCuc((byte) (equipData.getCuc() + 1));
            } else {
                // Normal scrolls
                equipData.applyScrollStats(upgradeItemInfo.getItemInfos());
                equipData.setCuc((byte) (equipData.getCuc() + 1));
            }
        } else {
            // Check if item should be destroyed
            final int destroyRate = upgradeItemInfo.getInfo(ItemInfoType.cursed, 0);
            if (Util.succeedProp(destroyRate)) {
                final Optional<InventoryOperation> destroyItemResult = im.removeItem(equipItemPosition, equipItem);
                if (destroyItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not destroy equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
                }
                user.write(WvsContext.inventoryOperation(destroyItemResult.get(), true));
                user.getField().broadcastPacket(UserPacket.userItemUpgradeEffect(user, false, true, enchantSkill, whiteScroll && requireUpgradeCount));
                return;
            }
        }

        // Decrement upgrade slot (if applicable) and update client
        if (requireUpgradeCount && (success || !whiteScroll)) {
            equipData.setRuc((byte) (equipData.getRuc() - 1));
        }
        final Optional<InventoryOperation> updateItemResult = im.updateItem(equipItemPosition, equipItem);
        if (updateItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not update equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
        }
        user.write(WvsContext.inventoryOperation(updateItemResult.get(), true));
        user.getField().broadcastPacket(UserPacket.userItemUpgradeEffect(user, success, false, enchantSkill, whiteScroll && requireUpgradeCount));
    }

    @Handler(InHeader.UserHyperUpgradeItemUseRequest)
    public static void handleUserHyperUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        // Resolve upgrade item
        final InventoryManager im = user.getInventoryManager();
        final Item upgradeItem = im.getInventoryByType(InventoryType.CONSUME).getItem(upgradeItemPosition);
        if (upgradeItem == null) {
            log.error("Received UserHyperUpgradeItemUseRequest with upgrade item position {}", upgradeItemPosition);
            user.dispose();
            return;
        }

        // Resolve equip item
        final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
        final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
        if (equipItem == null) {
            log.error("Received UserUpgradeItemUseRequest with equip item position {}", equipItemPosition);
            user.dispose();
            return;
        }
        final Optional<ItemInfo> equipItemInfoResult = ItemProvider.getItemInfo(equipItem.getItemId());
        if (equipItemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for equip item ID : {}", equipItem.getItemId());
            user.dispose();
            return;
        }
        final ItemInfo equipItemInfo = equipItemInfoResult.get();
        final EquipData equipData = equipItem.getEquipData();
        if (equipItemInfo.isCash() || equipItemInfo.getInfo(ItemInfoType.tuc) == 0 || equipData == null || equipData.getRuc() > 0 || equipData.getChuc() >= 15 ||
                !ItemConstants.isHyperUpgradeItem(upgradeItem.getItemId()) || !ItemConstants.isCorrectUpgradeEquip(upgradeItem.getItemId(), equipItem.getItemId())) {
            log.error("Tried to upgrade equip item {} with item {}", equipItem.getItemId(), upgradeItem.getItemId());
            user.dispose();
            return;
        }

        // Consume hyper upgrade scroll
        final Optional<InventoryOperation> removeUpgradeItemResult = im.removeItem(upgradeItemPosition, upgradeItem, 1);
        if (removeUpgradeItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not remove upgrade item %d in position %d", upgradeItem.getItemId(), upgradeItemPosition));
        }
        user.write(WvsContext.inventoryOperation(removeUpgradeItemResult.get(), false));

        // Upgrade item
        final boolean success = Util.succeedProp(ItemConstants.getHyperUpgradeSuccessProp(upgradeItem.getItemId(), equipData.getChuc()));
        if (success) {
            equipData.applyHyperUpgradeStats(equipItemInfo);
            equipData.setChuc((byte) (equipData.getChuc() + 1));
            // Update client
            final Optional<InventoryOperation> updateItemResult = im.updateItem(equipItemPosition, equipItem);
            if (updateItemResult.isEmpty()) {
                throw new IllegalStateException(String.format("Could not update equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
            }
            user.write(WvsContext.inventoryOperation(updateItemResult.get(), true));
            user.getField().broadcastPacket(UserPacket.userItemHyperUpgradeEffect(user, true, false, enchantSkill));
        } else {
            // Destroy item
            final Optional<InventoryOperation> destroyItemResult = im.removeItem(equipItemPosition, equipItem);
            if (destroyItemResult.isEmpty()) {
                throw new IllegalStateException(String.format("Could not destroy equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
            }
            user.write(WvsContext.inventoryOperation(destroyItemResult.get(), true));
            user.getField().broadcastPacket(UserPacket.userItemHyperUpgradeEffect(user, false, true, enchantSkill));
        }
    }

    @Handler(InHeader.UserItemOptionUpgradeItemUseRequest)
    public static void handleUserItemOptionUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        // Resolve upgrade item
        final InventoryManager im = user.getInventoryManager();
        final Item upgradeItem = im.getInventoryByType(InventoryType.CONSUME).getItem(upgradeItemPosition);
        if (upgradeItem == null) {
            log.error("Received UserItemOptionUpgradeItemUseRequest with upgrade item position {}", upgradeItemPosition);
            user.dispose();
            return;
        }

        // Resolve equip item
        final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
        final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
        if (equipItem == null) {
            log.error("Received UserItemOptionUpgradeItemUseRequest with equip item position {}", equipItemPosition);
            user.dispose();
            return;
        }
        final Optional<ItemInfo> equipItemInfoResult = ItemProvider.getItemInfo(equipItem.getItemId());
        if (equipItemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for equip item ID : {}", equipItem.getItemId());
            user.dispose();
            return;
        }
        final ItemInfo equipItemInfo = equipItemInfoResult.get();
        final EquipData equipData = equipItem.getEquipData();
        final Set<BodyPart> bodyParts = BodyPart.getByItemId(equipItem.getItemId());
        if (equipItemInfo.isCash() || equipItemInfo.getInfo(ItemInfoType.tuc) == 0 || equipData == null || equipData.getGrade() != 0 ||
                bodyParts.stream().anyMatch(BodyPart::isDragon) || bodyParts.stream().anyMatch(BodyPart::isMechanic) ||
                !ItemConstants.isItemOptionUpgradeItem(upgradeItem.getItemId()) || !ItemConstants.isCorrectUpgradeEquip(upgradeItem.getItemId(), equipItem.getItemId())) {
            log.error("Tried to upgrade equip item {} with item {}", equipItem.getItemId(), upgradeItem.getItemId());
            user.dispose();
            return;
        }

        // Resolve item options
        final ItemGrade itemGrade = ItemGrade.RARE;
        final List<ItemOptionInfo> primeItemOptions = ItemProvider.getPossibleItemOptions(equipItemInfo, itemGrade);
        final List<ItemOptionInfo> lowerItemOptions = ItemProvider.getPossibleItemOptions(equipItemInfo, itemGrade.getLowerGrade());
        if (primeItemOptions.isEmpty() || lowerItemOptions.isEmpty()) {
            log.error("Could not resolve item options for grade : {}", equipData.getItemGrade());
            user.dispose();
            return;
        }

        // Consume item option upgrade scroll
        final Optional<InventoryOperation> removeUpgradeItemResult = im.removeItem(upgradeItemPosition, upgradeItem, 1);
        if (removeUpgradeItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not remove upgrade item %d in position %d", upgradeItem.getItemId(), upgradeItemPosition));
        }
        user.write(WvsContext.inventoryOperation(removeUpgradeItemResult.get(), false));

        // Upgrade item
        final boolean success = Util.succeedProp(ItemConstants.getItemOptionUpgradeSuccessProp(upgradeItem.getItemId()));
        if (success) {
            final boolean thirdLine = Util.succeedProp(ItemConstants.POTENTIAL_THIRD_LINE_PROP);
            final boolean primeLine2 = Util.succeedProp(ItemConstants.POTENTIAL_PRIME_LINE2_PROP);
            final boolean primeLine3 = Util.succeedProp(ItemConstants.POTENTIAL_PRIME_LINE3_PROP);

            final int option1 = Util.getRandomFromCollection(primeItemOptions).map(ItemOptionInfo::getItemOptionId).orElse(0);
            final int option2 = Util.getRandomFromCollection(primeLine2 ? primeItemOptions : lowerItemOptions).map(ItemOptionInfo::getItemOptionId).orElse(0);
            final int option3 = thirdLine ? Util.getRandomFromCollection(primeLine3 ? primeItemOptions : lowerItemOptions).map(ItemOptionInfo::getItemOptionId).orElse(0) : 0;
            equipData.setOption1((short) option1);
            equipData.setOption2((short) option2);
            equipData.setOption3((short) option3);
            equipData.setGrade((byte) itemGrade.getValue());
            // Update client
            final Optional<InventoryOperation> updateItemResult = im.updateItem(equipItemPosition, equipItem);
            if (updateItemResult.isEmpty()) {
                throw new IllegalStateException(String.format("Could not update equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
            }
            user.write(WvsContext.inventoryOperation(updateItemResult.get(), true));
            user.getField().broadcastPacket(UserPacket.userItemOptionUpgradeEffect(user, true, false, enchantSkill));
        } else {
            // Destroy item
            final Optional<InventoryOperation> destroyItemResult = im.removeItem(equipItemPosition, equipItem);
            if (destroyItemResult.isEmpty()) {
                throw new IllegalStateException(String.format("Could not destroy equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
            }
            user.write(WvsContext.inventoryOperation(destroyItemResult.get(), true));
            user.getField().broadcastPacket(UserPacket.userItemOptionUpgradeEffect(user, false, true, enchantSkill));
        }
    }

    @Handler(InHeader.UserItemReleaseRequest)
    public static void handleUserItemReleaseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS

        // Resolve upgrade item
        final InventoryManager im = user.getInventoryManager();
        final Item upgradeItem = im.getInventoryByType(InventoryType.CONSUME).getItem(upgradeItemPosition);
        if (upgradeItem == null) {
            log.error("Received UserItemReleaseRequest with upgrade item position {}", upgradeItemPosition);
            user.dispose();
            return;
        }

        // Resolve equip item
        final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
        final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
        if (equipItem == null) {
            log.error("Received UserItemReleaseRequest with equip item position {}", equipItemPosition);
            user.dispose();
            return;
        }
        final Optional<ItemInfo> equipItemInfoResult = ItemProvider.getItemInfo(equipItem.getItemId());
        if (equipItemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for equip item ID : {}", equipItem.getItemId());
            user.dispose();
            return;
        }
        final ItemInfo equipItemInfo = equipItemInfoResult.get();
        final EquipData equipData = equipItem.getEquipData();
        if (equipData == null || equipData.getGrade() == 0 || equipData.isReleased() ||
                !ItemConstants.isReleaseItem(upgradeItem.getItemId()) ||
                ItemConstants.getReleaseItemLevelLimit(upgradeItem.getItemId()) < equipItemInfo.getReqLevel()) {
            log.error("Tried to release equip item {} with item {}", equipItem.getItemId(), upgradeItem.getItemId());
            user.dispose();
            return;
        }

        // Consume release item
        final Optional<InventoryOperation> removeUpgradeItemResult = im.removeItem(upgradeItemPosition, upgradeItem, 1);
        if (removeUpgradeItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not remove upgrade item %d in position %d", upgradeItem.getItemId(), upgradeItemPosition));
        }
        user.write(WvsContext.inventoryOperation(removeUpgradeItemResult.get(), false));

        // Release item and update client
        equipData.setGrade((byte) (equipData.getGrade() | ItemGrade.RELEASED.getValue()));
        final Optional<InventoryOperation> updateItemResult = im.updateItem(equipItemPosition, equipItem);
        if (updateItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not update equip item %d in position %d", equipItem.getItemId(), equipItemPosition));
        }
        user.write(WvsContext.inventoryOperation(updateItemResult.get(), true));
        user.write(UserPacket.userItemReleaseEffect(user, equipItemPosition));
    }

    private static void itemUpgradeEffectError(User user, boolean enchantSkill) {
        if (enchantSkill) {
            user.write(UserPacket.userItemUpgradeEffectEnchantError(user));
        } else {
            user.systemMessage("That scroll cannot be used on this item.");
        }
        user.dispose();
    }
}
