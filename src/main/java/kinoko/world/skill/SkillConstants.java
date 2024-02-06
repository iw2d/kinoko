package kinoko.world.skill;

import kinoko.world.job.JobConstants;

public final class SkillConstants {

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
        }
        return false;
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
