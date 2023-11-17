package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.provider.map.PortalInfo;
import kinoko.server.ChannelServer;
import kinoko.server.Server;
import kinoko.server.client.Client;
import kinoko.server.client.MigrationRequest;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.Account;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.TRANSFER_FIELD)
    public static void handleTransferField(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte();
        final int targetField = inPacket.decodeInt(); // dwTargetField
        final String portalName = inPacket.decodeString(); // sPortal
        if (!portalName.isEmpty()) {
            final short x = inPacket.decodeShort();
            final short y = inPacket.decodeShort();
        }
        inPacket.decodeByte(); // 0
        inPacket.decodeByte(); // bPremium
        inPacket.decodeByte(); // bChase -> int, int

        final Field currentField = user.getField();
        final Optional<PortalInfo> portalResult = currentField.getPortalByName(portalName);
        if (portalResult.isEmpty() || !portalResult.get().hasDestinationField()) {
            log.error("[FieldHandler] Tried to use portal : {} on field ID : {}", portalName, currentField.getFieldId());
            user.write(FieldPacket.transferFieldReqIgnored(2));
            return;
        }

        // Move User to Field
        final int nextFieldId = portalResult.get().getDestinationFieldId();
        final String nextPortalName = portalResult.get().getDestinationPortalName();
        final Optional<Field> nextFieldResult = user.getConnectedServer().getFieldById(nextFieldId);
        if (nextFieldResult.isEmpty()) {
            user.write(FieldPacket.transferFieldReqIgnored(2));
            return;
        }
        final Field nextField = nextFieldResult.get();
        final Optional<PortalInfo> nextPortalResult = nextField.getPortalByName(nextPortalName);
        if (nextPortalResult.isEmpty()) {
            log.error("[FieldHandler] Tried to warp to portal : {} on field ID : {}", nextPortalName, nextField.getFieldId());
            user.write(FieldPacket.transferFieldReqIgnored(2));
            return;
        }
        user.warp(nextField, nextPortalResult.get().getPortalId(), false, false);
    }

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
        final Optional<MigrationRequest> mrResult = Server.submitMigrationRequest(c, channelServer, c.getUser().getCharacterId());
        if (mrResult.isEmpty()) {
            log.error("[FieldHandler] Failed to submit migration request for character ID : {}", c.getUser().getCharacterId());
            c.write(FieldPacket.transferChannelReqIgnored(1));
            return;
        }
        c.write(ClientPacket.migrateCommand(channelServer.getAddress(), channelServer.getPort()));
    }
}
