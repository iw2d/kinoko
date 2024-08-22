package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.SummonedPacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedActionType;
import kinoko.world.field.summoned.SummonedLeaveType;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.skill.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public final class SummonedHandler {
    private static final Logger log = LogManager.getLogger(SummonedHandler.class);

    @Handler(InHeader.SummonedMove)
    public static void handleSummonedMove(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID
        if (summonedId == 0) {
            return; // CTutor, ignore
        }

        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getField().getSummonedPool().getById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SummonedMove for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(summoned);
        summoned.getField().broadcastPacket(SummonedPacket.summonedMove(user, summoned, movePath), user);
    }

    @Handler(InHeader.SummonedAttack)
    public static void handleSummonedAttack(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID

        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getField().getSummonedPool().getById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SummonedAttack for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();
        final Attack attack = new Attack(OutHeader.SummonedAttack);
        attack.skillId = summoned.getSkillId();

        inPacket.decodeInt(); // ~drInfo.dr0
        inPacket.decodeInt(); // ~drInfo.dr1
        inPacket.decodeInt(); // update_time
        inPacket.decodeInt(); // ~drInfo.dr2
        inPacket.decodeInt(); // ~drInfo.dr3

        attack.actionAndDir = inPacket.decodeByte(); // nAction & 0x7F | (bLeft << 7)

        final SummonedActionType actionType = SummonedActionType.getByValue(attack.actionAndDir & 0x7F);
        if (actionType == null) {
            log.error("Unknown summoned action type : {}", attack.actionAndDir & 0x7F);
            return;
        }

        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        final int mobCount = inPacket.decodeByte();
        attack.mask = (byte) (1 | (mobCount << 4)); // because we're reusing the Attack object

        if (actionType == SummonedActionType.ATTACK_TRIANGLE) {
            for (int j = 0; j < 3; j++) {
                inPacket.decodeInt(); // padwTeslaFamily.p->a[j]
            }
        }

        attack.userX = inPacket.decodeShort();
        attack.userY = inPacket.decodeShort();
        inPacket.decodeShort(); // summonedX
        inPacket.decodeShort(); // summonedY

        inPacket.decodeInt(); // CUserLocal::GetRepeatSkillPoint

        while (inPacket.getRemaining() > 4) {
            final AttackInfo ai = new AttackInfo();
            ai.mobId = inPacket.decodeInt(); // mobID
            inPacket.decodeInt(); // dwTemplateID
            ai.hitAction = inPacket.decodeByte(); // nHitAction
            ai.actionAndDir = inPacket.decodeByte(); // nForeAction & 0x7F | (bLeft << 7)
            inPacket.decodeByte(); // nFrameIdx
            inPacket.decodeByte(); // CalcDamageStatIndex
            inPacket.decodeShort(); // x?
            inPacket.decodeShort(); // y?
            inPacket.decodeShort();
            inPacket.decodeShort();
            ai.delay = Math.min(inPacket.decodeShort(), 1000); // tDelay
            ai.damage[0] = inPacket.decodeInt(); // aDamage[0]
            attack.getAttackInfo().add(ai);
        }

        attack.crc = inPacket.decodeInt(); // Crc

        // Check CRC
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(summoned.getSkillId());
        if (skillInfoResult.isEmpty()) {
            log.error("Could not to resolve skill info for summoned attack for {}", summoned);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (si.getSkillEntryCrc() != attack.crc) {
            log.warn("Received mismatching CRC for summoned attack for skill ID : {}", summoned.getSkillId());
        }

        // Process attack
        final Field field = summoned.getField();
        try (var locked = user.acquire()) {
            for (AttackInfo ai : attack.getAttackInfo()) {
                final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
                if (mobResult.isEmpty()) {
                    continue;
                }
                try (var lockedMob = mobResult.get().acquire()) {
                    // Skill specific handling
                    SkillProcessor.processAttack(locked, lockedMob, attack, ai.delay);
                    // Process damage
                    final Mob mob = lockedMob.get();
                    final int totalDamage = Arrays.stream(ai.damage).sum();
                    mob.damage(user, totalDamage, ai.delay);
                }
            }

            if (actionType == SummonedActionType.DIE) {
                user.removeSummoned(summoned);
            }
        }

        field.broadcastPacket(SummonedPacket.summonedAttack(user, summoned, attack), user);
    }

    @Handler(InHeader.SummonedHit)
    public static void handleSummonedHit(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID

        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getField().getSummonedPool().getById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SummonedHit for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        final HitInfo hitInfo = new HitInfo();
        hitInfo.attackIndex = inPacket.decodeByte();
        hitInfo.damage = inPacket.decodeInt();
        if (hitInfo.attackIndex > -2) {
            hitInfo.templateId = inPacket.decodeInt(); // dwTemplateID
            hitInfo.dir = inPacket.decodeByte();
        }

        user.getField().broadcastPacket(SummonedPacket.summonedHit(user, summoned, hitInfo));
        try (var lockedSummoned = summoned.acquire()) {
            summoned.setHp(summoned.getHp() - hitInfo.damage);
            if (summoned.getHp() <= 0) {
                summoned.setLeaveType(SummonedLeaveType.SUMMONED_DEAD);
                user.removeSummoned(summoned);
            }
        }
    }

    @Handler(InHeader.SummonedSkill)
    public static void handleSummonedSkill(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt(); // dwSummonedID

        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getField().getSummonedPool().getById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SummonedSkill for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        final Skill skill = new Skill();
        skill.skillId = inPacket.decodeInt(); // nSkillID
        final byte actionAndDir = inPacket.decodeByte(); // (nMoveAction << 7) | (nAttackAction & 0x7F)
        if (skill.skillId == Warrior.HEX_OF_THE_BEHOLDER) {
            skill.summonBuffType = inPacket.decodeByte();
        }

        // Resolve skill level
        if (skill.skillId == summoned.getSkillId()) {
            skill.slv = summoned.getSkillLevel();
        } else {
            skill.slv = user.getSkillLevel(skill.skillId);
        }
        skill.summoned = summoned;

        // Skill specific handling
        try (var locked = user.acquire()) {
            SkillProcessor.processSkill(locked, skill);
        }

        summoned.getField().broadcastPacket(SummonedPacket.summonedSkill(user, summoned, actionAndDir));
    }

    @Handler(InHeader.SummonedRemove)
    public static void handleSummonedRemove(User user, InPacket inPacket) {
        final int summonedId = inPacket.decodeInt();

        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getField().getSummonedPool().getById(summonedId);
        if (summonedResult.isEmpty()) {
            log.error("Received SummonedRemove for invalid object with ID : {}", summonedId);
            return;
        }
        final Summoned summoned = summonedResult.get();

        // Remove summoned
        try (var locked = user.acquire()) {
            locked.get().removeSummoned(summoned);

            // There is no way to differentiate between the user removing the satellite summon manually and the buff
            // from Satellite Safety (35121006) removing the summon after absorbing damage.
            if (summoned.getSkillId() == Mechanic.SATELLITE ||
                    summoned.getSkillId() == Mechanic.SATELLITE_2 ||
                    summoned.getSkillId() == Mechanic.SATELLITE_3) {
                if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.SafetyDamage)) {
                    user.resetTemporaryStat(Set.of(CharacterTemporaryStat.SafetyDamage, CharacterTemporaryStat.SafetyAbsorb));
                    user.setSkillCooltime(Mechanic.SATELLITE_SAFETY, user.getSkillStatValue(Mechanic.SATELLITE_SAFETY, SkillStat.cooltime));
                }
            }
        }
    }
}
