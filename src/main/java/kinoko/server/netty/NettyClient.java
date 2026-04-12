package kinoko.server.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import kinoko.server.ServerConfig;
import kinoko.server.header.OutHeader;
import kinoko.server.node.ServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.NioBufferInPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.crypto.IGCipher;
import kinoko.util.crypto.MapleCrypto;
import kinoko.util.crypto.ShandaCrypto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NettyClient {
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("C");
    private static final Logger log = LogManager.getLogger(NettyClient.class);
    private final ServerNode serverNode;
    private final SocketChannel socketChannel;
    private final byte[] recvSeq;
    private final byte[] sendSeq;

    public NettyClient(ServerNode serverNode, SocketChannel socketChannel, byte[] recvSeq, byte[] sendSeq) {
        this.serverNode = serverNode;
        this.socketChannel = socketChannel;
        this.recvSeq = recvSeq;
        this.sendSeq = sendSeq;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public final synchronized InPacket read(byte[] data) {
        if (!ServerConfig.PLAIN_TRAFFIC) {
            MapleCrypto.crypt(data, recvSeq);
            ShandaCrypto.decrypt(data);
            IGCipher.innoHash(recvSeq);
        }
        return new NioBufferInPacket(data);
    }

    public final synchronized void write(OutPacket outPacket) {
        final OutHeader header = outPacket.getHeader();
        log.log(ServerConfig.DEBUG_MODE && !header.isIgnoreHeader() ? Level.DEBUG : Level.TRACE, "[Out] | {}", outPacket);

        final byte[] data = outPacket.getData();
        if (!ServerConfig.PLAIN_TRAFFIC) {
            ShandaCrypto.encrypt(data);
            MapleCrypto.crypt(data, sendSeq);
            IGCipher.innoHash(sendSeq);
        }
        socketChannel.writeAndFlush(data);
    }

    public void close() {
        socketChannel.close();
    }
}
