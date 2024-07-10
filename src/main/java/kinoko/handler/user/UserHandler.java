package kinoko.handler.user;

import kinoko.database.CharacterInfo;
import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.field.*;
import kinoko.packet.stage.CashShopPacket;
import kinoko.packet.user.*;
import kinoko.packet.world.*;
import kinoko.provider.ItemProvider;
import kinoko.provider.QuestProvider;
import kinoko.provider.ShopProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.quest.QuestInfo;
import kinoko.script.common.ScriptAnswer;
import kinoko.script.common.ScriptDispatcher;
import kinoko.script.common.ScriptMessageType;
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
import kinoko.server.party.PartyRequest;
import kinoko.server.party.PartyRequestType;
import kinoko.server.party.PartyResultType;
import kinoko.server.user.RemoteUser;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.TownPortal;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.npc.Npc;
import kinoko.world.friend.*;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestRequestType;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);

    @Handler(InHeader.UserMove)
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

    @Handler(InHeader.UserSitRequest)
    public static void handleUserSitRequest(User user, InPacket inPacket) {
        // CUserLocal::HandleXKeyDown, CWvsContext::SendGetUpFromChairRequest
        final short fieldSeatId = inPacket.decodeShort();
        user.setPortableChairId(0);
        user.write(UserLocal.sitResult(fieldSeatId != -1, fieldSeatId)); // broadcast not required
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
        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && text.length() > 1) {
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
        // Handle trunk / npc shop dialog, lock user
        try (var locked = user.acquire()) {
            if (user.hasDialog()) {
                log.error("Tried to select npc ID {}, while already in a dialog", npc.getTemplateId());
                return;
            }
            if (npc.isTrunk()) {
                final TrunkDialog trunkDialog = TrunkDialog.from(npc.getTemplate());
                user.setDialog(trunkDialog);
                // Lock account to access trunk
                try (var lockedAccount = user.getAccount().acquire()) {
                    user.write(TrunkPacket.openTrunkDlg(npc.getTemplateId(), lockedAccount.get().getTrunk()));
                }
            } else if (ShopProvider.isShop(npc.getTemplateId())) {
                final ShopDialog shopDialog = ShopDialog.from(npc.getTemplate());
                user.setDialog(shopDialog);
                user.write(FieldPacket.openShopDlg(user, shopDialog));
            }
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
        try (var locked = user.acquire()) {
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
    }

    @Handler(InHeader.UserShopRequest)
    public static void handleUserShopRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            if (!(user.getDialog() instanceof ShopDialog shopDialog)) {
                log.error("Received UserShopRequest without associated shop dialog");
                return;
            }
            shopDialog.handlePacket(locked, inPacket);
        }
    }

    @Handler(InHeader.UserTrunkRequest)
    public static void handleUserTrunkRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            if (!(user.getDialog() instanceof TrunkDialog trunkDialog)) {
                log.error("Received UserTrunkRequest without associated trunk dialog");
                return;
            }
            trunkDialog.handlePacket(locked, inPacket);
        }
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
    }

    @Handler(InHeader.UserSortItemRequest)
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
                    final BodyPart exclusiveBodyPart = ItemConstants.getExclusiveEquipItemBodyPart(secondInventory, item.getItemId(), isCash);
                    if (exclusiveBodyPart != null) {
                        // Move exclusive body part equip item to inventory
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
                }
                // Swap item position and update client
                inventory.putItem(oldPos, secondItem);
                secondInventory.putItem(newPos, item);
                user.write(WvsContext.inventoryOperation(InventoryOperation.position(inventoryType, oldPos, newPos), true));
            }
            // Update user
            if (inventoryType == InventoryType.EQUIP) {
                user.getCharacterData().getCoupleRecord().reset(im.getEquipped(), im.getEquipInventory());
                user.validateStat();
                user.getField().broadcastPacket(UserRemote.avatarModified(user), user);
            }
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
        try (var locked = user.acquire()) {
            // Validate stat
            final CharacterStat cs = locked.get().getCharacterStat();
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
    }

    @Handler(InHeader.UserAbilityMassUpRequest)
    public static void handleUserAbilityMassUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int size = inPacket.decodeInt();
        final Map<Stat, Integer> stats = new HashMap<>();
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
        try (var locked = user.acquire()) {
            // Validate stats
            final CharacterStat cs = locked.get().getCharacterStat();
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
    }

    @Handler(InHeader.UserChangeStatRequest)
    public static void handleUserChangeStatRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int mask = inPacket.decodeInt(); // 0x1400
        if (mask != 0x1400) {
            log.error("Unhandled mask received for UserChangeStatRequest : {}", mask);
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

    @Handler(InHeader.UserSkillUpRequest)
    public static void handleUserSkillUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID
        try (var locked = user.acquire()) {
            final SkillManager sm = locked.get().getSkillManager();
            final Optional<SkillRecord> skillRecordResult = sm.getSkill(skillId);
            if (skillRecordResult.isEmpty()) {
                log.error("Tried to add a skill {} not owned by user", skillId);
                user.dispose();
                return;
            }
            final SkillRecord skillRecord = skillRecordResult.get();
            if (skillRecord.getSkillLevel() >= skillRecord.getMasterLevel()) {
                log.error("Tried to add a skill {} at master level {}/{}", skillId, skillRecord.getSkillLevel(), skillRecord.getMasterLevel());
                user.dispose();
                return;
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
            user.write(WvsContext.changeSkillRecordResult(skillRecord, true));
            user.updatePassiveSkillData();
            user.validateStat();
        }
    }


    // OTHER HANDLERS --------------------------------------------------------------------------------------------------

    @Handler(InHeader.UserDropMoneyRequest)
    public static void handleUserDropMoneyRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int money = inPacket.decodeInt(); // nAmount
        try (var locked = user.acquire()) {
            final InventoryManager im = user.getInventoryManager();
            if (money <= 0 || !im.addMoney(-money)) {
                user.dispose();
                return;
            }
            final Drop drop = Drop.money(DropOwnType.NOOWN, user, money, user.getCharacterId());
            user.getField().getDropPool().addDrop(drop, DropEnterType.CREATE, user.getX(), user.getY() - GameConstants.DROP_HEIGHT, 0);
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), true));
        }
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

    @Handler(InHeader.UserPortalTeleportRequest)
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

    @Handler(InHeader.UserMapTransferRequest)
    public static void handleUserMapTransferRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte(); // nType
        final MapTransferRequestType requestType = MapTransferRequestType.getByValue(type);
        if (requestType == null) {
            log.error("Received unknown map transfer request type : {}", type);
            return;
        }
        final boolean canTransferContinent = inPacket.decodeBoolean(); // bCanTransferContinent
        try (var locked = user.acquire()) {
            final MapTransferInfo mapTransferInfo = locked.get().getMapTransferInfo();
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
                final Set<Integer> lostItems = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    lostItems.add(inPacket.decodeInt()); // item id
                }
                try (var locked = user.acquire()) {
                    questInfo.restoreLostItems(locked, lostItems);
                }
            }
            case AcceptQuest -> {
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
                    user.write(MessagePacket.questRecord(startQuestResult.get()));
                    user.write(QuestPacket.success(questId, templateId, 0));
                    user.validateStat();
                }
            }
            case CompleteQuest -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final int rewardIndex = inPacket.decodeInt(); // nIdx - for selecting reward
                try (var locked = user.acquire()) {
                    final Optional<Tuple<QuestRecord, Integer>> questCompleteResult = questInfo.completeQuest(locked, rewardIndex);
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
                }
                // Quest complete effect
                user.write(UserLocal.effect(Effect.questComplete()));
                user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
            }
            case ResignQuest -> {
                try (var locked = user.acquire()) {
                    final Optional<QuestRecord> questRecordResult = questInfo.resignQuest(locked);
                    if (questRecordResult.isEmpty()) {
                        log.error("Failed to resign quest : {}", questId);
                        return;
                    }
                    user.write(MessagePacket.questRecord(questRecordResult.get()));
                    user.write(UserLocal.resignQuestReturn(questId));
                    user.validateStat();
                }
            }
            case OpeningScript, CompleteScript -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
                final boolean isStart = questRequestType == QuestRequestType.OpeningScript;
                ScriptDispatcher.startQuestScript(user, questId, isStart, templateId);
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
        try (var locked = user.acquire()) {
            locked.get().getConfigManager().updateMacroSysData(macroSysData);
        }
    }


    // SOCIAL HANDLERS -------------------------------------------------------------------------------------------------

    @Handler(InHeader.GroupMessage)
    public static void handleGroupMessage(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int type = inPacket.decodeByte(); // nChatTarget
        final GroupMessageType messageType = GroupMessageType.getByValue(type);
        if (messageType == null) {
            log.error("Unknown group message type : {}", type);
            return;
        }
        final int count = inPacket.decodeByte(); // nMemberCnt
        final Set<Integer> targetIds = new HashSet<>();
        for (int i = 0; i < count; i++) {
            targetIds.add(inPacket.decodeInt());
        }
        final String text = inPacket.decodeString(); // sText
        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && text.length() > 1) {
            CommandProcessor.tryProcessCommand(user, text);
            return;
        }
        user.getConnectedServer().submitUserPacketBroadcast(targetIds, FieldPacket.groupMessage(messageType, user.getCharacterName(), text));
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
                final CompletableFuture<Set<RemoteUser>> userRequestFuture = user.getConnectedServer().submitUserQueryRequest(Set.of(targetName));
                try {
                    final Set<RemoteUser> queryResult = userRequestFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
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
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("Exception caught while waiting for user query result", e);
                    user.write(WhisperPacket.locationResultNone(targetName));
                    e.printStackTrace();
                }
            }
            case WhisperRequest, WhisperRequestmanager -> {
                final String targetName = inPacket.decodeString();
                final String message = inPacket.decodeString();
                // Query target user
                final CompletableFuture<Set<RemoteUser>> userRequestFuture = user.getConnectedServer().submitUserQueryRequest(Set.of(targetName));
                try {
                    final Set<RemoteUser> queryResult = userRequestFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
                    if (queryResult.isEmpty()) {
                        user.write(WhisperPacket.whisperResult(targetName, false));
                        return;
                    }
                    // Write packet to target user
                    user.getConnectedServer().submitUserPacketRequest(targetName, WhisperPacket.whisperReceive(user.getChannelId(), user.getCharacterName(), message));
                    user.write(WhisperPacket.whisperResult(targetName, true));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("Exception caught while waiting for user query result", e);
                    user.write(WhisperPacket.whisperResult(targetName, false));
                    e.printStackTrace();
                }
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
                final CompletableFuture<Set<RemoteUser>> userRequestFuture = user.getConnectedServer().submitUserQueryRequest(Set.of(targetName));
                try {
                    final Set<RemoteUser> queryResult = userRequestFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
                    final Optional<RemoteUser> targetResult = queryResult.stream().findFirst();
                    if (targetResult.isEmpty()) {
                        user.write(MessengerPacket.inviteResult(targetName, false));
                        return;
                    }
                    user.getConnectedServer().submitUserPacketReceive(targetResult.get().getCharacterId(), MessengerPacket.invite(user, user.getMessengerId()));
                    user.write(MessengerPacket.inviteResult(targetName, true));
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("Exception caught while waiting for user query result", e);
                    user.write(MessengerPacket.inviteResult(targetName, false));
                    e.printStackTrace();
                }
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
        try (var locked = user.acquire()) {
            // TradingRoom Protocol
            if (mrp.getValue() >= MiniRoomProtocol.TRP_PutItem.getValue() && mrp.getValue() <= MiniRoomProtocol.TRP_LimitFail.getValue()) {
                if (!(user.getDialog() instanceof TradingRoom tradingRoom)) {
                    log.error("Received trading room action {} outside a trading room", mrp);
                    return;
                }
                tradingRoom.handlePacket(locked, mrp, inPacket);
                return;
            }
            // MiniGameRoom Protocol
            if (mrp.getValue() >= MiniRoomProtocol.MGRP_TieRequest.getValue() && mrp.getValue() <= MiniRoomProtocol.MGP_MatchCard.getValue()) {
                if (!(user.getDialog() instanceof MiniGameRoom miniGameRoom)) {
                    log.error("Received mini game room action {} outside a mini game room", mrp);
                    return;
                }
                miniGameRoom.handlePacket(locked, mrp, inPacket);
                return;
            }
            // Common MiniRoom Protocol
            switch (mrp) {
                case MRP_Create -> {
                    if (user.getDialog() != null) {
                        log.error("Tried to create mini room with another dialog open");
                        user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                        return;
                    }
                    final int type = inPacket.decodeByte();
                    final MiniRoomType mrt = MiniRoomType.getByValue(type);
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
                                    new OmokGameRoom(title, password, gameSpec, user) :
                                    new MemoryGameRoom(title, password, gameSpec, user);
                            user.getField().getMiniRoomPool().addMiniRoom(miniGameRoom);
                            user.setDialog(miniGameRoom);
                            user.write(MiniRoomPacket.MiniGame.enterResult(miniGameRoom, user));
                            miniGameRoom.updateBalloon();
                        }
                        case TradingRoom -> {
                            // CField::SendInviteTradingRoomMsg
                            final TradingRoom tradingRoom = new TradingRoom(user);
                            user.getField().getMiniRoomPool().addMiniRoom(tradingRoom);
                            user.setDialog(tradingRoom);
                            user.write(MiniRoomPacket.enterResult(tradingRoom, user));
                        }
                        case PersonalShop, EntrustedShop -> {
                            // CWvsContext::SendOpenShopRequest
                            final String title = inPacket.decodeString(); // sTitle
                            inPacket.decodeByte(); // 0
                            final int position = inPacket.decodeShort(); // nPOS
                            final int itemId = inPacket.decodeInt(); // nItemID
                            // TODO
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
                    if (!(user.getDialog() instanceof TradingRoom tradingRoom)) {
                        log.error("Tried to invite user without a trading room");
                        user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                        return;
                    }
                    final int targetId = inPacket.decodeInt();
                    final Optional<User> targetResult = user.getField().getUserPool().getById(targetId);
                    if (targetResult.isEmpty()) {
                        user.write(MiniRoomPacket.inviteResult(InviteType.NoCharacter, null)); // Unable to find the character.
                        tradingRoom.cancelTrade(locked, LeaveType.UserRequest);
                        return;
                    }
                    try (var lockedTarget = targetResult.get().acquire()) {
                        final User target = lockedTarget.get();
                        if (target.getDialog() != null) {
                            user.write(MiniRoomPacket.inviteResult(InviteType.CannotInvite, target.getCharacterName())); // '%s' is doing something else right now.
                            tradingRoom.cancelTrade(locked, LeaveType.UserRequest);
                            return;
                        }
                        target.write(MiniRoomPacket.inviteStatic(MiniRoomType.TradingRoom, user.getCharacterName(), tradingRoom.getId()));
                    }
                }
                case MRP_InviteResult -> {
                    // CMiniRoomBaseDlg::SendInviteResult
                    final int miniRoomId = inPacket.decodeInt(); // dwSN
                    final int type = inPacket.decodeByte(); // nErrCode
                    final InviteType resultType = InviteType.getByValue(type);
                    if (resultType == null) {
                        log.error("Unknown invite result type {}", type);
                        return;
                    }
                    // Resolve trading room
                    final Optional<MiniRoom> miniRoomResult = user.getField().getMiniRoomPool().getById(miniRoomId);
                    if (miniRoomResult.isEmpty() || !(miniRoomResult.get() instanceof TradingRoom tradingRoom)) {
                        return;
                    }
                    // Cancel trade
                    try (var lockedInviter = tradingRoom.getInviter().acquire()) {
                        final User inviter = lockedInviter.get();
                        inviter.write(MiniRoomPacket.inviteResult(resultType, user.getCharacterName()));
                        tradingRoom.cancelTrade(lockedInviter, LeaveType.UserRequest);
                    }
                }
                case MRP_Enter -> {
                    // CMiniRoomBaseDlg::SendInviteResult
                    // CUserLocal::HandleLButtonDblClk
                    if (user.getDialog() != null) {
                        log.error("Tried to enter mini room with another dialog open");
                        user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                        return;
                    }
                    final int miniRoomId = inPacket.decodeInt(); // dwSN
                    final boolean isPrivate = inPacket.decodeBoolean();
                    final String password = isPrivate ? inPacket.decodeString() : null;
                    inPacket.decodeByte(); // 0
                    // Resolve mini room
                    final Optional<MiniRoom> miniRoomResult = user.getField().getMiniRoomPool().getById(miniRoomId);
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
                    if (miniRoom instanceof TradingRoom tradingRoom) {
                        if (!tradingRoom.addUser(user)) {
                            user.write(BroadcastPacket.alert("This request has failed due to an unknown error."));
                            return;
                        }
                        user.setDialog(tradingRoom);
                        user.write(MiniRoomPacket.enterResult(tradingRoom, user));
                    } else if (miniRoom instanceof MiniGameRoom miniGameRoom) {
                        if (!miniGameRoom.addUser(user)) {
                            user.write(MiniRoomPacket.enterResult(EnterResultType.Full)); // You can't enter the room due to full capacity.
                            return;
                        }
                        user.setDialog(miniGameRoom);
                        user.write(MiniRoomPacket.MiniGame.enterResult(miniGameRoom, user));
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
                        log.error("Received mini room chat without a mini room");
                        return;
                    }
                    miniRoom.broadcastPacket(MiniRoomPacket.chat(miniRoom.getPosition(user), user.getCharacterName(), message));
                }
                case MRP_Leave -> {
                    if (!(user.getDialog() instanceof MiniRoom miniRoom)) {
                        log.error("Tried to leave from a mini room without a dialog open");
                        return;
                    }
                    if (miniRoom instanceof TradingRoom tradingRoom) {
                        tradingRoom.cancelTradeUnsafe(user);
                    } else if (miniRoom instanceof MiniGameRoom miniGameRoom) {
                        miniGameRoom.leaveUnsafe(user);
                    } else {
                        log.error("Tried to leave from a mini room with unhandled type {}", miniRoom.getType());
                        user.setDialog(null);
                    }
                }
                case MRP_Balloon -> {
                    // ignore?
                }
                default -> {
                    log.error("Unhandled mini room action {}", mrp);
                }
            }
        }
    }

    @Handler(InHeader.PartyRequest)
    public static void handlePartyRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyRequestType requestType = PartyRequestType.getByValue(type);
        switch (requestType) {
            case CreateNewParty -> {
                // CField::SendCreateNewPartyMsg
                if (user.getPartyId() != 0) {
                    user.write(PartyPacket.of(PartyResultType.CreateNewParty_AlreadyJoined));
                    return;
                }
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.createNewParty());
            }
            case WithdrawParty -> {
                // CField::SendWithdrawPartyMsg
                inPacket.decodeByte(); // hardcoded 0
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.withdrawParty());
            }
            case JoinParty -> {
                // CWvsContext::OnPartyResult
                final int inviterId = inPacket.decodeInt();
                inPacket.decodeByte(); // unknown byte from INVITE_PARTY
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
            }
            case InviteParty -> {
                // CField::SendJoinPartyMsg
                final String targetName = inPacket.decodeString();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.invite(targetName));
            }
            case KickParty -> {
                // CField::SendKickPartyMsg
                final int targetId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.kickParty(targetId));
            }
            case ChangePartyBoss -> {
                // CField::SendChangePartyBossMsg
                final int targetId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.changePartyBoss(targetId));
            }
            case null -> {
                log.error("Unknown party request type : {}", type);
            }
            default -> {
                log.error("Unhandled party request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.PartyResult)
    public static void handlePartyResult(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyResultType resultType = PartyResultType.getByValue(type);
        switch (resultType) {
            case InviteParty_Sent, InviteParty_BlockedUser, InviteParty_AlreadyInvited,
                    InviteParty_AlreadyInvitedByInviter, InviteParty_Rejected -> {
                final int inviterId = inPacket.decodeInt();
                final String message = switch (resultType) {
                    // These messages are from the client string pool, but are not used (except for InviteParty_Sent)
                    case InviteParty_Sent, InviteParty_BlockedUser ->
                            String.format("You have invited '%s' to your party.", user.getCharacterName());
                    case InviteParty_AlreadyInvited ->
                            String.format("'%s' is taking care of another invitation.", user.getCharacterName());
                    case InviteParty_AlreadyInvitedByInviter ->
                            String.format("You have already invited '%s' to your party.", user.getCharacterName());
                    case InviteParty_Rejected ->
                            String.format("'%s' has declined the party request.", user.getCharacterName());
                    default -> {
                        throw new IllegalStateException("Unexpected party result type");
                    }
                };
                user.getConnectedServer().submitUserPacketReceive(inviterId, PartyPacket.serverMsg(message));
            }
            case InviteParty_Accepted -> {
                final int inviterId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
            }
            case null -> {
                log.error("Unknown party result type : {}", type);
            }
            default -> {
                log.error("Unhandled party result type : {}", resultType);
            }
        }
    }

    @Handler(InHeader.FriendRequest)
    public static void handleFriendRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final FriendRequestType requestType = FriendRequestType.getByValue(type);
        switch (requestType) {
            case LoadFriend -> {
                FriendManager.updateFriendsFromDatabase(user);
                FriendManager.updateFriendsFromCentralServer(user, FriendResultType.LoadFriend_Done, false);
            }
            case SetFriend -> {
                final String targetName = inPacket.decodeString(); // sTarget
                final String friendGroup = inPacket.decodeString(); // sFriendGroup
                // Check if friend already exists
                final Optional<Friend> friendResult = user.getFriendManager().getFriendByName(targetName);
                if (friendResult.isPresent() && friendResult.get().getStatus() == FriendStatus.NORMAL) {
                    // Update friend group
                    final Friend friend = friendResult.get();
                    friend.setFriendGroup(friendGroup);
                    if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                        user.write(FriendPacket.setFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                } else {
                    // Create new friend - resolve target character id
                    final Optional<CharacterInfo> characterInfoResult = DatabaseManager.characterAccessor().getCharacterInfoByName(targetName);
                    if (characterInfoResult.isEmpty()) {
                        user.write(FriendPacket.setFriendUnknownUser()); // That character is not registered.
                        return;
                    }
                    final int targetCharacterId = characterInfoResult.get().getCharacterId();
                    final String targetCharacterName = characterInfoResult.get().getCharacterName();
                    // Check if target can be added as a friend
                    final List<Friend> friends = user.getFriendManager().getRegisteredFriends();
                    if (friends.size() >= user.getFriendManager().getFriendMax()) {
                        user.write(FriendPacket.setFriendFullMe()); // Your buddy list is full.
                        return;
                    }
                    if (friends.stream().anyMatch((friend) -> friend.getFriendId() == targetCharacterId)) {
                        user.write(FriendPacket.setFriendAlreadySet()); // That character is already registered as your buddy.
                        return;
                    }
                    // Add target as friend, force creation
                    final Friend friendForUser = new Friend(user.getCharacterId(), targetCharacterId, targetCharacterName, friendGroup, FriendStatus.NORMAL);
                    if (!DatabaseManager.friendAccessor().saveFriend(friendForUser, true)) {
                        user.write(FriendPacket.setFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    // Add user as a friend for target, not forced - existing friends, requests, and refused records
                    final Friend friendForTarget = new Friend(targetCharacterId, user.getCharacterId(), user.getCharacterName(), GameConstants.DEFAULT_FRIEND_GROUP, FriendStatus.REQUEST);
                    if (DatabaseManager.friendAccessor().saveFriend(friendForTarget, false)) {
                        // Send invite to target if request was created
                        // This operation is a noop if target offline, the request will be processed on target login
                        user.getConnectedServer().submitUserPacketRequest(targetCharacterName, FriendPacket.invite(friendForTarget));
                    }
                }
                // Reload friends and update client
                FriendManager.updateFriendsFromDatabase(user);
                FriendManager.updateFriendsFromCentralServer(user, FriendResultType.SetFriend_Done, false);
            }
            case AcceptFriend -> {
                final int friendId = inPacket.decodeInt();
                FriendManager.updateFriendsFromDatabase(user);
                // Resolve friend from id
                final Optional<Friend> friendResult = user.getFriendManager().getFriend(friendId);
                if (friendResult.isEmpty()) {
                    user.write(FriendPacket.acceptFriendUnknown()); // The request was denied due to an unknown error.
                    return;
                }
                // Update friend status
                final Friend friend = friendResult.get();
                friend.setStatus(FriendStatus.NORMAL);
                if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                    user.write(FriendPacket.acceptFriendUnknown()); // The request was denied due to an unknown error.
                    return;
                }
                // Notify newly added friend (noop if offline)
                user.getConnectedServer().submitUserPacketRequest(friend.getFriendName(), FriendPacket.notify(user.getCharacterId(), user.getChannelId(), false));
                // Reload friends and update client
                FriendManager.updateFriendsFromCentralServer(user, FriendResultType.SetFriend_Done, false);
            }
            case DeleteFriend -> {
                final int friendId = inPacket.decodeInt();
                FriendManager.updateFriendsFromDatabase(user);
                // Resolve friend from id
                final Optional<Friend> friendResult = user.getFriendManager().getFriend(friendId);
                if (friendResult.isEmpty()) {
                    user.write(FriendPacket.deleteFriendUnknown()); // The request was denied due to an unknown error.
                    return;
                }
                final Friend friend = friendResult.get();
                // Update friend request status to refused (this is set for deletion too, in order to update the client)
                friend.setStatus(FriendStatus.REFUSED);
                if (friend.getStatus() == FriendStatus.REQUEST) {
                    // Save friend request result
                    if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                        user.write(FriendPacket.deleteFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                } else {
                    // Delete friend
                    if (!DatabaseManager.friendAccessor().deleteFriend(user.getCharacterId(), friend.getFriendId())) {
                        user.write(FriendPacket.deleteFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    // Notify deleted friend (noop if offline)
                    user.getConnectedServer().submitUserPacketRequest(friend.getFriendName(), FriendPacket.notify(user.getCharacterId(), GameConstants.CHANNEL_OFFLINE, false));
                }
                // Reload friends and update client
                FriendManager.updateFriendsFromCentralServer(user, FriendResultType.DeleteFriend_Done, false);
            }
            case null -> {
                log.error("Unknown friend request type : {}", type);
            }
            default -> {
                log.error("Unhandled friend request type : {}", requestType);
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
                try (var lockedAccount = user.getAccount().acquire()) {
                    final Locker locker = lockedAccount.get().getLocker();
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
                    user.write(CashShopPacket.loadLockerDone(lockedAccount.get()));
                }
                // Resolve receiver
                final Optional<CharacterInfo> receiverIdResult = DatabaseManager.characterAccessor().getCharacterInfoByName(receiverName);
                if (receiverIdResult.isEmpty()) {
                    user.write(MemoPacket.sendWarningName()); // Please check the name of the receiving character.
                    return;
                }
                final int receiverCharacterId = receiverIdResult.get().getCharacterId();
                // Create memo
                final Optional<Integer> memoIdResult = DatabaseManager.memoAccessor().nextMemoId();
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
                        try (var locked = user.acquire()) {
                            locked.get().addPop(1);
                            user.write(MessagePacket.incPop(1));
                        }
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
        try (var locked = user.acquire()) {
            if (townPortal.getTownField() == user.getField()) {
                user.warp(townPortal.getField(), townPortal.getX(), townPortal.getY(), townPortalId, false, false);
            } else {
                final int x, y;
                final Optional<PortalInfo> portalPointResult = townPortal.getTownPortalPoint();
                if (portalPointResult.isPresent()) {
                    x = portalPointResult.get().getX();
                    y = portalPointResult.get().getY();
                } else {
                    x = y = 0;
                }
                user.warp(townPortal.getTownField(), x, y, townPortalId, false, false);
            }
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
        try (var locked = user.acquire()) {
            final ConfigManager cm = locked.get().getConfigManager();
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
                    log.error("Received unknown type {} for FUNC_KEY_MAPPED_MODIFIED", type);
                }
                default -> {
                    log.error("Unhandled func key mapped type : {}", funcKeyMappedType);
                }
            }
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
    }

    @Handler(InHeader.QuickslotKeyMappedModified)
    public static void handleQuickslotKeyMappedModified(User user, InPacket inPacket) {
        final int[] quickslotKeyMap = new int[GameConstants.QUICKSLOT_KEY_MAP_SIZE];
        for (int i = 0; i < quickslotKeyMap.length; i++) {
            quickslotKeyMap[i] = inPacket.decodeInt();
        }
        try (var locked = user.acquire()) {
            final ConfigManager cm = locked.get().getConfigManager();
            cm.updateQuickslotKeyMap(quickslotKeyMap);
        }
    }

    @Handler(InHeader.PassiveskillInfoUpdate)
    public static void handlePassiveSkillInfoUpdate(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        try (var locked = user.acquire()) {
            user.updatePassiveSkillData();
            user.validateStat();
        }
    }

    @Handler(InHeader.UpdateScreenSetting)
    public static void handleUpdateScreenSetting(User user, InPacket inPacket) {
        inPacket.decodeByte(); // bSysOpt_LargeScreen
        inPacket.decodeByte(); // bSysOpt_WindowedMode
    }
}
