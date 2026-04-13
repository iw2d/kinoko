package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.node.Client;
import kinoko.server.node.ServerNode;
import kinoko.server.packet.OutPacket;
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
    public void initChannel(SocketChannel channel) {
        if (!node.isInitialized()) {
            channel.close();
            return;
        }

        final Client client = new Client(node, channel);
        final byte[] recvSeq = client.getRecvSeq().clone();
        final byte[] sendSeq = client.getSendSeq().clone();
        channel.pipeline().addLast("decoder", ServerConfig.PLAIN_TRAFFIC ? new PlainPacketDecoder() : new PacketDecoder(recvSeq));
        channel.pipeline().addLast("handler", handler);
        channel.pipeline().addLast("connect", new ConnectHandler(sendSeq));

        client.setClientKey(getNewClientKey());
        channel.attr(NettyClient.CLIENT_KEY).set(client);
        channel.writeAndFlush(connect(recvSeq, sendSeq));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while initializing channel", cause);
        cause.printStackTrace();
        ctx.close();
    }

    private static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        Util.getRandom().nextBytes(clientKey);
        return clientKey;
    }

    public static byte[] connect(byte[] recvSeq, byte[] sendSeq) {
        final OutPacket outPacket = OutPacket.of();
        outPacket.encodeShort(ServerConstants.GAME_VERSION);
        outPacket.encodeString(ServerConstants.PATCH);
        outPacket.encodeArray(recvSeq);
        outPacket.encodeArray(sendSeq);
        outPacket.encodeByte(ServerConstants.LOCALE);
        return outPacket.getData();
    }
}
