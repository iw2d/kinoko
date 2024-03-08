package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.field.SummonedPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAttack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SummonedHandler {
    private static final Logger log = LogManager.getLogger(SummonedHandler.class);

    @Handler(InHeader.SUMMONED_MOVE)
    public static void handleSummonedMove(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwSummonedID

        final Field field = user.getField();
        final Optional<Summoned> summonedResult = field.getSummonedPool().getById(objectId);
        if (summonedResult.isEmpty()) {
            log.error("Received SUMMONED_MOVE for invalid object with ID : {}", objectId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(summoned);
        field.broadcastPacket(SummonedPacket.move(summoned, movePath), user);
    }

    @Handler(InHeader.SUMMONED_ATTACK)
    public static void handleSummonedAttack(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwSummonedID

        final Field field = user.getField();
        final Optional<Summoned> summonedResult = field.getSummonedPool().getById(objectId);
        if (summonedResult.isEmpty()) {
            log.error("Received SUMMONED_ATTACK for invalid object with ID : {}", objectId);
            return;
        }
        final Summoned summoned = summonedResult.get();
        final SummonedAttack attack = new SummonedAttack();

        inPacket.decodeInt(); // ~drInfo.dr0
        inPacket.decodeInt(); // ~drInfo.dr1
        inPacket.decodeInt(); // update_time
        inPacket.decodeInt(); // ~drInfo.dr2
        inPacket.decodeInt(); // ~drInfo.dr3

        attack.actionAndDir = inPacket.decodeByte(); // nAction & 0x7F | (bLeft << 7)

        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32
        attack.mobCount = inPacket.decodeByte();

        attack.userX = inPacket.decodeShort();
        attack.userY = inPacket.decodeShort();
        attack.summonedX = inPacket.decodeShort();
        attack.summonedY = inPacket.decodeShort();

        inPacket.decodeInt(); // CUserLocal::GetRepeatSkillPoint

        for (int i = 0; i < attack.mobCount; i++) {
            final AttackInfo ai = new AttackInfo();
            ai.mobId = inPacket.decodeInt(); // mobID
            ai.hitAction = inPacket.decodeByte(); // nHitAction
            ai.actionAndDir = inPacket.decodeByte(); // nForeAction & 0x7F | (bLeft << 7)
            inPacket.decodeByte(); // nFrameIdx
            inPacket.decodeByte(); // CalcDamageStatIndex
            inPacket.decodeShort(); // x?
            inPacket.decodeShort(); // y?
            inPacket.decodeShort();
            inPacket.decodeShort();
            inPacket.decodeShort(); // tDelay
            ai.damage[0] = inPacket.decodeInt(); // aDamage[0]
            attack.getAttackInfo().add(ai);
        }

        inPacket.decodeInt(); // Crc


        // TODO: check SummonedAttackInfo
    }
}
