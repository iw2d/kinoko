package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.packet.OutPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class CentralPacketEncoder extends MessageToByteEncoder<OutPacket> {
    private static final Logger log = LogManager.getLogger(CentralPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, OutPacket outPacket, ByteBuf out) {
        final NettyContext c = ctx.channel().attr(NettyContext.CONTEXT_KEY).get();
        if (c == null) {
            throw new IllegalStateException("Context missing for CentralPacketEncoder");
        }
        c.acquireEncoderState();
        try {
            out.writeIntLE(outPacket.getSize());
            out.writeBytes(outPacket.getData());
        } finally {
            c.releaseEncoderState();
        }
    }
}
