package kinoko.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.ServerConfig;
import kinoko.server.node.Client;
import kinoko.server.node.ServerNode;
import kinoko.util.Util;

public class PacketChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final boolean plain;
    private final PacketHandler handler;
    private final ServerNode node;

    public PacketChannelInitializer(PacketHandler handler, ServerNode node, boolean plain) {
        this.plain = plain;
        this.handler = handler;
        this.node = node;
    }

    public PacketChannelInitializer(PacketHandler handler, ServerNode node) {
        this(handler, node, ServerConfig.PLAIN_TRAFFIC);
    }


    @Override
    protected void initChannel(SocketChannel c) {
        var recvIv = getNewIv();
        var sendIv = getNewIv();
        if (plain) {
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

    protected static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        Util.getRandom().nextBytes(iv);
        return iv;
    }

    protected static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        Util.getRandom().nextBytes(clientKey);
        return clientKey;
    }
}
