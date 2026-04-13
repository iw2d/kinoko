package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kinoko.server.ServerConfig;
import kinoko.util.Util;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConnectHandler extends MessageToByteEncoder<byte[]> {
    private static final Logger log = LogManager.getLogger(ConnectHandler.class);
    private final byte[] sendSeq;

    public ConnectHandler(byte[] sendSeq) {
        this.sendSeq = sendSeq;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] data, ByteBuf out) {
        log.log(ServerConfig.DEBUG_MODE ? Level.DEBUG : Level.TRACE, "[Out] | Plain sending {}", Util.readableByteArray(data));
        out.writeShortLE(data.length);
        out.writeBytes(data);
        ctx.pipeline().replace(this, "encoder", ServerConfig.PLAIN_TRAFFIC ? new PlainPacketEncoder() : new PacketEncoder(sendSeq));
    }
}
