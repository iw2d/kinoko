package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.server.node.*;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class CentralServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralServerHandler.class);
    private final CentralServerNode centralServerNode;

    public CentralServerHandler(CentralServerNode centralServerNode) {
        this.centralServerNode = centralServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final RemoteChildNode remoteChildNode = ctx.channel().attr(RemoteChildNode.NODE_KEY).get();
        if (remoteChildNode == null) {
            log.error("Received packet from unknown node {}", ctx.channel().remoteAddress());
            return;
        }
        final int op = inPacket.decodeInt();
        final CentralPacketHeader header = CentralPacketHeader.getByValue(op);
        log.log(Level.DEBUG, "[CentralServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        switch (header) {
            case INITIALIZE_RESULT -> {
                final int channelId = inPacket.decodeInt();
                final byte[] channelHost = inPacket.decodeArray(4);
                final int channelPort = inPacket.decodeInt();
                // Initialize child node
                remoteChildNode.setChannelId(channelId);
                remoteChildNode.setChannelHost(channelHost);
                remoteChildNode.setChannelPort(channelPort);
                centralServerNode.addChildNode(remoteChildNode);
            }
            case SHUTDOWN_RESULT -> {
                final int channelId = inPacket.decodeInt();
                final boolean success = inPacket.decodeBoolean();
                if (!success) {
                    log.error("Failed to shutdown channel {}, trying again", channelId + 1);
                    remoteChildNode.write(CentralPacket.shutdownRequest());
                    return;
                }
                centralServerNode.removeChildNode(channelId);
            }
            case MIGRATION_REQUEST -> {
                // Channel migration - complete stored migration request
                final int requestId = inPacket.decodeInt();
                final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
                final Optional<MigrationInfo> migrationResult = centralServerNode.completeMigrationRequest(migrationInfo);
                remoteChildNode.write(CentralPacket.migrationResult(requestId, migrationResult.orElse(null)));
            }
            case TRANSFER_REQUEST -> {
                // Channel transfer - create migration request and reply with transfer info
                final int requestId = inPacket.decodeInt();
                final MigrationInfo migrationInfo = MigrationInfo.decode(inPacket);
                if (!centralServerNode.submitMigrationRequest(migrationInfo)) {
                    // Transfer request failed
                    log.error("Failed to submit migration request for character ID : {}", migrationInfo.getCharacterId());
                    remoteChildNode.write(CentralPacket.transferResult(requestId, null));
                    return;
                }
                // Resolve target channel
                final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(migrationInfo.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", migrationInfo.getChannelId() + 1);
                    remoteChildNode.write(CentralPacket.transferResult(requestId, null));
                    return;
                }
                final RemoteChildNode targetNode = targetNodeResult.get();
                // Reply with transfer info
                remoteChildNode.write(CentralPacket.transferResult(requestId, new TransferInfo(
                        targetNode.getChannelHost(),
                        targetNode.getChannelPort()
                )));
            }
            case USER_CONNECT -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.addUser(remoteUser);
            }
            case USER_UPDATE -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.updateUser(remoteUser);
            }
            case USER_DISCONNECT -> {
                final RemoteUser remoteUser = RemoteUser.decode(inPacket);
                centralServerNode.removeUser(remoteUser);
            }
            case USER_PACKET_REQUEST -> {
                final String characterName = inPacket.decodeString();
                final int packetLength = inPacket.decodeInt();
                final byte[] packetData = inPacket.decodeArray(packetLength);
                // Resolve target user
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterName(characterName);
                if (targetResult.isEmpty()) {
                    return;
                }
                final RemoteUser target = targetResult.get();
                // Resolve target node
                final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
                    return;
                }
                // Send USER_PACKET_RECEIVE to target channel node
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
            }
            case USER_PACKET_RECEIVE -> {
                final int characterId = inPacket.decodeInt();
                final int packetLength = inPacket.decodeInt();
                final byte[] packetData = inPacket.decodeArray(packetLength);
                // Resolve target user
                final Optional<RemoteUser> targetResult = centralServerNode.getUserByCharacterId(characterId);
                if (targetResult.isEmpty()) {
                    return;
                }
                final RemoteUser target = targetResult.get();
                // Resolve target node
                final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
                if (targetNodeResult.isEmpty()) {
                    // Transfer request failed
                    log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
                    return;
                }
                // Send USER_PACKET_RECEIVE to target channel node
                targetNodeResult.get().write(CentralPacket.userPacketReceive(target.getCharacterId(), OutPacket.of(packetData)));
            }
            case USER_PACKET_BROADCAST -> {
                final int size = inPacket.decodeInt();
                final Set<Integer> characterIds = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    characterIds.add(inPacket.decodeInt());
                }
                final int packetLength = inPacket.decodeInt();
                final byte[] packetData = inPacket.decodeArray(packetLength);
                for (RemoteChildNode childNode : centralServerNode.getConnectedNodes()) {
                    childNode.write(CentralPacket.userPacketBroadcast(characterIds, OutPacket.of(packetData)));
                }
            }
            case USER_QUERY_REQUEST -> {
                // Resolve queried users
                final int requestId = inPacket.decodeInt();
                final int size = inPacket.decodeInt();
                final Set<RemoteUser> remoteUsers = new HashSet<>();
                for (int i = 0; i < size; i++) {
                    final String characterName = inPacket.decodeString();
                    centralServerNode.getUserByCharacterName(characterName)
                            .ifPresent(remoteUsers::add);
                }
                // Reply with queried remote users
                remoteChildNode.write(CentralPacket.userQueryResult(requestId, remoteUsers));
            }
            case PARTY_REQUEST -> {
                // TODO
            }
            case PARTY_UPDATE -> {
                // TODO
            }
            case null -> {
                log.error("Central Server received an unknown opcode : {}", op);
            }
            default -> {
                log.error("Central Server received an unhandled header : {}", header);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final RemoteChildNode remoteChildNode = ctx.channel().attr(RemoteChildNode.NODE_KEY).get();
        if (remoteChildNode != null) {
            log.error("Lost connection to channel {}", remoteChildNode.getChannelId());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }
}
