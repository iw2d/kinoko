package kinoko.meta;

public enum SkillTargetFlags {
    None(0),
    Default(1),
    Party(2),
    Mobs(4);


    private final int value;
    SkillTargetFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SkillTargetFlags fromValue(int value) {
        for (SkillTargetFlags flag : values()) {
            if (flag.value == value) {
                return flag;
            }
        }
        return None;
    }
}
