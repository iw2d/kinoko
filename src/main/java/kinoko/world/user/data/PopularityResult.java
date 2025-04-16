package kinoko.world.user.data;

public enum PopularityResult {
    // GivePopularityRes
    Success(0),
    InvalidCharacterID(1),
    LevelLow(2),
    AlreadyDoneToday(3),
    AlreadyDoneTarget(4),
    Notify(5),
    UnknownError(-1);

    private final int value;

    PopularityResult(int value) {
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }
}