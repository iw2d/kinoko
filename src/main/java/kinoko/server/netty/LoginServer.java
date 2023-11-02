package kinoko.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.Client;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

public final class LoginServer implements Runnable {
    private static final Logger log = LogManager.getLogger(LoginServer.class);

    private static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        ThreadLocalRandom.current().nextBytes(iv);
        return iv;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new PacketDecoder(), new PacketHandler(), new PacketEncoder());

                    Client c = new Client(ch);
                    c.setSendIv(getNewIv());
                    c.setRecvIv(getNewIv());
                    c.write(LoginPacket.connect(c));

                    ch.attr(NettyClient.CLIENT_KEY).set(c);
                }
            });
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(ServerConstants.LOGIN_PORT).sync();
            log.info("Login Server listening on port {}", ServerConstants.LOGIN_PORT);

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
