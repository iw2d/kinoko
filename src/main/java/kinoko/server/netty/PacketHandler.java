package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.server.client.Client;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PacketHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(PacketHandler.class);
    private final NettyServer server;

    public PacketHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        final short op = inPacket.decodeShort();
        final InHeader header = InHeader.getByValue(op);
        final Method handler = server.getHandler(header);
        if (header == null) {
            log.warn("[PacketHandler] Unknown opcode {} | {}", Util.opToString(op), inPacket);
        } else if (handler == null) {
            if (!header.isIgnoreHeader()) {
                log.warn("[PacketHandler] Unhandled header {}({}) | {}", header, Util.opToString(op), inPacket);
            }
        } else {
            if (!header.isIgnoreHeader()) {
                log.debug("[In]  | {}({}) {}", header, Util.opToString(op), inPacket);
            }
            try {
                handler.invoke(this, client, inPacket);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Exception caught while handling packet", e);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("[PacketHandler] | Channel inactive");
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (client != null) {
            client.close();
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause);
    }
}
