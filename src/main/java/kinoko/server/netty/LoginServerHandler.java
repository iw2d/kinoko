package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.packet.CentralPacket;
import kinoko.server.ServerConstants;
import kinoko.server.header.CentralHeader;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.ChannelInfo;
import kinoko.server.node.LoginServerNode;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoginServerHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(LoginServerHandler.class);
    private final LoginServerNode loginServerNode;

    public LoginServerHandler(LoginServerNode loginServerNode) {
        this.loginServerNode = loginServerNode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final int op = inPacket.decodeShort();
        final CentralHeader header = CentralHeader.getByValue(op);
        log.log(Level.TRACE, "[ChannelServerNode] | {}({}) {}", header, Util.opToString(op), inPacket);
        ServerExecutor.submitService(() -> {
            switch (header) {
                case InitializeRequest -> {
                    ctx.channel().writeAndFlush(CentralPacket.initializeResult(GameConstants.CHANNEL_LOGIN, ServerConstants.SERVER_HOST, ServerConstants.LOGIN_PORT));
                }
                case InitializeComplete -> {
                    final int size = inPacket.decodeInt();
                    for (int i = 0; i < size; i++) {
                        final int channelId = inPacket.decodeInt();
                        final int userCount = inPacket.decodeInt();
                        loginServerNode.setChannel(ChannelInfo.from(channelId, userCount));
                    }
                    loginServerNode.setInitialized(true);
                }
                case OnlineResult -> {
                    final int requestId = inPacket.decodeInt();
                    final boolean online = inPacket.decodeBoolean();
                    loginServerNode.completeOnlineRequest(requestId, online);
                }
                case TransferResult -> {
                    final int requestId = inPacket.decodeInt();
                    final boolean success = inPacket.decodeBoolean();
                    final TransferInfo transferResult = success ? TransferInfo.decode(inPacket) : null;
                    loginServerNode.completeLoginRequest(requestId, transferResult);
                }
                case ShutdownRequest -> {
                    try {
                        loginServerNode.shutdown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (loginServerNode.isShutdown()) {
            return;
        }
        log.error("Central client {} lost connection to central server", 0);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }
}
