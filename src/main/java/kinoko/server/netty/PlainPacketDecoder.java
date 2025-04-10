package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.NioBufferInPacket;

import java.util.List;

public final class PlainPacketDecoder extends ByteToMessageDecoder {
    private int length = -1;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            if (length == -1) {
                if (in.readableBytes() < 4) {
                    return;
                }

                in.readShortLE();
                length = in.readShortLE();
                return;
            }

            if (in.readableBytes() < length) {
                return;
            }

            final byte[] data = new byte[length];
            in.readBytes(data);
            length = -1;

            final InPacket inPacket = new NioBufferInPacket(data);
            out.add(inPacket);
        }
    }
}
