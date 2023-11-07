package kinoko.world.skill;

import kinoko.world.job.JobConstants;

public final class SkillConstants {

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
        final int job = skillId / 10000;
        if (JobConstants.isEvanJob(job)) {
            final int jobLevel = JobConstants.getJobLevel(job);
            return jobLevel == 0 || jobLevel == 10 || jobLevel == 22111001 || jobLevel == 22141002 || jobLevel == 22140000;
        }
        if (JobConstants.isDualJob(job)) {
            return JobConstants.getJobLevel(job) == 4 ||
                    skillId == 4311003 ||
                    skillId == 4321000 ||
                    skillId == 4331002 ||
                    skillId == 4331005;
        }
        if (job == 100 * (job / 100)) {
            return false;
        }
        return job % 10 == 2;
    }
}
