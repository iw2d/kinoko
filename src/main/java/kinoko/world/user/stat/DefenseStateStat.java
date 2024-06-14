package kinoko.world.user.stat;

public enum DefenseStateStat {
    CURSE('C'),
    DARKNESS('D'),
    SEAL('S'),
    STUN('F'),
    WEAKNESS('W');

    private final int value;

    DefenseStateStat(char value) {
        this.value = value;
    }

    public final CharacterTemporaryStat getStat() {
        return switch (this) {
            case CURSE -> CharacterTemporaryStat.Curse;
            case DARKNESS -> CharacterTemporaryStat.Darkness;
            case SEAL -> CharacterTemporaryStat.Seal;
            case STUN -> CharacterTemporaryStat.Stun;
            case WEAKNESS -> CharacterTemporaryStat.Weakness;
        };
    }

    public static DefenseStateStat getByValue(int value) {
        for (DefenseStateStat stat : values()) {
            if (stat.value == value) {
                return stat;
            }
        }
        return null;
    }
}
