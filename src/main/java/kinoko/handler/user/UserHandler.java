package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.server.ServerConfig;
import kinoko.server.client.Client;
import kinoko.server.command.CommandProcessor;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import kinoko.world.quest.QuestAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(Client c, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        MovePath.decode(inPacket);
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(Client c, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(c, text)) {
            return;
        }

        c.write(UserCommonPacket.userChat(c.getUser().getCharacterId(), 0, text, onlyBalloon));
    }

    @Handler(InHeader.USER_QUEST_ACTION)
    public static void handleUserQuestAction(Client c, InPacket inPacket) {
        final byte actionType = inPacket.decodeByte();
        final int questId = Short.toUnsignedInt(inPacket.decodeShort()); // usQuestID

        final QuestAction action = QuestAction.getByValue(actionType);
        switch (action) {
            case LOST_ITEM -> {
                // TODO
            }
            case ACCEPT_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                // if (!isAutoAlertQuest())
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
            }
            case COMPLETE_QUEST -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final int itemPos = inPacket.decodeInt(); // CWvsContext.m_nQuestDeliveryItemPos
                // if (!isAutoAlertQuest())
                    final short x = inPacket.decodeShort(); // ptUserPos.x
                    final short y = inPacket.decodeShort(); // ptUserPos.y
                final int index = inPacket.decodeInt(); // nIdx
            }
            case RESIGN_QUEST -> {

            }
            case START_SCRIPT, COMPLETE_SCRIPT -> {
                final int templateId = inPacket.decodeInt(); // dwNpcTemplateID
                final short x = inPacket.decodeShort(); // ptUserPos.x
                final short y = inPacket.decodeShort(); // ptUserPos.y
            }
        }


    }
}
