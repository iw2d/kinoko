package kinoko.handler.life;

import kinoko.handler.Handler;
import kinoko.packet.life.MobPacket;
import kinoko.provider.mob.MobSkillType;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.world.field.Field;
import kinoko.world.life.Life;
import kinoko.world.life.MovePath;
import kinoko.world.life.mob.Mob;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MobHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.MOB_MOVE)
    public static void handleMobMove(User user, InPacket inPacket) {
        // CMob::GenerateMovePath
        final int objectId = inPacket.decodeInt(); // dwMobID

        final Field field = user.getField();
        final Optional<Life> lifeResult = field.getLifeById(objectId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Mob mob)) {
            log.error("Received MOB_MOVE for invalid life with ID : {}", objectId);
            return;
        }

        final short mobCtrlSn = inPacket.decodeShort(); // nMobCtrlSN
        final byte actionMask = inPacket.decodeByte(); // bDirLeft | (4 * (bRushMove | (2 * bRiseByToss | 2 * nMobCtrlState)))
        final byte actionAndDir = inPacket.decodeByte(); // nActionAndDir
        final int targetInfo = inPacket.decodeInt(); // CMob::TARGETINFO { short x, short y } || { short nSkillIDandLev, short nDelay }

        final List<Tuple<Integer, Integer>> multiTargetForBall = new ArrayList<>();
        final int multiTargetForBallCount = inPacket.decodeInt();
        for (int i = 0; i < multiTargetForBallCount; i++) {
            multiTargetForBall.add(new Tuple<>(
                    inPacket.decodeInt(), // aMultiTargetForBall[i].x
                    inPacket.decodeInt() // aMultiTargetForBall[i].y
            ));
        }
        final List<Integer> randTimeForAreaAttack = new ArrayList<>();
        final int randTimeForAreaAttackCount = inPacket.decodeInt();
        for (int i = 0; i < randTimeForAreaAttackCount; i++) {
            randTimeForAreaAttack.add(inPacket.decodeInt()); // aRandTimeforAreaAttack[i]
        }

        inPacket.decodeByte(); // (bActive == 0) | (16 * !(CVecCtrlMob::IsCheatMobMoveRand(pvcActive) == 0))
        inPacket.decodeInt(); // HackedCode
        inPacket.decodeInt(); // moveCtx.fc.ptTarget->x
        inPacket.decodeInt(); // moveCtx.fc.ptTarget->y
        inPacket.decodeInt(); // dwHackedCodeCRC

        final MovePath movePath = MovePath.decode(inPacket);

        inPacket.decodeByte(); // this->bChasing
        inPacket.decodeByte(); // pTarget != 0
        inPacket.decodeByte(); // pvcActive->bChasing
        inPacket.decodeByte(); // pvcActive->bChasingHack
        inPacket.decodeInt(); // pvcActive->tChaseDuration

        // handle mob skill
        int skillId = targetInfo & 0xFF;
        int slv = (targetInfo >> 8) & 0xFF;
        int option = (targetInfo >> 16) & 0xFFFF;

        if (skillId >= MobSkillType.POWER_UP.getValue() && skillId <= MobSkillType.SUMMON_CUBE.getValue()) {
            final MobSkillType skillType = MobSkillType.getByValue(skillId);
        }

        movePath.applyTo(mob);
        user.write(MobPacket.mobCtrlAck(mob.getObjectId(), mobCtrlSn, false, mob.getMp(), skillId, slv));
        field.broadcastPacket(MobPacket.mobMove(mob.getObjectId(), actionAndDir, targetInfo, multiTargetForBall, randTimeForAreaAttack, movePath), user);
    }
}
