package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.quest.QuestResult;

public final class UserLocalPacket {
    public static OutPacket userSitResult(boolean sit, short fieldSeatId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SIT_RESULT);
        outPacket.encodeByte(sit);
        if (sit) {
            outPacket.encodeShort(fieldSeatId);
        }
        return outPacket;
    }

    public static OutPacket userQuestResult(QuestResult questResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_QUEST_RESULT);
        questResult.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userChatMsg(int type, String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT_MSG);
        outPacket.encodeShort(type); // lType
        outPacket.encodeString(text); // sChat
        return outPacket;
    }
}
