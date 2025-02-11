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
            if (this.length == -1) {
                if (in.readableBytes() < 4) {
                    return;
                }

                in.readShortLE();
                this.length = in.readShortLE();
                return;
            }

            if (in.readableBytes() < this.length) {
                return;
            }

            final byte[] data = new byte[this.length];
            in.readBytes(data);
            this.length = -1;

            final InPacket inPacket = new NioBufferInPacket(data);
            out.add(inPacket);
        }
    }
}
