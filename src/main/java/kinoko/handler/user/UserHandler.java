package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.user.ChatType;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserPacket;
import kinoko.packet.user.UserRemote;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.provider.ItemProvider;
import kinoko.provider.QuestProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.ServerConfig;
import kinoko.server.command.CommandProcessor;
import kinoko.server.dialog.shop.ShopDialog;
import kinoko.server.dialog.trunk.TrunkDialog;
import kinoko.server.dialog.trunk.TrunkResult;
import kinoko.server.header.InHeader;
import kinoko.server.memo.MemoRequestType;
import kinoko.server.memo.MemoType;
import kinoko.server.packet.InPacket;
import kinoko.server.script.NpcScriptManager;
import kinoko.server.script.ScriptAnswer;
import kinoko.server.script.ScriptDispatcher;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.npc.Npc;
import kinoko.world.friend.FriendRequestType;
import kinoko.world.item.*;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestRequestType;
import kinoko.world.quest.QuestResult;
import kinoko.world.user.User;
import kinoko.world.user.funckey.FuncKeyManager;
import kinoko.world.user.funckey.FuncKeyMapped;
import kinoko.world.user.funckey.FuncKeyMappedType;
import kinoko.world.user.funckey.FuncKeyType;
import kinoko.world.user.stat.CharacterStat;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(User user, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(user);
        user.getField().broadcastPacket(UserRemote.move(user, movePath), user);
    }

    @Handler(InHeader.USER_SIT_REQUEST)
    public static void handleUserSitRequest(User user, InPacket inPacket) {
        // CUserLocal::HandleXKeyDown, CWvsContext::SendGetUpFromChairRequest
        final short fieldSeatId = inPacket.decodeShort();
        user.setPortableChairId(0);
        user.write(UserLocal.sitResult(fieldSeatId != -1, fieldSeatId)); // broadcast not required
    }

    @Handler(InHeader.USER_PORTABLE_CHAIR_SIT_REQUEST)
    public static void handleUserPortableChairSitRequest(User user, InPacket inPacket) {
        // CWvsContext::SendSitOnPortableChairRequest
        final int itemId = inPacket.decodeInt(); // nItemID
        if (!ItemConstants.isPortableChairItem(itemId)) {
            log.error("Received USER_PORTABLE_CHAIR_SIT_REQUEST with a non-portable chair item ID : {}", itemId);
            user.dispose();
            return;
        }
        user.setPortableChairId(itemId);
        user.getField().broadcastPacket(UserRemote.setActivePortableChair(user, itemId), user); // self-cast not required
        user.dispose();
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon
        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(user, text)) {
            return;
        }
        user.getField().broadcastPacket(UserPacket.chat(user, ChatType.NORMAL, text, onlyBalloon));
    }

    @Handler(InHeader.USER_EMOTION)
    public static void handleUserEmotion(User user, InPacket inPacket) {
        final int emotion = inPacket.decodeInt(); // nEmotion
        final int duration = inPacket.decodeInt(); // nDuration
        final boolean isByItemOption = inPacket.decodeBoolean(); // bByItemOption
        user.getField().broadcastPacket(UserRemote.emotion(user, emotion, duration, isByItemOption), user);
    }


    // NPC HANDLERS ----------------------------------------------------------------------------------------------------

    @Handler(InHeader.USER_SELECT_NPC)
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
            ScriptDispatcher.startNpcScript(user, npc.getTemplateId(), npc.getScript());
            return;
        }
        // Handle trunk / npc shop dialog, lock user
        try (var locked = user.acquire()) {
            if (user.hasDialog()) {
                log.error("Tried to select npc ID {}, while already in a dialog", npc.getTemplateId());
                return;
            }
            if (npc.isTrunk()) {
                final TrunkDialog trunkDialog = TrunkDialog.from(npc);
                user.setDialog(trunkDialog);
                // Lock account to access trunk
                try (var lockedAccount = user.getAccount().acquire()) {
                    user.write(FieldPacket.trunkResult(TrunkResult.open(lockedAccount.get().getTrunk(), npc.getTemplateId())));
                }
            } else {
                final ShopDialog shopDialog = ShopDialog.from(npc);
                user.setDialog(shopDialog);
                user.write(FieldPacket.openShopDlg(shopDialog));
            }
        }
    }

    @Handler(InHeader.USER_SCRIPT_MESSAGE_ANSWER)
    public static void handleUserScriptMessageAnswer(User user, InPacket inPacket) {
        final byte type = inPacket.decodeByte(); // nMsgType
        final byte action = inPacket.decodeByte();
        final ScriptMessageType lastMessageType = ScriptMessageType.getByValue(type);
        if (lastMessageType == null) {
            log.error("Unknown script message type {}", type);
            return;
        }
        final Optional<NpcScriptManager> scriptManagerResult = ScriptDispatcher.getNpcScriptManager(user);
        if (scriptManagerResult.isEmpty()) {
            log.error("Could not retrieve ScriptManager instance for character ID : {}", user.getCharacterId());
            return;
        }
        final NpcScriptManager scriptManager = scriptManagerResult.get();
        switch (lastMessageType) {
            case SAY, SAY_IMAGE, ASK_YES_NO, ASK_ACCEPT -> {
                scriptManager.submitAnswer(ScriptAnswer.withAction(action));
            }
            case ASK_TEXT, ASK_BOX_TEXT -> {
                if (action == 1) {
                    final String answer = inPacket.decodeString(); // sInputStr_Result
                    scriptManager.submitAnswer(ScriptAnswer.withTextAnswer(action, answer));
                } else {
                    scriptManager.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            case ASK_NUMBER, ASK_MENU, ASK_SLIDE_MENU -> {
                if (action == 1) {
                    final int answer = inPacket.decodeInt(); // nInputNo_Result | nSelect
                    scriptManager.submitAnswer(ScriptAnswer.withAnswer(action, answer));
                } else {
                    scriptManager.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            case ASK_AVATAR, ASK_MEMBER_SHOP_AVATAR -> {
                if (action == 1) {
                    final byte answer = inPacket.decodeByte(); // nAvatarIndex
                    scriptManager.submitAnswer(ScriptAnswer.withAnswer(action, answer));
                } else {
                    scriptManager.submitAnswer(ScriptAnswer.withAction(-1));
                }
            }
            default -> {
                log.error("Unhandled script message type {}", lastMessageType);
            }
        }
    }

    @Handler(InHeader.USER_SHOP_REQUEST)
    public static void handleUserShopRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            if (!(user.getDialog() instanceof ShopDialog shopDialog)) {
                log.error("Received USER_SHOP_REQUEST without associated shop dialog");
                return;
            }
            shopDialog.onPacket(locked, inPacket);
        }
    }

    @Handler(InHeader.USER_TRUNK_REQUEST)
    public static void handleUserTrunkRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            if (!(user.getDialog() instanceof TrunkDialog trunkDialog)) {
                log.error("Received USER_TRUNK_REQUEST without associated trunk dialog");
                return;
            }
            trunkDialog.onPacket(locked, inPacket);
        }
    }


    // INVENTORY HANDLERS ----------------------------------------------------------------------------------------------

    @Handler(InHeader.USER_GATHER_ITEM_REQUEST)
    public static void handlerUserGatherItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            final Inventory inventory = im.getInventoryByType(inventoryType);
            // Find stackable items : itemId -> Set<Tuple<position, item>>
            final Map<Integer, Set<Tuple<Integer, Item>>> stackable = new HashMap<>();
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
                    stackable.put(item.getItemId(), new HashSet<>());
                }
                stackable.get(item.getItemId()).add(new Tuple<>(position, item));
            }
            // Get required inventory operations
            final List<InventoryOperation> inventoryOperations = new ArrayList<>();
            for (var entry : stackable.entrySet()) {
                if (entry.getValue().size() <= 1) {
                    continue;
                }
                final int slotMax = ItemProvider.getItemInfo(entry.getKey()).orElseThrow().getSlotMax(); // getItemInfo succeeded in above loop
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
    }

    @Handler(InHeader.USER_SORT_ITEM_REQUEST)
    public static void handlerUserSortItemRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final InventoryType inventoryType = InventoryType.getByValue(inPacket.decodeByte()); // nType
        if (inventoryType == null || inventoryType == InventoryType.EQUIPPED) {
            user.dispose();
            return;
        }
        try (var locked = user.acquire()) {
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
    }

    @Handler(InHeader.USER_CHANGE_SLOT_POSITION_REQUEST)
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

        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            final Inventory inventory = im.getInventoryByType(InventoryType.getByPosition(inventoryType, oldPos));
            final Item item = inventory.getItem(oldPos);
            if (item == null) {
                log.error("Could not find item in {} inventory, position {}", inventoryType.name(), oldPos);
                return;
            }
            if (newPos == 0) {
                // CDraggableItem::ThrowItem : item is deleted if (quest || tradeBlock) && POSSIBLE_TRADING attribute not set
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
                final boolean isQuest = itemInfoResult.map(ItemInfo::isQuest).orElse(false);
                final boolean isTradeBlock = itemInfoResult.map(ItemInfo::isTradeBlock).orElse(false);
                final DropEnterType dropEnterType = ((isQuest || isTradeBlock) && !item.isPossibleTrading()) ?
                        DropEnterType.FADING_OUT :
                        DropEnterType.CREATE;
                if (item.getItemType() == ItemType.BUNDLE && !ItemConstants.isRechargeableItem(item.getItemId()) &&
                        item.getQuantity() > count) {
                    // Update item count
                    item.setQuantity((short) (item.getQuantity() - count));
                    user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(inventoryType, oldPos, item.getQuantity()), true));
                    // Create partial item
                    final Item partialItem = new Item(item);
                    partialItem.setItemSn(user.getNextItemSn());
                    partialItem.setQuantity(count);
                    // Create drop
                    final Drop drop = Drop.item(DropOwnType.NO_OWN, user, partialItem, 0);
                    user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
                } else {
                    // Full drop
                    if (!inventory.removeItem(oldPos, item)) {
                        log.error("Failed to remove item in {} inventory, position {}", inventoryType.name(), oldPos);
                        return;
                    }
                    // Remove item from client inventory
                    user.write(WvsContext.inventoryOperation(InventoryOperation.delItem(inventoryType, oldPos), true));
                    // Create drop
                    final Drop drop = Drop.item(DropOwnType.NO_OWN, user, item, 0);
                    user.getField().getDropPool().addDrop(drop, dropEnterType, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
                }
            } else {
                final InventoryType secondInventoryType = InventoryType.getByPosition(inventoryType, newPos);
                final Inventory secondInventory = im.getInventoryByType(secondInventoryType);
                final Item secondItem = secondInventory.getItem(newPos);
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
                }
                // Swap item position and update client
                inventory.putItem(oldPos, secondItem);
                secondInventory.putItem(newPos, item);
                user.write(WvsContext.inventoryOperation(InventoryOperation.position(inventoryType, oldPos, newPos), true));
            }
            user.validateStat();
        }
    }


    // STAT HANDLERS ---------------------------------------------------------------------------------------------------

    @Handler(InHeader.USER_STAT_CHANGE_REQUEST)
    public static void handleUserStatChangeRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int mask = inPacket.decodeInt(); // 0x1400
        if (mask != 0x1400) {
            log.error("Unhandled mask received for USER_STAT_CHANGE_REQUEST : {}", mask);
            return;
        }
        final int hp = Short.toUnsignedInt(inPacket.decodeShort()); // nHP
        final int mp = Short.toUnsignedInt(inPacket.decodeShort()); // nMP
        inPacket.decodeByte(); // nOption

        try (var locked = user.acquire()) {
            if (hp > 0) {
                user.addHp(hp);
            }
            if (mp > 0) {
                user.addMp(mp);
            }
        }
    }


    // OTHER HANDLERS --------------------------------------------------------------------------------------------------

    @Handler(InHeader.USER_DROP_MONEY_REQUEST)
    public static void handleUserDropMoneyRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int money = inPacket.decodeInt(); // nAmount
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            if (money <= 0 || !im.addMoney(-money)) {
                user.dispose();
                return;
            }
            final Drop drop = Drop.money(DropOwnType.NO_OWN, user, money, user.getCharacterId());
            user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        }
    }

    @Handler(InHeader.USER_CHARACTER_INFO_REQUEST)
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

    @Handler(InHeader.USER_PORTAL_SCRIPT_REQUEST)
    public static void handleUserPortalScriptRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
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

    @Handler(InHeader.USER_PORTAL_TELEPORT_REQUEST)
    public static void handleUserPortalTeleportRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
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

    @Handler(InHeader.USER_QUEST_REQUEST)
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
            case LOST_ITEM -> {
                final int size = inPacket.decodeInt();
                final int[] lostItems = new int[size];
                for (int i = 0; i < size; i++) {
                    lostItems[i] = inPacket.decodeInt();
                }
                // TODO
            }
            case ACCEPT_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                try (var locked = user.acquire()) {
                    final Optional<QuestRecord> startQuestResult = questInfo.startQuest(locked);
                    if (startQuestResult.isEmpty()) {
                        log.error("Failed to accept quest : {}", questId);
                        user.dispose();
                        return;
                    }
                    user.write(WvsContext.message(Message.questRecord(startQuestResult.get())));
                    user.write(UserLocal.questResult(QuestResult.success(questId, templateId, 0)));
                    user.validateStat();
                }
            }
            case COMPLETE_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final int index = inPacket.decodeInt(); // nIdx, for selecting reward? TODO
                try (var locked = user.acquire()) {
                    final Optional<Tuple<QuestRecord, Integer>> questCompleteResult = questInfo.completeQuest(locked);
                    if (questCompleteResult.isEmpty()) {
                        log.error("Failed to complete quest : {}", questId);
                        user.dispose();
                        return;
                    }
                    final QuestRecord qr = questCompleteResult.get().getLeft();
                    final int nextQuest = questCompleteResult.get().getRight();
                    user.write(WvsContext.message(Message.questRecord(questCompleteResult.get().getLeft())));
                    user.write(UserLocal.questResult(QuestResult.success(questId, templateId, nextQuest)));
                    user.validateStat();
                }
                // Quest complete effect
                user.write(UserLocal.effect(Effect.questComplete()));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
            }
            case RESIGN_QUEST -> {
                try (var locked = user.acquire()) {
                    final Optional<QuestRecord> questRecordResult = questInfo.resignQuest(locked);
                    if (questRecordResult.isEmpty()) {
                        log.error("Failed to resign quest : {}", questId);
                        return;
                    }
                    user.write(WvsContext.message(Message.questRecord(questRecordResult.get())));
                    user.write(UserLocal.resignQuestReturn(questId));
                    user.validateStat();
                }
            }
            case OPENING_SCRIPT, COMPLETE_SCRIPT -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
                ScriptDispatcher.startQuestScript(user, templateId, questId, questRequestType == QuestRequestType.OPENING_SCRIPT);
            }
            case null -> {
                log.error("Unknown quest action type : {}", action);
            }
            default -> {
                log.error("Unhandled quest action type : {}", questRequestType);
            }
        }
    }

    @Handler(InHeader.FRIEND_REQUEST)
    public static void handleFriendRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final FriendRequestType requestType = FriendRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown friend request type : {}", type);
            return;
        }
        switch (requestType) {
            case LOAD_FRIEND -> {
            }
            case SET_FRIEND -> {
                final String target = inPacket.decodeString(); // sTarget
                final String friendGroup = inPacket.decodeString(); // sFriendGroup
            }
            case ACCEPT_FRIEND -> {
                final int friendId = inPacket.decodeInt();
            }
            case DELETE_FRIEND -> {
                final int friendId = inPacket.decodeInt();
            }
            default -> {
                log.error("Unhandled friend request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.MEMO_REQUEST)
    public static void handleMemoRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final MemoRequestType requestType = MemoRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Unknown memo request type : {}", type);
            return;
        }
        switch (requestType) {
            case SEND -> {
                // CCashShop::OnCashItemResLoadGiftDone
                final String characterName = inPacket.decodeString();
                final String message = inPacket.decodeString(); // sMsg
                final int flag = inPacket.decodeByte(); // nFlag
                final int index = inPacket.decodeInt(); // nIdx
                final long itemSn = inPacket.decodeLong(); // GW_GiftList->liSN
                final MemoType memoType = MemoType.getByValue(flag);
                if (memoType == null) {
                    log.error("Tried to send memo with unknown type : {}", flag);
                    user.dispose();
                }
                // TODO create memo
            }
            case DELETE -> {
                // CMemoListDlg::SetRet
                final int size = inPacket.decodeByte(); // size
                inPacket.decodeByte(); // number of memos where nFlag == 3 (INVITATION) -> number of slots required
                inPacket.decodeByte(); // nEmptySlotCount
                for (int i = 0; i < size; i++) {
                    inPacket.decodeInt(); // dwSN
                    final int flag = inPacket.decodeByte(); // nFlag
                    final MemoType memoType = MemoType.getByValue(flag);
                    if (memoType == MemoType.INVITATION) {
                        final int marriageId = inPacket.decodeInt(); // atoi(strMarriageNo)
                        // TODO: add marriage invitation item
                    }
                    // TODO: db handling for memos
                    if (memoType == MemoType.INC_POP) {
                        try (var locked = user.acquire()) {
                            final CharacterStat cs = locked.get().getCharacterStat();
                            final short newPop = (short) Math.min(cs.getPop() + 1, Short.MAX_VALUE);
                            cs.setPop(newPop);
                            user.write(WvsContext.statChanged(Stat.POP, newPop, false));
                        }
                    }
                }
            }
            case LOAD -> {
                // CWvsContext::OnMemoNotify_Receive
                // TODO fetch memo from DB
            }
            default -> {
                log.error("Unhandled memo request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.FUNC_KEY_MAPPED_MODIFIED)
    public static void handleFuncKeyMappedModified(User user, InPacket inPacket) {
        final int type = inPacket.decodeInt();
        final FuncKeyMappedType funcKeyMappedType = FuncKeyMappedType.getByValue(type);
        if (funcKeyMappedType == null) {
            log.error("Received unknown type {} for FUNC_KEY_MAPPED_MODIFIED", type);
            return;
        }
        try (var locked = user.acquire()) {
            final FuncKeyManager fkm = locked.get().getFuncKeyManager();
            switch (funcKeyMappedType) {
                case KEY_MODIFIED -> {
                    final int size = inPacket.decodeInt(); // *(anChangedIdx.a - 1)
                    final Map<Integer, FuncKeyMapped> changed = new HashMap<>();
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
                        changed.put(index, new FuncKeyMapped(funcKeyType, funcKeyId));
                    }
                    fkm.updateFuncKeyMap(changed);
                }
                case PET_CONSUME_ITEM_MODIFIED -> {
                    final int itemId = inPacket.decodeInt(); // nPetConsumeItemID
                    fkm.setPetConsumeItem(itemId);
                }
                case PET_CONSUME_MP_ITEM_MODIFIED -> {
                    final int itemId = inPacket.decodeInt(); // nPetConsumeMPItemID
                    fkm.setPetConsumeMpItem(itemId);
                }
                default -> {
                    log.error("Unhandled func key mapped type : {}", funcKeyMappedType);
                }
            }
        }
    }

    @Handler(InHeader.UPDATE_GM_BOARD)
    public static void handleUpdateGmBoard(User user, InPacket inPacket) {
        inPacket.decodeInt(); // nGameOpt_OpBoardIndex
    }

    @Handler(InHeader.QUICKSLOT_KEY_MAPPED_MODIFIED)
    public static void handleQuickslotKeyMappedModified(User user, InPacket inPacket) {
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_SIZE];
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyMap[i] = inPacket.decodeInt();
        }
        try (var locked = user.acquire()) {
            final FuncKeyManager fkm = locked.get().getFuncKeyManager();
            fkm.setQuickslotKeyMap(quickslotKeyMap);
        }
    }

    @Handler(InHeader.UPDATE_SCREEN_SETTING)
    public static void handleUpdateScreenSetting(User user, InPacket inPacket) {
        inPacket.decodeByte(); // bSysOpt_LargeScreen
        inPacket.decodeByte(); // bSysOpt_WindowedMode
    }
}
