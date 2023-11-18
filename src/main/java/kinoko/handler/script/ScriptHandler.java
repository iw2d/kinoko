package kinoko.handler.script;

import kinoko.handler.Handler;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
            }
            case COMPLETE_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                if (!questInfo.isAutoAlert()) {
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                }
                final int index = inPacket.decodeInt(); // nIdx
            }
            case RESIGN_QUEST -> {
                // TODO
            }
            case START_SCRIPT, COMPLETE_SCRIPT -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
            }
        }
    }

    private enum QuestAction {
        LOST_ITEM(0),
        ACCEPT_QUEST(1),
        COMPLETE_QUEST(2),
        RESIGN_QUEST(3),
        START_SCRIPT(4),
        COMPLETE_SCRIPT(5);

        private final byte value;

        QuestAction(int value) {
            this.value = (byte) value;
        }

        public final byte getValue() {
            return value;
        }

        public static QuestAction getByValue(int value) {
            for (QuestAction action : values()) {
                if (action.getValue() == value) {
                    return action;
                }
            }
            return null;
        }
    }
}
