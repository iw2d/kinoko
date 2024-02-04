package kinoko.packet.life;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;
import kinoko.world.life.MovePath;
import kinoko.world.life.mob.Mob;

import java.util.List;

public final class MobPacket {
    public static OutPacket mobEnterField(Mob mob) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_ENTER_FIELD);
        outPacket.encodeInt(mob.getObjectId()); // dwMobId
        outPacket.encodeByte(1); // nCalcDamageIndex
        outPacket.encodeInt(mob.getTemplateId()); // dwTemplateID
        mob.getMobStatManager().encode(outPacket);
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
            mob.getMobStatManager().encode(outPacket);
            mob.encodeInit(outPacket);
        }
        return outPacket;
    }

    public static OutPacket mobMove(int objectId, byte actionAndDir, int targetInfo, List<Tuple<Integer, Integer>> multiTargetForBall, List<Integer> randTimeForAreaAttack, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_MOVE);
        outPacket.encodeInt(objectId); // dwMobId
        outPacket.encodeByte(false); // bNotForceLandingWhenDiscard
        outPacket.encodeByte(false); // bNotChangeAction
        outPacket.encodeByte(false); // bNextAttackPossible
        outPacket.encodeByte(actionAndDir); // bLeft (actionAndDir)
        outPacket.encodeInt(targetInfo); // sEffect (CMob::TARGETINFO)
        outPacket.encodeInt(multiTargetForBall.size()); // aMultiTargetForBall
        for (var target : multiTargetForBall) {
            outPacket.encodeInt(target.getLeft()); // x
            outPacket.encodeInt(target.getRight()); // y
        }
        outPacket.encodeInt(randTimeForAreaAttack.size()); // aRandTimeforAreaAttack
        for (int value : randTimeForAreaAttack) {
            outPacket.encodeInt(value);
        }
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket mobCtrlAck(int objectId, int mobCtrlSn, boolean isNextAttackPossible, int mobMp, int skillId, int slv) {
        final OutPacket outPacket = OutPacket.of(OutHeader.MOB_CTRL_ACK);
        outPacket.encodeInt(objectId); // dwMobId
        outPacket.encodeShort(mobCtrlSn); // nMobCtrlSN
        outPacket.encodeByte(isNextAttackPossible); // bNextAttackPossible
        outPacket.encodeShort(mobMp); // nMP
        outPacket.encodeByte(skillId); // nSkillCommand
        outPacket.encodeByte(slv); // nSLV
        return outPacket;
    }
}
