package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.ServerConfig;
import kinoko.server.node.Client;
import kinoko.server.node.ServerNode;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PacketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LogManager.getLogger(PacketChannelInitializer.class);
    private final PacketHandler handler;
    private final ServerNode node;

    public PacketChannelInitializer(PacketHandler handler, ServerNode node) {
        this.handler = handler;
        this.node = node;
    }

    @Override
    public void initChannel(SocketChannel c) {
        final byte[] recvIv = getNewIv();
        final byte[] sendIv = getNewIv();
        if (ServerConfig.PLAIN_TRAFFIC) {
            c.pipeline().addLast(new PlainPacketDecoder(), handler, new PlainPacketEncoder());
        } else {
            c.pipeline().addLast(new PacketDecoder(recvIv), handler, new PacketEncoder(sendIv));
        }

        if (!node.isInitialized()) {
            c.close();
            return;
        }

        final Client client = new Client(node, c);
        client.setClientKey(getNewClientKey());
        client.write(LoginPacket.connect(recvIv, sendIv));
        c.attr(NettyClient.CLIENT_KEY).set(client);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while initializing channel", cause);
        cause.printStackTrace();
        ctx.close();
    }

    private static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        Util.getRandom().nextBytes(iv);
        return iv;
    }

    private static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        Util.getRandom().nextBytes(clientKey);
        return clientKey;
    }
}
