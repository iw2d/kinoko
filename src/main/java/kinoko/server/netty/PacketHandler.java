package kinoko.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kinoko.handler.Handler;
import kinoko.server.header.InHeader;
import kinoko.server.node.Client;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class PacketHandler extends SimpleChannelInboundHandler<InPacket> {
    private static final Logger log = LogManager.getLogger(PacketHandler.class);
    private static final ThreadFactory executorThreadFactory = Thread.ofVirtual().factory();
    private final Map<Client, ExecutorService> executorMap = new ConcurrentHashMap<>();
    private final Map<InHeader, Method> handlerMap;

    protected PacketHandler(Map<InHeader, Method> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public final void channelRead0(ChannelHandlerContext ctx, InPacket inPacket) {
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        final ExecutorService executor = executorMap.computeIfAbsent(client, (key) -> Executors.newSingleThreadExecutor(executorThreadFactory));
        executor.submit(() -> {
            final short op = inPacket.decodeShort();
            final InHeader header = InHeader.getByValue(op);
            final Method handler = handlerMap.get(header);
            if (header == null) {
                log.debug("Unknown opcode {} | {}", Util.opToString(op), inPacket);
            } else if (handler == null) {
                if (!header.isIgnoreHeader()) {
                    log.debug("Unhandled header {}({}) | {}", header, Util.opToString(op), inPacket);
                }
            } else {
                log.log(header.isIgnoreHeader() ? Level.TRACE : Level.DEBUG, "[In]  | {}({}) {}", header, Util.opToString(op), inPacket);
                try {
                    if (handler.getParameterTypes()[0] == Client.class) {
                        handler.invoke(null, client, inPacket);
                    } else if (handler.getParameterTypes()[0] == User.class) {
                        handler.invoke(null, client.getUser(), inPacket);
                    } else {
                        throw new IllegalStateException("Handler with incorrect parameter types.");
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Exception caught while invoking packet handler", e);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public final void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Channel inactive");
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (client != null) {
            log.debug("Closing client");
            client.close();
            final ExecutorService executor = executorMap.remove(client);
            if (executor != null) {
                executor.close();
            }
        }
    }

    @Override
    public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught while handling packet", cause);
        cause.printStackTrace();
    }

    protected static Map<InHeader, Method> loadHandlers(Class<?>... handlerClasses) {
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
