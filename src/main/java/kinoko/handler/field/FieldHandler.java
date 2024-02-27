package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.ClientPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.world.WvsContext;
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
import kinoko.world.user.stat.Stat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class FieldHandler {
    private static final Logger log = LogManager.getLogger(FieldHandler.class);

    @Handler(InHeader.USER_TRANSFER_FIELD_REQUEST)
    public static void handleUserTransferFieldRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte();
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final int targetField = inPacket.decodeInt(); // dwTargetField
        final String portalName = inPacket.decodeString(); // sPortal
        if (!portalName.isEmpty()) {
            inPacket.decodeShort();
            inPacket.decodeShort();
        }
        inPacket.decodeByte(); // 0
        inPacket.decodeByte(); // bPremium
        inPacket.decodeByte(); // bChase -> int, int

        try (var locked = user.acquire()) {
            final boolean isRevive = portalName.isEmpty() && user.getHp() == 0;
            final int nextFieldId;
            final String nextPortalName;
            if (isRevive) {
                // Handle revive
                user.setHp(50);
                user.write(WvsContext.statChanged(Stat.HP, user.getHp(), true));
                nextFieldId = user.getField().getReturnMap();
                nextPortalName = "sp"; // spawn point
            } else {
                // Handle portal name
                final Field currentField = user.getField();
                final Optional<PortalInfo> portalResult = currentField.getPortalByName(portalName);
                if (portalResult.isEmpty() || !portalResult.get().hasDestinationField()) {
                    log.error("Tried to use portal : {} on field ID : {}", portalName, currentField.getFieldId());
                    user.dispose();
                    return;
                }
                final PortalInfo portal = portalResult.get();
                nextFieldId = portal.getDestinationFieldId();
                nextPortalName = portal.getDestinationPortalName();
            }

            // Move User to Field
            final Optional<Field> nextFieldResult = user.getConnectedServer().getFieldById(nextFieldId);
            if (nextFieldResult.isEmpty()) {
                user.write(FieldPacket.transferFieldReqIgnored(2));
                return;
            }
            final Field nextField = nextFieldResult.get();
            final Optional<PortalInfo> nextPortalResult = nextField.getPortalByName(nextPortalName);
            if (nextPortalResult.isEmpty()) {
                log.error("Tried to warp to portal : {} on field ID : {}", nextPortalName, nextField.getFieldId());
                user.dispose();
                return;
            }
            user.warp(nextField, nextPortalResult.get(), false, isRevive);
        }
    }

    @Handler(InHeader.USER_TRANSFER_CHANNEL_REQUEST)
    public static void handleUserTransferChannelRequest(Client c, InPacket inPacket) {
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
            log.error("Failed to submit migration request for character ID : {}", c.getUser().getCharacterId());
            c.write(FieldPacket.transferChannelReqIgnored(1));
            return;
        }
        c.write(ClientPacket.migrateCommand(channelServer.getAddress(), channelServer.getPort()));
    }
}
