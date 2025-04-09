package kinoko.handler.user.item;

import kinoko.handler.Handler;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.field.MapleTvPacket;
import kinoko.packet.field.TrunkPacket;
import kinoko.packet.user.PetPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.MapTransferPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.StringProvider;
import kinoko.provider.item.*;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.npc.NpcTemplate;
import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.trunk.TrunkDialog;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.field.Field;
import kinoko.world.field.MapleTvMessage;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.item.*;
import kinoko.world.user.AvatarLook;
import kinoko.world.user.Pet;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.stat.StatConstants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class CashItemHandler extends ItemHandler {

    @Handler(InHeader.UserConsumeCashItemUseRequest)
    public static void handleUserConsumeCashItemUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int position = inPacket.decodeShort(); // nPOS
        final int itemId = inPacket.decodeInt(); // nItemID

        // Resolve item
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            user.dispose();
            return;
        }
        final ItemInfo itemInfo = itemInfoResult.get();

        // Check item
        final InventoryManager im = user.getInventoryManager();
        final Item item = im.getInventoryByType(InventoryType.CASH).getItem(position);
        if (item == null || item.getItemId() != itemId) {
            log.error("Tried to use an item in position {} as item ID : {}", position, itemId);
            user.dispose();
            return;
        }

        final CashItemType cashItemType = CashItemType.getByItemId(item.getItemId());
        switch (cashItemType) {
            case SPEAKERCHANNEL -> {
                final String message = formatSpeakerMessage(user, inPacket.decodeString());
                // Check level
                if (user.getLevel() < 10) {
                    user.write(WvsContext.avatarMegaphoneResLevelLimit()); // This megaphone is only available for characters that are over Level 10.
                    return;
                }
                // Remove item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    log.error("Could not remove speaker channel item from inventory");
                    user.dispose();
                    return;
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                // Channel broadcast
                user.getConnectedServer().submitChannelPacketBroadcast(BroadcastPacket.speakerChannel(message));
            }
            case SPEAKERWORLD, SKULLSPEAKER -> {
                final String message = formatSpeakerMessage(user, inPacket.decodeString());
                final boolean whisperIcon = inPacket.decodeBoolean();
                final OutPacket outPacket = cashItemType == CashItemType.SPEAKERWORLD ?
                        BroadcastPacket.speakerWorld(message, user.getChannelId(), whisperIcon) :
                        BroadcastPacket.skullSpeaker(message, user.getChannelId(), whisperIcon);
                handleWorldSpeaker(user, position, item, false, outPacket);
            }
            case ITEMSPEAKER -> {
                final String message = formatSpeakerMessage(user, inPacket.decodeString());
                final boolean whisperIcon = inPacket.decodeBoolean();
                final Item targetItem;
                if (inPacket.decodeBoolean()) {
                    final int targetType = inPacket.decodeInt(); // nTargetTI
                    final int targetPosition = inPacket.decodeInt(); // nTargetPOS
                    final InventoryType inventoryType = InventoryType.getByPosition(InventoryType.getByValue(targetType), targetPosition);
                    if (inventoryType == null) {
                        log.error("Received unknown target inventory type {} for item speaker", targetType);
                        user.dispose();
                        return;
                    }
                    targetItem = im.getInventoryByType(inventoryType).getItem(targetPosition);
                } else {
                    targetItem = null;
                }
                handleWorldSpeaker(user, position, item, false, BroadcastPacket.itemSpeaker(message, targetItem, user.getChannelId(), whisperIcon));
            }
            case ARTSPEAKERWORLD -> {
                final List<String> messages = new ArrayList<>();
                final int size = inPacket.decodeByte();
                for (int i = 0; i < size; i++) {
                    messages.add(formatSpeakerMessage(user, inPacket.decodeString()));
                }
                final boolean whisperIcon = inPacket.decodeBoolean();
                handleWorldSpeaker(user, position, item, false, BroadcastPacket.artSpeakerWorld(messages, user.getChannelId(), whisperIcon));
            }
            case AVATARMEGAPHONE -> {
                final String s1 = inPacket.decodeString();
                final String s2 = inPacket.decodeString();
                final String s3 = inPacket.decodeString();
                final String s4 = inPacket.decodeString();
                final boolean whisperIcon = inPacket.decodeBoolean();
                handleWorldSpeaker(user, position, item, true, WvsContext.avatarMegaphoneUpdateMessage(user, itemId, s1, s2, s3, s4, whisperIcon));
            }
            case MAPLETV, MAPLESOLETV, MAPLELOVETV, MEGATV, MEGASOLETV, MEGALOVETV -> {
                final boolean isMega = cashItemType == CashItemType.MEGATV || cashItemType == CashItemType.MEGASOLETV || cashItemType == CashItemType.MEGALOVETV;
                final int type = switch (cashItemType) {
                    case MAPLESOLETV, MEGASOLETV -> 1;
                    case MAPLELOVETV, MEGALOVETV -> 2;
                    default -> 0;
                };
                final int flag = type == 0 ? inPacket.decodeByte() : (type == 1 ? 1 : 3);
                final boolean whisperIcon = isMega && inPacket.decodeBoolean();
                String receiverName = null;
                AvatarLook receiver = null;
                if ((flag & 2) != 0) {
                    receiverName = inPacket.decodeString();
                    final Optional<User> receiverResult = user.getField().getUserPool().getByCharacterName(receiverName);
                    if (receiverResult.isEmpty()) {
                        user.write(WvsContext.mapleTvUseRes("Unable to find the character."));
                        return;
                    }
                    receiver = receiverResult.get().getCharacterData().getAvatarLook();
                }
                final String s1 = inPacket.decodeString();
                final String s2 = inPacket.decodeString();
                final String s3 = inPacket.decodeString();
                final String s4 = inPacket.decodeString();
                final String s5 = inPacket.decodeString();
                // Check maple tv queue
                final Instant expireTime;
                final int duration = type == 0 ? 15 : (type == 1 ? 30 : 60);
                if (user.getField().getMapleTvQueue().isEmpty()) {
                    expireTime = Instant.now().plus(duration, ChronoUnit.SECONDS);
                } else {
                    final Instant lastExpire = user.getField().getMapleTvQueue().getLast().getExpireTime();
                    if (lastExpire.isAfter(Instant.now().plus(60, ChronoUnit.SECONDS))) {
                        user.write(WvsContext.mapleTvUseRes("The waiting line is longer than a minute. Please try using it at a later time."));
                        return;
                    }
                    expireTime = lastExpire.plus(duration, ChronoUnit.SECONDS);
                }
                // Remove item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    log.error("Could not remove maple tv item from inventory");
                    user.dispose();
                    return;
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                // Show message
                final MapleTvMessage message = new MapleTvMessage(
                        flag,
                        type,
                        user.getCharacterData().getAvatarLook(),
                        user.getCharacterName(),
                        receiver,
                        receiverName,
                        s1,
                        s2,
                        s3,
                        s4,
                        s5,
                        expireTime
                );
                final int totalWaitTime = (int) Math.max(expireTime.getEpochSecond() - Instant.now().getEpochSecond(), 0);
                if (user.getField().getMapleTvQueue().isEmpty()) {
                    user.getField().broadcastPacket(MapleTvPacket.updateMessage(message, totalWaitTime));
                }
                user.getField().getMapleTvQueue().addLast(message);
                if (isMega) {
                    user.getConnectedServer().submitWorldSpeakerRequest(user.getCharacterId(), false, BroadcastPacket.speakerWorld(formatSpeakerMessage(user, String.join("", s1, s2, s3, s4, s5)), user.getChannelId(), whisperIcon));
                }
            }
            case ADBOARD -> {
                final String message = inPacket.decodeString();
                user.setAdBoard(message);
                user.getField().broadcastPacket(UserPacket.userAdBoard(user, message));
                user.dispose();
            }
            case CONSUMEEFFECTITEM -> {
                // Remove item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    log.error("Could not remove consume effect item from inventory");
                    user.dispose();
                    return;
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                // Show effect
                user.write(UserLocal.effect(Effect.consumeEffect(item.getItemId())));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.consumeEffect(item.getItemId())), user);
                // Create affected area
                final Instant expireTime = Instant.now().plus(itemInfo.getInfo(ItemInfoType.time, 60), ChronoUnit.SECONDS);
                user.getField().getAffectedAreaPool().addAffectedArea(AffectedArea.buff(user, item.getItemId(), itemInfo.getRect(), expireTime));
            }
            case KARMASCISSORS -> {
                // Resolve target item
                final int targetType = inPacket.decodeInt(); // nTargetTI
                final int targetPosition = inPacket.decodeInt(); // nTargetPOS
                final InventoryType inventoryType = InventoryType.getByPosition(InventoryType.getByValue(targetType), targetPosition);
                if (inventoryType == null) {
                    log.error("Received unknown target inventory type {} for karma scissors", targetType);
                    user.dispose();
                    return;
                }
                final Item targetItem = im.getInventoryByType(inventoryType).getItem(targetPosition);
                if (targetItem == null) {
                    log.error("Could not resolve item in inventory type {} position {} for karma scissors", inventoryType, targetPosition);
                    user.dispose();
                    return;
                }
                // Remove item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    log.error("Could not remove karma scissors item from inventory");
                    user.dispose();
                    return;
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Update target item
                targetItem.setPossibleTrading(true);
                final Optional<InventoryOperation> updateResult = im.updateItem(targetPosition, targetItem);
                if (updateResult.isEmpty()) {
                    throw new IllegalStateException("Could not update item");
                }
                user.write(WvsContext.inventoryOperation(updateResult.get(), true));
            }
            case ITEMUPGRADE -> {
                final int targetType = inPacket.decodeInt(); // nItemTI
                final int targetPosition = inPacket.decodeInt(); // nSlotPosition
                inPacket.decodeInt(); // update_time (again)
                // Resolve equip item
                final InventoryType inventoryType = InventoryType.getByPosition(InventoryType.getByValue(targetType), targetPosition);
                if (inventoryType == null) {
                    log.error("Received unknown target inventory type {} for vicious' hammer", targetType);
                    user.write(FieldPacket.itemUpgradeResultErr(0)); // Unknown error
                    return;
                }
                final Item targetItem = im.getInventoryByType(inventoryType).getItem(targetPosition);
                if (targetItem == null) {
                    log.error("Could not resolve item in inventory type {} position {} for vicious' hammer", inventoryType, targetPosition);
                    user.write(FieldPacket.itemUpgradeResultErr(0)); // Unknown error
                    return;
                }
                final Optional<ItemInfo> targetItemInfoResult = ItemProvider.getItemInfo(targetItem.getItemId());
                if (targetItemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for target item ID : {}", targetItem.getItemId());
                    user.write(FieldPacket.itemUpgradeResultErr(0)); // Unknown error
                    return;
                }
                final ItemInfo equipItemInfo = targetItemInfoResult.get();
                final EquipData equipData = targetItem.getEquipData();
                // Check target item
                if (equipData == null || equipItemInfo.getInfo(ItemInfoType.tuc) == 0) {
                    log.error("Tried to vicious' hammer item {} in position {}", targetItem.getItemId(), targetPosition);
                    user.write(FieldPacket.itemUpgradeResultErr(1)); // The item is not upgradable.
                    return;
                }
                if (equipData.getIuc() >= equipItemInfo.getInfo(ItemInfoType.IUCMax, 2)) {
                    user.write(FieldPacket.itemUpgradeResultErr(2)); // 2 upgrade increases have been used already.
                    return;
                }
                if (targetItem.getItemId() == ItemConstants.HORNTAIL_NECKLACE || targetItem.getItemId() == ItemConstants.CHAOS_HORNTAIL_NECKLACE) {
                    user.write(FieldPacket.itemUpgradeResultErr(3)); // You can't use Vicious' Hammer on Horntail Necklace.
                    return;
                }
                // Remove item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove vicious' hammer item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Update target item
                equipData.setRuc((byte) (equipData.getRuc() + 1));
                equipData.setIuc(equipData.getIuc() + 1);
                final Optional<InventoryOperation> updateItemResult = im.updateItem(targetPosition, targetItem);
                if (updateItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not update equip item %d in position %d", targetItem.getItemId(), targetPosition));
                }
                user.write(WvsContext.inventoryOperation(updateItemResult.get(), false));
                user.write(FieldPacket.itemUpgradeResultSuccess(equipData.getIuc())); // exclRequest by ItemUpgradeResult
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
            case WEATHER -> {
                final String message = inPacket.decodeString();
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove weather item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
                // Blow weather
                user.getField().blowWeather(itemId, String.format("%s : %s", user.getCharacterName(), message), 30);
                // Apply state change item
                final int stateChangeItem = itemInfo.getInfo(ItemInfoType.stateChangeItem);
                if (stateChangeItem == 0) {
                    user.dispose();
                    return;
                }
                final Optional<ItemInfo> stateChangeItemInfoResult = ItemProvider.getItemInfo(stateChangeItem);
                if (stateChangeItemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", stateChangeItem);
                    user.dispose();
                    return;
                }
                final ItemInfo stateChangeItemInfo = stateChangeItemInfoResult.get();
                changeStat(user, stateChangeItemInfo);
                user.getField().getUserPool().forEach((other) -> {
                    if (other.getCharacterId() != user.getCharacterId()) {
                        changeStat(other, stateChangeItemInfo);
                    }
                });
            }
            case SETPETNAME -> {
                final String petName = inPacket.decodeString();
                // Resolve pet
                final Pet pet = user.getPet(0);
                if (pet == null) {
                    log.error("Could not resolve target for pet name change");
                    user.dispose();
                    return;
                }
                // Resolve pet item
                final Optional<Tuple<Integer, Item>> itemEntryResult = im.getItemBySn(InventoryType.CASH, pet.getItemSn());
                if (itemEntryResult.isEmpty()) {
                    throw new IllegalStateException("Could not resolve target pet item");
                }
                final int petPosition = itemEntryResult.get().getLeft();
                final Item petItem = itemEntryResult.get().getRight();
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove weather item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Set pet name and update item
                petItem.getPetData().setPetName(petName);
                final Optional<InventoryOperation> updateResult = im.updateItem(petPosition, petItem);
                if (updateResult.isEmpty()) {
                    throw new IllegalStateException("Could not update pet item");
                }
                user.write(WvsContext.inventoryOperation(updateResult.get(), true));
                user.getField().broadcastPacket(PetPacket.petNameChanged(user, pet.getPetIndex(), petName));
            }
            case SELECTNPC -> {
                final int npcId = itemInfo.getInfo(ItemInfoType.npc);
                // Resolve npc
                final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(npcId);
                if (npcTemplateResult.isEmpty()) {
                    log.error("Could not resolve npc ID {} for item ID : {}", npcId, itemId);
                    user.dispose();
                    return;
                }
                final NpcTemplate npcTemplate = npcTemplateResult.get();
                // Check if dialog present
                if (user.hasDialog()) {
                    log.error("Tried to use select npc item ID {}, while already in a dialog", itemId);
                    user.dispose();
                    return;
                }
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove select npc item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                if (npcTemplate.isTrunk()) {
                    final TrunkDialog trunkDialog = TrunkDialog.from(npcTemplate);
                    user.setDialog(trunkDialog);
                    user.write(TrunkPacket.openTrunkDlg(npcId, user.getAccount().getTrunk()));
                } else {
                    final ShopDialog shopDialog = ShopDialog.from(npcTemplate);
                    user.setDialog(shopDialog);
                    user.write(FieldPacket.openShopDlg(user, shopDialog));
                }
            }
            case MORPH -> {
                // Consume item and change stat
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove morph item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                changeStat(user, itemInfo);
            }
            case MAPTRANSFER -> {
                final boolean targetUser = inPacket.decodeBoolean();
                if (targetUser) {
                    // Resolve target location
                    final String targetName = inPacket.decodeString();
                    user.getConnectedServer().submitUserQueryRequest(List.of(targetName), (queryResult) -> {
                        final Optional<RemoteUser> targetResult = queryResult.stream().findFirst();
                        if (targetResult.isEmpty()) {
                            user.write(MapTransferPacket.targetNotExist()); // %s is currently difficult to locate, so the teleport will not take place.
                            user.dispose();
                            return;
                        }
                        final RemoteUser target = targetResult.get();
                        if (target.getChannelId() != user.getChannelId()) {
                            user.write(MapTransferPacket.targetNotExist()); // %s is currently difficult to locate, so the teleport will not take place.
                            user.dispose();
                            return;
                        }
                        handleMapTransfer(user, target.getFieldId(), item, position);
                    });
                } else {
                    final int targetField = inPacket.decodeInt(); // dwTargetField
                    final List<Integer> availableFields = itemId / 1000 != 5040 ? // canTransferContinent
                            user.getMapTransferInfo().getMapTransferEx() :
                            user.getMapTransferInfo().getMapTransfer();
                    if (!availableFields.contains(targetField)) {
                        user.write(MapTransferPacket.unknown()); // You cannot go to that place.
                        user.dispose();
                        return;
                    }
                    handleMapTransfer(user, targetField, item, position);
                }
            }
            case STATCHANGE -> {
                final int inc = inPacket.decodeInt(); // dwInc
                final int dec = inPacket.decodeInt(); // dwDec
                // Validate stats
                final Stat incStat = Stat.getByValue(inc);
                final Stat decStat = Stat.getByValue(dec);
                final CharacterStat cs = user.getCharacterStat();
                if (incStat == null || !StatConstants.isAbilityUpStat(incStat) || decStat == null || !StatConstants.isAbilityUpStat(decStat) ||
                        incStat == decStat || !cs.isValidAp(incStat, 1) || !cs.isValidAp(decStat, -1)) {
                    log.error("Received invalid stats {}, {} for stat change item ID : {}", inc, dec, itemId);
                    user.dispose();
                    return;
                }
                if (decStat == Stat.MHP || decStat == Stat.MMP) {
                    final int currentAp = cs.getBaseStr() + cs.getBaseDex() + cs.getBaseInt() + cs.getBaseLuk() + cs.getAp();
                    final int expectedAp = StatConstants.getSumAp(cs.getLevel(), cs.getJob(), cs.getSubJob());
                    if (currentAp >= expectedAp) {
                        log.error("Tried to remove hp/mp without enough ap - current : {}, expected : {}", currentAp, expectedAp);
                        user.dispose();
                        return;
                    }
                }
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove stat change item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Add stats
                final Map<Stat, Object> statMap = new EnumMap<>(Stat.class);
                statMap.putAll(cs.removeAp(decStat));
                statMap.putAll(cs.addAp(incStat, user.getBasicStat().getInt()));
                // Update client
                user.validateStat();
                user.write(WvsContext.statChanged(statMap, true));
            }
            case COLORLENS -> {
                // Resolve face
                final int face = user.getCharacterStat().getFace();
                final int color = (face % 1000) - (face % 100);
                final int newFace = (face - color) + ((itemId - 5152100) * 100);
                if (ItemProvider.getItemInfo(newFace).isEmpty()) {
                    log.error("Tried to change use color lens to change to face {} using item ID : {}", newFace, itemId);
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                    return;
                }
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove color lens item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Change avatar
                user.getCharacterStat().setFace(newFace);
                user.write(WvsContext.statChanged(Stat.FACE, user.getCharacterStat().getFace(), true));
                user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
            }
            case REWARD -> {
                // Resolve reward info
                final Optional<ItemRewardInfo> itemRewardInfoResult = ItemProvider.getItemRewardInfo(itemId);
                if (itemRewardInfoResult.isEmpty()) {
                    log.error("Could not resolve reward info for item ID : {}", itemId);
                    user.dispose();
                    return;
                }
                final ItemRewardInfo itemRewardInfo = itemRewardInfoResult.get();
                // Resolve reward
                if (!itemRewardInfo.canAddReward(user)) {
                    user.write(MessagePacket.system("You do not have enough inventory space."));
                    user.dispose();
                    return;
                }
                final Optional<ItemRewardEntry> rewardResult = Util.getRandomFromCollection(itemRewardInfo.getEntries(), ItemRewardEntry::getProbability);
                if (rewardResult.isEmpty()) {
                    log.error("Could not resolve lottery item reward for item {}", itemId);
                    return;
                }
                final ItemRewardEntry rewardEntry = rewardResult.get();
                final Optional<ItemInfo> rewardItemInfoResult = ItemProvider.getItemInfo(rewardEntry.getItemId());
                if (rewardItemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", rewardEntry.getItemId());
                    return;
                }
                // Consume item
                final Optional<InventoryOperation> removeItemResult = im.removeItem(position, item, 1);
                if (removeItemResult.isEmpty()) {
                    throw new IllegalStateException(String.format("Could not remove reward item %d in position %d", item.getItemId(), position));
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                // Add reward item
                final Item rewardItem = rewardItemInfoResult.get().createItem(user.getNextItemSn(), rewardEntry.getCount());
                if (rewardEntry.getPeriod() > 0) {
                    rewardItem.setDateExpire(Instant.now().plus(rewardEntry.getPeriod(), ChronoUnit.MINUTES));
                }
                final Optional<List<InventoryOperation>> addResult = user.getInventoryManager().addItem(rewardItem);
                if (addResult.isEmpty()) {
                    throw new IllegalStateException("Could not add reward item to inventory");
                }
                user.write(WvsContext.inventoryOperation(addResult.get(), true));
                if (rewardEntry.hasEffect()) {
                    user.write(UserLocal.effect(Effect.lotteryUse(itemId, rewardEntry.getEffect())));
                }
            }
            case null -> {
                log.error("Unknown cash item type for item ID : {}", item.getItemId());
                user.dispose();
            }
            default -> {
                log.error("Unhandled cash item type {} for item ID : {}", cashItemType, item.getItemId());
                user.dispose();
            }
        }
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    private static String formatSpeakerMessage(User user, String message) {
        final Item medalItem = user.getInventoryManager().getEquipped().getItem(BodyPart.MEDAL.getValue());
        if (medalItem != null) {
            final String medalName = StringProvider.getItemName(medalItem.getItemId());
            return String.format("<%s> %s : %s", medalName, user.getCharacterName(), message);
        } else {
            return String.format("%s : %s", user.getCharacterName(), message);
        }
    }

    private static void handleWorldSpeaker(User user, int position, Item item, boolean avatar, OutPacket outPacket) {
        // Check if speaker can be sent
        if (user.getLevel() < 10) {
            user.write(WvsContext.avatarMegaphoneResLevelLimit()); // This megaphone is only available for characters that are over Level 10.
            return;
        }
        if (avatar && !user.getConnectedServer().canSubmitAvatarSpeaker()) {
            user.write(WvsContext.avatarMegaphoneResQueueFull()); // The waiting line is longer than 15 seconds. Please try using it at a later time.
            return;
        }
        if (!user.getConnectedServer().canSubmitWorldSpeaker(user.getCharacterId())) {
            user.write(WvsContext.avatarMegaphoneRes("You may not use this item yet."));
            return;
        }
        // Remove item
        final Optional<InventoryOperation> removeItemResult = user.getInventoryManager().removeItem(position, item, 1);
        if (removeItemResult.isEmpty()) {
            log.error("Could not remove world speaker item from inventory");
            user.dispose();
            return;
        }
        user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
        // Submit world speaker request
        user.getConnectedServer().submitWorldSpeakerRequest(user.getCharacterId(), avatar, outPacket);
    }

    private static void handleMapTransfer(User user, int targetFieldId, Item item, int position) {
        final Field currentField = user.getField();
        final boolean canTransferContinent = item.getItemId() / 1000 != 5040;
        if (currentField.isMapTransferLimit() || (!canTransferContinent && !currentField.isSameContinent(targetFieldId))) {
            user.write(MapTransferPacket.notAllowed()); // You cannot go to that place.
            user.dispose();
            return;
        }
        // Resolve target field
        final Optional<Field> targetFieldResult = user.getConnectedServer().getFieldById(targetFieldId);
        if (targetFieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", targetFieldId);
            user.write(MapTransferPacket.notAllowed()); // You cannot go to that place.
            user.dispose();
            return;
        }
        final Field targetField = targetFieldResult.get();
        final Optional<PortalInfo> targetPortalResult = targetField.getRandomStartPoint();
        if (targetPortalResult.isEmpty()) {
            log.error("Could not resolve start point portal for field ID : {}", targetFieldId);
            user.write(MapTransferPacket.notAllowed()); // You cannot go to that place.
            user.dispose();
            return;
        }
        if (targetField.isMapTransferLimit()) {
            user.write(MapTransferPacket.notAllowed()); // You cannot go to that place.
            user.dispose();
            return;
        }
        // Consume item and warp
        final Optional<InventoryOperation> removeItemResult = user.getInventoryManager().removeItem(position, item, 1);
        if (removeItemResult.isEmpty()) {
            throw new IllegalStateException(String.format("Could not remove map transfer item %d in position %d", item.getItemId(), position));
        }
        user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
        user.warp(targetField, targetPortalResult.get(), false, false);
    }
}
