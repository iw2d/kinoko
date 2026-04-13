package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class PlainPacketEncoder extends MessageToByteEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] data, ByteBuf out) {
        out.writeShortLE(0);
        out.writeShortLE(data.length);
        out.writeBytes(data);
    }
}
