package kinoko.world;

import kinoko.util.Tuple;
import kinoko.world.user.funckey.FuncKeyMapped;
import kinoko.world.user.funckey.FuncKeyType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class GameConstants {
    // USER CONSTANTS --------------------------------------------------------------------------------------------------

    public static final int CHARACTER_SLOT_MAX = 15;
    public static final int INVENTORY_SLOT_MAX = 96;
    public static final int TRUNK_SLOT_MAX = 48;
    public static final int LOCKER_SLOT_MAX = 500; // avoid reaching packet size limit

    public static final int DEFAULT_ITEM_SLOT_MAX = 100; // CItemInfo::GetBundleItemInfoData
    public static final int MONEY_MAX = Integer.MAX_VALUE;

    public static final int HP_MAX = 99999;
    public static final int MP_MAX = 99999;
    public static final int PAD_MAX = 29999;
    public static final int PDD_MAX = 30000;
    public static final int MAD_MAX = 29999;
    public static final int MDD_MAX = 30000;
    public static final int ACC_MAX = 9999;
    public static final int EVA_MAX = 9999;
    public static final int SPEED_MIN = 100;
    public static final int SPEED_MAX = 140;
    public static final int JUMP_MIN = 100;
    public static final int JUMP_MAX = 123;
    public static final int LEVEL_MAX = 200;
    public static final int[] EXP_TABLE = initializeExpTable();

    public static final int DAMAGE_MAX = 999_999;

    public static final int FUNC_KEY_MAP_SIZE = 89;
    public static final Map<Integer, FuncKeyMapped> DEFAULT_FUNC_KEY_MAP = defaultFuncKeyMap(); // aDefaultFKM
    public static final int QUICKSLOT_KEY_SIZE = 8;
    public static final int[] DEFAULT_QUICKSLOT_KEY_MAP = new int[]{ 0x2A, 0x52, 0x47, 0x49, 0x1D, 0x53, 0x4F, 0x51 }; // aDefaultQKM


    // PET CONSTANTS ---------------------------------------------------------------------------------------------------

    public static final int PET_COUNT_MAX = 3;
    public static final int PET_LEVEL_MAX = 30;
    public static final int PET_FULLNESS_FOR_TAMENESS = 75;
    public static final int PET_FULLNESS_MAX = 100;
    public static final int PET_TAMENESS_MAX = 30000; // closeness in GMS
    public static final int[] PET_TAMENESS_TABLE = new int[]{
            0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22548, 26084, 30000
    };


    // MOB CONSTANTS ---------------------------------------------------------------------------------------------------

    public static final int MOB_ATTACK_CHANCE = 75;
    public static final int MOB_ATTACK_COOLTIME_MIN = 3;
    public static final int MOB_ATTACK_COOLTIME_MAX = 13;
    public static final int MOB_ATTACK_COOLTIME_MAX_BOSS = 7;
    public static final int MOB_RECOVER_TIME = 5;
    public static final int MOB_RESPAWN_TIME = 7;
    public static final int MOB_CAPACITY_MAX = 40;
    public static final double MOB_CAPACITY_CONSTANT = 0.0000078125;


    // DROP CONSTANTS --------------------------------------------------------------------------------------------------

    public static final int DROP_HEIGHT = 100;
    public static final int DROP_SPREAD = 25;
    public static final int DROP_EXPIRE_INTERVAL = 3;
    public static final int DROP_REMAIN_ON_GROUND_TIME = 120;
    public static final double DROP_MONEY_PROB = 0.60;


    // REACTOR CONSTANTS -----------------------------------------------------------------------------------------------

    public static final int REACTOR_EXPIRE_INTERVAL = 5; // interval to check whether reactors can be reset
    public static final int REACTOR_END_DELAY = 5; // tStateEnd = update_time + 100 * x


    public static boolean isValidCharacterName(String name) {
        return name.length() >= 4 && name.length() <= 13 && name.matches("[a-zA-Z0-9]+");
    }

    public static Tuple<Integer, Integer> getMoneyForMobLevel(int level) {
        // modern maple values, may not be accurate
        final double min;
        final double max;
        if (level == 1) {
            min = 1.0;
            max = 1.0;
        } else if (level <= 20) {
            min = 1.6;
            max = 2.4;
        } else if (level <= 30) {
            min = 2.0;
            max = 3.0;
        } else if (level <= 40) {
            min = 2.4;
            max = 3.6;
        } else if (level <= 50) {
            min = 2.8;
            max = 4.2;
        } else if (level <= 60) {
            min = 4.0;
            max = 6.0;
        } else if (level <= 70) {
            min = 4.8;
            max = 7.2;
        } else if (level <= 80) {
            min = 5.2;
            max = 7.8;
        } else if (level <= 90) {
            min = 5.6;
            max = 8.4;
        } else {
            min = 6.0;
            max = 9.0;
        }
        return new Tuple<>((int) (min * level), (int) (max * level));
    }

    public static int getNextLevelExp(int level) {
        return EXP_TABLE[level];
    }

    public static int getNextLevelPetCloseness(int level) {
        return PET_TAMENESS_TABLE[level];
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

    private static Map<Integer, FuncKeyMapped> defaultFuncKeyMap() {
        final Map<Integer, FuncKeyMapped> defaultFuncKeyMap = new HashMap<>();
        final int[] indexArray = { 2, 3, 4, 5, 6, 7, 8, 16, 17, 18, 19, 20, 23, 24, 25, 26, 27, 29, 31, 33, 34, 35, 37, 38, 39, 40, 41, 43, 44, 45, 46, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65 };
        final int[] typeArray = { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6 };
        final int[] idArray = { 10, 12, 13, 18, 24, 21, 29, 8, 5, 0, 4, 28, 1, 25, 19, 14, 15, 52, 2, 26, 17, 11, 3, 20, 27, 16, 23, 9, 50, 51, 6, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106 };
        for (int i = 0; i < indexArray.length; i++) {
            final FuncKeyType type = FuncKeyType.getByValue(typeArray[i]);
            assert type != null;
            defaultFuncKeyMap.put(indexArray[i], FuncKeyMapped.of(type, idArray[i]));
        }
        return Collections.unmodifiableMap(defaultFuncKeyMap);
    }
}
