package kinoko.world;

import kinoko.world.job.Job;

public final class GameConstants {
    public static final int CHARACTER_MAX_SLOTS = 15;
    public static final int INVENTORY_MAX_SLOTS = 96;
    public static final int DEFAULT_ITEM_SLOT_MAX = 100; // CItemInfo::GetBundleItemInfoData
    public static final int MAX_MONEY = Integer.MAX_VALUE;

    public static final int DROP_HEIGHT = 100;
    public static final int DROP_REMOVE_OWNERSHIP_TIME = 30;
    public static final int DROP_REMAIN_ON_GROUND_TIME = 120;

    public static final int MAX_LEVEL = 200;
    public static final int[] EXP_TABLE = initializeExpTable();

    public static final int MAX_DAMAGE = 999_999;

    public static final int MOB_ATTACK_CHANCE = 75;
    public static final int MOB_ATTACK_COOLTIME_MIN = 3;
    public static final int MOB_ATTACK_COOLTIME_MAX = 13;
    public static final int MOB_ATTACK_COOLTIME_MAX_BOSS = 7;

    public static boolean isValidCharacterName(String name) {
        return name.length() >= 4 && name.length() <= 13 && name.matches("[a-zA-Z0-9]+");
    }

    public static int getNextLevelExp(int level) {
        return EXP_TABLE[level];
    }

    public static int getMinHp(int level, int jobId) {
        // CUIStatChange::IsUnderMinHP
        switch (Job.getById(jobId)) {
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                return 16 * level - 2;
            }
            case MECHANIC_1 -> {
                return 22 * (level + 4);
            }
            case MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                return 22 * level + 238;
            }
            case WILD_HUNTER_1 -> {
                return 20 * level + 108;
            }
            case WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                return 20 * level + 258;
            }
            case BATTLE_MAGE_1 -> {
                return 34 * level - 32;
            }
            case BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                return 34 * level + 168;
            }
            default -> {
                final int jobSuffix = jobId % 1000;
                switch (jobSuffix) {
                    case 100, 120, 121, 122, 130, 131, 132 -> {
                        return 24 * level + 118;
                    }
                    case 110, 111, 112 -> {
                        return 24 * level + 418;
                    }
                    case 200, 210, 211, 212, 220, 221, 222, 230, 231, 232 -> {
                        return 10 * level + 54;
                    }
                    case 300, 400 -> {
                        return 20 * level + 58;
                    }
                    case 310, 311, 312, 320, 321, 322, 410, 411, 412, 420, 421, 422 -> {
                        return 20 * level + 358;
                    }
                    case 430, 431, 432, 433, 434 -> {
                        return 20 * level + 808;
                    }
                    case 500 -> {
                        return 22 * level + 38;
                    }
                    case 510, 511, 512, 520, 521, 522 -> {
                        return 22 * level + 338;
                    }
                    case 0, 1 -> {
                        return 12 * level + 38;
                    }
                    default -> {
                        return 0;
                    }
                }
            }
        }
    }

    public static int getMinMp(int level, int jobId) {
        // CUIStatChange::IsUnderMinMP
        switch (Job.getById(jobId)) {
            case EVAN_1, EVAN_2, EVAN_3, EVAN_4, EVAN_5, EVAN_6, EVAN_7, EVAN_8, EVAN_9, EVAN_10 -> {
                int mp = 150;
                if (jobId < 2210 || jobId > 2214) {
                    if (jobId >= 2215) {
                        mp = 650;
                    }
                } else {
                    return 100 * (jobId % 10) + 250;
                }
                return 35 * level + mp - 275;
            }
            case MECHANIC_1 -> {
                return 18 * level - 17;
            }
            case MECHANIC_2, MECHANIC_3, MECHANIC_4 -> {
                return 18 * level + 83;
            }
            case WILD_HUNTER_1 -> {
                return 14 * level + 23;
            }
            case WILD_HUNTER_2, WILD_HUNTER_3, WILD_HUNTER_4 -> {
                return 14 * level + 123;
            }
            case BATTLE_MAGE_1 -> {
                return 22 * level + 43;
            }
            case BATTLE_MAGE_2, BATTLE_MAGE_3, BATTLE_MAGE_4 -> {
                return 22 * level + 143;
            }
            default -> {
                final int jobSuffix = jobId % 1000;
                switch (jobSuffix) {
                    case 100, 110, 111, 112 -> {
                        return 4 * level + 55;
                    }
                    case 120, 121, 122, 130, 131, 132 -> {
                        return 4 * level + 155;
                    }
                    case 200 -> {
                        return 22 * level - 1;
                    }
                    case 210, 211, 212, 220, 221, 222, 230, 231, 232 -> {
                        return 22 * level + 449;
                    }
                    case 300, 400 -> {
                        return 14 * level - 15;
                    }
                    case 310, 311, 312, 320, 321, 322, 410, 411, 412, 420, 421, 422 -> {
                        return 14 * level + 135;
                    }
                    case 430, 431, 432, 433, 434 -> {
                        return 14 * level + 355;
                    }
                    case 500 -> {
                        return 18 * level - 55;
                    }
                    case 510, 511, 512, 520, 521, 522 -> {
                        return 18 * level + 95;
                    }
                    case 0, 1 -> {
                        return 10 * level - 5;
                    }
                    default -> {
                        return 0;
                    }
                }
            }
        }
    }

    private static int[] initializeExpTable() {
        // NEXTLEVEL::NEXTLEVEL
        final int[] n = new int[201];
        n[1] = 15;
        n[2] = 34;
        n[3] = 57;
        n[4] = 92;
        n[5] = 135;
        n[6] = 372;
        n[7] = 560;
        n[8] = 840;
        n[9] = 1242;
        n[10] = n[9];
        n[11] = n[9];
        n[12] = n[9];
        n[13] = n[9];
        n[14] = n[9];
        for (int i = 15; i <= 29; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.2 + 0.5);
        }
        n[30] = n[29];
        n[31] = n[29];
        n[32] = n[29];
        n[33] = n[29];
        n[34] = n[29];
        for (int i = 35; i <= 39; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.2 + 0.5);
        }
        for (int i = 40; i <= 69; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.08 + 0.5);
        }
        n[70] = n[69];
        n[71] = n[69];
        n[72] = n[69];
        n[73] = n[69];
        n[74] = n[69];
        for (int i = 75; i <= 119; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.07 + 0.5);
        }
        n[120] = n[119];
        n[121] = n[119];
        n[122] = n[119];
        n[123] = n[119];
        n[124] = n[119];
        for (int i = 125; i <= 159; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.07 + 0.5);
        }
        for (int i = 160; i <= 199; i++) {
            n[i] = (int) ((double) n[i - 1] * 1.06 + 0.5);
        }
        n[200] = 0;
        return n;
    }
}
