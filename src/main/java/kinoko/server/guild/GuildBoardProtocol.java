package kinoko.server.guild;

public enum GuildBoardProtocol {
    // GuildBBS
    Register(0),
    Delete(1),
    LoadListRequest(2),
    ViewEntryRequest(3),
    RegisterComment(4),
    DeleteComment(5),
    LoadListResult(6),
    ViewEntryResult(7),
    EntryNotFound(8);

    private final int value;

    GuildBoardProtocol(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static GuildBoardProtocol getByValue(int value) {
        for (GuildBoardProtocol type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
