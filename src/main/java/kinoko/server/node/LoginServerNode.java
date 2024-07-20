package kinoko.server.node;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.packet.CentralPacket;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.netty.*;
import kinoko.world.user.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class LoginServerNode extends ServerNode {
    private static final Logger log = LogManager.getLogger(LoginServerNode.class);
    private final ConcurrentHashMap<Integer, ChannelInfo> channels = new ConcurrentHashMap<>();

    private ChannelFuture centralClientFuture;
    private ChannelFuture loginServerFuture;
    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public List<ChannelInfo> getChannels() {
        return channels.values().stream().sorted(Comparator.comparingInt(ChannelInfo::getId)).toList();
    }

    public Optional<ChannelInfo> getChannelById(int channelId) {
        return Optional.ofNullable(channels.get(channelId));
    }

    public void setChannel(ChannelInfo channelInfo) {
        channels.put(channelInfo.getId(), channelInfo);
    }


    // MIGRATION METHODS -----------------------------------------------------------------------------------------------

    public void submitOnlineRequest(Account account, Consumer<Boolean> consumer) {
        final CompletableFuture<Boolean> onlineRequestFuture = new CompletableFuture<>();
        onlineRequestFuture.thenAccept(consumer);
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, onlineRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.onlineRequest(requestId, account.getId()));
    }

    @SuppressWarnings("unchecked")
    public void completeOnlineRequest(int requestId, boolean online) {
        final CompletableFuture<Boolean> onlineRequestFuture = (CompletableFuture<Boolean>) requestFutures.remove(requestId);
        if (onlineRequestFuture != null) {
            onlineRequestFuture.complete(online);
        }
    }

    public void submitLoginRequest(MigrationInfo migrationInfo, Consumer<Optional<TransferInfo>> consumer) {
        final CompletableFuture<Optional<TransferInfo>> transferRequestFuture = new CompletableFuture<>();
        transferRequestFuture.thenAccept(consumer);
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, transferRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.transferRequest(requestId, migrationInfo));
    }

    @SuppressWarnings("unchecked")
    public void completeLoginRequest(int requestId, TransferInfo transferInfo) {
        final CompletableFuture<Optional<TransferInfo>> transferRequestFuture = (CompletableFuture<Optional<TransferInfo>>) requestFutures.remove(requestId);
        if (transferRequestFuture != null) {
            transferRequestFuture.complete(Optional.ofNullable(transferInfo));
        }
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws InterruptedException, UnknownHostException {
        // Start login server
        final LoginServerNode self = this;
        loginServerFuture = startServer(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new PacketDecoder(), new LoginPacketHandler(), new PacketEncoder());
                if (!self.isInitialized()) {
                    ch.close();
                    return;
                }
                final Client c = new Client(self, ch);
                c.setSendIv(getNewIv());
                c.setRecvIv(getNewIv());
                c.setClientKey(getNewClientKey());
                c.write(LoginPacket.connect(c.getRecvIv(), c.getSendIv()));
                ch.attr(NettyClient.CLIENT_KEY).set(c);
            }
        }, ServerConstants.LOGIN_PORT);
        loginServerFuture.sync();
        log.info("Login server listening on port {}", ServerConstants.LOGIN_PORT);

        // Start central client
        centralClientFuture = startClient(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new CentralPacketDecoder(), new LoginServerHandler(self), new CentralPacketEncoder());
                ch.attr(NettyContext.CONTEXT_KEY).set(new NettyContext());
            }
        }, InetAddress.getByAddress(ServerConstants.CENTRAL_HOST), ServerConstants.CENTRAL_PORT);
        centralClientFuture.sync();
    }

    @Override
    public void shutdown() throws InterruptedException {
        // Close client channels
        startShutdown();
        for (Client client : clientStorage.getConnectedClients()) {
            client.close();
        }

        // Close login server
        loginServerFuture.channel().close().sync();
        getShutdownFuture().orTimeout(ServerConfig.SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        log.info("Login server closed");

        // Close central client
        centralClientFuture.channel().writeAndFlush(CentralPacket.shutdownResult(-1, true));
        centralClientFuture.channel().close().sync();
        log.info("Central client 0 closed");
    }
}
