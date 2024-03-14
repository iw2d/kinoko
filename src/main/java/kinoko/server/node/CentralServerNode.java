package kinoko.server.node;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.packet.CentralPacket;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.ServerConstants;
import kinoko.server.netty.*;
import kinoko.world.Account;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class CentralServerNode extends ServerNode {
    private static final Logger log = LogManager.getLogger(CentralServerNode.class);
    private final ChannelStorage channelStorage = new ChannelStorage();
    private final MigrationStorage migrationStorage = new MigrationStorage();
    private final UserStorage userStorage = new UserStorage();

    private final CompletableFuture<?> initializeFuture = new CompletableFuture<>();
    private final CompletableFuture<?> shutdownFuture = new CompletableFuture<>();
    private ChannelFuture centralServerFuture;
    private ChannelFuture loginServerFuture;


    // CHANNEL METHODS -------------------------------------------------------------------------------------------------

    public synchronized void addChildNode(RemoteChildNode childNode) {
        channelStorage.addChildNode(childNode);
        if (channelStorage.isFull()) {
            initializeFuture.complete(null);
        }
    }

    public synchronized void removeChildNode(int channelId) {
        channelStorage.removeChildNode(channelId);
        if (channelStorage.isEmpty()) {
            shutdownFuture.complete(null);
        }
    }

    public Optional<RemoteChildNode> getChildNodeByChannelId(int channelId) {
        return channelStorage.getChildNodeByChannelId(channelId);
    }

    public List<RemoteChildNode> getConnectedNodes() {
        return channelStorage.getConnectedNodes();
    }


    // MIGRATION METHODS -----------------------------------------------------------------------------------------------

    public synchronized boolean submitMigrationRequest(MigrationInfo migrationInfo) {
        return migrationStorage.submitMigrationRequest(migrationInfo);
    }

    public boolean completeMigrationRequest(MigrationInfo migrationInfo) {
        return migrationStorage.completeMigrationRequest(migrationInfo);
    }


    // USER METHODS ----------------------------------------------------------------------------------------------------

    public Optional<UserProxy> getUserByCharacterName(String characterName) {
        return userStorage.getByCharacterName(characterName);
    }

    public void addUser(UserProxy userProxy) {
        userStorage.putUser(userProxy);
        getChildNodeByChannelId(userProxy.getChannelId()).ifPresent(RemoteChildNode::incrementUserCount);
    }

    public void updateUser(UserProxy userProxy) {
        userStorage.putUser(userProxy);
    }

    public void removeUser(UserProxy userProxy) {
        userStorage.removeUser(userProxy);
        getChildNodeByChannelId(userProxy.getChannelId()).ifPresent(RemoteChildNode::decrementUserCount);
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public boolean isConnected(Account account) {
        return clientStorage.isConnected(account) || migrationStorage.isMigrating(account) ||
                userStorage.getByAccountId(account.getId()).isPresent();
    }

    @Override
    public void initialize() throws InterruptedException {
        // Start central server
        final CentralServerNode self = this;
        centralServerFuture = startServer(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new CentralPacketDecoder(), new CentralServerHandler(self), new CentralPacketEncoder());
                ch.attr(RemoteChildNode.NODE_KEY).set(new RemoteChildNode(ch));
                ch.writeAndFlush(CentralPacket.initializeRequest());
            }
        }, ServerConstants.CENTRAL_PORT);
        centralServerFuture.sync();
        log.info("Central server listening on port {}", ServerConstants.CENTRAL_PORT);

        // Wait for child node connections
        final Instant start = Instant.now();
        initializeFuture.join();
        log.info("All channels connected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Start login server
        loginServerFuture = startServer(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new PacketDecoder(), new LoginPacketHandler(), new PacketEncoder());
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
    }

    @Override
    public void shutdown() throws InterruptedException {
        // Close client channels
        for (Client client : clientStorage.getConnectedClients()) {
            client.close();
        }

        // Close login server
        loginServerFuture.channel().close().sync();
        log.info("Login server closed");

        // Shutdown child nodes
        final Instant start = Instant.now();
        for (RemoteChildNode childNode : channelStorage.getConnectedNodes()) {
            childNode.write(CentralPacket.shutdownRequest());
        }
        shutdownFuture.join();
        log.info("All channels disconnected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Close central server
        centralServerFuture.channel().close().sync();
        log.info("Central server closed");
    }
}
