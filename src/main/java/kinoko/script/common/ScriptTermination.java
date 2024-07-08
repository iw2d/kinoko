package kinoko.script.common;

public final class ScriptTermination extends Error {
    private static final ScriptTermination instance = new ScriptTermination();

    public static ScriptTermination getInstance() {
        return instance;
    }
}
