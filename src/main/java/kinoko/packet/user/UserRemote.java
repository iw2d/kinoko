package kinoko.packet.user;

import kinoko.packet.user.effect.Effect;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.skill.*;
import kinoko.world.user.User;

public final class UserRemote {
    // CUserPool::OnUserRemotePacket -----------------------------------------------------------------------------------

    public static OutPacket move(User user, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_MOVE);
        outPacket.encodeInt(user.getCharacterId());
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket attack(User user, Attack a) {
        final OutPacket outPacket = OutPacket.of(a.getHeaderType());
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(a.mask); // nDamagePerMob | (16 * nMobCount)
        outPacket.encodeByte(user.getCharacterStat().getLevel()); // nLevel
        outPacket.encodeByte(a.slv);
        if (a.slv != 0) {
            outPacket.encodeInt(a.skillId);
        }
        if (a.skillId == 3211006) {
            outPacket.encodeByte(0); // nPassiveSLV
            if (false) {
                outPacket.encodeInt(0); // nSkillID
            }
        }
        outPacket.encodeByte(a.flag);
        outPacket.encodeShort(a.actionAndDir);
        if (a.getAction() < ActionType.NO.getValue()) {
            outPacket.encodeByte(a.attackSpeed);
            outPacket.encodeByte(a.mastery);
            outPacket.encodeInt(a.bulletItemId);
            for (AttackInfo ai : a.getAttackInfo()) {
                outPacket.encodeInt(ai.mobId);
                if (ai.mobId == 0) {
                    continue;
                }
                if (a.skillId == 4211006) {

                } else if (a.getDamagePerMob() > 0) {
                    for (int i = 0; i < a.getDamagePerMob(); i++) {
                        outPacket.encodeByte(ai.critical[i]);
                        outPacket.encodeInt(ai.damage[i]);
                    }
                }
            }
            if (a.getHeaderType() == OutHeader.USER_SHOOT_ATTACK) { // nType == 212
                outPacket.encodeShort(0); // ptBallStart.x
                outPacket.encodeShort(0); // ptBallStart.y
            }
            if (SkillConstants.isMagicKeydownSkill(a.skillId)) {
                outPacket.encodeInt(a.keyDown); // tKeyDown
            } else if (a.skillId == 33101007) {
                outPacket.encodeInt(0); // dwSwallowMobTemplateID
            }
        }
        return outPacket;
    }

    public static OutPacket skillPrepare(User user, int skillId, int slv, short actionAndDir, byte attackSpeed) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SKILL_PREPARE);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(skillId);
        outPacket.encodeByte(slv);
        outPacket.encodeShort(actionAndDir);
        outPacket.encodeByte(attackSpeed);
        return outPacket;
    }

    public static OutPacket movingShootAttackPrepare(User user, int skillId, int slv, short actionAndDir, byte attackSpeed) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_MOVING_SHOOT_ATTACK_PREPARE);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(user.getCharacterStat().getLevel());
        outPacket.encodeByte(slv);
        if (slv != 0) {
            outPacket.encodeInt(skillId);
        }
        outPacket.encodeShort(actionAndDir);
        outPacket.encodeByte(attackSpeed);
        return outPacket;
    }

    public static OutPacket skillCancel(User user, int skillId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SKILL_PREPARE);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(skillId);
        return outPacket;
    }

    public static OutPacket hit(User user, HitInfo hitInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_HIT);
        outPacket.encodeInt(user.getCharacterId());

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

    public static OutPacket emotion(User user, int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EMOTION);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }

    public static OutPacket setActivePortableChair(User user, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SET_ACTIVE_PORTABLE_CHAIR);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(itemId); // nPortableChairID
        return outPacket;
    }

    public static OutPacket effect(User user, Effect effect) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_SET_ACTIVE_PORTABLE_CHAIR);
        outPacket.encodeInt(user.getCharacterId());
        effect.encode(outPacket);
        return outPacket;
    }
}
