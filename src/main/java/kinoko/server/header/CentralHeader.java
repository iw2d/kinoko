package kinoko.server.header;

import java.util.HashMap;
import java.util.Map;

/**
 * Central server communication opcode enum, which uses integer ordinal values for en/decoding.
 *
 * <pre>
 * Initialization:
 *   CentralServerNode
 *      1 : start central server, send InitializeRequest to connecting central clients
 *      2 : block until all expected ChannelServerNode clients connected and received InitializeResult
 *      6 : start login server
 *   ChannelServerNode
 *      3 : start channel server
 *      4 : start central client
 *      5 : send InitializeResult upon connecting to central server and receiving InitializeRequest
 *
 * Shutdown:
 *   CentralServerNode
 *      1 : close all connected client channels, close login server
 *      2 : send ShutdownRequest to all connected ChannelServerNode clients
 *      3 : block until ShutdownResult received from all connected ChannelServerNode clients
 *      7 : close central server
 *   ChannelServerNode
 *      4 : receive ShutdownRequest
 *      5 : close all connected client channels and close channel server
 *      6 : send ShutdownResult to CentralServerNode
 *
 * Migration (from login server):
 *   CentralServerNode
 *      1 : create MigrationInfo, store in MigrationStorage
 *      2 : send SelectCharacterResult to client (client connects to target channel server)
 *      5 : receive MigrateRequest from ChannelServerNode
 *      6 : remove MigrationInfo from MigrationStorage
 *      7 : reply to ChannelServerNode with MigrateResult
 *   ChannelServerNode (target)
 *      3 : receive MigrateIn (channel server), send MigrateRequest to CentralServerNode
 *      4 : block until MigrateResult received from CentralServerNode
 *      8 : perform migration, send UserConnect to central server
 *
 * Transfer (from channel server):
 *   ChannelServerNode (source)
 *      1 : send TransferRequest to CentralServerNode
 *      2 : block until TransferResult received
 *      6 : send MigrateCommand to client (client connects to target channel server)
 *   CentralServerNode
 *      3 : receive TransferRequest from source ChannelServerNode
 *      4 : store received MigrationInfo in MigrationStorage
 *      5 : send TransferResult to source ChannelServerNode
 *      9 : receive MigrateRequest from ChannelServerNode
 *     10 : remove MigrationInfo from MigrationStorage
 *     11 : reply to ChannelServerNode with MigrateResult and the removed MigrationInfo
 *   ChannelServerNode (target)
 *      7 : receive MigrateIn (channel server), send MigrateRequest to CentralServerNode [same as migration step 3 onwards]
 *      8 : block until MigrateResult received from CentralServerNode
 *     12 : perform migration, send UserConnect to central server
 *
 * User tracking
 *   ChannelServerNode
 *      1 : receive MigrateIn (channel server)
 *      2 : send UserConnect to CentralServerNode
 *      4 : user in channel server requires updating (level, job, location)
 *      5 : send UserUpdate to CentralServerNode
 *      7 : user disconnects from channel server
 *      8 : send UserDisconnect to CentralServerNode
 *   CentralServerNode
 *      3 : receive UserConnect, store in UserStorage
 *      6 : receive UserUpdate, update in UserStorage
 *      7 : receive UserDisconnect, remove from UserStorage
 *
 * User remote packets
 *   ChannelServerNode (source)
 *      1 : send UserPacketRequest [character name, packet] to CentralServerNode
 *          or UserPacketReceive [character id, packet]
 *   CentralServerNode
 *      2 : receive UserPacketRequest/UserPacketReceive
 *      3 : resolve target user and channel using UserStorage
 *      4 : send UserPacketReceive [character id, packet] to target ChannelServerNode
 *   ChannelServerNode (target)
 *      5 : receive UserPacketReceive
 *      6 : send packet to target user
 *
 * User packet broadcast
 *   ChannelServerNode (source)
 *      1 : send UserPacketBroadcast [character ids, packet] to CentralServerNode
 *   CentralServerNode
 *      2 : receive UserPacketBroadcast
 *      3 : send UserPacketBroadcast [character ids, packet] to all ChannelServerNodes
 *   ChannelServerNode (targets)
 *      4 : receive UserPacketBroadcast
 *      5 : send packet to target users
 *
 * User query
 *   ChannelServerNode
 *      1 : send UserQueryRequest [character names] to CentralServerNode
 *      5 : receive UserQueryResult and resolve future
 *   CentralServerNode
 *      2 : receive UserQueryRequest
 *      3 : resolve queried users from UserStorage
 *      4 : send UserQueryResult to ChannelServerNode
 *
 * Party request
 *   ChannelServerNode (target)
 *      1 : receive PartyRequest (channel server)
 *      2 : send PartyRequest [character id, party request] to CentralServerNode
 *   CentralServerNode
 *      3 : receive PartyRequest
 *      4 : process packet according to received party request type
 *      5 : send UserPacketReceive to required recipients
 *          optionally send PartyResult [character id, party id] back to target ChannelServerNode (update user party id)
 * </pre>
 */
public enum CentralHeader {
    InitializeRequest,
    InitializeResult,
    ShutdownRequest,
    ShutdownResult,
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
    PartyRequest,
    PartyResult,
    MessengerRequest,
    MessengerResult;

    private static final Map<Integer, CentralHeader> headerMap = new HashMap<>();

    static {
        for (CentralHeader header : values()) {
            headerMap.put(header.getValue(), header);
        }
    }

    public final int getValue() {
        return ordinal();
    }

    public static CentralHeader getByValue(int op) {
        return headerMap.get(op);
    }
}
