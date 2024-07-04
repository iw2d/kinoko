package kinoko.server.script;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.MobProvider;
import kinoko.provider.NpcProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.Foothold;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reward.Reward;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.server.field.Instance;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.FieldObject;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.Context;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * The utility methods implemented by these classes are designed to be executed inside the Python context, which
 * acquires and holds onto the user's lock during its execution, only releasing it while waiting for the user input for
 * the conversation methods.
 * <p>
 * The {@link ScriptDialog} is assigned to the User while the lock is released and ScriptManager is waiting for the user
 * input to act as a guard against multiple dialogs during script execution. It also keeps the reference to the
 * ScriptManager object associated with the user to accept the user input.
 */
public class ScriptManager {
    private static final Logger log = LogManager.getLogger(ScriptManager.class);
    private final Context context;
    private final User user;
    private final FieldObject source;
    private final ScriptMemory scriptMemory;
    private final Set<ScriptMessageParam> messageParams;
    private int speakerId;
    private CompletableFuture<ScriptAnswer> answerFuture;

    public ScriptManager(Context context, User user, FieldObject source, int speakerId) {
        this.context = context;
        this.user = user;
        this.source = source;
        this.speakerId = speakerId;
        this.scriptMemory = new ScriptMemory();
        this.messageParams = EnumSet.noneOf(ScriptMessageParam.class);
    }

    public Context getContext() {
        return context;
    }

    public User getUser() {
        return user;
    }


    // UTILITY METHODS --------------------------------------------------------------------------------------------

    public void dispose() {
        user.dispose();
        if (answerFuture != null) {
            answerFuture.cancel(true);
        }
        context.close(true);
    }

    public void scriptProgressMessage(String message) {
        user.write(WvsContext.scriptProgressMessage(message));
    }

    public void message(String message) {
        user.write(MessagePacket.system(message));
    }

    public void broadcastMessage(String message) {
        getField().broadcastPacket(MessagePacket.system(message));
    }

    public void playPortalSE() {
        user.write(UserLocal.effect(Effect.playPortalSE()));
    }

    public void avatarOriented(String effectPath) {
        user.write(UserLocal.effect(Effect.avatarOriented(effectPath)));
    }

    public void squibEffect(String effectPath) {
        user.write(UserLocal.effect(Effect.squibEffect(effectPath)));
    }

    public void balloonMsg(String text, int width, int duration) {
        user.write(UserLocal.balloonMsg(text, width, duration));
    }

    public void setDirectionMode(boolean set, int delay) {
        user.write(UserLocal.setDirectionMode(set, delay));
    }

    public void screenEffect(String effectPath) {
        user.write(FieldEffectPacket.screen(effectPath));
    }

    public void soundEffect(String effectPath) {
        user.write(FieldEffectPacket.sound(effectPath));
    }

    public void clock(int remain) {
        user.write(FieldPacket.clock(remain));
    }


    // STAT METHODS ----------------------------------------------------------------------------------------------------

    public int getGender() {
        return user.getGender();
    }

    public int getLevel() {
        return user.getLevel();
    }

    public int getJob() {
        return user.getJob();
    }

    public int getMaxHp() {
        return user.getMaxHp();
    }

    public int getHp() {
        return user.getHp();
    }

    public void setHp(int hp) {
        user.setHp(hp);
    }

    public void addExp(int exp) {
        user.addExp(exp);
        user.write(MessagePacket.incExp(exp, 0, true, true));
    }

