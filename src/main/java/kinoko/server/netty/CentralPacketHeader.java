package kinoko.server.netty;

import java.util.HashMap;
import java.util.Map;

/**
 * Central server communication opcode enum, which uses integer ordinal values for en/decoding.
 *
 * <pre>
 * Initialization:
 *   CentralServerNode
 *      1 : start central server, send INITIALIZE_REQUEST to connecting central clients
 *      2 : block until all expected ChannelServerNode clients connected and received INITIALIZE_RESULT
 *      6 : start login server
 *   ChannelServerNode
 *      3 : start channel server
 *      4 : start central client
 *      5 : send INITIALIZE_RESULT upon connecting to central server and receiving INITIALIZE_REQUEST
 *
 * Shutdown:
 *   CentralServerNode
 *      1 : close all connected client channels, close login server
 *      2 : send SHUTDOWN_REQUEST to all connected ChannelServerNode clients
 *      3 : block until SHUTDOWN_RESULT received from all connected ChannelServerNode clients
 *      7 : close central server
 *   ChannelServerNode
 *      4 : receive SHUTDOWN_REQUEST
 *      5 : close all connected client channels and close channel server
 *      6 : send SHUTDOWN_RESULT to CentralServerNode
 *
 * Migration (from login server):
 *   CentralServerNode
 *      1 : create MigrationInfo, store in MigrationStorage
 *      2 : send SELECT_CHARACTER_RESULT to client (client connects to target channel server)
 *      5 : receive MIGRATION_REQUEST from ChannelServerNode
 *      6 : remove MigrationInfo from MigrationStorage
 *      7 : reply to ChannelServerNode with MIGRATION_RESULT
 *   ChannelServerNode (target)
 *      3 : receive MIGRATE_IN (channel server), send MIGRATION_REQUEST to CentralServerNode
 *      4 : block until MIGRATION_RESULT received from CentralServerNode
 *      8 : perform migration, send USER_CONNECT to central server
 *
 * Transfer (from channel server):
 *   ChannelServerNode (source)
 *      1 : send TRANSFER_REQUEST to CentralServerNode
 *      2 : block until TRANSFER_RESULT received
 *      6 : send MIGRATE_COMMAND to client (client connects to target channel server)
 *   CentralServerNode
 *      3 : receive TRANSFER_REQUEST from source ChannelServerNode
 *      4 : store received MigrationInfo in MigrationStorage
 *      5 : send TRANSFER_RESULT to source ChannelServerNode
 *      9 : receive MIGRATION_REQUEST from ChannelServerNode
 *     10 : remove MigrationInfo from MigrationStorage
 *     11 : reply to ChannelServerNode with MIGRATION_RESULT and the removed MigrationInfo
 *   ChannelServerNode (target)
 *      7 : receive MIGRATE_IN (channel server), send MIGRATION_REQUEST to CentralServerNode [same as migration step 3 onwards]
 *      8 : block until MIGRATION_RESULT received from CentralServerNode
 *     12 : perform migration, send USER_CONNECT to central server
 *
 * User tracking
 *   ChannelServerNode
 *      1 : receive MIGRATE_IN (channel server)
 *      2 : send USER_CONNECT to CentralServerNode
 *      4 : user in channel server requires updating (level, job, location)
 *      5 : send USER_UPDATE to CentralServerNode
 *      7 : user disconnects from channel server
 *      8 : send USER_DISCONNECT to CentralServerNode
 *   CentralServerNode
 *      3 : receive USER_CONNECT, store in UserStorage
 *      6 : receive USER_UPDATE, update in UserStorage
 *      7 : receive USER_DISCONNECT, remove from UserStorage
 *
 * User remote packets
 *   ChannelServerNode (source)
 *      1 : send USER_REMOTE_PACKET [character name, packet] to CentralServerNode
 *   CentralServerNode
 *      2 : receive USER_REMOTE_PACKET
 *      3 : resolve target user and channel using UserStorage
 *      4 : send USER_REMOTE_PACKET [character id, packet] to target ChannelServerNode
 *   ChannelServerNode (target)
 *      5 : receive USER_REMOTE_PACKET
 *      6 : send packet to target user
 *
 * User query
 *   ChannelServerNode
 *      1 : send USER_QUERY_REQUEST [character names] to CentralServerNode
 *      5 : receive USER_QUERY_RESULT and resolve future
 *   CentralServerNode
 *      2 : receive USER_QUERY_REQUEST
 *      3 : resolve queried users from UserStorage
 *      4 : send USER_QUERY_RESULT to ChannelServerNode
 * </pre>
 */
public enum CentralPacketHeader {
    INITIALIZE_REQUEST,
    INITIALIZE_RESULT,
    SHUTDOWN_REQUEST,
    SHUTDOWN_RESULT,
    MIGRATION_REQUEST,
    MIGRATION_RESULT,
    TRANSFER_REQUEST,
    TRANSFER_RESULT,
    USER_CONNECT,
    USER_UPDATE,
    USER_DISCONNECT,
    USER_PACKET_REQUEST,
    USER_PACKET_RECEIVE,
    USER_QUERY_REQUEST,
    USER_QUERY_RESULT;

    private static final Map<Integer, CentralPacketHeader> headerMap = new HashMap<>();

    static {
        for (CentralPacketHeader header : values()) {
            headerMap.put(header.getValue(), header);
        }
    }

    public final int getValue() {
        return ordinal();
    }

    public static CentralPacketHeader getByValue(int op) {
        return headerMap.get(op);
    }
}
