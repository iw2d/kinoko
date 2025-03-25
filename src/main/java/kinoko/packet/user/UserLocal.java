package kinoko.packet.user;

import kinoko.server.dialog.UIType;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.job.explorer.Bowman;
import kinoko.world.user.effect.Effect;

public final class UserLocal {
    // CUserPool::OnUserLocalPacket ------------------------------------------------------------------------------------

    public static OutPacket sitResult(boolean sit, short fieldSeatId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserSitResult);
        outPacket.encodeByte(sit);
        if (sit) {
            outPacket.encodeShort(fieldSeatId);
        }
        return outPacket;
    }

    public static OutPacket emotion(int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserEmotionLocal);
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }

    public static OutPacket effect(Effect effect) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserEffectLocal);
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket teleport(boolean exclRequest, int portalId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTeleport);
        outPacket.encodeByte(exclRequest); // bool -> bExclRequestSent = 0
        outPacket.encodeByte(portalId);
        return outPacket;
    }

    public static OutPacket balloonMsg(String text, int width, int duration) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserBalloonMsg);
        outPacket.encodeString(text); // str
        outPacket.encodeShort(width); // nWidth
        outPacket.encodeShort(duration); // tTimeout = 1000 * short
        outPacket.encodeByte(true); // avatar oriented, if false: int, int
        return outPacket;
    }

    public static OutPacket openUI(UIType type) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserOpenUI);
        outPacket.encodeByte(type.getValue());
        return outPacket;
    }

    public static OutPacket setDirectionMode(boolean set, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SetDirectionMode);
        outPacket.encodeByte(set);
        outPacket.encodeInt(delay); // tAfterLeaveDirectionMode
        return outPacket;
    }

    public static OutPacket hireTutor(boolean hire) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserHireTutor);
        outPacket.encodeByte(hire);
        return outPacket;
    }

    public static OutPacket tutorMsg(int index, int duration) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTutorMsg);
        outPacket.encodeByte(true);
        outPacket.encodeInt(index); // nIdx
        outPacket.encodeInt(duration); // nDuration
        return outPacket;
    }

    public static OutPacket tutorMsg(String message, int width, int duration) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTutorMsg);
        outPacket.encodeByte(false);
        outPacket.encodeString(message);
        outPacket.encodeInt(width); // nWidth
        outPacket.encodeInt(duration); // nDuration
        return outPacket;
    }

    public static OutPacket incCombo(int combo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.IncCombo);
        outPacket.encodeInt(combo); // nCombo
        return outPacket;
    }

    public static OutPacket openSkillGuide() {
        return OutPacket.of(OutHeader.UserOpenSkillGuide);
    }

    public static OutPacket resignQuestReturn(int questId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.ResignQuestReturn);
        outPacket.encodeShort(questId); // usQuestID
        return outPacket;
    }

    public static OutPacket noticeMsg(String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserNoticeMsg);
        outPacket.encodeString(text); // sMsg
        return outPacket;
    }

    public static OutPacket chatMsg(ChatType type, String text) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserChatMsg);
        outPacket.encodeShort(type.getValue()); // lType
        outPacket.encodeString(text); // sChat
        return outPacket;
    }

    public static OutPacket timeBombAttack(int skillId, int x, int y, int impactDegree, int damage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTimeBombAttack);
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(x); // nTimeBombX
        outPacket.encodeInt(y); // nTimeBombY
        outPacket.encodeInt(impactDegree); // nUserImpactDeg
        outPacket.encodeInt(damage); // nDamage
        return outPacket;
    }

    public static OutPacket requestVengeance() {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserRequestVengeance);
        outPacket.encodeInt(Bowman.VENGEANCE);
        return outPacket;
    }

    public static OutPacket requestExJablin() {
        return OutPacket.of(OutHeader.UserRequestExJablin);
    }

    public static OutPacket skillCooltimeSet(int skillId, int remainSeconds) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SkillCooltimeSet);
        outPacket.encodeInt(skillId);
        outPacket.encodeInt(remainSeconds); // usRemainSec
        return outPacket;
    }
}
