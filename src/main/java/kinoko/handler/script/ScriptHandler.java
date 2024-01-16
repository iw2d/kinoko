package kinoko.handler.script;

import kinoko.handler.Handler;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.script.ScriptAnswer;
import kinoko.server.script.ScriptDispatcher;
import kinoko.server.script.ScriptManager;
import kinoko.server.script.ScriptMessageType;
import kinoko.world.life.Life;
import kinoko.world.life.npc.Npc;
import kinoko.world.quest.QuestAction;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class ScriptHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.USER_SELECT_NPC)
    public static void handleUserSelectNpc(User user, InPacket inPacket) {
        final int npcId = inPacket.decodeInt(); // dwNpcId
        final short x = inPacket.decodeShort(); // ptUserPos.x
        final short y = inPacket.decodeShort(); // ptUserPos.y

        final Optional<Life> lifeResult = user.getField().getLifeById(npcId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Npc npc)) {
            log.error("Tried to select invalid npc ID : {}", npcId);
            return;
        }

        if (npc.getScript().isEmpty()) {
            log.error("Npc template ID {} does not have an associated script", npc.getTemplateId());
            return;
        }

        ScriptDispatcher.startNpcScript(user, npc.getTemplateId(), npc.getScript().get());
    }

    @Handler(InHeader.USER_SCRIPT_MESSAGE_ANSWER)
    public static void handleUserScriptMessageAnswer(User user, InPacket inPacket) {
        final byte type = inPacket.decodeByte(); // nMsgType
        final byte action = inPacket.decodeByte();

        final ScriptMessageType lastMessageType = ScriptMessageType.getByValue(type);

        final Optional<ScriptManager> scriptManagerResult = ScriptDispatcher.getScriptManager(user);
        if (scriptManagerResult.isEmpty()) {
            log.error("Could not retrieve ScriptManager instance for character ID : {}", user.getCharacterId());
            return;
        }
        final ScriptManager scriptManager = scriptManagerResult.get();
        switch (lastMessageType) {
            case SAY, SAY_IMAGE, ASK_YES_NO, ASK_YES_NO_QUEST -> {
                scriptManager.submitAnswer(ScriptAnswer.withAction(action));
            }
            case ASK_TEXT, ASK_BOX_TEXT -> {
                if (action == 1) {
                    final String answer = inPacket.decodeString(); // sInputStr_Result
                    scriptManager.submitAnswer(ScriptAnswer.withTextAnswer(action, answer));
                }
            }
            case ASK_NUMBER, ASK_MENU, ASK_SLIDE_MENU -> {
                if (action == 1) {
                    final int answer = inPacket.decodeInt(); // nInputNo_Result | nSelect
                    scriptManager.submitAnswer(ScriptAnswer.withAnswer(action, answer));
                }
            }
            // nSelect
            case ASK_AVATAR, ASK_MEMBER_SHOP_AVATAR -> {
                if (action == 1) {
                    final byte answer = inPacket.decodeByte(); // nAvatarIndex
                    scriptManager.submitAnswer(ScriptAnswer.withAnswer(action, answer));
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

    @Handler(InHeader.USER_PORTAL_SCRIPT_REQUEST)
    public static void handleUserPortalScriptRequest(User user, InPacket inPacket) {
        user.dispose(); // TODO
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

        final QuestAction questAction = QuestAction.getByValue(action);
        switch (questAction) {
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
                // ScriptManager.start(user, questId, templateId);
            }
            case null -> {
                log.error("Unknown quest action type : {}", action);
            }
            default -> {
                log.error("Unhandled quest action type : {}", questAction);
            }
        }
    }
}
