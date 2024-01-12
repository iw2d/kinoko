package kinoko.server.script;

public final class ScriptAnswer {
    private final int action;
    private final int selection;
    private final int numberAnswer;
    private final String textAnswer;

    private ScriptAnswer(int action, int selection, int numberAnswer, String textAnswer) {
        this.action = action;
        this.selection = selection;
        this.numberAnswer = numberAnswer;
        this.textAnswer = textAnswer;
    }

    public int getAction() {
        return action;
    }

    public int getSelection() {
        return selection;
    }

    public int getNumberAnswer() {
        return numberAnswer;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public static ScriptAnswer withAction(int action) {
        return new ScriptAnswer(action, -1, -1, null);
    }

    public static ScriptAnswer withSelection(int selection) {
        return new ScriptAnswer(-1, selection, -1, null);
    }

    public static ScriptAnswer withNumberAnswer(int answer) {
        return new ScriptAnswer(-1, -1, answer, null);
    }

    public static ScriptAnswer withTextAnswer(String answer) {
        return new ScriptAnswer(-1, -1, -1, answer);
    }
}
