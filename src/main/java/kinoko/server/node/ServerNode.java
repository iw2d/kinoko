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
import kinoko.world.user.Account;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public abstract class ServerNode {
    private static final Random random = new SecureRandom();
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    protected final ClientStorage clientStorage = new ClientStorage();

    private final CompletableFuture<Void> shutdownFuture = new CompletableFuture<>();
    private boolean isShutdown = false;

    public abstract void initialize() throws Exception;

    public abstract void shutdown() throws Exception;

    public abstract boolean isConnected(Account account);

    public final CompletableFuture<Void> getShutdownFuture() {
        return shutdownFuture;
    }

    public final boolean isShutdown() {
        return isShutdown;
    }

    public final void startShutdown() {
        this.isShutdown = true;
    }

    public final void addClient(Client client) {
        if (isShutdown) {
            throw new IllegalStateException("Tried to add client after shutdown");
        }
        clientStorage.addClient(client);
    }

    public final void removeClient(Client client) {
        clientStorage.removeClient(client);
        if (isShutdown && clientStorage.isEmpty()) {
            shutdownFuture.complete(null);
        }
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

    protected final ChannelFuture startServer(ChannelInitializer<SocketChannel> initializer, int port) {
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(initializer);
        b.childOption(ChannelOption.TCP_NODELAY, true);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        return b.bind(port);
    }

    protected static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        random.nextBytes(iv);
        return iv;
    }

    protected static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        random.nextBytes(clientKey);
        return clientKey;
    }
}
