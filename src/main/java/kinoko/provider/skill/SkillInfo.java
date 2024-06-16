package kinoko.provider.skill;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.List;

public interface SkillInfo {
    int getSkillId();

    int getMaxLevel();

    boolean isInvisible();

    boolean isCombatOrders();

    boolean isPsd();

    List<Integer> getPsdSkills();

    int getValue(SkillStat stat, int slv);

    Rect getRect();

    ElementAttribute getElemAttr();

    default int getDuration(int slv) {
        return getValue(SkillStat.time, slv) * 1000;
    }

    default int getBulletCon(int slv) {
        return Math.max(getValue(SkillStat.bulletConsume, slv), getValue(SkillStat.bulletCount, slv));
    }

    default int getHpCon(User user, int slv, int keyDown) {
        final int skillId = getSkillId();
        if (skillId == Warrior.SACRIFICE || skillId == Warrior.DRAGON_ROAR || skillId == Pirate.MP_RECOVERY) {
            return user.getMaxHp() * getValue(SkillStat.x, slv) / 100;
        } else if (skillId == Thief.FINAL_CUT) {
            final int percentage = getValue(SkillStat.x, slv) * keyDown / SkillConstants.getMaxGaugeTime(skillId);
            return user.getMaxHp() * percentage / 100;
        }
        return getValue(SkillStat.hpCon, slv);
    }

    default int getMpCon(User user, int slv) {
        // CSkillInfo::CheckConsumeForActiveSkill
        final int incMpCon = 100 + user.getSkillStatValue(SkillConstants.getAmplificationSkill(user.getJob()), SkillStat.x);
        int mpCon = getValue(SkillStat.mpCon, slv) * incMpCon / 100;
        // Check CTS affecting mpCon
        final SecondaryStat ss = user.getSecondaryStat();
        if (ss.hasOption(CharacterTemporaryStat.Infinity)) {
            mpCon = 0;
        }
        if (ss.hasOption(CharacterTemporaryStat.Concentration)) {
            final int percentage = 100 - ss.getOption(CharacterTemporaryStat.Concentration).nOption;
            mpCon = (int) (percentage * mpCon / 100.0 + 0.99);
        }
        if (SkillConstants.isTeleportSkill(getSkillId()) && ss.hasOption(CharacterTemporaryStat.TeleportMasteryOn)) {
            mpCon += ss.getOption(CharacterTemporaryStat.TeleportMasteryOn).nOption;
        }
        return mpCon;
    }

    default SkillRecord createRecord() {
        final SkillRecord skillRecord = new SkillRecord(getSkillId());
        skillRecord.setSkillLevel(0);
        skillRecord.setMasterLevel(getMaxLevel());
        return skillRecord;
    }

    static SkillInfo from(int skillId, WzListProperty skillProp) throws ProviderError {
        if (skillProp.get("level") instanceof WzListProperty) {
            return StaticSkillInfo.from(skillId, skillProp);
        } else {
            return ComputedSkillInfo.from(skillId, skillProp);
        }
    }
}
