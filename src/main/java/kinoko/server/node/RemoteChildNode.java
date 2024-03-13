package kinoko.server.node;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import kinoko.server.packet.OutPacket;

import java.util.concurrent.atomic.AtomicInteger;

public final class RemoteChildNode {
    public static final AttributeKey<RemoteChildNode> NODE_KEY = AttributeKey.valueOf("R");
    private final AtomicInteger userCount = new AtomicInteger(0);
    private final SocketChannel socketChannel;
    private int channelId;
    private byte[] channelHost;
    private int channelPort;

    public RemoteChildNode(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public byte[] getChannelHost() {
        return channelHost;
    }

    public void setChannelHost(byte[] channelHost) {
        this.channelHost = channelHost;
    }

    public int getChannelPort() {
        return channelPort;
    }

    public void setChannelPort(int channelPort) {
        this.channelPort = channelPort;
    }

    public int getUserCount() {
        return userCount.get();
    }

    public void incrementUserCount() {
        userCount.incrementAndGet();
    }

    public void decrementUserCount() {
        userCount.decrementAndGet();
    }

    public void write(OutPacket outPacket) {
        socketChannel.writeAndFlush(outPacket);
    }
}
