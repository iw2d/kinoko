package kinoko.world.skill;

import kinoko.world.job.JobConstants;
import kinoko.world.job.cygnus.NightWalker;
import kinoko.world.job.cygnus.Noblesse;
import kinoko.world.job.cygnus.ThunderBreaker;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.Citizen;

import java.util.List;

public final class SkillConstants {
    public static final List<Integer> SECONDARY_STAT_SKILLS = List.of(
            Thief.NIMBLE_BODY,
            NightWalker.NIMBLE_BODY,
            Pirate.BULLET_TIME, // PirateQuickMotion
            ThunderBreaker.QUICK_MOTION, // StrikerQuickMotion
            Evan.DRAGON_SOUL,
            Beginner.BLESSING_OF_THE_FAIRY,
            Noblesse.BLESSING_OF_THE_FAIRY,
            Citizen.BLESSING_OF_THE_FAIRY,
            Aran.BLESSING_OF_THE_FAIRY,
            Evan.BLESSING_OF_THE_FAIRY
    );

    public static final List<Integer> WILD_HUNTER_JAGUARS = List.of(
            1932015, 1932030, 1932031, 1932032, 1932033, 1932036
    );

    public static final int MECHANIC_VEHICLE = 1932016;

    public static int getSkillRoot(int skillId) {
        return skillId / 10000;
    }

    public static boolean isBeginnerSpAddableSkill(int skillId) {
        if (!JobConstants.isBeginnerJob(getSkillRoot(skillId))) {
            return false;
        }
        final int skillType = skillId % 10000;
        return skillType == 1000 || skillType == 1001 || skillType == 1002;
    }

    public static boolean isEncodePositionSkill(int skillId) {
        return isAntiRepeatBuffSkill(skillId); // TODO
    }

    public static boolean isAntiRepeatBuffSkill(int skillId) {
        switch (skillId) {
            case 1001003:
            case 1101006:
            case 1111007:
            case 1121000:
            case 1201006:
            case 1211009:
            case 1211010:
            case 1221000:
            case 1301006:
            case 1301007:
            case 1311007:
            case 1321000:
            case 2101001:
            case 2101003:
            case 2121000:
            case 2201001:
            case 2201003:
            case 2221000:
            case 2301004:
            case 2311001:
            case 2311003:
            case 2321000:
            case 2321005:
            case 3121000:
            case 3121002:
            case 3221000:
            case 4101004:
            case 4111001:
            case 4121000:
            case 4201003:
            case 4221000:
            case 4311001:
            case 4341000:
            case 4341007:
            case 5111007:
            case 5121000:
            case 5121009:
            case 5211007:
            case 5221000:
            case 11001001:
            case 11101003:
            case 12101000:
            case 12101001:
            case 14101003:
            case 15111005:
            case 21121000:
            case 22141003:
            case 22171000:
            case 22181000:
            case 32111004:
            case 32121007:
            case 33121007:
            case 35111013:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShootSkillNotUsingShootingWeapon(int skillId) {
        switch (skillId) {
            case 4121003:
            case 4221003:
            case 5121002:
            case 11101004:
            case 15111006:
            case 15111007:
            case 21100004:
            case 21110004:
            case 21120006:
            case 33101007:
                return true;
            default:
                return false;
        }
    }

    public static boolean isShootSkillNotConsumingBullet(int skillId) {
        if (isShootSkillNotUsingShootingWeapon(skillId)) {
            return true;
        }
        switch (skillId) {
            case 3101003:
            case 3201003:
            case 4111004:
            case 13101005:
            case 14101006:
            case 33101002:
            case 35001001:
            case 35001004:
            case 35101009:
            case 35101010:
            case 35111004:
            case 35111015:
            case 35121005:
            case 35121012:
            case 35121013:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMagicKeydownSkill(int skillId) {
        switch (skillId) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 22121000:
            case 22151000:
                return true;
            default:
                return false;
        }
    }

    public static boolean isKeydownSkill(int skillId) {
        switch (skillId) {
            case 2121001:
            case 2221001:
            case 2321001:
            case 3121004:
            case 3221001:
            case 4341002:
            case 4341003:
            case 5101004:
            case 5201002:
            case 5221004:
            case 13111002:
            case 14111006:
            case 15101003:
            case 22121000:
            case 22151001:
            case 33101005:
            case 33121009:
            case 35001001:
            case 35101009:
                return true;
            default:
                return false;
        }
    }

    public static boolean isJaguarMeleeAttackSkill(int skillId) {
        switch (skillId) {
            case 33101002:
            case 33101007:
            case 33111002:
            case 33111006:
            case 33121002:
                return true;
            default:
                return false;
        }
    }

    public static boolean isIgnoreMasterLevelForCommon(int skillId) {
        switch (skillId) {
            case 1120012:
            case 1220013:
            case 1320011:
            case 2120009:
            case 2220009:
            case 2320010:
            case 3120010:
            case 3120011:
            case 3220009:
            case 3220010:
            case 4120010:
            case 4220009:
            case 5120011:
            case 5220012:
            case 32120009:
            case 33120010:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSkillNeedMasterLevel(int skillId) {
        if (isIgnoreMasterLevelForCommon(skillId)) {
            return false;
        }
        final int jobId = skillId / 10000;
        if (JobConstants.isEvanJob(jobId)) {
            final int jobLevel = JobConstants.getJobLevel(jobId);
            return jobLevel == 9 || jobLevel == 10 || skillId == 22111001 || skillId == 22141002 || skillId == 22140000;
        }
        if (JobConstants.isDualJob(jobId)) {
            return JobConstants.getJobLevel(jobId) == 4 ||
                    skillId == 4311003 ||
                    skillId == 4321000 ||
                    skillId == 4331002 ||
                    skillId == 4331005;
        }
        if (jobId == 100 * (jobId / 100)) {
            return false;
        }
        return jobId % 10 == 2;
    }
}
