package kinoko.world.job;

import java.util.ArrayList;
import java.util.List;

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

    public static int getExtendSpJobLevel(int jobId, int level) {
        if (JobConstants.isEvanJob(jobId)) {
            // is_jobchange_level_in_evan
            if (level <= 10) {
                return 0;
            } else if (level <= 20) {
                return 1;
            } else if (level <= 30) {
                return 2;
            } else if (level <= 40) {
                return 3;
            } else if (level <= 50) {
                return 4;
            } else if (level <= 60) {
                return 5;
            } else if (level <= 80) {
                return 6;
            } else if (level <= 100) {
                return 7;
            } else if (level <= 120) {
                return 8;
            } else if (level <= 160) {
                return 9;
            } else {
                return 10;
            }
        } else {
            if (level <= 10) {
                return 0;
            } else if (level <= 30) {
                return 1;
            } else if (level <= 70) {
                return 2;
            } else if (level <= 120) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    public static int getJobChangeLevel(int jobId, int subJob, int step) {
        final int group = jobId / 1000;
        if (isResistanceJob(jobId) || isEvanJob(jobId)) { // probably extend sp jank
            return group != 1 ? 200 : 120;
        }
        switch (step) {
            case 1 -> {
                if (group == 0 && getJobCategory(jobId) == 2) {
                    return 8; // explorer magicians
                }
                return 10;
            }
            case 2 -> {
                return isDualJobBorn(jobId, subJob) ? 20 : 30;
            }
            case 3 -> {
                return isDualJobBorn(jobId, subJob) ? 55 : 70;
            }
            case 4 -> {
                return 120;
            }
            default -> {
                return group != 1 ? 200 : 120;
            }
        }
    }

    public static int getJobCategory(int jobId) {
        return jobId % 1000 / 100;
    }

    public static boolean isBeginnerJob(int jobId) {
        return jobId % 1000 == 0 || jobId == 2001;
    }

    public static boolean isExplorerWarriorJob(int jobId) {
        return jobId / 100 == 1;
    }

    public static boolean isExplorerMagicianJob(int jobId) {
        return jobId / 100 == 2;
    }

    public static boolean isExplorerBowmanJob(int jobId) {
        return jobId / 100 == 3;
    }

    public static boolean isExplorerThiefJob(int jobId) {
        return jobId / 100 == 4;
    }

    public static boolean isExplorerPirateJob(int jobId) {
        return jobId / 100 == 5;
    }

    public static boolean isHeroJob(int jobId) {
        return jobId / 10 == 11;
    }

    public static boolean isPaladinJob(int jobId) {
        return jobId / 10 == 12;
    }

    public static boolean isDarkKnightJob(int jobId) {
        return jobId / 10 == 13;
    }

    public static boolean isFirePoisonJob(int jobId) {
        return jobId / 10 == 21;
    }

    public static boolean isIceLightningJob(int jobId) {
        return jobId / 10 == 22;
    }

    public static boolean isBishopJob(int jobId) {
        return jobId / 10 == 23;
    }

    public static boolean isBowmasterJob(int jobId) {
        return jobId / 10 == 31;
    }

    public static boolean isMarksmanJob(int jobId) {
        return jobId / 10 == 32;
    }

    public static boolean isNightLordJob(int jobId) {
        return jobId / 10 == 41;
    }

    public static boolean isShadowerJob(int jobId) {
        return jobId / 10 == 42;
    }

    public static boolean isDualJob(int jobId) {
        return jobId / 10 == 43;
    }

    public static boolean isBuccaneerJob(int jobId) {
        return jobId / 10 == 51;
    }

    public static boolean isCorsairJob(int jobId) {
        return jobId / 10 == 52;
    }

    public static boolean isAranJob(int jobId) {
        return jobId / 100 == 21 || jobId == 2000;
    }

    public static boolean isEvanJob(int jobId) {
        return jobId / 100 == 22 || jobId == 2001;
    }

    public static boolean isDragonJob(int jobId) {
        return jobId / 100 == 22;
    }

    public static boolean isDualJobBorn(int jobId, int subJob) {
        if (jobId / 1000 != 0) {
            return false;
        }
        return subJob == 1;
    }

    public static boolean isCygnusJob(int jobId) {
        return jobId / 1000 == 1;
    }

    public static boolean isBlazeWizardJob(int jobId) {
        return jobId / 100 == 12;
    }

    public static boolean isWindArcherJob(int jobId) {
        return jobId / 100 == 13;
    }

    public static boolean isNightWalkerJob(int jobId) {
        return jobId / 100 == 14;
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

    public static boolean canUseBareHand(int jobId) {
        return getJobCategory(jobId) == 5;
    }

    public static List<Integer> getSkillRootFromJob(int jobId) {
        final List<Integer> jobs = new ArrayList<>();
        if (Job.getById(jobId) == null) {
            return jobs;
        }
        final int jobCategory = getJobCategory(jobId);
        if (jobCategory != 0) {
            final int firstJob = 100 * (jobCategory + 10 * (jobId / 1000));
            jobs.add(firstJob);
            final int secondJobType = jobId % 100 / 10;
            if (secondJobType != 0) {
                final int secondJob = firstJob + 10 * secondJobType;
                jobs.add(secondJob);
                for (int i = 1; i <= 8; i++) {
                    if (jobId % 10 < i) {
                        break;
                    }
                    jobs.add(secondJob + i);
                }
            }
        }
        if (isEvanJob(jobId)) {
            jobs.add(2001);
        } else {
            jobs.add(1000 * (jobId / 1000));
        }
        return jobs;
    }

    public static int getNoviceSkillRootFromJob(int jobId) {
        if (isEvanJob(jobId)) {
            return 2001;
        } else {
            return 1000 * (jobId / 1000);
        }
    }

    public static boolean isCorrectJobForSkillRoot(int jobId, int skillRoot) {
        if (skillRoot % 100 == 0) {
            return skillRoot / 100 == jobId / 100;
        }
        return skillRoot / 10 == jobId / 10 && jobId % 10 >= skillRoot % 10;
    }
}
