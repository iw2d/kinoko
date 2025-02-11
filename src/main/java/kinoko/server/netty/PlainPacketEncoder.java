package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.ServerConfig;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PlainPacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger log = LogManager.getLogger(PlainPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, OutPacket outPacket, ByteBuf out) {
        final NettyClient c = ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        final OutHeader header = outPacket.getHeader();
        final byte[] data = outPacket.getData();
        if (c == null) {
            log.log(ServerConfig.DEBUG_MODE && !header.isIgnoreHeader() ? Level.DEBUG : Level.TRACE, "[Out] | Plain sending " + Util.readableByteArray(data));
            out.writeShortLE(data.length);
            out.writeBytes(data);
            return;
        }
        log.log(ServerConfig.DEBUG_MODE && !header.isIgnoreHeader() ? Level.DEBUG : Level.TRACE, "[Out] | {}", outPacket);
        out.writeShortLE(0);
        out.writeShortLE(data.length);
        out.writeBytes(data);
    }
}
