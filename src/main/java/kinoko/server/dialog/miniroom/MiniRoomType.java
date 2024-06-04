package kinoko.server.dialog.miniroom;

public enum MiniRoomType {
    // MR
    OMOK_ROOM(1),
    MEMORY_GAME_ROOM(2),
    TRADING_ROOM(3),
    PERSONAL_SHOP(4),
    ENTRUSTED_SHOP(5),
    CASH_TRADING_ROOM(6),
    TYPE_NO(7);

    private final int value;

    MiniRoomType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static MiniRoomType getByValue(int value) {
        for (MiniRoomType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
