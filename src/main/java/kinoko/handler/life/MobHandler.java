package kinoko.handler.life;

import kinoko.handler.Handler;
import kinoko.packet.life.MobPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.life.Life;
import kinoko.world.life.MovePath;
import kinoko.world.life.mob.Mob;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class MobHandler {
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
        inPacket.decodeByte(); // bDirLeft | (4 * (bRushMove | (2 * bRiseByToss | 2 * nMobCtrlState)))
        inPacket.decodeByte(); // nActionAndDir
        inPacket.decodeInt(); // CMob::TARGETINFO { short x, short y } || { short nSkillIDandLev, short nDelay }

        final int multiTargetForBallCount = inPacket.decodeInt();
        for (int i = 0; i < multiTargetForBallCount; i++) {
            inPacket.decodeInt(); // aMultiTargetForBall[i].x
            inPacket.decodeInt(); // aMultiTargetForBall[i].y
        }
        final int randTimeForAreaAttackCount = inPacket.decodeInt();
        for (int i = 0; i < randTimeForAreaAttackCount; i++) {
            inPacket.decodeInt(); // aRandTimeforAreaAttack[i]
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

        // TODO: MobSkills

        movePath.applyTo(mob);
        user.write(MobPacket.mobCtrlAck(mob, mobCtrlSn, false));
        field.broadcastPacket(MobPacket.mobMove(mob, movePath), user);
    }
}
