package kinoko.world.job.staff;

import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.field.Field;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;

import java.util.Map;
import java.util.Set;

public final class SuperGM extends SkillProcessor {
    public static final int HEAL_DISPEL = 9101000;
    public static final int HASTE_SUPER = 9101001;
    public static final int HOLY_SYMBOL = 9101002;
    public static final int BLESS = 9101003;
    public static final int HIDE = 9101004;
    public static final int RESURRECTION = 9101005;
    public static final int SUPER_DRAGON_ROAR = 9101006;
    public static final int TELEPORT = 9101007;
    public static final int HYPER_BODY = 9101008;

    public static void handleSkill(User user, Skill skill) {
        final SkillInfo si = SkillProvider.getSkillInfoById(skill.skillId).orElseThrow();
        final int skillId = skill.skillId;
        final int slv = skill.slv;

        final Field field = user.getField();
        switch (skillId) {
            // COMMON
            case HYPER_BODY:
                applyFieldBuff(skill, field,
                        Map.of(
                            CharacterTemporaryStat.MaxHP, si.getValue(SkillStat.x, slv),
                            CharacterTemporaryStat.MaxMP, si.getValue(SkillStat.x, slv)
                        ),
                        si.getDuration(slv));
                return;
            case HASTE_SUPER:
                applyFieldBuff(skill, field,
                        Map.of(
                                CharacterTemporaryStat.Speed, si.getValue(SkillStat.speed, slv),
                                CharacterTemporaryStat.Jump, si.getValue(SkillStat.jump, slv)
                        ),
                        si.getDuration(slv));
                return;
            case BLESS:
                applyFieldBuff(skill, field,
                        Map.of(
                            CharacterTemporaryStat.PAD, si.getValue(SkillStat.pad, slv),
                            CharacterTemporaryStat.MAD, si.getValue(SkillStat.mad, slv),
                            CharacterTemporaryStat.PDD, si.getValue(SkillStat.pdd, slv),
                            CharacterTemporaryStat.MDD, si.getValue(SkillStat.mdd, slv),
                            CharacterTemporaryStat.ACC, si.getValue(SkillStat.acc, slv),
                            CharacterTemporaryStat.EVA, si.getValue(SkillStat.eva, slv)
                        ),
                        si.getDuration(slv));
                return;
            case HOLY_SYMBOL:
                applyFieldBuff(skill, field,
                        Map.of(
                                CharacterTemporaryStat.HolySymbol, si.getValue(SkillStat.x, slv)
                        ),
                        si.getDuration(slv));
                return;
            case HEAL_DISPEL:
                resetFieldTemporaryStats(skill, field, Set.of(
                        CharacterTemporaryStat.Poison,
                        CharacterTemporaryStat.Seal,
                        CharacterTemporaryStat.Darkness,
                        CharacterTemporaryStat.Weakness,
                        CharacterTemporaryStat.Curse,
                        CharacterTemporaryStat.Slow
                ));
                skill.forEachAffectedUser(field, User::heal, false);
                return;
            case RESURRECTION:
                skill.forEachAffectedUser(field, User::heal, false);
                return;
            case HIDE:
                user.hide(!user.isHidden(), false);
                return;
        }
        log.error("Unhandled skill {}", skill.skillId);
    }

}