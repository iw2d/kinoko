package kinoko.server.script;

import java.util.Set;

public enum ScriptMessageParam {
    NOT_CANCELLABLE(0x1),
    PLAYER_AS_SPEAKER(0x2),
    OVERRIDE_SPEAKER_ID(0x4),
    FLIP_SPEAKER(0x8);

    private final byte value;

    ScriptMessageParam(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static byte from(Set<ScriptMessageParam> flags) {
        return flags.stream()
                .map(ScriptMessageParam::getValue)
                .reduce((byte) 0, (a, b) -> (byte) (a | b));
    }
}
