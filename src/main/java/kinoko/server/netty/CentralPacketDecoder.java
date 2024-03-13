package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.packet.NioBufferInPacket;

import java.util.List;

public final class CentralPacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        final byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        out.add(new NioBufferInPacket(data));
    }
}
