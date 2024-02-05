package kinoko.packet.life;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.life.MovePath;
import kinoko.world.life.mob.Mob;
import kinoko.world.life.mob.MobAttackInfo;

public final class MobPacket {
    public static OutPacket mobEnterField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_ENTER_FIELD);
        outPacket.encodeInt(mob.getObjectId()); // dwMobId
        outPacket.encodeByte(1); // nCalcDamageIndex
        outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
        mob.getMobStatManager().encode(outPacket, true);
        mob.encodeInit(outPacket);
        return outPacket;
    }

    public static OutPacket mobLeaveField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_LEAVE_FIELD);
        outPacket.encodeInt(mob.getObjectId()); // dwMobID
        outPacket.encodeByte(1); // nDeadType
        // if nDeadType == 4, encodeInt(dwSwallowCharacterID);
        return outPacket;
    }

    public static OutPacket mobChangeController(Mob mob, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CHANGE_CONTROLLER);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(mob.getObjectId()); // dwMobId
        if (forController) {
            outPacket.encodeByte(1); // nCalcDamageIndex
            // CMobPool::SetLocalMob
            outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
            mob.getMobStatManager().encode(outPacket, true);
            mob.encodeInit(outPacket);
        }
        return outPacket;
    }

    public static OutPacket mobMove(Mob mob, MobAttackInfo mai, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_MOVE);
        outPacket.encodeInt(mob.getObjectId()); // dwMobId
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
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CTRL_ACK);
        outPacket.encodeInt(mob.getObjectId()); // dwMobId
        outPacket.encodeShort(mobCtrlSn); // nMobCtrlSN
        outPacket.encodeByte(nextAttackPossible); // bNextAttackPossible
        outPacket.encodeShort(mob.getMp()); // nMP
        outPacket.encodeByte(mai.skillId); // nSkillCommand
        outPacket.encodeByte(mai.slv); // nSLV
        return outPacket;
    }
}
