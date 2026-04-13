package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.ServerConstants;
import kinoko.util.crypto.IGCipher;

public final class PacketEncoder extends MessageToByteEncoder<byte[]> {
    public static final short SEND_VERSION = (short) (0xFFFF - ServerConstants.GAME_VERSION);
    private final byte[] sendSeq;

    public PacketEncoder(byte[] sendSeq) {
        this.sendSeq = sendSeq;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] data, ByteBuf out) {
        final int rawSeq = ((sendSeq[2] & 0xFF) | ((sendSeq[3] << 8) & 0xFF00)) ^ SEND_VERSION;
        final int dataLen = data.length ^ rawSeq;

        out.writeShortLE(rawSeq);
        out.writeShortLE(dataLen);

        out.writeBytes(data);
        IGCipher.innoHash(sendSeq);
    }
}
