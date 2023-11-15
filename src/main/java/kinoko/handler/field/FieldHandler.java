package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.server.ChannelServer;
import kinoko.server.Server;
import kinoko.server.client.Client;
import kinoko.server.client.MigrationRequest;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.TRANSFER_CHANNEL)
    public static void handleTransferChannel(Client c, InPacket inPacket) {
        final byte channelId = inPacket.decodeByte();
        inPacket.decodeInt(); // update_time

        final Account account = c.getAccount();
        final Optional<ChannelServer> channelResult = Server.getChannelServerById(account.getWorldId(), channelId);
        if (channelResult.isEmpty()) {
            c.write(FieldPacket.transferChannelReqIgnored(1));
            return;
        }
        final ChannelServer channelServer = channelResult.get();
        final Optional<MigrationRequest> mrResult = Server.submitMigrationRequest(c, channelServer, c.getUser().getId());
        if (mrResult.isEmpty()) {
            log.debug("Failed to submit migration request for character ID : {}", c.getUser().getId());
            c.write(FieldPacket.transferChannelReqIgnored(1));
            return;
        }
        c.write(ClientPacket.migrateCommand(channelServer.getAddress(), channelServer.getPort()));
    }
}
