package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.packet.user.UserRemote;
import kinoko.server.ServerConstants;
import kinoko.server.header.CentralHeader;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.user.RemoteUser;
import kinoko.util.Util;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.user.PartyInfo;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterTemporaryStat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class CentralClientHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralClientHandler.class);
    private final ChannelServerNode channelServerNode;

    public CentralClientHandler(ChannelServerNode channelServerNode) {
        this.channelServerNode = channelServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[ChannelServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeRequest -> {
                    ctx.channel().writeAndFlush(CentralPacket.initializeResult(channelServerNode.getChannelId(), ServerConstants.SERVER_HOST, channelServerNode.getChannelPort()));
                }
                case ShutdownRequest -> {
                    try {
                        channelServerNode.shutdown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                case MigrateResult -> handleMigrateResult(inPacket);
                case TransferResult -> handleTransferResult(inPacket);
                case UserPacketReceive -> handleUserPacketReceive(inPacket);
                case UserPacketBroadcast -> handleUserPacketBroadcast(inPacket);
                case UserQueryResult -> handleUserQueryResult(inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(inPacket);
                case PartyResult -> handlePartyResult(inPacket);
                case MessengerResult -> handleMessengerResult(inPacket);
                case null -> log.error("Central client {} received an unknown opcode : {}", channelServerNode.getChannelId() + 1, op);
                default -> log.error("Central client {} received an unhandled header : {}", channelServerNode.getChannelId() + 1, header);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (channelServerNode.isShutdown()) {
            return;
        }
        log.error("Central client {} lost connection to central server", channelServerNode.getChannelId() + 1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }


    // HANDLER METHODS -------------------------------------------------------------------------------------------------

    private void handleMigrateResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        final MigrationInfo migrationResult = success ? MigrationInfo.decode(inPacket) : null;
        channelServerNode.completeMigrationRequest(requestId, migrationResult);
    }

    private void handleTransferResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        final TransferInfo transferResult = success ? TransferInfo.decode(inPacket) : null;
        channelServerNode.completeTransferRequest(requestId, transferResult);
    }

    private void handleUserPacketReceive(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for UserPacketReceive");
            return;
        }
        // Write to target client
        targetUserResult.get().write(OutPacket.of(packetData));
    }

    private void handleUserPacketBroadcast(InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final Set<Integer> characterIds = new HashSet<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        for (int characterId : characterIds) {
            final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
            if (targetUserResult.isEmpty()) {
                continue;
            }
            targetUserResult.get().write(outPacket);
        }
    }

    private void handleUserQueryResult(InPacket inPacket) {
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final Set<RemoteUser> remoteUsers = new HashSet<>();
        for (int i = 0; i < size; i++) {
            remoteUsers.add(RemoteUser.decode(inPacket));
        }
        channelServerNode.completeUserQueryRequest(requestId, remoteUsers);
    }

    private void handleServerPacketBroadcast(InPacket inPacket) {
        final int packetLength = inPacket.decodeInt();
        final byte[] packetData = inPacket.decodeArray(packetLength);
        final OutPacket outPacket = OutPacket.of(packetData);
        channelServerNode.submitChannelPacketBroadcast(outPacket);
    }

    private void handlePartyResult(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean hasParty = inPacket.decodeBoolean();
        final PartyInfo partyInfo = hasParty ? PartyInfo.decode(inPacket) : null;
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for PartyResult");
            return;
        }
        try (var locked = targetUserResult.get().acquire()) {
            final User user = locked.get();
            // Cancel party aura
            user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
            if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Aura)) {
                BattleMage.cancelPartyAura(user, user.getSecondaryStat().getOption(CharacterTemporaryStat.Aura).rOption);
            }
            // Set party info and update members
            user.setPartyInfo(partyInfo);
            user.getField().getUserPool().forEachPartyMember(user, (member) -> {
                try (var lockedMember = member.acquire()) {
                    user.write(UserRemote.receiveHp(lockedMember.get()));
                    lockedMember.get().write(UserRemote.receiveHp(user));
                }
            });
            if (user.getTownPortal() != null && user.getTownPortal().getTownField() == user.getField()) {
                user.write(FieldPacket.townPortalRemoved(user, false));
            }
        }
    }

    private void handleMessengerResult(InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final int messengerId = inPacket.decodeInt();
        // Resolve target user
        final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(characterId);
        if (targetUserResult.isEmpty()) {
            log.error("Could not resolve target user for MessengerResult");
            return;
        }
        try (var locked = targetUserResult.get().acquire()) {
            final User user = locked.get();
            user.setMessengerId(messengerId);
        }
    }
}
