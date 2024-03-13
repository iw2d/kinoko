package kinoko.server.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import kinoko.server.node.ServerNode;
import kinoko.server.packet.OutPacket;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class NettyClient {
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");
    private final Lock encoderLock = new ReentrantLock();
    private final ServerNode serverNode;
    private final SocketChannel socketChannel;
    private byte[] sendIv;
    private byte[] recvIv;
    private int storedLength = -1;

    public NettyClient(ServerNode serverNode, SocketChannel socketChannel) {
        this.serverNode = serverNode;
        this.socketChannel = socketChannel;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }

    public final byte[] getSendIv() {
        return sendIv;
    }

    public final void setSendIv(byte[] siv) {
        this.sendIv = siv;
    }

    public final byte[] getRecvIv() {
        return recvIv;
    }

    public final void setRecvIv(byte[] riv) {
        this.recvIv = riv;
    }

    public final int getStoredLength() {
        return storedLength;
    }

    public final void setStoredLength(int storedLength) {
        this.storedLength = storedLength;
    }

    public final void acquireEncoderState() {
        encoderLock.lock();
    }

    public final void releaseEncoderState() {
        encoderLock.unlock();
    }

    public final void write(OutPacket outPacket) {
        socketChannel.writeAndFlush(outPacket);
    }

    public void close() {
        socketChannel.close();
    }
}
