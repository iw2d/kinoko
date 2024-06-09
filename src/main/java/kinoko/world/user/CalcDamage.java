package kinoko.world.user;

import kinoko.world.item.WeaponType;
import kinoko.world.job.JobConstants;
import kinoko.world.user.stat.CharacterStat;

public final class CalcDamage {

    public static double calcDamageByWT(WeaponType wt, CharacterStat cs, int pad, int mad) {
        final int jobId = cs.getJob();
        if (JobConstants.isBeginnerJob(jobId)) {
            return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.2);
        } else if (JobConstants.getJobCategory(jobId) == 2) { // is_mage_job
            return calcBaseDamage(cs.getBaseInt(), cs.getBaseLuk(), 0, mad, 1.0);
        }
        switch (wt) {
            case OH_SWORD, OH_AXE, OH_MACE -> {
                return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.2);
            }
            case DAGGER -> {
                return calcBaseDamage(cs.getBaseLuk(), cs.getBaseDex(), cs.getBaseStr(), pad, 1.3);
            }
            case BAREHAND -> {
                return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.43);
            }
            case TH_SWORD, TH_AXE, TH_MACE -> {
                return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.32);
            }
            case SPEAR, POLEARM -> {
                return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.49);
            }
            case BOW -> {
                return calcBaseDamage(cs.getBaseDex(), cs.getBaseStr(), 0, pad, 1.2);
            }
            case CROSSBOW -> {
                return calcBaseDamage(cs.getBaseDex(), cs.getBaseStr(), 0, pad, 1.35);
            }
            case THROWINGGLOVE -> {
                return calcBaseDamage(cs.getBaseLuk(), cs.getBaseDex(), 0, pad, 1.75);
            }
            case KNUCKLE -> {
                return calcBaseDamage(cs.getBaseStr(), cs.getBaseDex(), 0, pad, 1.7);
            }
            case GUN -> {
                return calcBaseDamage(cs.getBaseDex(), cs.getBaseStr(), 0, pad, 1.5);
            }
            default -> {
                return 0.0;
            }
        }
    }

    private static int calcBaseDamage(int p1, int p2, int p3, int ad, double k) {
        return (int) ((double) (p3 + p2 + 4 * p1) / 100.0 * ((double) ad * k) + 0.5);
    }

    private static double getMasteryConstByWT(WeaponType wt) {
        switch (wt) {
            case WAND, STAFF -> {
                return 0.25;
            }
            case BOW, CROSSBOW, THROWINGGLOVE, GUN -> {
                return 0.15;
            }
            default -> {
                return 0.2;
            }
        }
    }
}
