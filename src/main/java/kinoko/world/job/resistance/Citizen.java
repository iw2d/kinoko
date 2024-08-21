package kinoko.world.job.resistance;


import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.world.WvsContext;
import kinoko.provider.MobProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobAppearType;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.TemporaryStatOption;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Citizen extends SkillProcessor {
    // CITIZEN
    public static final int POTION_MASTERY = 30000002;
    public static final int BLESSING_OF_THE_FAIRY = 30000012;
    public static final int DEADLY_CRITS = 30000022;
    public static final int CRYSTAL_THROW = 30001000;
    public static final int INFILTRATE = 30001001;
    public static final int LEGENDARY_SPIRIT = 30001003;
    public static final int HEROS_ECHO = 30001005;
    public static final int MAKER = 30001007;
    public static final int FOLLOW_THE_LEAD = 30001024;
    public static final int SOARING = 30001026;

    public static final int CAPTURE = 30001061;
    public static final int CALL_OF_THE_HUNTER = 30001062;
    public static final int MECHANIC_DASH = 30001068;

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            case Citizen.INFILTRATE:
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.Speed, TemporaryStatOption.of(si.getValue(SkillStat.speed, slv), skillId, si.getDuration(slv)),
                        CharacterTemporaryStat.Sneak, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv))
                ));
                return;
            case Citizen.CAPTURE:
                final Optional<Mob> captureResult = field.getMobPool().getById(skill.captureTargetMobId);
                if (captureResult.isEmpty()) {
                    user.write(UserLocal.effect(Effect.skillUseInfo(skillId, slv, user.getLevel(), 2))); // Monster cannot be captured.
                    return;
                }
                try (var lockedMob = captureResult.get().acquire()) {
                    final Mob mob = lockedMob.get();
                    final int templateId = mob.getTemplateId();
                    // Should implement all client side checks in CUserLocal::DoActiveSkill_MobCapture
                    final List<Integer> capturedMobs = user.getWildHunterInfo().getCapturedMobs();
                    if (mob.isBoss() || mob.getLevel() > user.getLevel() || capturedMobs.contains(templateId)) {
                        user.write(UserLocal.effect(Effect.skillUseInfo(skillId, slv, user.getLevel(), 2))); // Monster cannot be captured.
                        return;
                    }
                    // Check hp below 50%
                    final int percentage = (int) ((double) mob.getHp() / mob.getMaxHp() * 100.0);
                    if (percentage > si.getValue(SkillStat.x, slv)) {
                        user.write(UserLocal.effect(Effect.skillUseInfo(skillId, slv, user.getLevel(), 1))); // Capture failed. Monster HP too high.
                        return;
                    }
                    // Capture success
                    user.write(UserLocal.effect(Effect.skillUseInfo(skillId, slv, user.getLevel(), 0))); // Monster successfully captured.
                    field.getMobPool().removeMob(mob);
                    // Update WildHunterInfo
                    if (GameConstants.isJaguarMob(templateId)) {
                        user.getWildHunterInfo().setRidingType((templateId % 10) + 1);
                    } else {
                        capturedMobs.add(templateId);
                        if (capturedMobs.size() > 5) {
                            capturedMobs.removeFirst();
                        }
                    }
                    user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
                }
                return;
            case Citizen.CALL_OF_THE_HUNTER:
                // Remove from captured mob list and update client
                final boolean removeSuccess = user.getWildHunterInfo().getCapturedMobs().remove(Integer.valueOf(skill.randomCapturedMobId));
                if (!removeSuccess) {
                    log.error("Could not remove captured mob ID {} for Call of the Hunter skill", skill.randomCapturedMobId);
                    return;
                }
                user.write(WvsContext.wildHunterInfo(user.getWildHunterInfo()));
                // Create mob
                final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(skill.randomCapturedMobId);
                if (mobTemplateResult.isEmpty()) {
                    log.error("Could not resolve mob template ID {} for Call of the Hunter skill", skill.randomCapturedMobId);
                    return;
                }
                final Mob mob = new Mob(
                        mobTemplateResult.get(),
                        null,
                        skill.positionX,
                        skill.positionY,
                        field.getFootholdBelow(skill.positionX, skill.positionY).map(Foothold::getSn).orElse(user.getFoothold())
                );
                mob.getMobStat().getTemporaryStats().put(MobTemporaryStat.Dazzle, MobStatOption.of(1, skillId, 0));
                mob.setRemoveAfter(Instant.now().plus(si.getDuration(slv), ChronoUnit.MILLIS));
                mob.setAppearType(MobAppearType.NORMAL);
                // Add mob to field, force user to be its controller
                field.getMobPool().addMob(mob);
                if (mob.getController() != user) {
                    mob.setController(user);
                    user.write(MobPacket.mobChangeController(mob, true));
                    field.broadcastPacket(MobPacket.mobChangeController(mob, false), user);
                }
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }
}