package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.MapProvider;
import kinoko.provider.WzProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemOptionInfo;
import kinoko.provider.item.ItemSpecType;
import kinoko.provider.map.FieldOption;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.event.EventScheduler;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.item.*;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ItemHandler {
    private static final Logger log = LogManager.getLogger(ItemHandler.class);

    @Handler(InHeader.UserStatChangeItemUseRequest)
    public static void handleUserStatChangeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Apply stat change
            changeStat(locked, itemInfoResult.get());
        }
    }

    @Handler(InHeader.UserStatChangeItemCancelRequest)
    public static void handleUserStatChangeItemCancelRequest(User user, InPacket inPacket) {
        final int itemId = inPacket.decodeInt(); // sign inverted
        try (var locked = user.acquire()) {
            locked.get().resetTemporaryStat(itemId);
        }
    }

    @Handler(InHeader.UserPetFoodItemUseRequest)
    public static void handleUserPetFoodItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Check item
        if (!ItemConstants.isPetFoodItem(itemId)) {
            log.error("Received UserPetFoodItemUseRequest with an invalid pet food item {}", itemId);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo ii = itemInfoResult.get();
        final int incFullness = ii.getSpec(ItemSpecType.inc);

        try (var locked = user.acquire()) {
            // Select target pet
            Pet target = null;
            for (Pet pet : user.getPets()) {
                if (target == null || target.getFullness() > pet.getFullness()) {
                    target = pet;
                }
            }
            if (target == null) {
                log.error("Could not select target pet for using pet food item : {}", itemId);
                user.dispose();
                return;
            }
            final Optional<Integer> petIndexResult = user.getPetIndex(target.getItemSn());
            if (petIndexResult.isEmpty()) {
                log.error("Could not resolve pet index for item : {}", target.getItemSn());
                user.dispose();
                return;
            }
            final long petSn = target.getItemSn();
            final int petIndex = petIndexResult.get();

            // Resolve pet item
            final InventoryManager im = user.getInventoryManager();
            final Optional<Map.Entry<Integer, Item>> itemEntry = im.getCashInventory().getItems().entrySet().stream()
                    .filter((entry) -> entry.getValue().getItemSn() == petSn)
                    .findFirst();
            if (itemEntry.isEmpty()) {
                log.error("Could not resolve pet item : {}", target.getItemSn());
                user.dispose();
                return;
            }
            final int petPosition = itemEntry.get().getKey();
            final Item petItem = itemEntry.get().getValue();

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Increase fullness
            final PetData petData = petItem.getPetData();
            final int fullness = petData.getFullness();
            final boolean success = fullness < GameConstants.PET_FULLNESS_MAX;
            petData.setFullness((byte) Math.min(fullness + incFullness, GameConstants.PET_FULLNESS_MAX));

            boolean levelUp = false;
            if (fullness <= GameConstants.PET_FULLNESS_FOR_TAMENESS) {
                // Increase tameness (closeness)
                final int newTameness = Math.min(petData.getTameness() + 1, GameConstants.PET_TAMENESS_MAX);
                petData.setTameness((short) newTameness);

                // Level up
                while (petData.getLevel() < GameConstants.PET_LEVEL_MAX &&
                        newTameness > GameConstants.getNextLevelPetCloseness(petData.getLevel())) {
                    petData.setLevel((byte) (petData.getLevel() + 1));
                    levelUp = true;
                }
            } else if (fullness == GameConstants.PET_FULLNESS_MAX) {
                // Decrease tameness (closeness)
                final int newTameness = Math.max(petData.getTameness() - 1, 0);
                petData.setTameness((short) newTameness);
            }

            // Update pet item
            final Optional<InventoryOperation> updateResult = user.getInventoryManager().updateItem(petPosition, petItem);
            if (updateResult.isEmpty()) {
                throw new IllegalStateException("Could not update pet item");
            }

            // Update client
            user.write(WvsContext.inventoryOperation(updateResult.get(), false));
            if (levelUp) {
                user.write(UserLocal.effect(Effect.petLevelUp(petIndex)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.petLevelUp(petIndex)), user);
            }

            // Broadcast pet action
            user.getField().broadcastPacket(PetPacket.petActionFeed(user, petIndex, success, false));
        }
    }

    @Handler(InHeader.UserConsumeCashItemUseRequest)
    public static void handleUserConsumeCashItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nIdemID

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check item
            final InventoryManager im = locked.get().getInventoryManager();
            final Item item = im.getInventoryByType(InventoryType.CASH).getItem(position);
            if (item == null || item.getItemId() != itemId) {
                log.error("Tried to use an item in position {} as item ID : {}", position, itemId);
                user.dispose();
                return;
            }

            final CashItemType cashItemType = CashItemType.getByItemId(item.getItemId());
            switch (cashItemType) {
                case SPEAKERCHANNEL -> {
                    final String message = formatMessage(user, inPacket.decodeString());
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove speaker channel item from inventory");
                        return;
                    }
                    user.write(WvsContext.inventoryOperation(removeResult.get(), true));
                    user.getConnectedServer().submitChannelPacketBroadcast(BroadcastPacket.speakerChannel(message));
                }
                case SPEAKERWORLD, SKULLSPEAKER -> {
                    final String message = formatMessage(user, inPacket.decodeString());
                    final boolean whisperIcon = inPacket.decodeBoolean();
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove speaker world item from inventory");
                        return;
                    }
                    user.write(WvsContext.inventoryOperation(removeResult.get(), true));
                    if (cashItemType == CashItemType.SPEAKERWORLD) {
                        user.getConnectedServer().submitServerPacketBroadcast(BroadcastPacket.speakerWorld(message, user.getChannelId(), whisperIcon));
                    } else {
                        user.getConnectedServer().submitServerPacketBroadcast(BroadcastPacket.skullSpeaker(message, user.getChannelId(), whisperIcon));
                    }
                }
                case ITEMSPEAKER -> {
                    final String message = formatMessage(user, inPacket.decodeString());
                    final boolean whisperIcon = inPacket.decodeBoolean();
                    final Item targetItem;
                    if (inPacket.decodeBoolean()) {
                        final int targetType = inPacket.decodeInt(); // nTargetTI
                        final int targetPosition = inPacket.decodeInt(); // nTargetPOS
                        final InventoryType inventoryType = InventoryType.getByPosition(InventoryType.getByValue(targetType), targetPosition);
                        if (inventoryType == null) {
                            log.error("Received unknown target inventory type {} for item speaker", targetType);
                            return;
                        }
                        targetItem = im.getInventoryByType(inventoryType).getItem(targetPosition);
                    } else {
                        targetItem = null;
                    }
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove item speaker item from inventory");
                        return;
                    }
                    user.write(WvsContext.inventoryOperation(removeResult.get(), true));
                    user.getConnectedServer().submitServerPacketBroadcast(BroadcastPacket.itemSpeaker(message, targetItem, user.getChannelId(), whisperIcon));
                }
                case ARTSPEAKERWORLD -> {
                    final List<String> messages = new ArrayList<>();
                    final int size = inPacket.decodeByte();
                    for (int i = 0; i < size; i++) {
                        messages.add(formatMessage(user, inPacket.decodeString()));
                    }
                    final boolean whisperIcon = inPacket.decodeBoolean();
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove art speaker world item from inventory");
                        return;
                    }
                    user.write(WvsContext.inventoryOperation(removeResult.get(), true));
                    user.getConnectedServer().submitServerPacketBroadcast(BroadcastPacket.artSpeakerWorld(messages, user.getChannelId(), whisperIcon));

                }
                case AVATARMEGAPHONE -> {
                    final String s1 = inPacket.decodeString();
                    final String s2 = inPacket.decodeString();
                    final String s3 = inPacket.decodeString();
                    final String s4 = inPacket.decodeString();
                    final boolean whisperIcon = inPacket.decodeBoolean();
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove avatar megaphone item from inventory");
                        return;
                    }
                    user.write(WvsContext.inventoryOperation(removeResult.get(), true));
                    user.getConnectedServer().submitServerPacketBroadcast(WvsContext.avatarMegaphoneUpdateMessage(user, itemId, s1, s2, s3, s4, whisperIcon));
                    EventScheduler.addEvent(() -> {
                        user.getConnectedServer().submitServerPacketBroadcast(WvsContext.avatarMegaphoneClearMessage());
                    }, 5, TimeUnit.SECONDS); // TODO : scheduling system?
                }
                case ADBOARD -> {
                    final String message = inPacket.decodeString();
                    user.setAdBoard(message);
                    user.getField().broadcastPacket(UserPacket.userAdBoard(user, message));
                }
                case KARMASCISSORS -> {
                    // Resolve target item
                    final int targetType = inPacket.decodeInt(); // nTargetTI
                    final int targetPosition = inPacket.decodeInt(); // nTargetPOS
                    final InventoryType inventoryType = InventoryType.getByPosition(InventoryType.getByValue(targetType), targetPosition);
                    if (inventoryType == null) {
                        log.error("Received unknown target inventory type {} for karma scissors", targetType);
                        return;
                    }
                    final Item targetItem = im.getInventoryByType(inventoryType).getItem(targetPosition);
                    if (targetItem == null) {
                        log.error("Could not resolve item in inventory type {} position {} for karma scissors", inventoryType, targetPosition);
                        return;
                    }
                    // Remove item
                    final Optional<InventoryOperation> removeResult = user.getInventoryManager().removeItem(position, item, 1);
                    if (removeResult.isEmpty()) {
                        log.error("Could not remove karma scissors item from inventory");
                        return;
                    }
                    // Update target item
                    targetItem.setPossibleTrading(true);
                    final Optional<InventoryOperation> updateResult = im.updateItem(targetPosition, targetItem);
                    if (updateResult.isEmpty()) {
                        throw new IllegalStateException("Could not update item");
                    }
                    user.write(WvsContext.inventoryOperation(updateResult.get(), true));
                }
                case ITEMUPGRADE -> {
                    // TODO
                }
                case ITEM_UNRELEASE -> {
                    final int equipItemPosition = inPacket.decodeInt();
                    // Resolve equip item
                    final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
                    final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
                    if (equipItem == null) {
                        log.error("Could not resolve equip item to unrelease in position {}", equipItemPosition);
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
                    if (equipData == null || equipData.getGrade() == 0 || !equipData.isReleased()) {
                        log.error("Tried to unrelease equip item {} in position {}", equipItem.getItemId(), equipItemPosition);
                        user.dispose();
                        return;
                    }
                    // Resolve item options
                    ItemGrade itemGrade = equipData.getItemGrade();
                    if (itemGrade == ItemGrade.RARE && Util.succeedDouble(ItemConstants.POTENTIAL_TIER_UP_EPIC)) {
                        itemGrade = ItemGrade.EPIC;
                    }
                    if (itemGrade == ItemGrade.EPIC && Util.succeedDouble(ItemConstants.POTENTIAL_TIER_UP_UNIQUE)) {
                        itemGrade = ItemGrade.UNIQUE;
                    }
                    final List<ItemOptionInfo> primeItemOptions = ItemProvider.getPossibleItemOptions(equipItemInfo, itemGrade);
                    final List<ItemOptionInfo> lowerItemOptions = ItemProvider.getPossibleItemOptions(equipItemInfo, itemGrade.getLowerGrade());
                    if (primeItemOptions.isEmpty() || lowerItemOptions.isEmpty()) {
                        log.error("Could not resolve item options for grade : {}", equipData.getItemGrade());
                        user.dispose();
                        return;
                    }
                    // Check user can hold cube fragment
                    final Optional<ItemInfo> fragmentItemInfoResult = ItemProvider.getItemInfo(ItemConstants.MIRACLE_CUBE_FRAGMENT);
                    if (fragmentItemInfoResult.isEmpty()) {
                        log.error("Could not resolve cube fragment item : {}", ItemConstants.MIRACLE_CUBE_FRAGMENT);
                        user.dispose();
                        return;
                    }
                    final ItemInfo fragmentItemInfo = fragmentItemInfoResult.get();
                    if (!im.canAddItem(fragmentItemInfo.getItemId(), 1)) {
                        user.write(UserPacket.userItemUnreleaseEffect(user, false)); // Resetting Potential has failed due to insufficient space in the Use item.
                        user.dispose();
                        return;
                    }
                    // Consume item
                    final Optional<InventoryOperation> removeUpgradeItemResult = im.removeItem(position, item, 1);
                    if (removeUpgradeItemResult.isEmpty()) {
                        throw new IllegalStateException(String.format("Could not remove item unrelease item %d in position %d", item.getItemId(), position));
                    }
                    user.write(WvsContext.inventoryOperation(removeUpgradeItemResult.get(), false));
                    // Give cube fragment
                    final Item fragmentItem = fragmentItemInfo.createItem(user.getNextItemSn(), 1);
                    final Optional<List<InventoryOperation>> addItemResult = im.addItem(fragmentItem);
                    if (addItemResult.isEmpty()) {
                        throw new IllegalStateException("Could not add cube fragment item");
                    }
                    user.write(WvsContext.inventoryOperation(addItemResult.get(), false));
                    // Update item
                    final boolean secondLine = equipData.getOption2() != 0;
                    final boolean thirdLine = equipData.getOption3() != 0;
                    final boolean primeLine2 = Util.succeedProp(ItemConstants.POTENTIAL_PRIME_LINE2_PROP);
                    final boolean primeLine3 = Util.succeedProp(ItemConstants.POTENTIAL_PRIME_LINE3_PROP);
                    final int option1 = Util.getRandomFromCollection(primeItemOptions).map(ItemOptionInfo::getItemOptionId).orElse(0);
                    final int option2 = secondLine ? Util.getRandomFromCollection(primeLine2 ? primeItemOptions : lowerItemOptions).map(ItemOptionInfo::getItemOptionId).orElse(0) : 0;
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
                    user.write(UserPacket.userItemUnreleaseEffect(user, true)); // Potential successfully reset. Miracle Cube Fragment obtained!
                }
                case null -> {
                    log.error("Unknown cash item type for item ID : {}", item.getItemId());
                }
                default -> {
                    log.error("Unhandled cash item type for item ID : {}", item.getItemId());
                }
            }
            user.dispose();
        }
    }

    @Handler(InHeader.UserPortalScrollUseRequest)
    public static void handleUserPortalScrollUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt();

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Check portal scroll can be used
            final Field field = locked.get().getField();
            if (field.hasFieldOption(FieldOption.PORTALSCROLLLIMIT)) {
                user.write(MessagePacket.system("You can't use it here in this map."));
                user.dispose();
                return;
            }
            final int moveTo = itemInfoResult.get().getSpec(ItemSpecType.moveTo);
            if (moveTo != GameConstants.UNDEFINED_FIELD_ID && MapProvider.isConnected(field.getFieldId(), moveTo)) {
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }

            // Resolve target field
            final int destinationFieldId = moveTo == GameConstants.UNDEFINED_FIELD_ID ? field.getReturnMap() : moveTo;
            final Optional<Field> destinationFieldResult = user.getConnectedServer().getFieldById(destinationFieldId);
            if (destinationFieldResult.isEmpty()) {
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }
            final Field destinationField = destinationFieldResult.get();
            final Optional<PortalInfo> destinationPortalResult = destinationField.getRandomStartPoint();
            if (destinationPortalResult.isEmpty()) {
                user.write(MessagePacket.system("You cannot go to that place."));
                user.dispose();
                return;
            }

            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Move to field
            user.warp(destinationField, destinationPortalResult.get(), false, false);
        }
    }

    @Handler(InHeader.UserUpgradeItemUseRequest)
    public static void handleUserUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean whiteScroll = inPacket.decodeShort() > 1; // bWhiteScroll
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        try (var locked = user.acquire()) {
            // Resolve upgrade item
            final InventoryManager im = locked.get().getInventoryManager();
            final Item upgradeItem = im.getInventoryByType(InventoryType.CONSUME).getItem(upgradeItemPosition);
            if (upgradeItem == null) {
                log.error("Received UserUpgradeItemUseRequest with upgrade item position {}", upgradeItemPosition);
                user.dispose();
                return;
            }
            final Optional<ItemInfo> upgradeItemInfoResult = ItemProvider.getItemInfo(upgradeItem.getItemId());
            if (upgradeItemInfoResult.isEmpty()) {
                log.error("Could not resolve item info for upgrade item ID : {}", upgradeItem.getItemId());
                user.dispose();
                return;
            }
            final ItemInfo upgradeItemInfo = upgradeItemInfoResult.get();

            // Resolve equip item
            final InventoryType equipInventoryType = InventoryType.getByPosition(InventoryType.EQUIP, equipItemPosition);
            final Item equipItem = im.getInventoryByType(equipInventoryType).getItem(equipItemPosition);
            if (equipItem == null) {
                log.error("Received UserUpgradeItemUseRequest with equip item position {}", equipItemPosition);
                user.dispose();
                return;
            }
            final boolean recoverSlotItem = upgradeItemInfo.getInfo(ItemInfoType.recover) != 0;
            final boolean requireUpgradeCount = !recoverSlotItem && !ItemConstants.isUpgradeScrollNoConsumeWhiteScroll(upgradeItem.getItemId());
            final EquipData equipData = equipItem.getEquipData();
            if (equipData == null || (requireUpgradeCount && equipData.getRuc() <= 0) || !ItemConstants.isCorrectUpgradeEquip(upgradeItem.getItemId(), equipItem.getItemId())) {
                log.error("Tried to upgrade equip item {} with upgrade item {}", equipItem.getItemId(), upgradeItem.getItemId());
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
            if (recoverSlotItem && equipData.getRuc() >= equipItemInfo.getInfo(ItemInfoType.tuc)) {
                log.error("Tried to use recover slot item {} on item {} in position {}", upgradeItem.getItemId(), equipItem.getItemId(), equipItemPosition);
                user.dispose();
                return;
            }

            // Consume white scroll and upgrade scroll
            if (whiteScroll && requireUpgradeCount) {
                final Optional<List<InventoryOperation>> removeWhiteScrollResult = im.removeItem(ItemConstants.WHITE_SCROLL, 1);
                if (removeWhiteScrollResult.isEmpty()) {
                    log.error("Failed to consume a white scroll while upgrading equip item {}", equipItem.getItemId());
                    user.dispose();
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
                    final Map<ItemInfoType, Object> randStats = new HashMap<>();
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
    }

    @Handler(InHeader.UserHyperUpgradeItemUseRequest)
    public static void handleUserHyperUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        try (var locked = user.acquire()) {
            // Resolve upgrade item
            final InventoryManager im = locked.get().getInventoryManager();
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
    }

    @Handler(InHeader.UserItemOptionUpgradeItemUseRequest)
    public static void handleUserItemOptionUpgradeItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS
        final boolean enchantSkill = inPacket.decodeBoolean(); // bEnchantSkill

        try (var locked = user.acquire()) {
            // Resolve upgrade item
            final InventoryManager im = locked.get().getInventoryManager();
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
    }

    @Handler(InHeader.UserItemReleaseRequest)
    public static void handleUserItemReleaseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int upgradeItemPosition = inPacket.decodeShort(); // nUPOS
        final int equipItemPosition = inPacket.decodeShort(); // nEPOS

        try (var locked = user.acquire()) {
            // Resolve upgrade item
            final InventoryManager im = locked.get().getInventoryManager();
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
    }

    @Handler(InHeader.PetStatChangeItemUseRequest)
    public static void handlePetStatChangeItemUseRequest(User user, InPacket inPacket) {
        final long petSn = inPacket.decodeLong();
        inPacket.decodeBoolean(); // bBuffSkill
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nIdemID

        // Resolve pet
        if (user.getPetIndex(petSn).isEmpty()) {
            log.error("Received PetStatChangeItemUseRequest for invalid pet SN : {}", petSn);
            user.dispose();
            return;
        }

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item : {}", itemId);
            user.dispose();
            return;
        }

        try (var locked = user.acquire()) {
            // Consume item
            final Optional<InventoryOperation> consumeItemResult = consumeItem(locked, position, itemId);
            if (consumeItemResult.isEmpty()) {
                user.dispose();
                return;
            }
            user.write(WvsContext.inventoryOperation(consumeItemResult.get(), true));

            // Apply stat change
            changeStat(locked, itemInfoResult.get());
        }
    }

    public static Optional<InventoryOperation> consumeItem(Locked<User> locked, int position, int itemId) {
        final User user = locked.get();
        final InventoryType inventoryType = InventoryType.getByItemId(itemId);
        if (inventoryType != InventoryType.CONSUME) {
            log.error("Tried to use an invalid consume item : {}", itemId);
            return Optional.empty();
        }
        final InventoryManager im = user.getInventoryManager();
        final Item item = im.getInventoryByType(inventoryType).getItem(position);
        if (item == null || item.getItemId() != itemId) {
            log.error("Tried to use an item in position {} as item ID : {}", position, itemId);
            return Optional.empty();
        }
        final Optional<InventoryOperation> removeResult = im.removeItem(position, item, 1);
        if (removeResult.isEmpty()) {
            throw new IllegalStateException("Could not remove item from inventory");
        }
        return removeResult;
    }

    public static void changeStat(Locked<User> locked, ItemInfo ii) {
        // Apply recovery and resolve stat ups
        final User user = locked.get();
        int statUpDuration = 0;
        final Map<CharacterTemporaryStat, Integer> statUps = new HashMap<>(); // cts -> value
        final Set<CharacterTemporaryStat> resetStats = new HashSet<>();
        for (var entry : ii.getItemSpecs().entrySet()) {
            final ItemSpecType specType = entry.getKey();
            switch (specType) {
                // Recovery
                case hp -> {
                    user.addHp(getItemBonusRecovery(user, ii.getSpec(specType)));
                }
                case mp -> {
                    user.addMp(getItemBonusRecovery(user, ii.getSpec(specType)));
                }
                case hpR -> {
                    user.addHp(user.getMaxHp() * ii.getSpec(specType) / 100);
                }
                case mpR -> {
                    user.addMp(user.getMaxMp() * ii.getSpec(specType) / 100);
                }
                // Reset stats
                case curse, darkness, poison, seal, weakness -> {
                    resetStats.add(specType.getStat());
                }
                // Stat ups
                case time -> {
                    statUpDuration = getItemBonusDuration(user, ii.getSpec(specType));
                }
                case defenseAtt -> {
                    statUps.put(CharacterTemporaryStat.DefenseAtt, ii.getSpec(ItemSpecType.prob));
                    statUps.put(CharacterTemporaryStat.DefenseAtt_Elem, (int) WzProvider.getString(entry.getValue()).charAt(0));
                }
                case defenseState -> {
                    statUps.put(CharacterTemporaryStat.DefenseState, ii.getSpec(ItemSpecType.prob));
                    statUps.put(CharacterTemporaryStat.DefenseState_Stat, (int) WzProvider.getString(entry.getValue()).charAt(0)); // C | D | F | S | W
                }
                case respectPimmune, respectMimmune, itemupbyitem, mesoupbyitem -> {
                    statUps.put(specType.getStat(), ii.getSpec(ItemSpecType.prob));
                }
                default -> {
                    final CharacterTemporaryStat cts = specType.getStat();
                    if (cts != null) {
                        statUps.put(cts, ii.getSpec(specType));
                    } else if (!specType.name().endsWith("Rate") && !specType.name().endsWith("Pickup") && specType != ItemSpecType.respectFS) {
                        log.error("Unhandled item spec type : {} for item ID : {}", specType, ii.getItemId());
                    }
                }
            }
        }
        // Apply stat ups
        if (!statUps.isEmpty()) {
            if (statUpDuration <= 0) {
                log.error("Tried to apply stat up with duration {} for item ID : {}", statUpDuration, ii.getItemId());
                return;
            }
            final Map<CharacterTemporaryStat, TemporaryStatOption> setStats = new HashMap<>();
            for (var entry : statUps.entrySet()) {
                setStats.put(entry.getKey(), TemporaryStatOption.of(entry.getValue(), -ii.getItemId(), statUpDuration));
            }
            user.setTemporaryStat(setStats);
        }
        // Reset stats
        if (!resetStats.isEmpty()) {
            user.resetTemporaryStat(resetStats);
        }
    }

    private static int getItemBonusRecovery(User user, int recovery) {
        final int bonusRecoveryRate = user.getSkillStatValue(SkillConstants.getItemBonusRateSkill(user.getJob()), SkillStat.x);
        if (bonusRecoveryRate != 0) {
            return recovery * bonusRecoveryRate / 100;
        }
        return recovery;
    }

    private static int getItemBonusDuration(User user, int duration) {
        final int bonusDurationRate = user.getSkillStatValue(SkillConstants.getItemBonusRateSkill(user.getJob()), SkillStat.x);
        if (bonusDurationRate != 0) {
            return duration * bonusDurationRate / 100;
        }
        return duration;
    }

    private static String formatMessage(User user, String message) {
        return String.format("%s : %s", user.getCharacterName(), message); // TODO : title
    }
}
