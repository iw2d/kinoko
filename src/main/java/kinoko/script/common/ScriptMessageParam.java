package kinoko.script.common;

public enum ScriptMessageParam {
    NONE(0x0),
    NOT_CANCELLABLE(0x1),
    PLAYER_AS_SPEAKER(0x2),
    SPEAKER_ON_RIGHT(0x4),
    FLIP_SPEAKER(0x8);

    private final int value;

    ScriptMessageParam(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
