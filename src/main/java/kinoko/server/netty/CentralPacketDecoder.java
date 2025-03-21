package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.NettyInPacket;

import java.util.List;

public final class CentralPacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        final NettyContext c = ctx.channel().attr(NettyContext.CONTEXT_KEY).get();
        if (c == null) {
            throw new IllegalStateException("Context missing for CentralPacketDecoder");
        }
        if (c.getStoredLength() < 0) {
            if (in.readableBytes() < 4) {
                return;
            }
            final int length = in.readIntLE();
            c.setStoredLength(length);
        } else if (in.readableBytes() >= c.getStoredLength()) {
            final int length = c.getStoredLength();
            final ByteBuf buffer = in.retainedSlice(in.readerIndex(), length);
            in.readerIndex(in.readerIndex() + length);
            c.setStoredLength(-1);

            final InPacket inPacket = new NettyInPacket(buffer);
            out.add(inPacket);
        }
    }
}
