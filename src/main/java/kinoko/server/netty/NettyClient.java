package kinoko.server.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import kinoko.server.node.ServerNode;
import kinoko.server.packet.OutPacket;

public abstract class NettyClient {
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");
    private final ServerNode serverNode;
    private final SocketChannel socketChannel;

    public NettyClient(ServerNode serverNode, SocketChannel socketChannel) {
        this.serverNode = serverNode;
        this.socketChannel = socketChannel;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }

    public final void write(OutPacket outPacket) {
        socketChannel.writeAndFlush(outPacket);
    }

    public void close() {
        socketChannel.close();
    }
}
