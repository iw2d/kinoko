package kinoko.server.script;

import kinoko.packet.field.FieldEffectPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.world.field.Field;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.InventoryOperation;
import kinoko.world.item.Item;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * The inheritors of this abstract class will be the respective interfaces for the different types of Python scripts.
 * The utility methods implemented by these classes are designed to be executed inside the Python context, which
 * acquires and holds onto the user's lock during its execution, only releasing it while waiting for the user input in
 * the case of {@link NpcScriptManager}.
 */
public abstract class ScriptManager {
    protected static final Logger log = LogManager.getLogger(ScriptManager.class);
    protected final User user;

    public ScriptManager(User user) {
        this.user = user;
    }

    public abstract void disposeManager();

    public User getUser() {
        return user;
    }


    // UTILITY METHODS --------------------------------------------------------------------------------------------

    public final void dispose() {
        user.dispose();
        disposeManager();
    }

    public final void warp(int fieldId) {
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

    public final void warp(int fieldId, String portalName) {
        final Optional<Field> fieldResult = user.getConnectedServer().getFieldById(fieldId);
        if (fieldResult.isEmpty()) {
            log.error("Could not resolve field ID : {}", fieldId);
            dispose();
            return;
        }
        final Field targetField = fieldResult.get();
        final Optional<PortalInfo> portalResult = targetField.getPortalByName(portalName);
        if (portalResult.isEmpty()) {
            log.error("Tried to warp to portal : {} on field ID : {}", portalName, targetField.getFieldId());
            dispose();
            return;
        }
        user.warp(fieldResult.get(), portalResult.get(), false, false);
    }

    public final void message(String message) {
        user.write(MessagePacket.system(message));
    }

    public void broadcastMessage(String message) {
        user.getField().broadcastPacket(MessagePacket.system(message));
    }

    public final void playPortalSE() {
        user.write(UserLocal.effect(Effect.playPortalSE()));
    }

    public final void avatarOriented(String effectPath) {
        user.write(UserLocal.effect(Effect.avatarOriented(effectPath)));
        user.dispose();
    }

    public final void squibEffect(String effectPath) {
        user.write(UserLocal.effect(Effect.squibEffect(effectPath)));
        user.dispose();
    }

    public final void balloonMsg(String text, int width, int duration) {
        user.write(UserLocal.balloonMsg(text, width, duration));
        user.dispose();
    }

    public final void tutorMsg(int index, int duration) {
        user.write(UserLocal.tutorMsg(index, duration));
        user.dispose();
    }

    public final void setDirectionMode(boolean set, int delay) {
        user.write(UserLocal.setDirectionMode(set, delay));
    }

    public final void screenEffect(String effectPath) {
        user.write(FieldEffectPacket.screen(effectPath));
    }

    public final int getFieldId() {
        return user.getField().getFieldId();
    }


    // STAT METHODS ----------------------------------------------------------------------------------------------------

    public final int getGender() {
        return user.getGender();
    }

    public final int getLevel() {
        return user.getLevel();
    }

    public final int getJob() {
        return user.getJob();
    }

    public final int getHp() {
        return user.getHp();
    }

    public final void setHp(int hp) {
        user.setHp(hp);
    }

    public final void addExp(int exp) {
        user.addExp(exp);
        user.write(MessagePacket.incExp(exp, 0, true, true));
    }


    // INVENTORY METHODS -----------------------------------------------------------------------------------------------

    public final boolean addMoney(int money) {
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(money)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
        user.write(MessagePacket.incMoney(money));
        return true;
    }

    public final boolean addItem(int itemId) {
        return addItem(itemId, 1);
    }

    public final boolean addItem(int itemId, int quantity) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            log.error("Could not resolve item info for item ID : {}", itemId);
            return false;
        }
        final ItemInfo ii = itemInfoResult.get();
        final Item item = ii.createItem(user.getNextItemSn(), Math.min(quantity, ii.getSlotMax()));
        final Optional<List<InventoryOperation>> addItemResult = user.getInventoryManager().addItem(item);
        if (addItemResult.isEmpty()) {
            return false;
        }
        user.write(WvsContext.inventoryOperation(addItemResult.get(), true));
        user.write(UserLocal.effect(Effect.gainItem(item)));
        return true;
    }

    public final boolean removeItem(int itemId, int quantity) {
        final Optional<List<InventoryOperation>> removeItemResult = user.getInventoryManager().removeItem(itemId, quantity);
        if (removeItemResult.isPresent()) {
            user.write(WvsContext.inventoryOperation(removeItemResult.get(), true));
            user.write(UserLocal.effect(Effect.gainItem(itemId, -quantity)));
            return true;
        } else {
            return false;
        }
    }

    public final boolean hasItem(int itemId) {
        return hasItem(itemId, 1);
    }

    public final boolean hasItem(int itemId, int quantity) {
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
}
