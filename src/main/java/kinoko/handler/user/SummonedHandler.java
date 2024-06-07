package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.SummonedPacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SummonedAttackInfo;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.job.JobHandler;
import kinoko.world.skill.Attack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public final class SummonedHandler {
    private static final Logger log = LogManager.getLogger(SummonedHandler.class);

    @Handler(InHeader.SummonedMove)
    public static void handleSummonedMove(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID

        final Optional<Summoned> summonedResult = user.getSummonedById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SUMMONED_MOVE for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(summoned);
        user.getField().broadcastPacket(SummonedPacket.summonedMove(user, summoned, movePath), user);
    }

    @Handler(InHeader.SummonedAttack)
    public static void handleSummonedAttack(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID

        final Optional<Summoned> summonedResult = user.getSummonedById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SUMMONED_ATTACK for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();
        final Attack attack = new Attack(OutHeader.SummonedAttack);

        inPacket.decodeInt(); // ~drInfo.dr0
        inPacket.decodeInt(); // ~drInfo.dr1
        inPacket.decodeInt(); // update_time
        inPacket.decodeInt(); // ~drInfo.dr2
        inPacket.decodeInt(); // ~drInfo.dr3

        attack.actionAndDir = inPacket.decodeByte(); // nAction & 0x7F | (bLeft << 7)

        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        final int mobCount = inPacket.decodeByte();
        attack.mask = (byte) (1 | (mobCount << 4)); // because we're reusing the Attack object

        attack.userX = inPacket.decodeShort();
        attack.userY = inPacket.decodeShort();
        inPacket.decodeShort(); // summonedX
        inPacket.decodeShort(); // summonedY

        inPacket.decodeInt(); // CUserLocal::GetRepeatSkillPoint

        for (int i = 0; i < attack.getMobCount(); i++) {
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

        // Check summoned attack info
        final Optional<SummonedAttackInfo> summonedAttackInfoResult = SkillProvider.getSummonedAttackInfo(summoned.getSkillId());
        if (summonedAttackInfoResult.isEmpty()) {
            log.error("Failed to resolve summoned attack info for summoned with skill id : {}", summoned.getSkillId());
            return;
        }
        final SummonedAttackInfo sai = summonedAttackInfoResult.get();
        if (sai.getMobCount() < attack.getMobCount()) {
            log.error("Received SUMMON_ATTACK with mob count greater than expected : {}, actual : {}", sai.getMobCount(), attack.getMobCount());
            return;
        }

        // Skill specific handling
        try (var locked = user.acquire()) {
            JobHandler.handleAttack(locked, attack);
        }

        // Process attack damage
        final Field field = user.getField();
        for (AttackInfo ai : attack.getAttackInfo()) {
            final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
            if (mobResult.isEmpty()) {
                continue;
            }
            // Acquire and damage mob
            final int totalDamage = Arrays.stream(ai.damage).sum();
            try (var lockedMob = mobResult.get().acquire()) {
                lockedMob.get().damage(user, totalDamage);
            }
        }

        field.broadcastPacket(SummonedPacket.summonedAttack(user, summoned, attack), user);
    }
}
