package kinoko.server.node;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

public abstract class Node {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final CompletableFuture<Void> shutdownFuture = new CompletableFuture<>();
    private boolean shutdown = false;

    public abstract void initialize() throws Exception;

    public abstract void shutdown() throws Exception;

    public final CompletableFuture<Void> getShutdownFuture() {
        return shutdownFuture;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final void startShutdown() {
        this.shutdown = true;
    }

    protected final ChannelFuture startServer(ChannelInitializer<SocketChannel> initializer, int port) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(initializer);
        b.childOption(ChannelOption.TCP_NODELAY, true);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind(port);
    }

    protected final ChannelFuture startClient(ChannelInitializer<SocketChannel> initializer, InetAddress host, int port) {
        final Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(initializer);
        b.option(ChannelOption.TCP_NODELAY, true);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        return b.connect(host, port);
    }
}
