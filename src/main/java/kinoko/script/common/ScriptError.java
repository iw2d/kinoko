package kinoko.script.common;

public final class ScriptError extends Error {
    public ScriptError(String message) {
        super(message);
    }

    public ScriptError(String format, Object... args) {
        super(String.format(format, args));
    }

}
