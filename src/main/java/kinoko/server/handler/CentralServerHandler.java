package kinoko.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.MessengerPacket;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.GuildPacket;
import kinoko.packet.world.PartyPacket;
import kinoko.server.guild.*;
import kinoko.server.header.CentralHeader;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;
import kinoko.server.messenger.Messenger;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.messenger.MessengerUser;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.CentralServerNode;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.Party;
import kinoko.server.party.PartyRequest;
import kinoko.server.party.PartyResultType;
import kinoko.server.user.RemoteUser;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.user.GuildInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public final class CentralServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralServerHandler.class);
    private final CentralServerNode centralServerNode;

    private final CentralMessengerHandler messengerHandler;
    private final CentralPartyHandler partyHandler;
    private final CentralGuildHandler guildHandler;

    public CentralServerHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
        this.messengerHandler = new CentralMessengerHandler(centralServerNode);
        this.partyHandler = new CentralPartyHandler(centralServerNode);
        this.guildHandler = new CentralGuildHandler(centralServerNode);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final RemoteServerNode remoteServerNode = ctx.channel().attr(RemoteServerNode.NODE_KEY).get();
        if (remoteServerNode == null) {
            log.error("Received packet from unknown node {}", ctx.channel().remoteAddress());
            return;
        }
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[CentralServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeResult -> handleInitializeResult(remoteServerNode, inPacket);
                case ShutdownResult -> handleShutdownResult(remoteServerNode, inPacket);
                case OnlineRequest -> handleOnlineRequest(remoteServerNode, inPacket);
                case MigrateRequest -> handleMigrateRequest(remoteServerNode, inPacket);
                case TransferRequest -> handleTransferRequest(remoteServerNode, inPacket);
                case UserConnect -> handleUserConnect(remoteServerNode, inPacket);
                case UserUpdate -> handleUserUpdate(remoteServerNode, inPacket);
                case UserDisconnect -> handleUserDisconnect(remoteServerNode, inPacket);
                case UserPacketRequest -> handleUserPacketRequest(remoteServerNode, inPacket);
                case UserPacketReceive -> handleUserPacketReceive(remoteServerNode, inPacket);
                case UserPacketBroadcast -> handleUserPacketBroadcast(remoteServerNode, inPacket);
                case UserQueryRequest -> handleUserQueryRequest(remoteServerNode, inPacket);
                case WorldSpeakerRequest -> handleWorldSpeakerRequest(remoteServerNode, inPacket);
                case ServerPacketBroadcast -> handleServerPacketBroadcast(remoteServerNode, inPacket);
                case MessengerRequest -> messengerHandler.handleMessengerRequest(remoteServerNode, inPacket);
                case PartyRequest -> partyHandler.handlePartyRequest(remoteServerNode, inPacket);
                case GuildRequest -> guildHandler.handleGuildRequest(remoteServerNode, inPacket);
                case BoardRequest -> guildHandler.handleBoardRequest(remoteServerNode, inPacket);
                case null -> log.error("Central Server received an unknown opcode : {}", op);
                default -> log.error("Central Server received an unhandled header : {}", header);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final RemoteServerNode remoteServerNode = ctx.channel().attr(RemoteServerNode.NODE_KEY).get();
        if (remoteServerNode != null && !centralServerNode.isShutdown()) {
            log.error("Lost connection to channel {}", remoteServerNode.getChannelId() + 1);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }


    // HANDLER METHODS -------------------------------------------------------------------------------------------------

    private void handleInitializeResult(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final byte[] channelHost = inPacket.decodeArray(4);
        final int channelPort = inPacket.decodeInt();
        // Initialize remote node
        remoteServerNode.setChannelId(channelId);
        remoteServerNode.setChannelHost(channelHost);
        remoteServerNode.setChannelPort(channelPort);
        centralServerNode.addServerNode(remoteServerNode);
    }

    private void handleShutdownResult(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final boolean success = inPacket.decodeBoolean();
        if (!success) {
            log.error("Failed to shutdown channel {}, trying again", channelId + 1);
            remoteServerNode.write(CentralPacket.shutdownRequest());
            return;
        }
        centralServerNode.removeServerNode(channelId);
    }

    private void handleOnlineRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Login - check if account is already logged in
        final int requestId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final boolean online = centralServerNode.isOnline(accountId);
        remoteServerNode.write(CentralPacket.onlineResult(requestId, online));
    }

    private void handleMigrateRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Channel migration - complete stored migration request
        final int requestId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);
        final Optional<MigrationInfo> migrationResult = centralServerNode.completeMigrationRequest(remoteServerNode.getChannelId(), accountId, characterId, machineId, clientKey);
        remoteServerNode.write(CentralPacket.migrateResult(requestId, migrationResult.orElse(null)));
    }

    private void handleTransferRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Channel transfer - create migration request and reply with transfer info
        final int requestId = inPacket.decodeInt();
        final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
        // Resolve target channel
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(migrationInfo.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", migrationInfo.getChannelId() + 1);
            remoteServerNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        final RemoteServerNode targetNode = targetNodeResult.get();
        // Submit migration request
        if (!centralServerNode.submitMigrationRequest(migrationInfo)) {
            log.error("Failed to submit migration request for character ID : {}", migrationInfo.getCharacterId());
            remoteServerNode.write(CentralPacket.transferResult(requestId, null));
            return;
        }
        // Reply with transfer info
        remoteServerNode.write(CentralPacket.transferResult(requestId, new TransferInfo(
                targetNode.getChannelHost(),
                targetNode.getChannelPort()
        )));
    }

    private void handleUserConnect(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.addUser(remoteUser);
        messengerHandler.updateMessengerUser(remoteUser);
        partyHandler.updatePartyMember(remoteUser, false);
        guildHandler.updateGuildMember(remoteUser, false);
    }

    private void handleUserUpdate(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.updateUser(remoteUser);
        messengerHandler.updateMessengerUser(remoteUser);
        partyHandler.updatePartyMember(remoteUser, true);
        guildHandler.updateGuildMember(remoteUser, true);
    }

    private void handleUserDisconnect(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final RemoteUser remoteUser = RemoteUser.decode(inPacket);
        centralServerNode.removeUser(remoteUser);
        // Check if transfer
        if (centralServerNode.isMigrating(remoteUser.getAccountId())) {
            return;
        }
        // Leave messenger
        messengerHandler.leaveMessenger(remoteUser);
        // Update party and guild
        remoteUser.setChannelId(GameConstants.CHANNEL_OFFLINE);
        remoteUser.setFieldId(GameConstants.UNDEFINED_FIELD_ID);
        partyHandler.updatePartyMember(remoteUser, false);
        guildHandler.updateGuildMember(remoteUser, false);
    }

    private void handleUserPacketRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final String characterName = inPacket.decodeString();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        // Resolve target user
        final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(characterName);
        if (targetResult.isEmpty()) {
            return;
        }
        final RemoteUser target = targetResult.get();
        // Resolve target node
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), remotePacket));
    }

    private void handleUserPacketReceive(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        // Resolve target user
        final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(characterId);
        if (targetResult.isEmpty()) {
            return;
        }
        final RemoteUser target = targetResult.get();
        // Resolve target node
        final Optional<RemoteServerNode> targetNodeResult = centralServerNode.getChannelServerNodeById(target.getChannelId());
        if (targetNodeResult.isEmpty()) {
            log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
            return;
        }
        // Send UserPacketReceive to target channel node
        targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), remotePacket));
    }

    private void handleUserPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final List<Integer> characterIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            characterIds.add(inPacket.decodeInt());
        }
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.userPacketBroadcast(characterIds, remotePacket));
        }
    }

    private void handleUserQueryRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        // Resolve queried users
        final int requestId = inPacket.decodeInt();
        final int size = inPacket.decodeInt();
        final List<RemoteUser> remoteUsers;
        if (size < 0) {
            remoteUsers = centralServerNode.getUsers(); // Get all connected users
        } else {
            remoteUsers = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                final String characterName = inPacket.decodeString();
                centralServerNode.getUserByCharacterName(characterName).ifPresent(remoteUsers::add);
            }
        }
        // Reply with queried remote users
        remoteServerNode.write(CentralPacket.userQueryResult(requestId, remoteUsers));
    }

    private void handleWorldSpeakerRequest(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final int characterId = inPacket.decodeInt();
        final boolean avatar = inPacket.decodeBoolean();
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.worldSpeakerRequest(characterId, avatar, remotePacket));
        }
    }

    private void handleServerPacketBroadcast(RemoteServerNode remoteServerNode, InPacket inPacket) {
        final OutPacket remotePacket = OutPacket.decodeRemotePacket(inPacket);
        for (RemoteServerNode serverNode : centralServerNode.getChannelServerNodes()) {
            serverNode.write(CentralPacket.serverPacketBroadcast(remotePacket));
        }
    }
}
