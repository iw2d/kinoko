package kinoko.server.dialog;

import kinoko.script.common.ScriptAnswer;
import kinoko.script.common.ScriptManagerImpl;

public final class ScriptDialog implements Dialog {
    private final ScriptManagerImpl scriptManager;

    public ScriptDialog(ScriptManagerImpl scriptManager) {
        this.scriptManager = scriptManager;
    }

    public void submitAnswer(ScriptAnswer answer) {
        scriptManager.submitAnswer(answer);
    }

    public void close() {
        scriptManager.close();
    }

    public static ScriptDialog from(ScriptManagerImpl scriptManager) {
        return new ScriptDialog(scriptManager);
    }
}
