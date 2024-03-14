package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.packet.OutPacket;

public final class CentralPacketEncoder extends MessageToByteEncoder<OutPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, OutPacket outPacket, ByteBuf out) {
        out.writeBytes(outPacket.getData());
    }
}
