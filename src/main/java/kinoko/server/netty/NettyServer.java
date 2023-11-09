package kinoko.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public abstract class NettyServer {
    protected static final Logger log = LogManager.getLogger(NettyServer.class);
    private CompletableFuture<Channel> channelFuture;

    public abstract int getPort();

    public CompletableFuture<Channel> getFuture() {
        return channelFuture;
    }

    protected void setFuture(CompletableFuture<Channel> channelFuture) {
        this.channelFuture = channelFuture;
    }

    public CompletableFuture<Channel> start() {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new PacketDecoder(), new PacketHandler(), new PacketEncoder());

                final Client c = new Client(ch);
                c.setSendIv(getNewIv());
                c.setRecvIv(getNewIv());
                c.write(LoginPacket.connect(c));

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
        setFuture(startFuture);
        return startFuture;
    }

    public CompletableFuture<Void> stop() throws ExecutionException, InterruptedException {
        if (getFuture() == null) {
            throw new IllegalStateException("Tried to stop server before starting.");
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

    protected static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        ThreadLocalRandom.current().nextBytes(iv);
        return iv;
    }
}
