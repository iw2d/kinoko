package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.handler.Dispatch;
import kinoko.server.Client;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class PacketHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(PacketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        Client c = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        short op = inPacket.decodeShort();
        InHeader header = InHeader.getByValue(op);
        Method handler = Dispatch.getHandler(header);
        if (header == null) {
            log.warn("[PacketHandler] Unknown opcode {} | {}", Util.opToString(op), inPacket);
        } else if (handler == null) {
            log.warn("[PacketHandler] Unhandled header {}({}) | {}", header, Util.opToString(op), inPacket);
        } else {
            log.debug("[In]  | {}({}) {}", header, Util.opToString(op), inPacket);
            try {
                handler.invoke(this, c, inPacket);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("[ChannelHandler] | Channel inactive.");
        NettyClient c = ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
