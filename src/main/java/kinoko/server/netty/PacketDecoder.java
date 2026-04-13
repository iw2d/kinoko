package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.ServerConstants;
import kinoko.server.node.Client;
import kinoko.server.node.ServerExecutor;
import kinoko.util.crypto.IGCipher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {
    public static final short RECV_VERSION = ServerConstants.GAME_VERSION;
    private static final Logger log = LogManager.getLogger(PacketDecoder.class);
    private final byte[] header = new byte[4];
    private final byte[] recvSeq;
    private int storedLength = -1;


    public PacketDecoder(byte[] recvSeq) {
        this.recvSeq = recvSeq;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (storedLength < 0) {
            if (in.readableBytes() < 4) {
                return;
            }
            in.readBytes(header);

            final int version = ((header[0] ^ recvSeq[2]) & 0xFF) | (((header[1] ^ recvSeq[3]) << 8) & 0xFF00);
            if (version != RECV_VERSION) {
                log.warn("Incorrect packet seq, dropping client");
                final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
                ServerExecutor.submit(client, client::close);
                return;
            }
            storedLength = ((header[0] ^ header[2]) & 0xFF) | (((header[1] ^ header[3]) << 8) & 0xFF00);
        }
        if (in.readableBytes() >= storedLength) {
            final byte[] data = new byte[storedLength];
            in.readBytes(data);
            storedLength = -1;

            out.add(data);
            IGCipher.innoHash(recvSeq);
        }
    }
}
