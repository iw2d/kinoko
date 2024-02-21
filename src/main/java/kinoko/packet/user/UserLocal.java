package kinoko.packet.user;

import kinoko.packet.user.effect.Effect;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.quest.QuestResult;

public final class UserLocal {
    // CUserPool::OnUserLocalPacket ------------------------------------------------------------------------------------

    public static OutPacket sitResult(boolean sit, short fieldSeatId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SIT_RESULT);
        outPacket.encodeByte(sit);
        if (sit) {
            outPacket.encodeShort(fieldSeatId);
        }
        return outPacket;
    }

    public static OutPacket emotion(int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EMOTION_LOCAL);
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }

    public static OutPacket effect(Effect effect) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EFFECT_LOCAL);
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket teleport(boolean exclRequestSent, int portalId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EFFECT_LOCAL);
        outPacket.encodeByte(exclRequestSent); // bool -> bExclRequestSent = 0
        outPacket.encodeByte(portalId);
        return outPacket;
    }

    public static OutPacket questResult(QuestResult questResult) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_QUEST_RESULT);
        questResult.encode(outPacket);
        return outPacket;
    }

    public static OutPacket noticeMsg(String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_NOTICE_MSG);
        outPacket.encodeString(text); // sMsg
        return outPacket;
    }

    public static OutPacket chatMsg(ChatType type, String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT_MSG);
        outPacket.encodeShort(type.getValue()); // lType
        outPacket.encodeString(text); // sChat
        return outPacket;
    }
}
