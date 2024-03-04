package kinoko.world.job;

public final class JobConstants {
    public static int getJobLevel(int jobId) {
        if (jobId % 100 == 0 || jobId == 2001) {
            return 1;
        }
        int jobLevel;
        if (jobId / 10 == 43) {
            jobLevel = (jobId - 430) / 2;
        } else {
            jobLevel = jobId % 10;
        }
        jobLevel += 2;
        if (jobLevel >= 2 && (jobLevel <= 4 || jobLevel <= 10 && isEvanJob(jobId))) {
            return jobLevel;
        }
        return 0;
    }

    public static int getJobCategory(int jobId) {
        return jobId % 1000 / 100;
    }

    public static boolean isBeginnerJob(int jobId) {
        return jobId % 1000 == 0 || jobId == 2001;
    }

    public static boolean isAranJob(int jobId) {
        return jobId / 100 == 21 || jobId == 2000;
    }

    public static boolean isEvanJob(int jobId) {
        return jobId / 100 == 22 || jobId == 2001;
    }

    public static boolean isDualJob(int jobId) {
        return jobId / 10 == 43;
    }

    public static boolean isCygnusJob(int jobId) {
        return jobId / 1000 == 1;
    }

    public static boolean isResistanceJob(int jobId) {
        return jobId / 1000 == 3;
    }

    public static boolean isBattleMageJob(int jobId) {
        return jobId / 100 == 32;
    }

    public static boolean isWildHunterJob(int jobId) {
        return jobId / 100 == 33;
    }

    public static boolean isMechanicJob(int jobId) {
        return jobId / 100 == 35;
    }

    public static boolean isExtendSpJob(int jobId) {
        return isResistanceJob(jobId) || isEvanJob(jobId);
    }

    public static boolean isAdminJob(int jobId) {
        return jobId % 1000 / 100 == 9;
    }

    public static boolean isManagerJob(int jobId) {
        return jobId % 1000 / 100 == 8;
    }
}
