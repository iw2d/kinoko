package kinoko.server.dialog.miniroom;

public enum GameResultType {
    // GAME
    NORMAL(0),
    DRAW(1),
    GIVEUP(2);

    private final int value;

    GameResultType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
