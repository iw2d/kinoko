package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.world.field.life.MovePath;
import kinoko.world.job.explorer.Bowman;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.job.staff.SuperGM;
import kinoko.world.skill.*;
import kinoko.world.user.CharacterData;
import kinoko.world.user.GuildInfo;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

public final class UserRemote {
    // CUserPool::OnUserRemotePacket -----------------------------------------------------------------------------------

    public static OutPacket move(User user, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMove);
        outPacket.encodeInt(user.getCharacterId());
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket attack(User user, Attack attack) {
        final OutPacket outPacket = OutPacket.of(attack.getHeaderType());
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(attack.mask); // nDamagePerMob | (16 * nMobCount)
        outPacket.encodeByte(user.getLevel()); // nLevel
        outPacket.encodeByte(attack.slv);
        if (attack.slv != 0) {
            outPacket.encodeInt(attack.skillId);
        }
        if (attack.skillId == Bowman.STRAFE_MM) {
            outPacket.encodeByte(attack.passiveSlv); // nPassiveSLV
            if (attack.passiveSlv != 0) {
                outPacket.encodeInt(attack.passiveSkillId); // nSkillID
            }
        }
        outPacket.encodeByte(attack.flag);
        outPacket.encodeShort(attack.actionAndDir);
        if (attack.getAction() < ActionType.NO.getValue()) {
            outPacket.encodeByte(attack.attackSpeed);
            outPacket.encodeByte(attack.mastery);
            outPacket.encodeInt(attack.bulletItemId);
            for (AttackInfo ai : attack.getAttackInfo()) {
                outPacket.encodeInt(ai.mobId);
                if (ai.mobId == 0) {
                    continue;
                }
                outPacket.encodeByte(ai.actionAndDir);
                if (attack.skillId == Thief.MESO_EXPLOSION) {
                    outPacket.encodeByte(ai.attackCount);
                    for (int i = 0; i < ai.attackCount; i++) {
                        outPacket.encodeInt(ai.damage[i]);
                    }
                } else if (attack.getDamagePerMob() > 0) {
                    for (int i = 0; i < attack.getDamagePerMob(); i++) {
                        outPacket.encodeByte(ai.critical[i]);
                        outPacket.encodeInt(ai.damage[i]);
                    }
                }
            }
            if (attack.isShootAttack()) { // nType == 212
                outPacket.encodeShort(attack.ballStartX); // ptBallStart.x
                outPacket.encodeShort(attack.ballStartY); // ptBallStart.y
            }
            if (SkillConstants.isMagicKeydownSkill(attack.skillId)) {
                outPacket.encodeInt(attack.keyDown); // tKeyDown
            } else if (attack.skillId == WildHunter.JAGUAR_OSHI_ATTACK) {
                outPacket.encodeInt(attack.swallowMobTemplateId); // dwSwallowMobTemplateID
            }
        }
        return outPacket;
    }

