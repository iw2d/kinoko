package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.handler.Handler;
import kinoko.server.client.Client;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.user.User;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;

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
            log.log(header.isIgnoreHeader() ? Level.TRACE : Level.DEBUG, "[In]  | {}({}) {}", header, Util.opToString(op), inPacket);
            try {
                if (handler.getParameterTypes()[0] == Client.class) {
                    handler.invoke(this, client, inPacket);
                } else if (handler.getParameterTypes()[0] == User.class) {
                    handler.invoke(this, client.getUser(), inPacket);
                } else {
                    throw new IllegalStateException("Handler with incorrect parameter types.");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("[PacketHandler] Exception caught while handling packet", e);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("[PacketHandler] Channel inactive");
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (client != null) {
            client.close();
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("[PacketHandler] Exception caught", cause);
    }

    public static Map<InHeader, Method> loadHandlers(Class<?>... handlerClasses) {
        final Map<InHeader, Method> handlerMap = new EnumMap<>(InHeader.class);
        for (Class<?> clazz : handlerClasses) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Handler.class)) {
                    continue;
                }
                if (method.getParameterCount() != 2 || (method.getParameterTypes()[0] != Client.class && method.getParameterTypes()[0] != User.class) ||
                        method.getParameterTypes()[1] != InPacket.class) {
                    throw new RuntimeException(String.format("Incorrect parameters for handler method \"%s\"", method.getName()));
                }
                Handler annotation = method.getAnnotation(Handler.class);
                for (InHeader header : annotation.value()) {
                    if (handlerMap.containsKey(header)) {
                        throw new RuntimeException(String.format("Multiple handlers found for InHeader \"%s\"", header.name()));
                    }
                    handlerMap.put(header, method);
                }
            }
        }
        return handlerMap;
    }
}
