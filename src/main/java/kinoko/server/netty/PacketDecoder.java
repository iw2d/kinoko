package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.ServerConstants;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.NettyInPacket;
import kinoko.util.crypto.IGCipher;
import kinoko.util.crypto.MapleCrypto;
import kinoko.util.crypto.ShandaCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {
    public static final short RECV_VERSION = ServerConstants.GAME_VERSION;
    private static final Logger log = LogManager.getLogger(PacketDecoder.class);

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
                log.warn("Incorrect packet seq, dropping client");
                ServerExecutor.submitService(c::close);
                return;
            }
            final int length = ((header[0] ^ header[2]) & 0xFF) | (((header[1] ^ header[3]) << 8) & 0xFF00);
            c.setStoredLength(length);
        } else if (in.readableBytes() >= c.getStoredLength()) {
            final int length = c.getStoredLength();
            final ByteBuf buffer = in.retainedSlice(in.readerIndex(), length);
            in.readerIndex(in.readerIndex() + length);
            c.setStoredLength(-1);

            MapleCrypto.crypt(buffer, iv, length);
            ShandaCrypto.decrypt(buffer, length);
            c.setRecvIv(IGCipher.innoHash(iv));

            final InPacket inPacket = new NettyInPacket(buffer);
            out.add(inPacket);
        }
    }
}
