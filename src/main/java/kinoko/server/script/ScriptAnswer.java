package kinoko.server.script;

public final class ScriptAnswer {
    private final int action;
    private final int intValue;
    private final String stringValue;

    private ScriptAnswer(int action, int intValue, String stringValue) {
        this.action = action;
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    public int getAction() {
        return action;
    }

    public int getAnswer() {
        return intValue;
    }

    public String getTextAnswer() {
        return stringValue;
    }

    public static ScriptAnswer withAction(int action) {
        return new ScriptAnswer(action, 0, null);
    }

    public static ScriptAnswer withAnswer(int action, int answer) {
        return new ScriptAnswer(action, answer, null);
    }

    public static ScriptAnswer withTextAnswer(int action, String answer) {
        return new ScriptAnswer(action, 0, answer);
    }
}
