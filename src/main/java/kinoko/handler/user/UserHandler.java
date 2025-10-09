package kinoko.handler.user;

import kinoko.database.CharacterInfo;
import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.field.*;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.user.*;
import kinoko.packet.world.*;
import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemMakeInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.script.common.ScriptAnswer;
import kinoko.script.common.ScriptDispatcher;
import kinoko.script.common.ScriptMessageType;
import kinoko.script.quest.AranTutorial;
import kinoko.script.quest.CygnusTutorial;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.*;
import kinoko.server.command.CommandProcessor;
import kinoko.server.dialog.ScriptDialog;
import kinoko.server.dialog.miniroom.*;
import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.trunk.TrunkDialog;
import kinoko.server.header.InHeader;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoRequestType;
import kinoko.server.memo.MemoType;
import kinoko.server.messenger.MessengerProtocol;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.packet.InPacket;
import kinoko.server.rank.RankManager;
import kinoko.server.user.RemoteUser;
import kinoko.util.Triple;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.TownPortal;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.npc.Npc;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestRequestType;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.skill.maker.MakerConstants;
import kinoko.world.skill.maker.MakerResult;
import kinoko.world.skill.maker.RecipeClass;
import kinoko.world.user.Locker;
import kinoko.world.user.User;
import kinoko.world.user.data.*;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import kinoko.world.user.stat.StatConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.*;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);

    @Handler(InHeader.UserMove)
    public static void handleUserMove(User user, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        final int crc = inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final Field field = user.getField();
        if (field.getFieldCrc() != crc) {
            log.warn("Received mismatching CRC for field ID : {}", field.getFieldId());
        }
        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(user);
        field.broadcastPacket(UserRemote.move(user, movePath), user);
    }

    @Handler(InHeader.UserSitRequest)
    public static void handleUserSitRequest(User user, InPacket inPacket) {
        // CUserLocal::HandleXKeyDown, CWvsContext::SendGetUpFromChairRequest
        final short fieldSeatId = inPacket.decodeShort();
        user.setPortableChairId(0);
        user.write(UserLocal.sitResult(fieldSeatId != -1, fieldSeatId));
        user.getField().broadcastPacket(UserRemote.setActivePortableChair(user, 0), user);
    }

    @Handler(InHeader.UserPortableChairSitRequest)
    public static void handleUserPortableChairSitRequest(User user, InPacket inPacket) {
        // CWvsContext::SendSitOnPortableChairRequest
        final int itemId = inPacket.decodeInt(); // nItemID
        if (!ItemConstants.isPortableChairItem(itemId)) {
            log.error("Received UserPortableChairSitRequest with a non-portable chair item ID : {}", itemId);
            user.dispose();
            return;
        }
        user.setPortableChairId(itemId);
        user.getField().broadcastPacket(UserRemote.setActivePortableChair(user, itemId), user); // self-cast not required
        user.dispose();
    }

    @Handler(InHeader.UserChat)
    public static void handleUserChat(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon
        if (text.startsWith(ServerConfig.PLAYER_COMMAND_PREFIX) && text.length() > 1) {
            CommandProcessor.tryProcessCommand(user, text);
            return;
        }
        user.getField().broadcastPacket(UserPacket.userChat(user, ChatType.NORMAL, text, onlyBalloon));
    }

    @Handler(InHeader.UserADBoardClose)
    public static void handleUserAdBoardClose(User user, InPacket inPacket) {
        user.setAdBoard(null);
        user.getField().broadcastPacket(UserPacket.userAdBoard(user, null));
    }

    @Handler(InHeader.UserEmotion)
    public static void handleUserEmotion(User user, InPacket inPacket) {
        final int emotion = inPacket.decodeInt(); // nEmotion
        final int duration = inPacket.decodeInt(); // nDuration
        final boolean isByItemOption = inPacket.decodeBoolean(); // bByItemOption
        user.getField().broadcastPacket(UserRemote.emotion(user, emotion, duration, isByItemOption), user);
    }

    @Handler(InHeader.UserActivateEffectItem)
    public static void handleUserActivateEffectItem(User user, InPacket inPacket) {
        final int itemId = inPacket.decodeInt(); // nEffectItemID
        if (itemId != 0 && !ItemConstants.isCashEffectItem(itemId) && !ItemConstants.isNonCashEffectItem(itemId)) {
            log.error("Received UserActivateEffectItem with invalid effect item : {}", itemId);
        }
        user.setEffectItemId(itemId);
        user.getField().broadcastPacket(UserRemote.setActiveEffectItem(user, itemId), user); // self-cast not required
    }

    @Handler(InHeader.UserUpgradeTombEffect)
    public static void handleUserUpgradeTombEffect(User user, InPacket inPacket) {
        final int itemId = inPacket.decodeInt(); // 5510000 (Wheel of Destiny)
        final int x = inPacket.decodeInt(); // ptRevive.x
        final int y = inPacket.decodeInt(); // ptRevive.y
        user.getField().broadcastPacket(UserRemote.showUpgradeTombEffect(user, itemId, x, y), user);
    }


    // NPC HANDLERS ----------------------------------------------------------------------------------------------------

    @Handler(InHeader.UserSelectNpc)
    public static void handleUserSelectNpc(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwNpcId
        final short x = inPacket.decodeShort(); // ptUserPos.x
        final short y = inPacket.decodeShort(); // ptUserPos.y
        final Optional<Npc> npcResult = user.getField().getNpcPool().getById(objectId);
        if (npcResult.isEmpty()) {
            log.error("Tried to select invalid npc ID : {}", objectId);
            return;
        }
        final Npc npc = npcResult.get();
        // Handle script
        if (npc.hasScript()) {
            ScriptDispatcher.startNpcScript(user, npc, npc.getScript(), npc.getTemplateId());
            return;
        }
        // Handle guild rank
        if (npc.isGuildRank()) {
            user.write(GuildPacket.showGuildRanking(RankManager.getGuildRankings()));
            return;
        }
        // Handle trunk / npc shop dialog
        if (user.hasDialog()) {
            log.error("Tried to select npc ID {}, while already in a dialog", npc.getTemplateId());
            return;
        }
        if (npc.isTrunk()) {
            final TrunkDialog trunkDialog = TrunkDialog.from(npc.getTemplate());
            user.setDialog(trunkDialog);
            user.write(TrunkPacket.openTrunkDlg(npc.getTemplateId(), user.getAccount().getTrunk()));
        } else if (ShopProvider.isShop(npc.getTemplateId())) {
            final ShopDialog shopDialog = ShopDialog.from(npc.getTemplate());
            user.setDialog(shopDialog);
            user.write(FieldPacket.openShopDlg(user, shopDialog));
        }
    }

    @Handler(InHeader.UserScriptMessageAnswer)
    public static void handleUserScriptMessageAnswer(User user, InPacket inPacket) {
        final byte type = inPacket.decodeByte(); // nMsgType
        final byte action = inPacket.decodeByte();
        final ScriptMessageType lastMessageType = ScriptMessageType.getByValue(type);
        if (lastMessageType == null) {
            log.error("Unknown script message type {}", type);
            return;
        }
        if (!user.hasDialog() || !(user.getDialog() instanceof ScriptDialog scriptDialog)) {
            log.error("Received UserScriptMessageAnswer without an associated script dialog");
            return;
        }
        switch (lastMessageType) {
            case SAY, SAYIMAGE, ASKYESNO, ASKACCEPT -> {
                scriptDialog.submitAnswer(ScriptAnswer.withAction(action));
            }
            case ASKTEXT, ASKBOXTEXT -> {
                if (action == 1) {
                    final String answer = inPacket.decodeString(); // sInputStr_Result
                    scriptDialog.submitAnswer(ScriptAnswer.withTextAnswer(action, answer));
                } else {
                    scriptDialog.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            case ASKNUMBER, ASKMENU, ASKSLIDEMENU -> {
                if (action == 1) {
                    final int answer = inPacket.decodeInt(); // nInputNo_Result | nSelect
                    scriptDialog.submitAnswer(ScriptAnswer.withAnswer(action, answer));
                } else {
                    scriptDialog.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            case ASKAVATAR, ASKMEMBERSHOPAVATAR -> {
                if (action == 1) {
                    final byte answer = inPacket.decodeByte(); // nAvatarIndex
                    scriptDialog.submitAnswer(ScriptAnswer.withAnswer(action, answer));
                } else {
                    scriptDialog.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            default -> {
                log.error("Unhandled script message type {}", lastMessageType);
            }
        }
    }

    @Handler(InHeader.UserShopRequest)
    public static void handleUserShopRequest(User user, InPacket inPacket) {
        if (!(user.getDialog() instanceof ShopDialog shopDialog)) {
            log.error("Received UserShopRequest without associated shop dialog");
            return;
        }
        shopDialog.handlePacket(user, inPacket);
    }

    @Handler(InHeader.UserTrunkRequest)
    public static void handleUserTrunkRequest(User user, InPacket inPacket) {
        if (!(user.getDialog() instanceof TrunkDialog trunkDialog)) {
            log.error("Received UserTrunkRequest without associated trunk dialog");
            return;
        }
        trunkDialog.handlePacket(user, inPacket);
    }


    // INVENTORY HANDLERS ----------------------------------------------------------------------------------------------

    @Handler(InHeader.UserGatherItemRequest)
    public static void handlerUserGatherItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        final InventoryManager im = user.getInventoryManager();
        final Inventory inventory = im.getInventoryByType(inventoryType);
        // Find stackable items : itemId -> Set<Tuple<position, item>>
        final Map<Integer, List<Tuple<Integer, Item>>> stackable = new HashMap<>();
        for (var entry : inventory.getItems().entrySet()) {
            final int position = entry.getKey();
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.BUNDLE || ItemConstants.isRechargeableItem(item.getItemId())) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
            if (itemInfoResult.isEmpty() || itemInfoResult.get().getSlotMax() <= 1) {
                continue;
            }
            if (!stackable.containsKey(item.getItemId())) {
                stackable.put(item.getItemId(), new ArrayList<>());
            }
            stackable.get(item.getItemId()).add(Tuple.of(position, item));
        }
        // Get required inventory operations
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        for (var entry : stackable.entrySet()) {
            if (entry.getValue().size() <= 1) {
                continue;
            }
            final int slotMax = ItemProvider.getItemInfo(entry.getKey()).map(ItemInfo::getSlotMax).orElse(0);
            final List<Tuple<Integer, Item>> sortedItems = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(Tuple::getLeft))
                    .toList();
            int total = sortedItems.stream()
                    .mapToInt((tuple) -> tuple.getRight().getQuantity())
                    .sum();
            for (var tuple : sortedItems) {
                final int position = tuple.getLeft();
                if (total > slotMax) {
                    inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, position, slotMax));
                    total -= slotMax;
                } else {
                    if (total > 0) {
                        inventoryOperations.add(InventoryOperation.itemNumber(inventoryType, position, total));
                        total = 0;
                    } else {
                        inventoryOperations.add(InventoryOperation.delItem(inventoryType, position));
                    }
                }
            }
        }
        // Apply inventory operations and update client
        im.applyInventoryOperations(inventoryOperations);
        user.write(WvsContext.inventoryOperation(inventoryOperations, true));
        user.write(WvsContext.gatherItemResult(inventoryType));
    }

    @Handler(InHeader.UserSortItemRequest)
    public static void handlerUserSortItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        final InventoryManager im = user.getInventoryManager();
        // Create array for sorting
        final Item[] items = new Item[GameConstants.INVENTORY_SLOT_MAX]; // using 0-based indexing for positions (inventory uses 1-based)
        for (var entry : im.getInventoryByType(inventoryType).getItems().entrySet()) {
            items[entry.getKey() - 1] = entry.getValue();
        }
        // Selection sort to find required swaps
        final List<InventoryOperation> inventoryOperations = new ArrayList<>();
        for (int i = 0; i < items.length - 1; i++) {
            int k = i; // minimum index
            for (int j = i + 1; j < items.length; j++) {
                if (items[j] == null) {
                    continue;
                }
                // Consolidate, sorting by ID (increasing) and quantity (decreasing)
                if (items[k] == null ||
                        items[j].getItemId() < items[k].getItemId() ||
                        (items[j].getItemId() == items[k].getItemId() &&
                                items[j].getQuantity() > items[k].getQuantity())) {
                    k = j;
                }
            }
            // Perform swap
            final Item temp = items[k];
            items[k] = items[i];
            items[i] = temp;
            inventoryOperations.add(InventoryOperation.position(inventoryType, k + 1, i + 1)); // again, inventory uses 1-based positions
        }
        // Apply inventory operations and update client
        im.applyInventoryOperations(inventoryOperations);
        user.write(WvsContext.inventoryOperation(inventoryOperations, true));
        user.write(WvsContext.sortItemResult(inventoryType));
    }

    @Handler(InHeader.UserChangeSlotPositionRequest)
    public static void handleUserChangeSlotPositionRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int type = inPacket.decodeByte(); // nType
        final InventoryType inventoryType = InventoryType.getByValue(type);
        if (inventoryType == null) {
            log.error("Unknown inventory type : {}", type);
            return;
        }
        final short oldPos = inPacket.decodeShort(); // nOldPos
        final short newPos = inPacket.decodeShort(); // nNewPos
        final short count = inPacket.decodeShort(); // nCount

        final InventoryManager im = user.getInventoryManager();
        final Inventory inventory = im.getInventoryByType(InventoryType.getByPosition(inventoryType, oldPos));
        final Item item = inventory.getItem(oldPos);
        if (item == null) {
            log.error("Could not find item in {} inventory, position {}", inventoryType.name(), oldPos);
            return;
        }
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}, position {}", item.getItemId(), oldPos);
            return;
        }
        final ItemInfo itemInfo = itemInfoResult.get();

        if (newPos == 0) {
            // CDraggableItem::ThrowItem
            final DropEnterType dropEnterType = (itemInfo.isTradeBlock(item) || itemInfo.isAccountSharable()) ? DropEnterType.FADING_OUT : DropEnterType.CREATE;
            if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) &&
                    item.getQuantity() > count) {
                // Update item count
                item.setQuantity((short) (item.getQuantity() - count));
                user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, oldPos, item.getQuantity()), true));
                // Create partial item
                final Item partialItem = new Item(item);
                partialItem.setItemSn(user.getNextItemSn());
                partialItem.setQuantity(count);
                partialItem.setPossibleTrading(false);
                // Create drop
                final Drop drop = Drop.item(DropOwnType.NOOWN, user, partialItem, user.getCharacterId());
                user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT, 0);
            } else {
                // Full drop
                if (!inventory.removeItem(oldPos, item)) {
                    log.error("Failed to remove item in {} inventory, position {}", inventoryType.name(), oldPos);
                    return;
                }
                // Remove item from client inventory
                user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos), true));
                item.setPossibleTrading(false);
                // Create drop
                final Drop drop = Drop.item(DropOwnType.NOOWN, user, item, user.getCharacterId());
                user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT, 0);
            }
        } else {
            final InventoryType secondInventoryType = InventoryType.getByPosition(inventoryType, newPos);
            final Inventory secondInventory = im.getInventoryByType(secondInventoryType);
            if (secondInventoryType == InventoryType.EQUIPPED) {
                // Check body part
                final int absPos = Math.abs(newPos);
                final boolean isCash = absPos >= BodyPart.CASH_BASE.getValue() && absPos < BodyPart.CASH_END.getValue();
                final BodyPart bodyPart = BodyPart.getByValue(isCash ? (absPos - BodyPart.CASH_BASE.getValue()) : absPos);
                if (bodyPart == null || !ItemConstants.isCorrectBodyPart(item.getItemId(), bodyPart, user.getGender())) {
                    log.error("Failed to equip item ID {} in position {}", item.getItemId(), absPos);
                    user.dispose();
                    return;
                }
                // Move exclusive body part equip item to inventory
                final BodyPart exclusiveBodyPart = ItemConstants.getExclusiveEquipItemBodyPart(secondInventory, item.getItemId(), isCash);
                if (exclusiveBodyPart != null) {
                    final Item exclusiveEquipItem = secondInventory.getItem(exclusiveBodyPart.getValue() + (isCash ? BodyPart.CASH_BASE.getValue() : 0));
                    final Optional<Integer> availablePositionResult = InventoryManager.getAvailablePosition(im.getEquipInventory());
                    if (availablePositionResult.isEmpty()) {
                        log.error("No room in inventory remove exclusive equip item body part item ID {} in position {}", exclusiveEquipItem.getItemId(), exclusiveBodyPart);
                        user.dispose();
                        return;
                    }
                    final int availablePosition = availablePositionResult.get();
                    if (!secondInventory.removeItem(exclusiveBodyPart.getValue(), exclusiveEquipItem)) {
                        throw new IllegalStateException("Could not remove exclusive equip item");
                    }
                    im.getEquipInventory().putItem(availablePosition, exclusiveEquipItem);
                    user.write(WvsContext.inventoryOperation(InventoryOperation.position(InventoryType.EQUIP, -exclusiveBodyPart.getValue(), availablePosition), false)); // client uses negative index for equipped
                }
                // Handle items binded on equip
                if (itemInfo.isEquipTradeBlock() && !item.hasAttribute(ItemAttribute.EQUIP_BINDED)) {
                    item.addAttribute(ItemAttribute.EQUIP_BINDED);
                    user.write(WvsContext.inventoryOperation(InventoryOperation.newItem(InventoryType.EQUIP, oldPos, item), false));
                }
            } else if (secondInventory.getSize() < newPos) {
                user.dispose();
                return;
            }

            final Item secondItem = secondInventory.getItem(newPos);
            if (secondItem != null && secondItem.getItemId() == item.getItemId() &&
                    item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) &&
                    item.getQuantity() < itemInfo.getSlotMax() && secondItem.getQuantity() < itemInfo.getSlotMax()) {
                // Merge bundles : item -> secondItem
                final int combinedQuantity = item.getQuantity() + secondItem.getQuantity();
                if (combinedQuantity <= itemInfo.getSlotMax()) {
                    if (!inventory.removeItem(oldPos, item)) {
                        throw new IllegalStateException("Could not remove old item");
                    }
                    secondItem.setQuantity((short) combinedQuantity);
                    user.write(WvsContext.inventoryOperation(List.of(
                            InventoryOperation.position(inventoryType, oldPos, newPos), // move nLatestGetItemPos frame
                            InventoryOperation.delItem(inventoryType, oldPos),
                            InventoryOperation.itemNumber(secondInventoryType, newPos, secondItem.getQuantity())
                    ), true));
                } else {
                    item.setQuantity((short) (combinedQuantity - itemInfo.getSlotMax()));
                    secondItem.setQuantity((short) itemInfo.getSlotMax());
                    user.write(WvsContext.inventoryOperation(List.of(
                            InventoryOperation.position(inventoryType, oldPos, newPos), // move nLatestGetItemPos frame
                            InventoryOperation.itemNumber(inventoryType, oldPos, item.getQuantity()),
                            InventoryOperation.itemNumber(secondInventoryType, newPos, secondItem.getQuantity())
                    ), true));
                }
            } else {
                // Swap item position and update client
                inventory.putItem(oldPos, secondItem);
                secondInventory.putItem(newPos, item);
                user.write(WvsContext.inventoryOperation(InventoryOperation.position(inventoryType, oldPos, newPos), true));
            }
        }
        // Update user
        if (inventoryType == InventoryType.EQUIP) {
            user.getCharacterData().getCoupleRecord().reset(im.getEquipped(), im.getEquipInventory());
            user.validateStat();
            user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
        }
    }


    // STAT HANDLERS ---------------------------------------------------------------------------------------------------

    @Handler(InHeader.UserAbilityUpRequest)
    public static void handleUserAbilityUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int flag = inPacket.decodeInt(); // dwFlag
        final Stat stat = Stat.getByValue(flag);
        if (stat == null || !StatConstants.isAbilityUpStat(stat)) {
            log.error("Unknown stat flag {} received for UserAbilityUpRequest", flag);
            user.dispose();
            return;
        }
        // Validate stat
        final CharacterStat cs = user.getCharacterStat();
        if (cs.getAp() < 1) {
            log.error("Tried to add ap with {} remaining ap", cs.getAp());
            user.dispose();
            return;
        }
        if (!cs.isValidAp(stat, 1)) {
            log.error("Tried to add ap to stat {}", stat);
            user.dispose();
            return;
        }
        // Add stat
        final Map<Stat, Object> addApResult = cs.addAp(stat, user.getBasicStat().getInt());
        cs.setAp((short) (cs.getAp() - 1));
        addApResult.put(Stat.AP, cs.getAp());
        // Update client
        user.validateStat();
        user.write(WvsContext.statChanged(addApResult, true));
    }

    @Handler(InHeader.UserAbilityMassUpRequest)
    public static void handleUserAbilityMassUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int size = inPacket.decodeInt();
        final Map<Stat, Integer> stats = new EnumMap<>(Stat.class);
        for (int i = 0; i < size; i++) {
            final int flag = inPacket.decodeInt(); // dwStatFlag
            final int value = inPacket.decodeInt(); // nValue
            final Stat stat = Stat.getByValue(flag);
            if (stat == null || !StatConstants.isAbilityUpStat(stat)) {
                log.error("Unknown stat flag {} received for UserAbilityMassUpRequest", flag);
                user.dispose();
                return;
            }
            stats.put(stat, value);
        }
        // Validate stats
        final CharacterStat cs = user.getCharacterStat();
        final int requiredAp = stats.values().stream().mapToInt(Integer::intValue).sum();
        if (cs.getAp() < requiredAp) {
            log.error("Tried to add {} ap with {} remaining ap", requiredAp, cs.getAp());
            user.dispose();
            return;
        }
        for (var entry : stats.entrySet()) {
            final Stat stat = entry.getKey();
            final int value = entry.getValue();
            if (!cs.isValidAp(stat, value)) {
                log.error("Tried to add {} ap to stat {}", stat, value);
                user.dispose();
                return;
            }
        }
        // Add stats
        final Map<Stat, Object> addApResult = new EnumMap<>(Stat.class);
        for (var entry : stats.entrySet()) {
            final Stat stat = entry.getKey();
            final int value = entry.getValue();
            for (int i = 0; i < value; i++) {
                addApResult.putAll(cs.addAp(stat, user.getBasicStat().getInt()));
            }
        }
        cs.setAp((short) (cs.getAp() - requiredAp));
        addApResult.put(Stat.AP, cs.getAp());
        // Update client
        user.validateStat();
        user.write(WvsContext.statChanged(addApResult, true));
    }

    @Handler({ InHeader.UserChangeStatRequest, InHeader.UserChangeStatRequestByItemOption })
    public static void handleUserChangeStatRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int mask = inPacket.decodeInt(); // 0x1400
        if (mask != 0x1400) {
            log.error("Unhandled mask received for UserChangeStatRequest : {}", mask);
            return;
        }
        final int hp = Short.toUnsignedInt(inPacket.decodeShort()); // nHP
        final int mp = Short.toUnsignedInt(inPacket.decodeShort()); // nMP
        // inPacket.decodeByte(); // nOption for UserChangeStatRequest
        if (hp > 0) {
            user.addHp(hp);
        }
        if (mp > 0) {
            user.addMp(mp);
        }
    }

    @Handler(InHeader.UserSkillUpRequest)
    public static void handleUserSkillUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID

        // Resolve skill info
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for skill ID : {}", skillId);
            user.dispose();
            return;
        }
        final SkillInfo skillInfo = skillInfoResult.get();

        // Resolve skill record
        final SkillManager sm = user.getSkillManager();
        final Optional<SkillRecord> skillRecordResult = sm.getSkill(skillId);
        if (skillRecordResult.isEmpty()) {
            log.error("Tried to add a skill {} not owned by user", skillId);
            user.dispose();
            return;
        }
        final SkillRecord skillRecord = skillRecordResult.get();

        // Check skill level
        if (SkillConstants.isSkillNeedMasterLevel(skillId)) {
            if (skillRecord.getSkillLevel() >= skillRecord.getMasterLevel()) {
                log.error("Tried to add a skill {} at master level {}/{}", skillId, skillRecord.getSkillLevel(), skillRecord.getMasterLevel());
                user.dispose();
                return;
            }
        } else {
            if (skillRecord.getSkillLevel() >= skillInfo.getMaxLevel()) {
                log.error("Tried to add a skill {} at max level {}/{}", skillId, skillRecord.getSkillLevel(), skillInfo.getMaxLevel());
                user.dispose();
                return;
            }
        }

        final int skillRoot = SkillConstants.getSkillRoot(skillId);
        if (JobConstants.isBeginnerJob(skillRoot)) {
            // Check if valid beginner skill
            if (!SkillConstants.isBeginnerSpAddableSkill(skillId)) {
                log.error("Tried to add an invalid beginner skill {}", skillId);
                user.dispose();
                return;
            }
            // Compute sp spent on beginner skills
            final int spentSp = sm.getSkillRecords().stream()
                    .filter((sr) -> SkillConstants.isBeginnerSpAddableSkill(sr.getSkillId()))
                    .mapToInt(SkillRecord::getSkillLevel)
                    .sum();
            // Beginner sp is calculated by level
            final int totalSp;
            if (JobConstants.isResistanceJob(skillRoot)) {
                totalSp = Math.min(user.getLevel(), 10) - 1; // max total sp = 9
            } else {
                totalSp = Math.min(user.getLevel(), 7) - 1; // max total sp = 6
            }
            // Check if sp can be added
            if (spentSp >= totalSp) {
                log.error("Tried to add skill {} without having the required amount of sp", skillId);
                user.dispose();
                return;
            }
        } else if (JobConstants.isExtendSpJob(skillRoot)) {
            final int jobLevel = JobConstants.getJobLevel(skillRoot);
            if (!user.getCharacterStat().getSp().removeSp(jobLevel, 1)) {
                log.error("Tried to add skill {} without having the required amount of sp", skillId);
                user.dispose();
                return;
            }
        } else {
            if (!user.getCharacterStat().getSp().removeNonExtendSp(1)) {
                log.error("Tried to add skill {} without having the required amount of sp", skillId);
                user.dispose();
                return;
            }
        }

        // Add skill point and update client
        skillRecord.setSkillLevel(skillRecord.getSkillLevel() + 1);
        user.write(WvsContext.statChanged(Stat.SP, JobConstants.isExtendSpJob(user.getJob()) ? user.getCharacterStat().getSp() : (short) user.getCharacterStat().getSp().getNonExtendSp(), false));
        user.write(WvsContext.changeSkillRecordResult(skillRecord, true));
        user.updatePassiveSkillData();
        user.validateStat();
    }


    // OTHER HANDLERS --------------------------------------------------------------------------------------------------

    @Handler(InHeader.UserDropMoneyRequest)
    public static void handleUserDropMoneyRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int money = inPacket.decodeInt(); // nAmount
        final InventoryManager im = user.getInventoryManager();
        if (money <= 0 || !im.addMoney(-money)) {
            user.dispose();
            return;
        }
        final Drop drop = Drop.money(DropOwnType.NOOWN, user, money, user.getCharacterId());
        user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT, 0);
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
    }

    @Handler(InHeader.UserGivePopularityRequest)
    public static void handleUserGivePopularityRequest(User user, InPacket inPacket) {
        final int targetId = inPacket.decodeInt();
        boolean inc = inPacket.decodeBoolean();

        if (user.getLevel() < 15) {
            user.write(WvsContext.givePopularityResult(PopularityResultType.LevelLow)); // Users under level 15 are unable to toggle with fame.
            return;
        }
        final PopularityRecord pr = user.getCharacterData().getPopularityRecord();
        if (pr.hasGivenPopularityToday()) {
            user.write(WvsContext.givePopularityResult(PopularityResultType.AlreadyDoneToday)); // You can't raise or drop a level of fame anymore for today.
            return;
        }
        if (pr.hasGivenPopularityTarget(targetId)) {
            user.write(WvsContext.givePopularityResult(PopularityResultType.AlreadyDoneTarget)); // You can't raise or drop a level of fame of that character anymore for this month.
            return;
        }

        final Optional<User> targetResult = user.getField().getUserPool().getById(targetId);
        if (targetResult.isEmpty()) {
            user.write(MessagePacket.system("Unable to find the character."));
            return;
        }
        final User target = targetResult.get();
        target.addPop(inc ? 1 : -1);
        pr.addRecord(targetId, Instant.now());

        target.write(WvsContext.givePopularityResultNotify(user.getCharacterName(), inc)); // '%s' have raised/dropped '%s''s level of fame.
        user.write(WvsContext.givePopularityResultSuccess(target.getCharacterName(), inc, target.getPop())); // You have raised/dropped '%s''s level of fame.
    }

    @Handler(InHeader.UserCharacterInfoRequest)
    public static void handleUserCharacterInfoRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int characterId = inPacket.decodeInt(); // dwCharacterId
        inPacket.decodeBoolean(); // bPetInfo
        final Optional<User> userResult = user.getField().getUserPool().getById(characterId);
        if (userResult.isEmpty()) {
            user.dispose();
            return;
        }
        user.write(WvsContext.characterInfo(userResult.get()));
    }

    @Handler(InHeader.UserPortalScriptRequest)
    public static void handleUserPortalScriptRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final String portalName = inPacket.decodeString(); // sName
        final short x = inPacket.decodeShort(); // GetPos()->x
        final short y = inPacket.decodeShort(); // GetPos()->y
        final Optional<PortalInfo> portalResult = user.getField().getPortalByName(portalName);
        if (portalResult.isEmpty() || portalResult.get().getScript() == null) {
            user.dispose();
            return;
        }
        ScriptDispatcher.startPortalScript(user, portalResult.get());
    }

    @Handler(InHeader.UserPortalTeleportRequest)
    public static void handleUserPortalTeleportRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final String portalName = inPacket.decodeString(); // sPortalName
        final short x = inPacket.decodeShort(); // GetPos()->x
        final short y = inPacket.decodeShort(); // GetPos()->x
        inPacket.decodeShort(); // portal x
        inPacket.decodeShort(); // portal y
        // let USER_MOVE packets update user position
    }

    @Handler(InHeader.UserMapTransferRequest)
    public static void handleUserMapTransferRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte(); // nType
        final MapTransferRequestType requestType = MapTransferRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Received unknown map transfer request type : {}", type);
            return;
        }
        final boolean canTransferContinent = inPacket.decodeBoolean(); // bCanTransferContinent
        final MapTransferInfo mapTransferInfo = user.getMapTransferInfo();
        if (requestType == MapTransferRequestType.DeleteList) {
            final int targetField = inPacket.decodeInt(); // dwTargetField
            if (!mapTransferInfo.delete(targetField, canTransferContinent)) {
                log.error("Could not delete field {} from map transfer info", targetField);
                return;
            }
            user.write(MapTransferPacket.deleteList(mapTransferInfo, canTransferContinent));
        } else {
            final Field field = user.getField();
            if (field.isMapTransferLimit()) {
                user.write(MapTransferPacket.registerFail()); // This map is not available to enter for the list.
                return;
            }
            if (!mapTransferInfo.register(field.getFieldId(), canTransferContinent)) {
                log.error("Could not register field {} to map transfer info", field.getFieldId());
                return;
            }
            user.write(MapTransferPacket.registerList(mapTransferInfo, canTransferContinent));
        }
    }

    @Handler(InHeader.UserQuestRequest)
    public static void handleUserQuestRequest(User user, InPacket inPacket) {
        final byte action = inPacket.decodeByte();
        final int questId = Short.toUnsignedInt(inPacket.decodeShort()); // usQuestID

        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            log.error("Could not retrieve quest ID : {}", questId);
            return;
        }
        final QuestInfo questInfo = questInfoResult.get();

        final QuestRequestType questRequestType = QuestRequestType.getByValue(action);
        switch (questRequestType) {
            case LostItem -> {
                final int size = inPacket.decodeInt();
                final List<Integer> lostItems = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    lostItems.add(inPacket.decodeInt()); // item id
                }
                questInfo.restoreLostItems(user, lostItems);
            }
            case AcceptQuest -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final Optional<QuestRecord> startQuestResult = questInfo.startQuest(user);
                if (startQuestResult.isEmpty()) {
                    log.error("Failed to accept quest : {}", questId);
                    user.dispose();
                    return;
                }
                user.write(MessagePacket.questRecord(startQuestResult.get()));
                user.write(QuestPacket.success(questId, templateId, 0));
                user.validateStat();
            }
            case CompleteQuest -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final int rewardIndex = inPacket.decodeInt(); // nIdx - for selecting reward
                final Optional<Tuple<QuestRecord, Integer>> questCompleteResult = questInfo.completeQuest(user, rewardIndex);
                if (questCompleteResult.isEmpty()) {
                    log.error("Failed to complete quest : {}", questId);
                    user.dispose();
                    return;
                }
                final QuestRecord questRecord = questCompleteResult.get().getLeft();
                final int nextQuest = questCompleteResult.get().getRight();
                user.write(MessagePacket.questRecord(questRecord));
                user.write(QuestPacket.success(questId, templateId, nextQuest));
                user.validateStat();
                // Quest complete effect
                user.write(UserLocal.effect(Effect.questComplete()));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
            }
            case ResignQuest -> {
                final Optional<QuestRecord> questRecordResult = questInfo.resignQuest(user);
                if (questRecordResult.isEmpty()) {
                    log.error("Failed to resign quest : {}", questId);
                    return;
                }
                user.write(MessagePacket.questRecord(questRecordResult.get()));
                user.write(UserLocal.resignQuestReturn(questId));
                user.validateStat();
            }
            case OpeningScript -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
                if (!questInfo.canStartQuest(user)) {
                    log.error("Tried to start opening script for quest {} without meeting requirements", questId);
                    return;
                }
                ScriptDispatcher.startQuestScript(user, questId, true, templateId);
            }
            case CompleteScript -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
                if (!questInfo.canCompleteQuest(user)) {
                    log.error("Tried to start complete script for quest {} without meeting requirements", questId);
                    return;
                }
                ScriptDispatcher.startQuestScript(user, questId, false, templateId);
            }
            case null -> {
                log.error("Unknown quest action type : {}", action);
            }
            default -> {
                log.error("Unhandled quest action type : {}", questRequestType);
            }
        }
    }

    @Handler(InHeader.UserMacroSysDataModified)
    public static void handleUserMacroSysDataModified(User user, InPacket inPacket) {
        // MACROSYSDATA::Encode
        final List<SingleMacro> macroSysData = new ArrayList<>();
        final int size = inPacket.decodeByte();
        for (int i = 0; i < size; i++) {
            macroSysData.add(SingleMacro.decode(inPacket));
        }
        user.getConfigManager().updateMacroSysData(macroSysData);
    }

    @Handler(InHeader.UserItemMakeRequest)
    public static void handleUserItemMakeRequest(User user, InPacket inPacket) {
        // CUIItemMaker::RequestItemMake
        final int type = inPacket.decodeInt();
        final RecipeClass recipeClass = RecipeClass.getByValue(type); // nRecipeClass
        switch (recipeClass) {
            case NORMAL, HIDDEN -> {
                final int itemId = inPacket.decodeInt(); // nTargetItem
                final boolean catalyst = inPacket.decodeBoolean(); // CatalystSlot.bMounted
                final int gemCount = inPacket.decodeInt(); // nNumGem_Mounted
                final List<Integer> gems = new ArrayList<>();
                for (int i = 0; i < gemCount; i++) {
                    gems.add(inPacket.decodeInt()); // aGemSlot[i].pItem.p->nItemID
                }
                final Optional<ItemMakeInfo> itemMakeInfoResult = EtcProvider.getItemMakeInfo(itemId);
                if (itemMakeInfoResult.isEmpty()) {
                    log.error("Could not resolve item make info for item ID : {}", itemId);
                    user.write(MakerPacket.unknown());
                    return;
                }
                final ItemMakeInfo itemMakeInfo = itemMakeInfoResult.get();
                // Check requirements and validate
                if (!itemMakeInfo.canCreateItem(user, catalyst, gems)) {
                    user.write(MakerPacket.unknown());
                    return;
                }
                if (!itemMakeInfo.canAddReward(user)) {
                    user.write(MakerPacket.emptySlot()); // You don't have enough room in your Inventory.
                    return;
                }
                // Resolve reward item
                final int rewardItemId;
                final int rewardItemCount;
                if (itemMakeInfo.getRandomReward().isEmpty()) {
                    rewardItemId = itemMakeInfo.getItemId();
                    rewardItemCount = 1;
                } else {
                    final Optional<Triple<Integer, Integer, Integer>> rewardResult = Util.getRandomFromCollection(itemMakeInfo.getRandomReward(), Triple::getThird);
                    if (rewardResult.isEmpty()) {
                        log.error("Could not resolve maker random reward for item ID : {}", itemId);
                        user.write(MakerPacket.unknown());
                        return;
                    }
                    rewardItemId = rewardResult.get().getFirst();
                    rewardItemCount = rewardResult.get().getSecond();
                }
                final Optional<ItemInfo> rewardItemInfoResult = ItemProvider.getItemInfo(rewardItemId);
                if (rewardItemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", rewardItemId);
                    user.write(MakerPacket.unknown());
                    return;
                }
                // Deduct cost and items
                final InventoryManager im = user.getInventoryManager();
                final int totalCost = MakerConstants.getTotalCostToMake(itemMakeInfo.getCost(), catalyst, gems);
                if (!im.addMoney(-totalCost)) {
                    throw new IllegalStateException("Could not deduct total price from user");
                }
                final List<Tuple<Integer, Integer>> lostItems = new ArrayList<>(itemMakeInfo.getRecipe());
                if (catalyst) {
                    lostItems.add(Tuple.of(itemMakeInfo.getCatalyst(), 1));
                }
                for (int gemItemId : gems) {
                    lostItems.add(Tuple.of(gemItemId, 1));
                }
                final List<InventoryOperation> inventoryOperations = new ArrayList<>();
                for (var tuple : lostItems) {
                    final Optional<List<InventoryOperation>> removeResult = im.removeItem(tuple.getLeft(), tuple.getRight());
                    if (removeResult.isEmpty()) {
                        throw new IllegalStateException("Could not remove item from inventory");
                    }
                    inventoryOperations.addAll(removeResult.get());
                }
                // Add reward
                final boolean success = !catalyst || Util.succeedProp(90);
                if (success) {
                    final Item rewardItem = rewardItemInfoResult.get().createItem(user.getNextItemSn(), rewardItemCount, catalyst ? ItemVariationOption.NORMAL : ItemVariationOption.NONE);
                    if (rewardItem.getEquipData() != null) {
                        final EquipData originalEquipData = new EquipData(rewardItem.getEquipData());
                        for (int gemItemId : gems) {
                            final Optional<ItemInfo> gemItemInfoResult = ItemProvider.getItemInfo(gemItemId);
                            if (gemItemInfoResult.isEmpty()) {
                                log.error("Could not resolve item info for item ID : {}", gemItemId);
                                continue;
                            }
                            final ItemInfo gemItemInfo = gemItemInfoResult.get();
                            if (gemItemInfo.getInfo(ItemInfoType.randOption) != 0) {
                                // Black Crystal
                                final int randMax = gemItemInfo.getInfo(ItemInfoType.randOption);
                                final Map<ItemInfoType, Object> randStats = new EnumMap<>(ItemInfoType.class);
                                if (originalEquipData.getIncPad() > 0) {
                                    randStats.put(ItemInfoType.incPAD, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncMad() > 0) {
                                    randStats.put(ItemInfoType.incMAD, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncSpeed() > 0) {
                                    randStats.put(ItemInfoType.incSpeed, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncJump() > 0) {
                                    randStats.put(ItemInfoType.incJump, Util.getRandom(-randMax, randMax));
                                }
                                rewardItem.getEquipData().applyScrollStats(randStats);
                            } else if (gemItemInfo.getInfo(ItemInfoType.randStat) != 0) {
                                // Dark Crystal
                                final int randMax = gemItemInfo.getInfo(ItemInfoType.randStat);
                                final Map<ItemInfoType, Object> randStats = new EnumMap<>(ItemInfoType.class);
                                if (originalEquipData.getIncStr() > 0) {
                                    randStats.put(ItemInfoType.incSTR, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncDex() > 0) {
                                    randStats.put(ItemInfoType.incDEX, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncInt() > 0) {
                                    randStats.put(ItemInfoType.incINT, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncLuk() > 0) {
                                    randStats.put(ItemInfoType.incLUK, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncAcc() > 0) {
                                    randStats.put(ItemInfoType.incACC, Util.getRandom(-randMax, randMax));
                                }
                                if (originalEquipData.getIncEva() > 0) {
                                    randStats.put(ItemInfoType.incEVA, Util.getRandom(-randMax, randMax));
                                }
                                rewardItem.getEquipData().applyScrollStats(randStats);
                            } else {
                                // Other gems
                                rewardItem.getEquipData().applyScrollStats(gemItemInfo.getItemInfos());
                            }
                        }
                    }
                    final Optional<List<InventoryOperation>> addResult = im.addItem(rewardItem);
                    if (addResult.isEmpty()) {
                        throw new IllegalStateException("Could not add item to inventory");
                    }
                    inventoryOperations.addAll(addResult.get());
                }
                // Update client
                user.write(WvsContext.inventoryOperation(inventoryOperations, false));
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                user.write(MakerPacket.normal(success, rewardItemId, rewardItemCount, lostItems, totalCost));
                user.write(UserLocal.effect(Effect.itemMaker(success ? MakerResult.SUCCESS : MakerResult.DESTROYED)));
            }
            case MONSTER_CRYSTAL -> {
                final int itemId = inPacket.decodeInt(); // aRecipeSlot[0].pItem.p->nItemID
                final int trophyLevel = MakerConstants.getMonsterTrophyLevel(itemId);
                final int monsterCrystalId = MakerConstants.getMonsterCrystalByLevel(trophyLevel);
                if (monsterCrystalId == 0) {
                    user.write(MakerPacket.unknown());
                    return;
                }
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(monsterCrystalId);
                if (itemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", monsterCrystalId);
                    user.write(MakerPacket.unknown());
                    return;
                }
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddItem(monsterCrystalId, 1)) {
                    user.write(MakerPacket.emptySlot()); // You don't have enough room in your Inventory.
                    return;
                }
                final Optional<List<InventoryOperation>> removeItemResult = im.removeItem(itemId, 100);
                if (removeItemResult.isEmpty()) {
                    user.write(MakerPacket.unknown());
                    return;
                }
                final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), 1);
                final Optional<List<InventoryOperation>> addItemResult = im.addItem(item);
                if (addItemResult.isEmpty()) {
                    throw new IllegalStateException("Failed to add item to inventory");
                }
                user.write(WvsContext.inventoryOperation(removeItemResult.get(), false));
                user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
                user.write(MakerPacket.monsterCrystal(monsterCrystalId, itemId));
                user.write(UserLocal.effect(Effect.itemMaker(MakerResult.SUCCESS)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.itemMaker(MakerResult.SUCCESS)));
            }
            case EQUIP_DISASSEMBLE -> {
                final int itemId = inPacket.decodeInt(); // aRecipeSlot[0].pItem.p->nItemID
                final int inventoryType = inPacket.decodeInt(); // nTI_DisassbleItem
                final int slotPosition = inPacket.decodeInt(); // nSlotPosition_DisassbleItem
                if (!ItemConstants.isEquip(itemId) || InventoryType.getByValue(inventoryType) != InventoryType.EQUIP || slotPosition < 0) {
                    user.write(MakerPacket.unknown());
                    return;
                }
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
                if (itemInfoResult.isEmpty()) {
                    log.error("Could not resolve item info for item ID : {}", itemId);
                    user.write(MakerPacket.unknown());
                    return;
                }
                final ItemInfo itemInfo = itemInfoResult.get();
                final Optional<ItemMakeInfo> itemMakeInfoResult = EtcProvider.getItemMakeInfo(itemId);
                if (itemMakeInfoResult.isEmpty()) {
                    log.error("Could not resolve item make info for item ID : {}", itemId);
                    user.write(MakerPacket.unknown());
                    return;
                }
                final ItemMakeInfo itemMakeInfo = itemMakeInfoResult.get();
                final InventoryManager im = user.getInventoryManager();
                if (!im.canAddItems(itemMakeInfo.getRecipe())) {
                    user.write(MakerPacket.emptySlot()); // You don't have enough room in your Inventory.
                    return;
                }
                final Item item = im.getEquipInventory().getItem(slotPosition);
                if (item == null || item.getItemId() != itemId) {
                    log.error("Could not resolve item ID : {} for disassembly at position : {}", itemId, slotPosition);
                    user.write(MakerPacket.unknown());
                    return;
                }
                final int totalCost = MakerConstants.getTotalCostToDisassemble(itemMakeInfo.getCost(), itemInfo.calcEquipItemQuality(item));
                if (!im.canAddMoney(-totalCost)) {
                    user.write(MakerPacket.unknown());
                    return;
                }
                final List<Item> rewardItems = new ArrayList<>();
                for (var tuple : itemMakeInfo.getRecipe()) {
                    final int rewardItemId = tuple.getLeft();
                    final int rewardItemCount = tuple.getRight() / 2; // TODO : probably incorrect
                    if (rewardItemId / 10000 != 426 || rewardItemCount <= 0) {
                        continue;
                    }
                    final Optional<ItemInfo> rewardItemInfoResult = ItemProvider.getItemInfo(rewardItemId);
                    if (rewardItemInfoResult.isEmpty()) {
                        log.error("Could not resolve item info for item ID : {}", itemId);
                        user.write(MakerPacket.unknown());
                        return;
                    }
                    rewardItems.add(rewardItemInfoResult.get().createItem(user.getNextItemSn(), rewardItemCount));
                }
                if (!im.addMoney(-totalCost)) {
                    throw new IllegalStateException("Could not deduct total price from user");
                }
                final Optional<InventoryOperation> removeResult = im.removeItem(slotPosition, item);
                if (removeResult.isEmpty()) {
                    throw new IllegalStateException("Failed to remove item from inventory");
                }
                for (Item rewardItem : rewardItems) {
                    final Optional<List<InventoryOperation>> addResult = im.addItem(rewardItem);
                    if (addResult.isEmpty()) {
                        throw new IllegalStateException("Failed to add item to inventory");
                    }
                    user.write(WvsContext.inventoryOperation(addResult.get(), false));
                }
                user.write(WvsContext.inventoryOperation(removeResult.get(), false));
                user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
                user.write(MakerPacket.equipDisassemble(itemId, rewardItems, totalCost));
                user.write(UserLocal.effect(Effect.itemMaker(MakerResult.SUCCESS)));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.itemMaker(MakerResult.SUCCESS)));
            }
            case null -> {
                log.error("Unknown recipe class : {}", type);
            }
            default -> {
                log.error("Unhandled recipe class : {}", recipeClass);
            }
        }
    }


    // SOCIAL HANDLERS -------------------------------------------------------------------------------------------------

    @Handler(InHeader.GroupMessage)
    public static void handleGroupMessage(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int type = inPacket.decodeByte(); // nChatTarget
        final ChatGroupType groupType = ChatGroupType.getByValue(type);
        if (groupType == null) {
            log.error("Unknown chat group type : {}", type);
            return;
        }
        final List<Integer> targetIds = new ArrayList<>();
        final int count = inPacket.decodeByte(); // nMemberCnt
        for (int i = 0; i < count; i++) {
            targetIds.add(inPacket.decodeInt());
        }
        final String text = inPacket.decodeString(); // sText
        if (text.startsWith(ServerConfig.PLAYER_COMMAND_PREFIX) && text.length() > 1) {
            CommandProcessor.tryProcessCommand(user, text);
            return;
        }
        user.getConnectedServer().submitUserPacketBroadcast(targetIds, FieldPacket.groupMessage(groupType, user.getCharacterName(), text));
    }

    @Handler(InHeader.Whisper)
    public static void handleWhisper(User user, InPacket inPacket) {
        final int flag = inPacket.decodeByte();
        inPacket.decodeInt(); // update_time
        final WhisperFlag whisperFlag = WhisperFlag.getByValue(flag);
        switch (whisperFlag) {
            case LocationRequest, LocationRequest_F -> {
                final String targetName = inPacket.decodeString();
                // Query target user
                user.getConnectedServer().submitUserQueryRequest(List.of(targetName), (queryResult) -> {
                    final Optional<RemoteUser> targetResult = queryResult.stream().findFirst();
                    if (targetResult.isEmpty()) {
                        user.write(WhisperPacket.locationResultNone(targetName));
                        return;
                    }
                    final RemoteUser target = targetResult.get();
                    if (target.getChannelId() == user.getChannelId()) {
                        user.write(WhisperPacket.locationResultSameChannel(targetName, whisperFlag == WhisperFlag.LocationRequest_F, target.getFieldId()));
                    } else {
                        user.write(WhisperPacket.locationResultOtherChannel(targetName, whisperFlag == WhisperFlag.LocationRequest_F, target.getChannelId()));
                    }
                });
            }
            case WhisperRequest, WhisperRequestmanager -> {
                final String targetName = inPacket.decodeString();
                final String message = inPacket.decodeString();
                // Query target user
                user.getConnectedServer().submitUserQueryRequest(List.of(targetName), (queryResult) -> {
                    if (queryResult.isEmpty()) {
                        user.write(WhisperPacket.whisperResult(targetName, false));
                        return;
                    }
                    // Write packet to target user
                    user.getConnectedServer().submitUserPacketRequest(targetName, WhisperPacket.whisperReceive(user.getChannelId(), user.getCharacterName(), message));
                    user.write(WhisperPacket.whisperResult(targetName, true));
                });
            }
            case WhisperBlocked -> {
                final String targetName = inPacket.decodeString();
                user.getConnectedServer().submitUserPacketRequest(targetName, WhisperPacket.whisperBlocked(user.getCharacterName()));
            }
            case null -> {
                log.error("Unknown whisper flag : {}", flag);
            }
            default -> {
                log.error("Unhandled whisper flag : {}", whisperFlag);
            }
        }
    }

    @Handler(InHeader.Messenger)
    public static void handleMessenger(User user, InPacket inPacket) {
        final int action = inPacket.decodeByte();
        final MessengerProtocol msmp = MessengerProtocol.getByValue(action);
        switch (msmp) {
            case MSMP_Enter -> {
                // CUIMessenger::OnCreate
                final int messengerId = inPacket.decodeInt(); // pData (dwJoinSN)
                user.getConnectedServer().submitMessengerRequest(user, MessengerRequest.enter(messengerId, user));
            }
            case MSMP_Leave -> {
                // CUIMessenger::OnDestroy
                user.getConnectedServer().submitMessengerRequest(user, MessengerRequest.leave());
            }
            case MSMP_Invite -> {
                // CUIMessenger::SendInviteMsg
                if (user.getMessengerId() == 0) {
                    log.error("Tried to send messenger invite without an associated messenger ID");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                    return;
                }
                final String targetName = inPacket.decodeString(); // sTarget
                // Query target user
                user.getConnectedServer().submitUserQueryRequest(List.of(targetName), (queryResult) -> {
                    final Optional<RemoteUser> targetResult = queryResult.stream().findFirst();
                    if (targetResult.isEmpty()) {
                        user.write(MessengerPacket.inviteResult(targetName, false));
                        return;
                    }
                    user.getConnectedServer().submitUserPacketReceive(targetResult.get().getCharacterId(), MessengerPacket.invite(user, user.getMessengerId()));
                    user.write(MessengerPacket.inviteResult(targetName, true));
                });
            }
            case MSMP_Blocked -> {
                // CUIMessenger::OnInvite
                final String inviterName = inPacket.decodeString(); // sInviter
                inPacket.decodeString(); // sCharacterName
                final boolean blocked = inPacket.decodeBoolean(); // hardcoded 1
                user.getConnectedServer().submitUserPacketRequest(inviterName, MessengerPacket.blocked(user.getCharacterName(), blocked));
            }
            case MSMP_Chat -> {
                // CUIMessenger::ProcessChat | CUIMessenger::Update
                final String message = inPacket.decodeString();
                user.getConnectedServer().submitMessengerRequest(user, MessengerRequest.chat(message));
            }
            case null -> {
                log.error("Unknown messenger action {}", action);
            }
            default -> {
                log.error("Unhandled messenger action {}", action);
            }
        }
    }

    @Handler(InHeader.MiniRoom)
    public static void handleMiniRoom(User user, InPacket inPacket) {
        final int action = inPacket.decodeByte();
        final MiniRoomProtocol mrp = MiniRoomProtocol.getByValue(action);
        if (mrp == null) {
            log.error("Unknown mini room action {}", action);
            return;
        }
        // TradingRoom Protocol
        if (mrp.getValue() >= MiniRoomProtocol.TRP_PutItem.getValue() && mrp.getValue() <= MiniRoomProtocol.TRP_LimitFail.getValue()) {
            if (!(user.getDialog() instanceof TradingRoom tradingRoom)) {
                log.error("Received trading room action {} outside a trading room", mrp);
                return;
            }
            tradingRoom.handlePacket(user, mrp, inPacket);
            return;
        }
        // MiniGameRoom Protocol
        if (mrp.getValue() >= MiniRoomProtocol.MGRP_TieRequest.getValue() && mrp.getValue() <= MiniRoomProtocol.MGP_MatchCard.getValue()) {
            if (!(user.getDialog() instanceof MiniGameRoom miniGameRoom)) {
                log.error("Received mini game room action {} outside a mini game room", mrp);
                return;
            }
            miniGameRoom.handlePacket(user, mrp, inPacket);
            return;
        }
        // PersonalShop Protocol
        if (mrp.getValue() >= MiniRoomProtocol.PSP_PutItem.getValue() && mrp.getValue() <= MiniRoomProtocol.PSP_DeleteBlackList.getValue()) {
            if (!(user.getDialog() instanceof PersonalShop personalShop)) {
                log.error("Received personal shop action {} outside a personal shop", mrp);
                return;
            }
            personalShop.handlePacket(user, mrp, inPacket);
            return;
        }
        // Common MiniRoom Protocol
        final Field field = user.getField();
        switch (mrp) {
            case MRP_Create -> {
                final int type = inPacket.decodeByte();
                final MiniRoomType mrt = MiniRoomType.getByValue(type);
                if (user.getDialog() != null) {
                    log.error("Tried to create mini room with another dialog open");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                    return;
                }
                if (!field.getMiniRoomPool().canAddMiniRoom(mrt, user.getX(), user.getY())) {
                    user.write(MiniRoomPacket.enterResult(EnterResultType.ExistMiniRoom)); // You can't establish a miniroom right here.
                    return;
                }
                switch (mrt) {
                    case OmokRoom, MemoryGameRoom -> {
                        // CWvsContext::SendCreateMiniGameRequest
                        final String title = inPacket.decodeString(); // sTitle
                        final boolean isPrivate = inPacket.decodeBoolean();
                        final String password = isPrivate ? inPacket.decodeString() : null;
                        final int gameSpec = inPacket.decodeByte(); // nGameSpec
                        // Check for required item
                        if (mrt == MiniRoomType.OmokRoom) {
                            final int requiredItem = ItemConstants.OMOK_SET_BASE + gameSpec;
                            if (requiredItem < ItemConstants.OMOK_SET_BASE || requiredItem > ItemConstants.OMOK_SET_END ||
                                    !user.getInventoryManager().hasItem(requiredItem, 1)) {
                                log.error("Tried to create omok game room without the required item");
                                return;
                            }
                        } else {
                            if (!user.getInventoryManager().hasItem(ItemConstants.MATCH_CARDS, 1)) {
                                log.error("Tried to create memory game room without the required item");
                                return;
                            }
                        }
                        // Create mini game room
                        final MiniGameRoom miniGameRoom = mrt == MiniRoomType.OmokRoom ?
                                new OmokRoom(title, password, gameSpec) :
                                new MemoryGameRoom(title, password, gameSpec);
                        miniGameRoom.addUser(0, user);
                        field.getMiniRoomPool().addMiniRoom(miniGameRoom);
                        user.setDialog(miniGameRoom);
                        user.write(MiniRoomPacket.MiniGame.enterResult(miniGameRoom, user));
                        miniGameRoom.updateBalloon();
                    }
                    case TradingRoom -> {
                        // CField::SendInviteTradingRoomMsg
                        final TradingRoom tradingRoom = new TradingRoom();
                        tradingRoom.addUser(0, user);
                        field.getMiniRoomPool().addMiniRoom(tradingRoom);
                        user.setDialog(tradingRoom);
                        user.write(MiniRoomPacket.enterResult(tradingRoom, user));
                    }
                    case PersonalShop, EntrustedShop -> {
                        // CWvsContext::SendOpenShopRequest
                        final String title = inPacket.decodeString(); // sTitle
                        inPacket.decodeByte(); // 0
                        final int position = inPacket.decodeShort(); // nPOS
                        final int itemId = inPacket.decodeInt(); // nItemID
                        // Check field
                        if (!field.getMapInfo().isShop()) {
                            log.error("Tried to create player shop outside of the free market");
                            return;
                        }
                        // Check for required item
                        if (mrt == MiniRoomType.PersonalShop) {
                            if (itemId != ItemConstants.REGULAR_STORE_PERMIT || !user.getInventoryManager().hasItem(itemId, 1)) {
                                log.error("Tried to create personal shop without the required item");
                                return;
                            }
                            // Create personal shop
                            final PersonalShop personalShop = new PersonalShop(title);
                            personalShop.addUser(0, user);
                            field.getMiniRoomPool().addMiniRoom(personalShop);
                            user.setDialog(personalShop);
                            user.write(MiniRoomPacket.PlayerShop.enterResult(personalShop, user));
                        } else {
                            if (itemId / 10000 != 503 || !user.getInventoryManager().hasItem(itemId, 1)) {
                                log.error("Tried to create entrusted shop without the required item");
                            }
                            // TODO: entrusted shop handling
                        }
                    }
                    case null -> {
                        log.error("Tried to create unknown mini room type {}", type);
                    }
                    default -> {
                        log.error("Tried to create unhandled mini room type {}", mrt);
                    }
                }
            }
            case MRP_Invite -> {
                // CField::SendInviteTradingRoomMsg
                final int targetId = inPacket.decodeInt();
                if (!(user.getDialog() instanceof TradingRoom tradingRoom)) {
                    log.error("Tried to invite user without a trading room");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                    return;
                }
                final Optional<User> targetResult = field.getUserPool().getById(targetId);
                if (targetResult.isEmpty()) {
                    user.write(MiniRoomPacket.inviteResult(MiniRoomInviteType.NoCharacter, null)); // Unable to find the character.
                    tradingRoom.cancelTrade(user, MiniRoomLeaveType.UserRequest);
                    return;
                }
                final User target = targetResult.get();
                if (target.getDialog() != null) {
                    user.write(MiniRoomPacket.inviteResult(MiniRoomInviteType.CannotInvite, target.getCharacterName())); // '%s' is doing something else right now.
                    tradingRoom.cancelTrade(user, MiniRoomLeaveType.UserRequest);
                    return;
                }
                target.write(MiniRoomPacket.inviteStatic(MiniRoomType.TradingRoom, user.getCharacterName(), tradingRoom.getId()));
            }
            case MRP_InviteResult -> {
                // CMiniRoomBaseDlg::SendInviteResult
                final int miniRoomId = inPacket.decodeInt(); // dwSN
                final int type = inPacket.decodeByte(); // nErrCode
                final MiniRoomInviteType resultType = MiniRoomInviteType.getByValue(type);
                if (resultType == null) {
                    log.error("Unknown invite result type {}", type);
                    return;
                }
                // Resolve trading room
                final Optional<MiniRoom> miniRoomResult = field.getMiniRoomPool().getById(miniRoomId);
                if (miniRoomResult.isEmpty() || !(miniRoomResult.get() instanceof TradingRoom tradingRoom)) {
                    return;
                }
                // Cancel trade
                final User owner = tradingRoom.getUser(0);
                owner.write(MiniRoomPacket.inviteResult(resultType, user.getCharacterName()));
                tradingRoom.cancelTrade(owner, MiniRoomLeaveType.UserRequest);
            }
            case MRP_Enter -> {
                // CMiniRoomBaseDlg::SendInviteResult
                // CUserLocal::HandleLButtonDblClk
                final int miniRoomId = inPacket.decodeInt(); // dwSN
                final boolean isPrivate = inPacket.decodeBoolean();
                final String password = isPrivate ? inPacket.decodeString() : null;
                inPacket.decodeByte(); // 0
                if (user.getDialog() != null) {
                    log.error("Tried to enter mini room with another dialog open");
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                    return;
                }
                // Resolve mini room
                final Optional<MiniRoom> miniRoomResult = field.getMiniRoomPool().getById(miniRoomId);
                if (miniRoomResult.isEmpty()) {
                    user.write(MiniRoomPacket.enterResult(EnterResultType.NoRoom)); // The room is already closed.
                    return;
                }
                final MiniRoom miniRoom = miniRoomResult.get();
                // Check password
                if (!miniRoom.checkPassword(password)) {
                    user.write(MiniRoomPacket.enterResult(EnterResultType.InvalidPassword)); // The password is incorrect.
                    return;
                }
                // Handle for each mini room type
                if (miniRoom instanceof MiniGameRoom miniGameRoom) {
                    if (miniGameRoom.getUser(1) != null) {
                        user.write(MiniRoomPacket.enterResult(EnterResultType.Full)); // You can't enter the room due to full capacity.
                        return;
                    }
                    miniGameRoom.broadcastPacket(MiniRoomPacket.MiniGame.enter(1, user, miniGameRoom.getType()));
                    miniGameRoom.addUser(1, user);
                    miniGameRoom.updateBalloon();
                    user.setDialog(miniGameRoom);
                    user.write(MiniRoomPacket.MiniGame.enterResult(miniGameRoom, user));
                } else if (miniRoom instanceof TradingRoom tradingRoom) {
                    if (tradingRoom.getUser(1) != null) {
                        user.write(MiniRoomPacket.enterResult(EnterResultType.Full)); // You can't enter the room due to full capacity.
                        return;
                    }
                    tradingRoom.broadcastPacket(MiniRoomPacket.enterBase(1, user));
                    tradingRoom.addUser(1, user);
                    user.setDialog(tradingRoom);
                    user.write(MiniRoomPacket.enterResult(tradingRoom, user));
                } else if (miniRoom instanceof PersonalShop personalShop) {
                    final int userIndex = personalShop.getOpenUserIndex();
                    if (!personalShop.isOpen() || userIndex < 0) {
                        user.write(MiniRoomPacket.enterResult(EnterResultType.Full)); // You can't enter the room due to full capacity.
                        return;
                    }
                    personalShop.broadcastPacket(MiniRoomPacket.enterBase(userIndex, user));
                    personalShop.addUser(userIndex, user);
                    personalShop.updateBalloon();
                    user.setDialog(personalShop);
                    user.write(MiniRoomPacket.PlayerShop.enterResult(personalShop, user));
                } else {
                    log.error("Tried to enter mini room with unhandled type : {}", miniRoom.getType());
                    user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                }
            }
            case MRP_Chat -> {
                // CMiniRoomBaseDlg::CheckAndSendChat
                inPacket.decodeInt(); // update_time
                final String message = inPacket.decodeString(); // strChatMsg
                if (!(user.getDialog() instanceof MiniRoom miniRoom)) {
                    log.error("Received {} without a mini room", mrp);
                    return;
                }
                final int userIndex = miniRoom.getUserIndex(user);
                if (userIndex < 0) {
                    log.error("Received {} with user index", userIndex);
                    return;
                }
                miniRoom.broadcastPacket(MiniRoomPacket.chat(userIndex, user.getCharacterName(), message));
            }
            case MRP_Leave -> {
                if (!(user.getDialog() instanceof MiniRoom miniRoom)) {
                    log.error("Received {} without a mini room", mrp);
                    return;
                }
                final int userIndex = miniRoom.getUserIndex(user);
                if (userIndex < 0) {
                    log.error("Received {} with user index", userIndex);
                    return;
                }
                miniRoom.leave(user);
            }
            case MRP_Balloon -> {
                final boolean open = inPacket.decodeBoolean();
                if (!(user.getDialog() instanceof MiniRoom miniRoom)) {
                    log.error("Received {} without a mini room", mrp);
                    return;
                }
                if (miniRoom instanceof PersonalShop personalShop) {
                    personalShop.setOpen(open);
                    personalShop.updateBalloon();
                } else {
                    log.error("Received {} for unhandled mini room type {}", mrp, miniRoom.getType());
                }
            }
            default -> {
                log.error("Unhandled mini room action {}", mrp);
            }
        }
    }

    @Handler(InHeader.MemoRequest)
    public static void handleMemoRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final MemoRequestType requestType = MemoRequestType.getByValue(type);
        switch (requestType) {
            case Send -> {
                // CCashShop::OnCashItemResLoadGiftDone
                final String receiverName = inPacket.decodeString();
                final String message = inPacket.decodeString(); // sMsg
                final int flag = inPacket.decodeByte(); // nFlag
                final int index = inPacket.decodeInt(); // nIdx
                final long itemSn = inPacket.decodeLong(); // GW_GiftList->liSN
                final MemoType memoType = MemoType.getByValue(flag);
                if (memoType == null) {
                    log.error("Tried to send memo with unknown type : {}", flag);
                    user.dispose();
                    return;
                }
                // Resolve gift and confirm receiver
                final Optional<Gift> giftResult = DatabaseManager.giftAccessor().getGiftByItemSn(itemSn);
                if (giftResult.isEmpty()) {
                    log.error("Tried to send gift receipt memo for gift with item sn : {}", itemSn);
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                final Gift gift = giftResult.get();
                if (!receiverName.equalsIgnoreCase(gift.getSenderName())) {
                    log.error("Tried to send gift receipt memo with mismatching sender name - expected : {}, actual : {}", gift.getSenderName(), receiverName);
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                // Resolve commodity
                final Optional<Commodity> commodityResult = CashShop.getCommodity(gift.getCommodityId());
                if (commodityResult.isEmpty()) {
                    log.error("Failed to resolve gift commodity ID : {}", gift.getCommodityId());
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                final Optional<Tuple<Commodity, List<Commodity>>> packageResult = CashShop.getCashPackage(gift.getCommodityId());
                final int commodityCount = packageResult.map(tuple -> tuple.getRight().size()).orElse(1);
                // Receive gift
                final Locker locker = user.getAccount().getLocker();
                if (locker.getRemaining() < commodityCount) {
                    user.write(BroadcastPacket.alert("Could not receive gift as the locker is full."));
                    return;
                }
                // Create CashItemInfo(s)
                final List<CashItemInfo> cashItemInfos = new ArrayList<>();
                if (packageResult.isPresent()) {
                    // Cash package
                    for (Commodity commodity : packageResult.get().getRight()) {
                        final Optional<CashItemInfo> cashItemInfoResult = commodity.createCashItemInfo(gift.getGiftSn(), user.getAccountId(), user.getCharacterId(), gift.getSenderName());
                        if (cashItemInfoResult.isEmpty()) {
                            log.error("Failed to create cash item info for gift commodity ID : {}", commodity.getCommodityId());
                            user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                            return;
                        }
                        cashItemInfos.add(cashItemInfoResult.get());
                    }
                } else {
                    // Normal gift
                    final Optional<CashItemInfo> cashItemInfoResult = commodityResult.get().createCashItemInfo(gift.getGiftSn(), user.getAccountId(), user.getCharacterId(), gift.getSenderName());
                    if (cashItemInfoResult.isEmpty()) {
                        log.error("Failed to create cash item info for gift commodity ID : {}", gift.getCommodityId());
                        user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                        return;
                    }
                    final CashItemInfo cashItemInfo = cashItemInfoResult.get();
                    // Create RingData if pairItemSn was set
                    if (gift.getPairItemSn() != 0) {
                        final RingData ringData = new RingData();
                        ringData.setPairCharacterId(gift.getSenderId());
                        ringData.setPairCharacterName(gift.getSenderName());
                        ringData.setPairItemSn(gift.getPairItemSn());
                        cashItemInfo.getItem().setRingData(ringData);
                    }
                    cashItemInfos.add(cashItemInfo);
                }
                // Delete gift from DB and add to locker
                if (!DatabaseManager.giftAccessor().deleteGift(gift)) {
                    log.error("Failed to delete gift with sn : {}", gift.getGiftSn());
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                for (CashItemInfo cashItemInfo : cashItemInfos) {
                    locker.addCashItem(cashItemInfo);
                }
                user.write(CashShopPacket.loadLockerDone(user.getAccount()));

                // Resolve receiver
                final Optional<CharacterInfo> receiverIdResult = DatabaseManager.characterAccessor().getCharacterInfoByName(receiverName);
                if (receiverIdResult.isEmpty()) {
                    user.write(MemoPacket.sendWarningName()); // Please check the name of the receiving character.
                    return;
                }
                final int receiverCharacterId = receiverIdResult.get().getCharacterId();
                // Create memo
                final Optional<Integer> memoIdResult = DatabaseManager.idAccessor().nextMemoId();
                if (memoIdResult.isEmpty()) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                    return;
                }
                final Memo memo = new Memo(
                        memoType,
                        memoIdResult.get(),
                        user.getCharacterName(),
                        message,
                        Instant.now()
                );
                // Save memo
                if (!DatabaseManager.memoAccessor().newMemo(memo, receiverCharacterId)) {
                    user.write(CashShopPacket.fail(CashItemResultType.Gift_Failed, CashItemFailReason.Unknown)); // Due to an unknown error, the request for Cash Shop has failed.
                }
                // user.write(MemoPacket.sendSucceed()); // memo result not required
                // Notify memo recipient
                user.getConnectedServer().submitUserPacketReceive(receiverCharacterId, MemoPacket.receive());
            }
            case Delete -> {
                // CMemoListDlg::SetRet
                final int size = inPacket.decodeByte(); // size
                inPacket.decodeByte(); // number of memos where nFlag == 3 (INVITATION) -> number of slots required
                inPacket.decodeByte(); // nEmptySlotCount
                for (int i = 0; i < size; i++) {
                    final int memoId = inPacket.decodeInt(); // dwSN
                    final int flag = inPacket.decodeByte(); // nFlag
                    final MemoType memoType = MemoType.getByValue(flag);
                    if (!DatabaseManager.memoAccessor().deleteMemo(memoId, user.getCharacterId())) {
                        log.error("Failed to delete memo {} from database", memoId);
                        return;
                    }
                    if (memoType == MemoType.INVITATION) {
                        final int marriageId = inPacket.decodeInt(); // atoi(strMarriageNo)
                        log.error("Unhandled Marriage invitation memo for marriage ID : {}", marriageId);
                    } else if (memoType == MemoType.INCPOP) {
                        user.addPop(1);
                        user.write(MessagePacket.incPop(1));
                    }
                }
            }
            case Load -> {
                // CWvsContext::OnMemoNotify_Receive
                final List<Memo> memos = DatabaseManager.memoAccessor().getMemosByCharacterId(user.getCharacterId());
                user.write(MemoPacket.load(memos));
            }
            case null -> {
                log.error("Unknown memo request type : {}", type);
            }
            default -> {
                log.error("Unhandled memo request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.EnterTownPortalRequest)
    public static void handleEnterTownPortalRequest(User user, InPacket inPacket) {
        final int ownerId = inPacket.decodeInt(); // dwCharacterId
        inPacket.decodeBoolean();
        final Optional<TownPortal> townPortalResult = user.getField().getTownPortalPool().getById(ownerId);
        if (townPortalResult.isEmpty()) {
            log.error("Tried to enter unknown town portal id : {}", ownerId);
            user.dispose();
            return;
        }
        final TownPortal townPortal = townPortalResult.get();
        final int townPortalId = 0x80 | townPortal.getOwner().getTownPortalIndex(); // CUserLocal::Init
        if (townPortal.getTownField() == user.getField()) {
            user.warp(townPortal.getField(), townPortal.getX(), townPortal.getY(), townPortalId, false, false);
        } else {
            final PortalInfo portalInfo = townPortal.getTownPortalPoint().orElse(PortalInfo.EMPTY);
            user.warp(townPortal.getTownField(), portalInfo.getX(), portalInfo.getY(), townPortalId, false, false);
        }
    }

    @Handler(InHeader.EnterOpenGateRequest)
    public static void handleEnterOpenGateRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // dwCharacterID
        inPacket.decodeShort(); // p->x
        inPacket.decodeShort(); // p->y
        inPacket.decodeByte(); // isFirst?

        user.dispose(); // CWvsContext::OnOpenGate does nothing, just dispose
    }

    @Handler(InHeader.FuncKeyMappedModified)
    public static void handleFuncKeyMappedModified(User user, InPacket inPacket) {
        final int type = inPacket.decodeInt();
        final FuncKeyMappedType funcKeyMappedType = FuncKeyMappedType.getByValue(type);
        final ConfigManager cm = user.getConfigManager();
        switch (funcKeyMappedType) {
            case KeyModified -> {
                final int size = inPacket.decodeInt(); // *(anChangedIdx.a - 1)
                final Map<Integer, FuncKeyMapped> updates = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    final int index = inPacket.decodeInt();

                    // FUNCKEY_MAPPED::Encode
                    final int funcKeyValue = inPacket.decodeByte(); // nType
                    final int funcKeyId = inPacket.decodeInt(); // nID
                    // ~FUNCKEY_MAPPED::Encode

                    final FuncKeyType funcKeyType = FuncKeyType.getByValue(funcKeyValue);
                    if (funcKeyType == null) {
                        log.error("Received unknown func key type {}", funcKeyValue);
                        return;
                    }
                    updates.put(index, FuncKeyMapped.of(funcKeyType, funcKeyId));
                }
                cm.updateFuncKeyMap(updates);
            }
            case PetConsumeItemModified -> {
                final int itemId = inPacket.decodeInt(); // nPetConsumeItemID
                cm.setPetConsumeItem(itemId);
            }
            case PetConsumeMPItemModified -> {
                final int itemId = inPacket.decodeInt(); // nPetConsumeMPItemID
                cm.setPetConsumeMpItem(itemId);
            }
            case null -> {
                log.error("Received unknown type {} for FuncKeyMappedModified", type);
            }
            default -> {
                log.error("Unhandled func key mapped type : {}", funcKeyMappedType);
            }
        }
    }

    @Handler(InHeader.TalkToTutor)
    public static void handleTalkToTutor(User user, InPacket inPacket) {
        if (JobConstants.isCygnusJob(user.getJob())) {
            ScriptDispatcher.startNpcScript(user, user, CygnusTutorial.TALK_TO_TUTOR_SCRIPT, 1101008);
        } else {
            ScriptDispatcher.startNpcScript(user, user, AranTutorial.TALK_TO_TUTOR_SCRIPT, 1202000);
        }
    }

    @Handler(InHeader.RequestIncCombo)
    public static void handleRequestIncCombo(User user, InPacket inPacket) {
        // combo handled in AttackHandler
    }

    @Handler(InHeader.UpdateGMBoard)
    public static void handleUpdateGmBoard(User user, InPacket inPacket) {
        inPacket.decodeInt(); // nGameOpt_OpBoardIndex
    }

    @Handler(InHeader.DragonMove)
    public static void handleDragonMove(User user, InPacket inPacket) {
        final MovePath movePath = MovePath.decode(inPacket);
        user.getField().broadcastPacket(DragonPacket.dragonMove(user, movePath), user);
    }

    @Handler(InHeader.QuickslotKeyMappedModified)
    public static void handleQuickslotKeyMappedModified(User user, InPacket inPacket) {
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_MAP_SIZE];
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyMap[i] = inPacket.decodeInt();
        }
        final ConfigManager cm = user.getConfigManager();
        cm.updateQuickslotKeyMap(quickslotKeyMap);
    }

    @Handler(InHeader.PassiveskillInfoUpdate)
    public static void handlePassiveSkillInfoUpdate(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        user.updatePassiveSkillData();
        user.validateStat();
    }

    @Handler(InHeader.UpdateScreenSetting)
    public static void handleUpdateScreenSetting(User user, InPacket inPacket) {
        inPacket.decodeByte(); // bSysOpt_LargeScreen
        inPacket.decodeByte(); // bSysOpt_WindowedMode
    }
}
