package kinoko.server.guild;

public enum GuildRank {
    // GUILD
    NONE(0),
    MASTER(1),
    SUBMASTER(2),
    MEMBER1(3),
    MEMBER2(4),
    MEMBER3(5);

    private final int value;

    GuildRank(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static GuildRank getByValue(int value) {
        for (GuildRank rank : values()) {
            if (rank.getValue() == value) {
                return rank;
            }
        }
        return NONE;
    }
}