    public static OutPacket skillPrepare(User user, int skillId, int slv, short actionAndDir, byte attackSpeed) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserSkillPrepare);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(skillId);
        outPacket.encodeByte(slv);
        outPacket.encodeShort(actionAndDir);
        outPacket.encodeByte(attackSpeed);
        return outPacket;
    }

    public static OutPacket movingShootAttackPrepare(User user, int skillId, int slv, short actionAndDir, byte attackSpeed) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserMovingShootAttackPrepare);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(user.getLevel());
        outPacket.encodeByte(slv);
        if (slv != 0) {
            outPacket.encodeInt(skillId);
        }
        outPacket.encodeShort(actionAndDir);
        outPacket.encodeByte(attackSpeed);
        return outPacket;
    }

    public static OutPacket skillCancel(User user, int skillId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserSkillCancel);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(skillId);
        return outPacket;
    }

    public static OutPacket hit(User user, HitInfo hitInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserHit);
        outPacket.encodeInt(user.getCharacterId());

        outPacket.encodeByte(hitInfo.attackIndex); // nAttackIdx
        outPacket.encodeInt(hitInfo.damage); // nDamage
        if (hitInfo.attackIndex > -2) {
            outPacket.encodeInt(hitInfo.templateId); // dwTemplateID
            outPacket.encodeByte(hitInfo.dir); // bLeft
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
        }
        outPacket.encodeInt(hitInfo.finalDamage);
        if (hitInfo.damage == -1) {
            outPacket.encodeInt(hitInfo.missSkillId); // CUser::ShowSkillSpecialEffect, CAvatar::SetEmotion for 4120002, 4220002
        }
        return outPacket;
    }

    public static OutPacket emotion(User user, int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserEmotion);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }

    public static OutPacket setActiveEffectItem(User user, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserSetActiveEffectItem);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(itemId); // nItemID
        return outPacket;
    }

    public static OutPacket showUpgradeTombEffect(User user, int itemId, int x, int y) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserShowUpgradeTombEffect);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeInt(x); // nPosX
        outPacket.encodeInt(y); // nPosY
        return outPacket;
    }

    public static OutPacket setActivePortableChair(User user, int itemId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserSetActivePortableChair);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(itemId); // nPortableChairID
        return outPacket;
    }

    public static OutPacket avatarModified(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserAvatarModified);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeByte(-1); // flag : AVATAR_LOOK = 0x1, AVATAR_SPEED = 0x2, AVATAR_CHOCO = 0x4
        final CharacterData characterData = user.getCharacterData();
        characterData.getAvatarLook().encode(outPacket);
        outPacket.encodeByte(user.getSecondaryStat().getSpeed());
        outPacket.encodeByte(0); // nCount
        characterData.getCoupleRecord().encodeForRemote(outPacket);
        outPacket.encodeInt(0); // nCompletedSetItemID
        return outPacket;
    }

    public static OutPacket effect(User user, Effect effect) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserEffectRemote);
        outPacket.encodeInt(user.getCharacterId());
        effect.encode(outPacket);
        return outPacket;
    }

    public static OutPacket temporaryStatSet(User user, SecondaryStat secondaryStat, BitFlag<CharacterTemporaryStat> flag) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTemporaryStatSet);
        outPacket.encodeInt(user.getCharacterId());
        secondaryStat.encodeForRemote(flag, outPacket);
        outPacket.encodeShort(0); // tDelay
        return outPacket;
    }

    public static OutPacket temporaryStatReset(User user, BitFlag<CharacterTemporaryStat> flag) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserTemporaryStatReset);
        outPacket.encodeInt(user.getCharacterId());
        flag.encode(outPacket);
        return outPacket;
    }

    public static OutPacket receiveHp(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserHP);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(user.getHp());
        outPacket.encodeInt(user.getMaxHp());
        return outPacket;
    }

    public static OutPacket guildNameChanged(User user, GuildInfo guildInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserGuildNameChanged);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeString(guildInfo != null ? guildInfo.getGuildName() : ""); // sGuildName
        return outPacket;
    }

    public static OutPacket guildMarkChanged(User user, GuildInfo guildInfo) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserGuildMarkChanged);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeShort(guildInfo != null ? guildInfo.getMarkBg() : 0); // nGuildMarkBg
        outPacket.encodeByte(guildInfo != null ? guildInfo.getMarkBgColor() : 0); // nGuildMarkBgColor
        outPacket.encodeShort(guildInfo != null ? guildInfo.getMark() : 0); // nGuildMark
        outPacket.encodeByte(guildInfo != null ? guildInfo.getMarkColor() : 0); // nGuildMarkColor
        return outPacket;
    }

    public static OutPacket throwGrenade(User user, Skill skill) {
        final OutPacket outPacket = OutPacket.of(OutHeader.UserThrowGrenade);
        outPacket.encodeInt(user.getCharacterId());
        outPacket.encodeInt(skill.positionX);
        outPacket.encodeInt(skill.positionY);
        outPacket.encodeInt(skill.keyDown); // tKeyDown
        outPacket.encodeInt(skill.skillId); // nSkillID
        outPacket.encodeInt(skill.slv); // nSLV
        return outPacket;
    }
}
