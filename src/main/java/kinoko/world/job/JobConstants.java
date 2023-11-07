package kinoko.world.job;

public final class JobConstants {
    public static int getJobLevel(int job) {
        if (job % 100 != 0 || job == 2001) {
            return 1;
        }
        int jobLevel;
        if (job / 10 == 43) {
            jobLevel = (job - 430) / 2;
        } else {
            jobLevel = job % 10;
        }
        jobLevel += 2;
        if (jobLevel >= 2 && (jobLevel <= 4 || jobLevel <= 10 && isEvanJob(job))) {
            return jobLevel;
        }
        return 0;
    }

    public static boolean isEvanJob(int job) {
        return job / 100 == 22 || job == 2001;
    }

    public static boolean isDualJob(int job) {
        return job / 10 == 43;
    }

    public static boolean isWildHunterJob(int job) {
        return job / 100 == 33;
    }
}
