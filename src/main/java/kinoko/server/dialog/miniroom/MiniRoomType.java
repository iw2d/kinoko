package kinoko.server.dialog.miniroom;

public enum MiniRoomType {
    // MR
    OmokRoom(1),
    MemoryGameRoom(2),
    TradingRoom(3),
    PersonalShop(4),
    EntrustedShop(5),
    CashTradingRoom(6),
    TypeNo(7);

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
