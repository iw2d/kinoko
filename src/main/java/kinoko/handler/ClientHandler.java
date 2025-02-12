package kinoko.handler;

import kinoko.packet.CustomPacket;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.ChannelInfo;
import kinoko.server.node.Client;
import kinoko.server.node.LoginServerNode;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class ClientHandler {
    private static final Logger log = LogManager.getLogger(ClientHandler.class);

    @Handler(InHeader.AliveAck)
    public static void handleAliveAck(Client c, InPacket inPacket) {
    }

    @Handler(InHeader.ExceptionLog)
    public static void handleExceptionLog(Client c, InPacket inPacket) {
        final String data = inPacket.decodeString();
        log.error("Exception log : {}", data);
    }

    @Handler(InHeader.ClientDumpLog)
    public static void handleClientDumpLog(Client c, InPacket inPacket) {
        final short callType = inPacket.decodeShort();
        final int errorType = inPacket.decodeInt();
        final int bufferSize = inPacket.decodeShort();
        inPacket.decodeInt(); // unk

        final short op = inPacket.decodeShort();
        log.error("Error {} | {}({}) | {}", errorType, OutHeader.getByValue(op), Util.opToString(op), inPacket);
    }


    // Custom Packets

    @Handler(InHeader.RegisterMigration)
    public static void handleRegisterMigration(Client c, InPacket inPacket) {
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final int channelId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = c.getClientKey();

        // Resolve target channel
        final LoginServerNode loginServerNode = (LoginServerNode) c.getServerNode();
        final Optional<ChannelInfo> channelInfoResult = loginServerNode.getChannelById(channelId);
        if (channelInfoResult.isEmpty()) {
            log.error("Could not resolve target channel for migration request for character ID : {}", characterId);
            c.write(CustomPacket.registerMigrationResultFail());
            return;
        }

        // Create and submit migration request
        final MigrationInfo migrationInfo = MigrationInfo.from(channelId, accountId, characterId, machineId, clientKey);
        loginServerNode.submitLoginRequest(migrationInfo, (transferResult) -> {
            if (transferResult.isEmpty()) {
                log.error("Failed to submit migration request for character ID : {}", characterId);
                c.write(CustomPacket.registerMigrationResultFail());
                return;
            }

            // Send migration result success
            final TransferInfo transferInfo = transferResult.get();
            c.write(CustomPacket.registerMigrationResultSuccess(transferInfo.getChannelHost(), transferInfo.getChannelPort(), clientKey));
        });
    }
}
