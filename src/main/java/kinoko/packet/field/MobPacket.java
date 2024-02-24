package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.field.life.mob.MobAttackInfo;

public final class MobPacket {
    // CMobPool::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket mobEnterField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_ENTER_FIELD);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(1); // nCalcDamageIndex
        outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
        mob.getMobStatManager().encode(outPacket, true);
        mob.encode(outPacket);
        return outPacket;
    }

    public static OutPacket mobLeaveField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_LEAVE_FIELD);
        outPacket.encodeInt(mob.getId()); // dwMobID
        outPacket.encodeByte(1); // nDeadType
        // if nDeadType == 4, encodeInt(dwSwallowCharacterID);
        return outPacket;
    }

    public static OutPacket mobChangeController(Mob mob, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CHANGE_CONTROLLER);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(mob.getId()); // dwMobId
        if (forController) {
            outPacket.encodeByte(1); // nCalcDamageIndex
            // CMobPool::SetLocalMob
            outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
            mob.getMobStatManager().encode(outPacket, true);
            mob.encode(outPacket);
        }
        return outPacket;
    }


    // CMobPool::OnMobPacket -------------------------------------------------------------------------------------------

    public static OutPacket move(Mob mob, MobAttackInfo mai, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_MOVE);
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

    public static OutPacket ctrlAck(Mob mob, short mobCtrlSn, boolean nextAttackPossible, MobAttackInfo mai) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CTRL_ACK);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeShort(mobCtrlSn); // nMobCtrlSN
        outPacket.encodeByte(nextAttackPossible); // bNextAttackPossible
        outPacket.encodeShort(mob.getMp()); // nMP
        outPacket.encodeByte(mai.skillId); // nSkillCommand
        outPacket.encodeByte(mai.slv); // nSLV
        return outPacket;
    }

    public static OutPacket statSet(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_STAT_SET);
        outPacket.encodeInt(mob.getId()); // dwMobId
        // CMob::ProcessStatSet
        mob.getMobStatManager().encode(outPacket, false);
        outPacket.encodeShort(0); // tDelay
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat || bDoomReservedSN
        return outPacket;
    }

    public static OutPacket statReset(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_STAT_RESET);
        outPacket.encodeInt(mob.getId()); // dwMobId
        mob.getMobStatManager().encodeReset(outPacket);
        outPacket.encodeByte(true); // nCalcDamageStatIndex
        outPacket.encodeByte(0); // MobStat::IsMovementAffectingStat -> bStat
        return outPacket;
    }

    public static OutPacket damaged(Mob mob, int damage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_DAMAGED);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(0); // (byte != 2) -> CMob::ShowDamage
        outPacket.encodeInt(damage); // nDamage
        if (mob.isDamagedByMob()) { // this->m_pTemplate->bDamagedByMob
            outPacket.encodeInt(mob.getHp());
            outPacket.encodeInt(mob.getMaxHp());
        }
        return outPacket;
    }

    public static OutPacket specialEffectBySkill(Mob mob, int skillId, int characterId, int delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_SPECIAL_EFFECT_BY_SKILL);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeInt(skillId); // nSkillID
        outPacket.encodeInt(characterId); // dwCharacterID, only used for mortal blow?
        outPacket.encodeShort(delay); // tDelay
        return outPacket;
    }

    public static OutPacket hpIndicator(Mob mob, int percentage) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_HP_INDICATOR);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(percentage); // nHPpercentage
        return outPacket;
    }

    public static OutPacket catchEffect(Mob mob, boolean success, boolean delay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CATCH_EFFECT);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeByte(success); // bSuccess
        outPacket.encodeByte(delay); // tDelay = bool ? 270 : 0
        return outPacket;
    }

    public static OutPacket effectByItem(Mob mob, int itemId, boolean success) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_EFFECT_BY_ITEM);
        outPacket.encodeInt(mob.getId()); // dwMobId
        outPacket.encodeInt(itemId); // nItemID
        outPacket.encodeByte(success); // bSuccess
        return outPacket;
    }
}
