package kinoko.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.client.Client;
import kinoko.server.client.ClientStorage;
import kinoko.server.header.InHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class NettyServer {
    protected static final Logger log = LogManager.getLogger(NettyServer.class);
    private static final Random random = new SecureRandom();
    private final ClientStorage clientStorage = new ClientStorage();
    private CompletableFuture<Channel> channelFuture;

    public abstract int getPort();

    public abstract Method getHandler(InHeader header);

    public final ClientStorage getClientStorage() {
        return clientStorage;
    }

    public final CompletableFuture<Channel> getFuture() {
        return channelFuture;
    }

    public final CompletableFuture<Channel> start() {
        final NettyServer server = this;
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new PacketDecoder(), new PacketHandler(server), new PacketEncoder());

                final Client c = new Client(server, ch);
                c.setSendIv(getNewIv());
                c.setRecvIv(getNewIv());
                c.setClientKey(getNewClientKey());
                c.write(LoginPacket.connect(c.getRecvIv(), c.getSendIv()));

                ch.attr(NettyClient.CLIENT_KEY).set(c);
            }
        });
        b.childOption(ChannelOption.TCP_NODELAY, true);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);

        final CompletableFuture<Channel> startFuture = new CompletableFuture<>();
        b.bind(getPort()).addListener((ChannelFutureListener) (listenerFuture) -> {
            if (listenerFuture.isSuccess()) {
                startFuture.complete(listenerFuture.channel());
            } else if (listenerFuture.isCancelled()) {
                startFuture.cancel(true);
            } else {
                startFuture.completeExceptionally(listenerFuture.cause());
            }
        });
        this.channelFuture = startFuture;
        return startFuture;
    }

    public final CompletableFuture<Void> stop() throws ExecutionException, InterruptedException {
        if (getFuture() == null || !getFuture().isDone()) {
            throw new IllegalStateException("Tried to stop server before starting");
        }
        final CompletableFuture<Void> stopFuture = new CompletableFuture<>();
        getFuture().get().close().addListener((listenerFuture) -> {
            if (listenerFuture.isSuccess()) {
                stopFuture.complete(null);
            } else if (listenerFuture.isCancelled()) {
                stopFuture.cancel(true);
            } else {
                stopFuture.completeExceptionally(listenerFuture.cause());
            }
        });
        return stopFuture;
    }

    private static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        random.nextBytes(iv);
        return iv;
    }

    private static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        random.nextBytes(clientKey);
        return clientKey;
    }
}
