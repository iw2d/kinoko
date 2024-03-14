package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.packet.field.FieldPacket;
import kinoko.server.ServerConstants;
import kinoko.server.node.ChannelServerNode;
import kinoko.server.node.MigrationInfo;
import kinoko.server.node.TransferInfo;
import kinoko.server.node.UserProxy;
import kinoko.server.packet.InPacket;
import kinoko.server.whisper.WhisperFlag;
import kinoko.server.whisper.WhisperResult;
import kinoko.util.Util;
import kinoko.world.user.User;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class CentralClientHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(CentralClientHandler.class);
    private final ChannelServerNode channelServerNode;

    public CentralClientHandler(ChannelServerNode channelServerNode) {
        this.channelServerNode = channelServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final int op = inPacket.decodeInt();
        final CentralPacketHeader header = CentralPacketHeader.getByValue(op);
        log.log(Level.DEBUG, "[ChannelServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        switch (header) {
            case INITIALIZE_REQUEST -> {
                ctx.channel().writeAndFlush(CentralPacket.initializeResult(channelServerNode.getChannelId(), ServerConstants.SERVER_HOST, channelServerNode.getChannelPort()));
            }
            case SHUTDOWN_REQUEST -> {
                try {
                    channelServerNode.shutdown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            case MIGRATION_RESULT -> {
                final int requestId = inPacket.decodeInt();
                final boolean success = inPacket.decodeBoolean();
                final MigrationInfo migrationResult = success ? MigrationInfo.decode(inPacket) : null;
                channelServerNode.completeMigrationRequest(requestId, migrationResult);
            }
            case TRANSFER_RESULT -> {
                final int requestId = inPacket.decodeInt();
                final boolean success = inPacket.decodeBoolean();
                final TransferInfo transferResult = success ? TransferInfo.decode(inPacket) : null;
                channelServerNode.completeTransferRequest(requestId, transferResult);
            }
            case WHISPER_RECEIVE -> {
                final int targetCharacterId = inPacket.decodeInt();
                final int sourceChannelId = inPacket.decodeInt();
                final String sourceCharacterName = inPacket.decodeString();
                final WhisperFlag flag = WhisperFlag.getByValue(inPacket.decodeByte());
                if (flag != WhisperFlag.WHISPER && flag != WhisperFlag.BLOCKED) {
                    log.error("Unexpected flag {} received for WHISPER_RECEIVE", flag);
                    return;
                }
                // Resolve target user
                final Optional<User> targetUserResult = channelServerNode.getUserByCharacterId(targetCharacterId);
                if (targetUserResult.isEmpty()) {
                    log.error("Could not resolve target user for WHISPER_RECEIVE");
                    return;
                }
                // Write to target client
                if (flag == WhisperFlag.WHISPER) {
                    final String message = inPacket.decodeString();
                    targetUserResult.get().write(FieldPacket.whisper(WhisperResult.whisperReceive(sourceChannelId, sourceCharacterName, message)));
                } else {
                    targetUserResult.get().write(FieldPacket.whisper(WhisperResult.whisperBlocked(sourceCharacterName)));
                }
            }
            case WHISPER_RESULT -> {
                final int requestId = inPacket.decodeInt();
                final boolean success = inPacket.decodeBoolean();
                final UserProxy whisperResult = success ? UserProxy.decode(inPacket) : null;
                channelServerNode.completeWhisperRequest(requestId, whisperResult);
            }
            case null -> {
                log.error("Central client {} received an unknown opcode : {}", channelServerNode.getChannelId() + 1, op);
            }
            default -> {
                log.error("Central client {} received an unhandled header : {}", channelServerNode.getChannelId() + 1, header);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.error("Central client {} lost connection to central server", channelServerNode.getChannelId() + 1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }
}
