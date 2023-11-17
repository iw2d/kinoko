package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.packet.user.UserLocalPacket;
import kinoko.packet.user.UserRemotePacket;
import kinoko.provider.QuestProvider;
import kinoko.provider.quest.QuestInfo;
import kinoko.server.ServerConfig;
import kinoko.server.command.CommandProcessor;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import kinoko.world.quest.QuestAction;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(User user, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final MovePath movePath = MovePath.decode(inPacket);
        user.getField().broadcastPacket(UserRemotePacket.userMove(user.getCharacterId(), movePath), user);
    }

    @Handler(InHeader.USER_SIT)
    public static void handleUserSit(User user, InPacket inPacket) {
        final short fieldSeatId = inPacket.decodeShort();
        user.write(UserLocalPacket.sitResult(fieldSeatId != -1, fieldSeatId));
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(user, text)) {
            return;
        }

        user.getField().broadcastPacket(UserCommonPacket.userChat(user.getCharacterId(), 0, text, onlyBalloon));
    }

    @Handler(InHeader.USER_QUEST_ACTION)
    public static void handleUserQuestAction(User user, InPacket inPacket) {
        final byte actionType = inPacket.decodeByte();
        final int questId = Short.toUnsignedInt(inPacket.decodeShort()); // usQuestID

        final Optional<QuestInfo> questInfoResult = QuestProvider.getQuestInfo(questId);
        if (questInfoResult.isEmpty()) {
            log.error("[UserHandler] Could not retrieve quest ID : {}", questId);
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
}
