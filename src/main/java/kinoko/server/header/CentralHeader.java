package kinoko.server.header;

import java.util.List;

/**
 * Central server communication opcode enum, uses integer ordinal values for en/decoding.
 */
public enum CentralHeader {
    InitializeRequest,
    InitializeResult,
    InitializeComplete,
    ShutdownRequest,
    ShutdownResult,
    OnlineRequest,
    OnlineResult,
    MigrateRequest,
    MigrateResult,
    TransferRequest,
    TransferResult,
    UserConnect,
    UserUpdate,
    UserDisconnect,
    UserPacketRequest,
    UserPacketReceive,
    UserPacketBroadcast,
    UserQueryRequest,
    UserQueryResult,
    ServerPacketBroadcast,
    MessengerRequest,
    MessengerResult,
    PartyRequest,
    PartyResult,
    GuildRequest,
    GuildResult,
    FriendRequest;

    private static final List<CentralHeader> headers = List.of(values());

    public final int getValue() {
        return ordinal();
    }

    public static CentralHeader getByValue(int op) {
        if (op >= 0 && op < values().length) {
            return headers.get(op);
        }
        return null;
    }
}
