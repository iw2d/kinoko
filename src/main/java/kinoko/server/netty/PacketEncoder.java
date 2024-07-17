package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.util.crypto.IGCipher;
import kinoko.util.crypto.MapleCrypto;
import kinoko.util.crypto.ShandaCrypto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PacketEncoder extends MessageToByteEncoder<OutPacket> {
    public static final short SEND_VERSION = (short) (0xFFFF - ServerConstants.GAME_VERSION);
    private static final Logger log = LogManager.getLogger(PacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, OutPacket outPacket, ByteBuf out) {
        final NettyClient c = ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        final OutHeader header = outPacket.getHeader();
        final byte[] data = outPacket.getData();
        if (c == null) {
            log.log(ServerConfig.DEBUG_MODE && !header.isIgnoreHeader() ? Level.DEBUG : Level.TRACE, "[Out] | Plain sending " + Util.readableByteArray(data));
            out.writeBytes(data);
            return;
        }
        c.acquireEncoderState();
        try {
            log.log(ServerConfig.DEBUG_MODE && !header.isIgnoreHeader() ? Level.DEBUG : Level.TRACE, "[Out] | {}", outPacket);
            final byte[] iv = c.getSendIv();
            final int rawSeq = ((iv[2] & 0xFF) | ((iv[3] << 8) & 0xFF00)) ^ SEND_VERSION;
            final int dataLen = data.length ^ rawSeq;

            out.writeShortLE(rawSeq);
            out.writeShortLE(dataLen);

            ShandaCrypto.encrypt(data);
            MapleCrypto.crypt(data, iv);
            c.setSendIv(IGCipher.innoHash(iv));

            out.writeBytes(data);
        } finally {
            c.releaseEncoderState();
        }
    }
}
