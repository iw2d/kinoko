package kinoko.provider.item;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ItemOption {
    RECOVERHP(10151), // Recover x HP every 4 seconds.
    RECOVERMP(10156), // Recover x MP every 4 seconds.
    RECOVERYHPMP_STATE_SIT(20181), // Hidden Potential
    INVINCIBLE_INC1(20366), // After getting hit, increases invincibility duration by %d seconds. +1
    STATUS_TIME(20369), // -
    HP_STATE_KILL(20401), // 15% chance to recover x HP after defeating a monster.
    MP_STATE_KILL(20406), // 15% chance to recover x MP after defeating a monster.
    INVINCIBLE_INC2(30366), // After getting hit, increases invincibility duration by %d seconds. +2
    INVINCIBLE(30371), // 2% chance to become invincible for x seconds when hit.
    AUTOSTEAL1(30701), // %d%% chance to auto-steal when attacked. +1
    AUTOSTEAL2(30702), // %d%% chance to auto-steal when attacked. +2
    LEARN_SKILL_HASTE(31001), // The <Decent Haste> skill available for use.
    LEARN_SKILL_MYSTIC_DOOR(31002), // The <Decent Mystic Door> skill available for use.
    LEARN_SKILL_SHARP_EYES(31003), // The <Decent Sharp Eyes> skill available for use.
    LEARN_SKILL_HYPER_BODY(31004); // The <Decent Hyper Body> skill available for use.

    private static final Set<Integer> specialItemOptionIds = Arrays.stream(ItemOption.values())
            .map(ItemOption::getValue)
            .collect(Collectors.toUnmodifiableSet());

    private final int value;

    ItemOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static boolean isSpecialOption(int itemOptionId) {
        return specialItemOptionIds.contains(itemOptionId);
    }
}