    public void setConsumeItemEffect(int itemId) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            return;
        }
        user.setConsumeItemEffect(itemInfoResult.get());
        user.write(MessagePacket.giveBuff(itemId));
    }

    public void resetTemporaryStat(int skillId) {
        user.resetTemporaryStat(skillId);
    }


    // INVENTORY METHODS -----------------------------------------------------------------------------------------------

    public boolean addMoney(int money) {
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(money)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
        user.write(MessagePacket.incMoney(money));
        return true;
    }

    public boolean canAddMoney(int money) {
        return user.getInventoryManager().canAddMoney(money);
    }

    public boolean addItem(int itemId, int quantity) {
        return addItems(List.of(List.of(itemId, quantity)));
    }

    public boolean addItems(List<List<Integer>> itemList) {
        // Check inventory
        final List<Tuple<Integer, Integer>> itemCheck = new ArrayList<>();
        for (List<Integer> itemTuple : itemList) {
            if (itemTuple.size() != 2) {
                log.error("Invalid tuple length provided for addItems {}", itemTuple);
                return false;
            }
            itemCheck.add(new Tuple<>(itemTuple.get(0), itemTuple.get(1)));
        }
        if (!user.getInventoryManager().canAddItems(itemCheck)) {
            return false;
        }
        // Create items
        final List<Item> items = new ArrayList<>();
        for (var tuple : itemCheck) {
            final int itemId = tuple.getLeft();
            final int quantity = tuple.getRight();
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
            if (itemInfoResult.isEmpty()) {
                log.error("Could not resolve item info for item ID : {}", itemId);
                return false;
            }
            final ItemInfo itemInfo = itemInfoResult.get();
            items.add(itemInfo.createItem(user.getNextItemSn(), Math.min(quantity, itemInfo.getSlotMax())));
        }
        // Add items to inventory
        for (Item item : items) {
            final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
            if (addItemResult.isEmpty()) {
                throw new IllegalStateException("Failed to add item to inventory");
            }
            user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(item)));
        }
        return true;
    }

    public boolean canAddItem(int itemId, int quantity) {
        return canAddItems(List.of(List.of(itemId, quantity)));
    }

    public boolean canAddItems(List<List<Integer>> itemList) {
        final List<Tuple<Integer, Integer>> itemCheck = new ArrayList<>();
        for (List<Integer> itemTuple : itemList) {
            if (itemTuple.size() != 2) {
                log.error("Invalid tuple length provided for addItems {}", itemTuple);
                return false;
            }
            itemCheck.add(new Tuple<>(itemTuple.get(0), itemTuple.get(1)));
        }
        return user.getInventoryManager().canAddItems(itemCheck);
    }

    public boolean removeItem(int itemId, int quantity) {
        final Optional<List<InventoryOperation>> removeItemResult = user.getInventoryManager().removeItem(itemId, quantity);
        if (removeItemResult.isPresent()) {
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(itemId, -quantity)));
            return true;
        } else {
            return false;
        }
    }

    public boolean hasItem(int itemId) {
        return hasItem(itemId, 1);
    }

    public boolean hasItem(int itemId, int quantity) {
        return user.getInventoryManager().hasItem(itemId, quantity);
    }


    // QUEST METHODS ---------------------------------------------------------------------------------------------------

    public boolean hasQuestStarted(int questId) {
        return user.getQuestManager().hasQuestStarted(questId);
    }

    public void forceStartQuest(int questId) {
        final QuestRecord qr = user.getQuestManager().forceStartQuest(questId);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
    }

    public void forceCompleteQuest(int questId) {
        final QuestRecord qr = user.getQuestManager().forceCompleteQuest(questId);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
        // Quest complete effect
        user.write(UserLocal.effect(Effect.questComplete()));
        user.getField().broadcastPacket(UserRemote.effect(user, Effect.questComplete()), user);
    }

    public String getQRValue(int questId) {
        final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(questId);
        return questRecordResult.map(QuestRecord::getValue).orElse("");
    }

    public void setQRValue(int questId, String value) {
        final QuestRecord qr = user.getQuestManager().setQuestInfoEx(questId, value);
        user.write(MessagePacket.questRecord(qr));
        user.validateStat();
    }


    // WARP METHODS ----------------------------------------------------------------------------------------------------

    public void warp(int fieldId) {
        final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            dispose();
            return;
        }
        final Field targetField = fieldResult.get();
        final Optional<PortalInfo> portalResult = targetField.getPortalById(0);
        if (portalResult.isEmpty()) {
            log.error("Tried to warp to portal : {} on field ID : {}", 0, targetField.getFieldId());
            dispose();
            return;
        }
        user.warp(fieldResult.get(), portalResult.get(), false, false);
    }

    public void warp(int fieldId, String portalName) {
        // Resolve field
        final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            dispose();
            return;
        }
        final Field targetField = fieldResult.get();
        // Resolve portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            log.error("Tried to warp to portal : {} on field ID : {}", portalName, targetField.getFieldId());
            dispose();
            return;
        }
        user.warp(targetField, portalResult.get(), false, false);
    }

    public void warpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        warpInstance(List.of(mapId), portalName, returnMap, timeLimit);
    }

    public void warpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        // Create instance
        final Optional<Instance> instanceResult = user.getConnectedServer().createInstance(mapIds, returnMap, timeLimit);
        if (instanceResult.isEmpty()) {
            log.error("Could not create instance for map IDs : {}", mapIds);
            dispose();
            return;
        }
        final Instance instance = instanceResult.get();
        final Field targetField = instance.getFieldStorage().getFieldById(mapIds.get(0)).orElseThrow();
        // Resolve portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            log.error("Tried to warp to portal : {} on field ID : {}", portalName, targetField.getFieldId());
            dispose();
            return;
        }
        // Warp user
        user.warp(targetField, portalResult.get(), false, false);
    }


    // PARTY METHODS ---------------------------------------------------------------------------------------------------

    public boolean hasParty() {
        return getUser().getPartyId() != 0;
    }

    public boolean isPartyBoss() {
        return getUser().isPartyBoss();
    }

    public void partyWarpInstance(int mapId, String portalName, int returnMap, int timeLimit) {
        partyWarpInstance(List.of(mapId), portalName, returnMap, timeLimit);
    }

    public void partyWarpInstance(List<Integer> mapIds, String portalName, int returnMap, int timeLimit) {
        // Create instance
        final Optional<Instance> instanceResult = user.getConnectedServer().createInstance(mapIds, returnMap, timeLimit);
        if (instanceResult.isEmpty()) {
            log.error("Could not create instance for map IDs : {}", mapIds);
            dispose();
            return;
        }
        final Instance instance = instanceResult.get();
        final Field targetField = instance.getFieldStorage().getFieldById(mapIds.get(0)).orElseThrow();
        // Resolve portal
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            log.error("Tried to warp to portal : {} on field ID : {}", portalName, targetField.getFieldId());
            dispose();
            return;
        }
        final PortalInfo targetPortal = portalResult.get();
        // Warp user and party members in field
        user.getField().getUserPool().forEachPartyMember(user, (member) -> {
            try (var lockedMember = member.acquire()) {
                member.warp(targetField, targetPortal, false, false);
            }
        });
        user.warp(targetField, targetPortal, false, false);
    }


    // FIELD METHODS ---------------------------------------------------------------------------------------------------

    public Field getField() {
        return source.getField();
    }

    public int getFieldId() {
        return getField().getFieldId();
    }

    public String getEventState(String eventName) {
        // Resolve Event Type
        final EventType eventType = EventType.getByName(eventName);
        if (eventType == null) {
            log.error("Unknown event type provided for getEventState : {}", eventName);
            return "";
        }
        // Resolve Event State
        final Optional<EventState> eventStateResult = user.getConnectedServer().getEventState(eventType);
        return eventStateResult.map(Enum::name).orElse("");
    }

    public void dropRewards(List<List<Object>> rewardList) {
        // Resolve rewards
        final List<Reward> possibleRewards = new ArrayList<>();
        for (List<Object> rewardTuple : rewardList) {
            if (rewardTuple.size() < 4) {
                log.error("Invalid tuple length for ReactorScriptManager.dropRewards {}", rewardTuple);
                return;
            }
            final int itemId = ((Number) rewardTuple.get(0)).intValue(); // 0 if money
            final int min = ((Number) rewardTuple.get(1)).intValue();
            final int max = ((Number) rewardTuple.get(2)).intValue();
            final double prob = ((Number) rewardTuple.get(3)).doubleValue();
            final int questId = rewardTuple.size() > 4 ? ((Number) rewardTuple.get(4)).intValue() : 0;
            possibleRewards.add(new Reward(itemId, min, max, prob, questId));
        }
        // Create drops from possible rewards
        final List<Drop> drops = new ArrayList<>();
        for (Reward reward : possibleRewards) {
            // Drop probability
            if (!Util.succeedDouble(reward.getProb())) {
                continue;
            }
            // Create drop
            if (reward.isMoney()) {
                final int money = Util.getRandom(reward.getMin(), reward.getMax());
                if (money <= 0) {
                    continue;
                }
                drops.add(Drop.money(DropOwnType.USEROWN, source, money, user.getCharacterId()));
            } else {
                final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(reward.getItemId());
                if (itemInfoResult.isEmpty()) {
                    continue;
                }
                final int quantity = Util.getRandom(reward.getMin(), reward.getMax());
                final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), quantity);
                drops.add(Drop.item(DropOwnType.USEROWN, source, item, user.getCharacterId(), reward.getQuestId()));
            }
        }
        // Add drops to field
        getField().getDropPool().addDrops(drops, DropEnterType.CREATE, source.getX(), source.getY() - GameConstants.DROP_HEIGHT, 0);
    }

    public void changeBgm(String uol) {
        getField().broadcastPacket(FieldEffectPacket.changeBgm(uol));
    }

    public void spawnMob(int templateId, int appearType, int x, int y) {
        final MobAppearType mobAppearType = MobAppearType.getByValue(appearType);
        if (mobAppearType == null) {
            log.error("Unknown mob appear type received for spawnMob : {}", appearType);
        }
        spawnMob(templateId, mobAppearType != null ? mobAppearType : MobAppearType.REGEN, x, y);
    }

    private void spawnMob(int templateId, MobAppearType appearType, int x, int y) {
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(templateId);
        if (mobTemplateResult.isEmpty()) {
            log.error("Could not resolve mob template ID : {}", templateId);
            return;
        }
        final Optional<Foothold> footholdResult = getField().getFootholdBelow(x, y - GameConstants.REACTOR_SPAWN_HEIGHT);
        final Mob mob = new Mob(
                mobTemplateResult.get(),
                null,
                x,
                y,
                footholdResult.map(Foothold::getFootholdId).orElse(0)
        );
        mob.setAppearType(appearType);
        getField().getMobPool().addMob(mob);
    }


    // CONVERSATION METHODS --------------------------------------------------------------------------------------------

    public void openNpc(int templateId) {
        final Optional<NpcTemplate> npcTemplateResult = NpcProvider.getNpcTemplate(templateId);
        if (npcTemplateResult.isEmpty()) {
            log.error("Could not resolve npc ID : {}", templateId);
            return;
        }
        final String scriptName = npcTemplateResult.get().getScript();
        if (scriptName == null || scriptName.isEmpty()) {
            log.error("Could not find script for npc ID : {}", templateId);
            return;
        }
        ScriptDispatcher.startNpcScript(user, user, scriptName, templateId);
    }

    public int getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(int speakerId) {
        messageParams.add(ScriptMessageParam.OVERRIDE_SPEAKER_ID);
        this.speakerId = speakerId;
    }

    public void submitAnswer(ScriptAnswer answer) {
        answerFuture.complete(answer);
    }

    public void setNotCancellable(boolean isNotCancellable) {
        toggleParam(ScriptMessageParam.NOT_CANCELLABLE, isNotCancellable);
    }

    public void setPlayerAsSpeaker(boolean isPlayerAsSpeaker) {
        toggleParam(ScriptMessageParam.PLAYER_AS_SPEAKER, isPlayerAsSpeaker);
    }

    public void setFlipSpeaker(boolean isFlipSpeaker) {
        toggleParam(ScriptMessageParam.FLIP_SPEAKER, isFlipSpeaker);
    }

    public void sayOk(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, false, false));
        handleAnswer();
    }

    public void sayPrev(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, true, false));
        handleAnswer();
    }

    public void sayNext(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, false, true));
        handleAnswer();
    }

    public void sayBoth(String text) {
        sendMessage(ScriptMessage.say(speakerId, messageParams, text, true, true));
        handleAnswer();
    }

    public void sayImage(List<String> images) {
        sendMessage(ScriptMessage.sayImage(speakerId, messageParams, images));
        handleAnswer();
    }

    public boolean askYesNo(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKYESNO, text));
        return handleAnswer().getAction() != 0;
    }

    public boolean askAccept(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKACCEPT, text));
        return handleAnswer().getAction() != 0;
    }

    public int askMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKMENU, text));
        return handleAnswer().getAnswer();
    }

    public int askSlideMenu(String text) {
        sendMessage(ScriptMessage.ask(speakerId, messageParams, ScriptMessageType.ASKSLIDEMENU, text));
        return handleAnswer().getAnswer();
    }

    public int askAvatar(String text, List<Integer> options) {
        sendMessage(ScriptMessage.askAvatar(speakerId, messageParams, text, options));
        return handleAnswer().getAnswer();
    }

    public int askNumber(String text, int numberDefault, int numberMin, int numberMax) {
        sendMessage(ScriptMessage.askNumber(speakerId, messageParams, text, numberDefault, numberMin, numberMax));
        return handleAnswer().getAnswer();
    }

    public String askText(String text, String textDefault, int textLengthMin, int textLengthMax) {
        sendMessage(ScriptMessage.askText(speakerId, messageParams, text, textDefault, textLengthMin, textLengthMax));
        return handleAnswer().getTextAnswer();
    }

    public String askBoxText(String text, String textDefault, int textBoxColumns, int textBoxLines) {
        sendMessage(ScriptMessage.askBoxText(speakerId, messageParams, text, textDefault, textBoxColumns, textBoxLines));
        return handleAnswer().getTextAnswer();
    }

    private void toggleParam(ScriptMessageParam messageParam, boolean enabled) {
        if (enabled) {
            messageParams.add(messageParam);
        } else {
            messageParams.remove(messageParam);
        }
    }

    private void sendMessage(ScriptMessage scriptMessage) {
        scriptMemory.recordMessage(scriptMessage);
        if (user.hasDialog()) {
            throw new IllegalStateException("Tried to send script message with a dialog present");
        }
        user.setDialog(ScriptDialog.from(this));
        user.write(FieldPacket.scriptMessage(scriptMessage));
    }

    private ScriptAnswer handleAnswer() {
        // Unlock user while waiting on answer
        user.unlock();
        answerFuture = new CompletableFuture<>();
        final ScriptAnswer answer = answerFuture.join();
        answerFuture = null;
        user.lock();
        user.setDialog(null);
        // Handle answer
        if (answer.getAction() == -1 || answer.getAction() == 5) {
            dispose();
        } else if (answer.getAction() == 0 && scriptMemory.isPrevPossible()) {
            // prev message in memory
            user.write(FieldPacket.scriptMessage(scriptMemory.prevMessage()));
            return handleAnswer();
        } else if (scriptMemory.isInMemory()) {
            // next message in memory
            user.write(FieldPacket.scriptMessage(scriptMemory.nextMessage()));
            return handleAnswer();
        }
        return answer;
    }
}
