package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.life.MovePath;
import kinoko.world.user.HitInfo;

public final class UserRemotePacket {
    public static OutPacket userMove(int characterId, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT);
        outPacket.encodeInt(characterId);
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userHit(int characterId, HitInfo hitInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_HIT);
        outPacket.encodeInt(characterId);

        final int attackIndex = hitInfo.hitType.getValue();
        outPacket.encodeByte(attackIndex); // nAttackIdx
        outPacket.encodeInt(hitInfo.damage); // nDamage
        if (attackIndex > -2) {
            outPacket.encodeInt(hitInfo.templateId); // dwTemplateID
            outPacket.encodeByte(hitInfo.dir); // bLeft
        }
        outPacket.encodeByte(hitInfo.reflect);
        if (hitInfo.reflect != 0) {
            outPacket.encodeByte(hitInfo.powerGuard);
            outPacket.encodeInt(hitInfo.reflectMobId);
            outPacket.encodeByte(hitInfo.reflectMobAction); // nHitAction
            outPacket.encodeShort(hitInfo.reflectMobX); // ptHit.x
            outPacket.encodeShort(hitInfo.reflectMobY); // ptHit.y
        }
        outPacket.encodeByte(hitInfo.guard); // bGuard
        outPacket.encodeByte(hitInfo.stance);
        outPacket.encodeInt(hitInfo.damage);
        if (hitInfo.damage == -1) {
            outPacket.encodeInt(hitInfo.missSkillId); // CUser::ShowSkillSpecialEffect, CAvatar::SetEmotion for 4120002, 4220002
        }
        return outPacket;
    }

    public static OutPacket userEmotion(int characterId, int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EMOTION);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }

    public static OutPacket userSetActivePortableChair(int characterId, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SET_ACTIVE_PORTABLE_CHAIR);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(itemId); // nPortableChairID
        return outPacket;
    }
}
