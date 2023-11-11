package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.ServerConstants;
import kinoko.server.crypto.IGCipher;
import kinoko.server.crypto.MapleCrypto;
import kinoko.server.crypto.ShandaCrypto;
import kinoko.server.packet.NioBufferInPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger log = LogManager.getLogger(PacketDecoder.class);
    private static final short RECV_VERSION = ServerConstants.GAME_VERSION;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        final NettyClient c = ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (c == null) {
            return;
        }
        final byte[] iv = c.getRecvIv();
        if (c.getStoredLength() < 0) {
            if (in.readableBytes() < 4) {
                return;
            }
            final byte[] header = new byte[4];
            in.readBytes(header);

            final int version = ((header[0] ^ iv[2]) & 0xFF) | (((header[1] ^ iv[3]) << 8) & 0xFF00);
            if (version != RECV_VERSION) {
                log.warn("[PacketDecoder] | Incorrect packet seq, dropping client");
                c.close();
                return;
            }
            final int length = ((header[0] ^ header[2]) & 0xFF) | (((header[1] ^ header[3]) << 8) & 0xFF00);
            c.setStoredLength(length);
        } else if (in.readableBytes() >= c.getStoredLength()) {
            final byte[] data = new byte[c.getStoredLength()];
            in.readBytes(data);
            c.setStoredLength(-1);

            MapleCrypto.crypt(data, iv);
            ShandaCrypto.decrypt(data);
            c.setRecvIv(IGCipher.innoHash(iv));

            NioBufferInPacket inPacket = new NioBufferInPacket(data);
            out.add(inPacket);
        }
    }
}
