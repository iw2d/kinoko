package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.server.node.*;
import kinoko.server.packet.InPacket;
import kinoko.server.whisper.WhisperFlag;
import kinoko.util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

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
                final boolean success = centralServerNode.completeMigrationRequest(migrationInfo);
                remoteChildNode.write(CentralPacket.migrationResult(requestId, success ? migrationInfo : null));
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
                final UserProxy userProxy = UserProxy.decode(inPacket);
                centralServerNode.addUser(userProxy);
            }
            case USER_UPDATE -> {
                final UserProxy userProxy = UserProxy.decode(inPacket);
                centralServerNode.updateUser(userProxy);
            }
            case USER_DISCONNECT -> {
                final UserProxy userProxy = UserProxy.decode(inPacket);
                centralServerNode.removeUser(userProxy);
            }
            case WHISPER_REQUEST -> {
                final int requestId = inPacket.decodeInt();
                final String sourceCharacterName = inPacket.decodeString();
                final String targetCharacterName = inPacket.decodeString();
                final WhisperFlag flag = WhisperFlag.getByValue(inPacket.decodeByte());
                final String message = flag == WhisperFlag.WHISPER ? inPacket.decodeString() : null;
                // Resolve target user and reply with WHISPER_RESULT
                final Optional<UserProxy> targetResult = centralServerNode.getUserByCharacterName(targetCharacterName);
                ctx.channel().writeAndFlush(CentralPacket.whisperResult(requestId, targetResult.orElse(null)));
                // If not location request, send WHISPER_RECEIVE to target channel server node
                if (flag != null && flag != WhisperFlag.LOCATION && targetResult.isPresent()) {
                    final UserProxy target = targetResult.get();
                    final Optional<RemoteChildNode> targetNodeResult = centralServerNode.getChildNodeByChannelId(target.getChannelId());
                    if (targetNodeResult.isEmpty()) {
                        // Transfer request failed
                        log.error("Failed to resolve channel ID {}", target.getChannelId() + 1);
                        return;
                    }
                    targetNodeResult.get().write(CentralPacket.whisperReceive(flag, target.getCharacterId(), remoteChildNode.getChannelId(), sourceCharacterName, message));
                }
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
