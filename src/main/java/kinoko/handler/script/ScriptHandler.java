package kinoko.handler.script;

import kinoko.handler.Handler;
import kinoko.packet.script.ScriptMessage;
import kinoko.packet.script.ScriptMessageType;
import kinoko.packet.script.ScriptPacket;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.script.ScriptManager;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Set;

public final class ScriptHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.QUEST_ACTION)
    public static void handleUserQuestAction(User user, InPacket inPacket) {
        final byte actionType = inPacket.decodeByte();
        final int questId = Short.toUnsignedInt(inPacket.decodeShort()); // usQuestID

        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            log.error("Could not retrieve quest ID : {}", questId);
            return;
        }
        final QuestInfo questInfo = questInfoResult.get();

        final QuestAction action = QuestAction.getByValue(actionType);
        if (action == null) {
            log.error("Unknown quest action type : {}", actionType);
            return;
        }
        switch (action) {
            case LOST_ITEM -> {
                // TODO
            }
            case ACCEPT_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                // user.getCharacterData().getQuestManager().acceptQuest(questId);
            }
            case COMPLETE_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final int index = inPacket.decodeInt(); // nIdx
                // user.getCharacterData().getQuestManager().completeQuest(questId);
            }
            case RESIGN_QUEST -> {
                // TODO
                // user.getCharacterData().getQuestManager().resignQuest(questId);
            }
            case START_SCRIPT, COMPLETE_SCRIPT -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
                ScriptManager.startQuestScript(user, questId, templateId);
            }
        }
    }

    @Handler(InHeader.SCRIPT_START)
    public static void handleScriptStart(User user, InPacket inPacket) {
        final int npcId = inPacket.decodeInt(); // dwNpcId
        final short x = inPacket.decodeShort(); // ptUserPos.x
        final short y = inPacket.decodeShort(); // ptUserPos.y

        user.write(ScriptPacket.scriptMessage(ScriptMessage.say(npcId, Set.of(), "hi", false, false)));
    }

    @Handler(InHeader.SCRIPT_ACTION)
    public static void handleScriptAction(User user, InPacket inPacket) {
        final byte type = inPacket.decodeByte(); // nMsgType
        final ScriptMessageType lastMessageType = ScriptMessageType.getByValue(type);
        switch (lastMessageType) {
            case SAY, SAY_IMAGE, ASK_YES_NO, ASK_YES_NO_QUEST -> {
                final byte action = inPacket.decodeByte();
            }
            case ASK_TEXT, ASK_BOX_TEXT -> {
                if (inPacket.decodeByte() == 1) {
                    final String answer = inPacket.decodeString(); // sInputStr_Result
                }
            }
            case ASK_NUMBER -> {
                if (inPacket.decodeByte() == 1) {
                    final int answer = inPacket.decodeInt(); // nInputNo_Result
                }
            }
            case ASK_MENU, ASK_SLIDE_MENU -> {
                if (inPacket.decodeByte() == 1) {
                    final int selection = inPacket.decodeInt(); // nSelect
                }
            }
            case ASK_AVATAR, ASK_MEMBER_SHOP_AVATAR -> {
                if (inPacket.decodeByte() == 1) {
                    final byte selection = inPacket.decodeByte(); // nAvatarIndex
                }
            }
            case null -> {
                log.error("Unknown script message type {}", type);
            }
            default -> {
                log.error("Unhandled script message type {}", lastMessageType);
            }
        }
    }
}
