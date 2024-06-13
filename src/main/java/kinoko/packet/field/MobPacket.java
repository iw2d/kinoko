package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.mob.*;

import java.util.Map;
import java.util.Set;

public final class MobPacket {
    // CMobPool::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket mobEnterField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobEnterField);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(1); // nCalcDamageIndex
        outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
        MobStat.encode(outPacket, mob.getMobStat());
        mob.encode(outPacket);
        return outPacket;
    }

    public static OutPacket mobLeaveField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobLeaveField);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(1); // nDeadType
        // if nDeadType == 4, encodeInt(dwSwallowCharacterID);
        return outPacket;
    }

    public static OutPacket mobChangeController(Mob mob, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobChangeController);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(mob.getId()); // dwMobId
        if (forController) {
            outPacket.encodeByte(1); // nCalcDamageIndex
            // CMobPool::SetLocalMob
            outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
            MobStat.encode(outPacket, mob.getMobStat());
            mob.encode(outPacket);
        }
        return outPacket;
    }


    // CMobPool::OnMobPacket -------------------------------------------------------------------------------------------

    public static OutPacket mobMove(Mob mob, MobAttackInfo mai, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobMove);
        outPacket.encodeInt(mob.getId()); // dwMobId
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

    public static OutPacket mobCtrlAck(Mob mob, short mobCtrlSn, boolean nextAttackPossible, MobAttackInfo mai) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobCtrlAck);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeShort(mobCtrlSn); // nMobCtrlSN
        outPacket.encodeByte(nextAttackPossible); // bNextAttackPossible
        outPacket.encodeShort(mob.getMp()); // nMP
        outPacket.encodeByte(mai.skillId); // nSkillCommand
        outPacket.encodeByte(mai.slv); // nSLV
        return outPacket;
    }

    public static OutPacket mobStatSet(Mob mob, Map<MobTemporaryStat, MobStatOption> setStats, Set<BurnedInfo> setBurnedInfos) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobStatSet);
        outPacket.encodeInt(mob.getId()); // dwMobId
        // CMob::ProcessStatSet
        MobStat.encode(outPacket, setStats, setBurnedInfos);
        outPacket.encodeShort(0); // tDelay
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat || bDoomReservedSN
        return outPacket;
    }

    public static OutPacket mobStatReset(Mob mob, Set<MobTemporaryStat> resetStats, Set<BurnedInfo> resetBurnedInfos) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobStatReset);
        outPacket.encodeInt(mob.getId()); // dwMobId
        MobStat.encodeReset(outPacket, resetStats, resetBurnedInfos);
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat
        return outPacket;
    }

    public static OutPacket mobDamaged(Mob mob, int damage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobDamaged);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(0); // (byte != 2) -> CMob::ShowDamage
        outPacket.encodeInt(damage); // nDamage
        if (mob.getTemplate().isDamagedByMob()) { // this->m_pTemplate->bDamagedByMob
            outPacket.encodeInt(mob.getHp());
            outPacket.encodeInt(mob.getMaxHp());
        }
        return outPacket;
    }

    public static OutPacket mobSpecialEffectBySkill(Mob mob, int skillId, int characterId, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobSpecialEffectBySkill);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(characterId); // dwCharacterID, only used for mortal blow?
        outPacket.encodeShort(delay); // tDelay
        return outPacket;
    }

    public static OutPacket mobHpIndicator(Mob mob, int percentage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobHPIndicator);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(percentage); // nHPpercentage
        return outPacket;
    }

    public static OutPacket mobCatchEffect(Mob mob, boolean success, boolean delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobCatchEffect);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(delay); // tDelay = bool ? 270 : 0
        return outPacket;
    }

    public static OutPacket mobEffectByItem(Mob mob, int itemId, boolean success) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MobEffectByItem);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeByte(success); // bSuccess
        return outPacket;
    }
}
