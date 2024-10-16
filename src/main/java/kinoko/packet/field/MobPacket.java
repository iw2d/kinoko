package kinoko.packet.field;

import kinoko.provider.mob.MobSkill;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.mob.*;

import java.util.Set;

public final class MobPacket {
    // CMobPool::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket mobEnterField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobEnterField);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(1); // nCalcDamageIndex
        outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
        mob.getMobStat().encodeTemporary(outPacket);
        mob.encode(outPacket);
        return outPacket;
    }

    public static OutPacket mobLeaveField(Mob mob, MobLeaveType leaveType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobLeaveField);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(leaveType.getValue()); // nDeadType
        if (leaveType == MobLeaveType.SWALLOW) {
            outPacket.encodeInt(mob.getSwallowCharacterId()); // dwSwallowCharacterID
        }
        return outPacket;
    }

    public static OutPacket mobChangeController(Mob mob, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobChangeController);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(mob.getId()); // dwMobID
        if (forController) {
            outPacket.encodeByte(1); // nCalcDamageIndex
            // CMobPool::SetLocalMob
            outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
            mob.getMobStat().encodeTemporary(outPacket);
            mob.encode(outPacket);
        }
        return outPacket;
    }


    // CMobPool::OnMobPacket -------------------------------------------------------------------------------------------

    public static OutPacket mobMove(Mob mob, MobAttackInfo mai, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobMove);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(false); // bNotForceLandingWhenDiscard
        outPacket.encodeByte(false); // bNotChangeAction
        outPacket.encodeByte(mai.actionMask);
        outPacket.encodeByte(mai.actionAndDir);
        // CMob::TARGETINFO
        if (mai.isSkill) {
            outPacket.encodeByte(mai.skillId);
            outPacket.encodeByte(mai.slv);
            outPacket.encodeByte(mai.option);
        } else {
            outPacket.encodeInt(mai.targetInfo);
        }
        // aMultiTargetForBall
        outPacket.encodeInt(mai.multiTargetForBall.size());
        for (var target : mai.multiTargetForBall) {
            outPacket.encodeInt(target.getLeft()); // x
            outPacket.encodeInt(target.getRight()); // y
        }
        // aRandTimeforAreaAttack
        outPacket.encodeInt(mai.randTimeForAreaAttack.size());
        for (int value : mai.randTimeForAreaAttack) {
            outPacket.encodeInt(value);
        }
        // CMovePath::OnMovePacket
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket mobCtrlAck(Mob mob, short mobCtrlSn, boolean nextAttackPossible, MobSkill mobSkill) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobCtrlAck);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeShort(mobCtrlSn); // nMobCtrlSN
        outPacket.encodeByte(nextAttackPossible); // bNextAttackPossible
        outPacket.encodeShort(mob.getMp()); // nMP
        outPacket.encodeByte(mobSkill != null ? mobSkill.getSkillId() : 0); // nSkillCommand
        outPacket.encodeByte(mobSkill != null ? mobSkill.getSkillLevel() : 0); // nSLV
        return outPacket;
    }

    public static OutPacket mobStatSet(Mob mob, MobStat mobStat, BitFlag<MobTemporaryStat> flag, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobStatSet);
        outPacket.encodeInt(mob.getId()); // dwMobID
        // CMob::ProcessStatSet
        mobStat.encodeTemporary(flag, outPacket);
        outPacket.encodeShort(delay); // tDelay
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat || bDoomReservedSN
        return outPacket;
    }

    public static OutPacket mobStatReset(Mob mob, BitFlag<MobTemporaryStat> flag, Set<BurnedInfo> resetBurnedInfos) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobStatReset);
        outPacket.encodeInt(mob.getId()); // dwMobID
        flag.encode(outPacket); // uFlagReset
        if (flag.hasFlag(MobTemporaryStat.Burned)) {
            outPacket.encodeInt(resetBurnedInfos.size());
            for (BurnedInfo burnedInfo : resetBurnedInfos) {
                outPacket.encodeInt(burnedInfo.getCharacterId()); // dwCharacterID
                outPacket.encodeInt(burnedInfo.getSkillId()); // nSkillID
            }
        }
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat
        return outPacket;
    }

    public static OutPacket mobAffected(Mob mob, int skillId, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobAffected);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeShort(delay); // tStart = delay + update_time
        return outPacket;
    }

    public static OutPacket mobDamaged(Mob mob, int damage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobDamaged);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(0); // (byte != 2) -> CMob::ShowDamage
        outPacket.encodeInt(damage); // nDamage
        if (mob.isDamagedByMob()) { // this->m_pTemplate->bDamagedByMob
            outPacket.encodeInt(mob.getHp());
            outPacket.encodeInt(mob.getMaxHp());
        }
        return outPacket;
    }

    public static OutPacket mobSpecialEffectBySkill(Mob mob, int skillId, int characterId, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobSpecialEffectBySkill);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(characterId); // dwCharacterID, only used for mortal blow?
        outPacket.encodeShort(delay); // tDelay
        return outPacket;
    }

    public static OutPacket mobHpIndicator(Mob mob, int percentage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobHPIndicator);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(percentage); // nHPpercentage
        return outPacket;
    }

    public static OutPacket mobCatchEffect(Mob mob, boolean success, boolean delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobCatchEffect);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(delay); // tDelay = bool ? 270 : 0
        return outPacket;
    }

    public static OutPacket mobEffectByItem(Mob mob, int itemId, boolean success) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobEffectByItem);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeByte(success); // bSuccess
        return outPacket;
    }

    public static OutPacket mobNextAttack(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobNextAttack);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeInt(0); // ignored
        return outPacket;
    }
}
