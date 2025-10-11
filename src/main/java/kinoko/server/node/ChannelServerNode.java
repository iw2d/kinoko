package kinoko.server.node;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.packet.CentralPacket;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.event.EventManager;
import kinoko.server.event.EventState;
import kinoko.server.event.EventType;
import kinoko.server.field.ChannelFieldStorage;
import kinoko.server.field.Instance;
import kinoko.server.field.InstanceStorage;
import kinoko.server.guild.GuildBoardRequest;
import kinoko.server.guild.GuildRequest;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.netty.*;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.PartyRequest;
import kinoko.server.user.RemoteUser;
import kinoko.server.user.SpeakerManager;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class ChannelServerNode extends ServerNode {
    private static final Logger log = LogManager.getLogger(ChannelServerNode.class);
    private final ChannelFieldStorage fieldStorage = new ChannelFieldStorage();
    private final InstanceStorage instanceStorage = new InstanceStorage();
    private final SpeakerManager speakerManager = new SpeakerManager();
    private final EventManager eventManager = new EventManager();
    private final int channelId;
    private final int channelPort;
    private ChannelFuture centralClientFuture;
    private ChannelFuture channelServerFuture;

    public ChannelServerNode(int channelId, int channelPort) {
        this.channelId = channelId;
        this.channelPort = channelPort;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getChannelPort() {
        return channelPort;
    }


    // MIGRATION METHODS -----------------------------------------------------------------------------------------------

    public void submitMigrationRequest(int accountId, int characterId, byte[] machineId, byte[] clientKey, Consumer<Optional<MigrationInfo>> consumer) {
        final CompletableFuture<Optional<MigrationInfo>> migrationRequestFuture = new CompletableFuture<>();
        migrationRequestFuture.thenAccept(consumer).exceptionally(e -> {
            log.error("Exception caught while processing migration request", e);
            e.printStackTrace();
            return null;
        });
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, migrationRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.migrateRequest(requestId, accountId, characterId, machineId, clientKey));
    }

    @SuppressWarnings("unchecked")
    public void completeMigrationRequest(int requestId, MigrationInfo migrationInfo) {
        final CompletableFuture<Optional<MigrationInfo>> migrationRequestFuture = (CompletableFuture<Optional<MigrationInfo>>) requestFutures.remove(requestId);
        if (migrationRequestFuture != null) {
            migrationRequestFuture.complete(Optional.ofNullable(migrationInfo));
        }
    }

    public void submitTransferRequest(MigrationInfo migrationInfo, Consumer<Optional<TransferInfo>> consumer) {
        final CompletableFuture<Optional<TransferInfo>> transferRequestFuture = new CompletableFuture<>();
        transferRequestFuture.thenAccept(consumer).exceptionally(e -> {
            log.error("Exception caught while processing transfer request", e);
            e.printStackTrace();
            return null;
        });
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, transferRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.transferRequest(requestId, migrationInfo));
    }

    @SuppressWarnings("unchecked")
    public void completeTransferRequest(int requestId, TransferInfo transferInfo) {
        final CompletableFuture<Optional<TransferInfo>> transferRequestFuture = (CompletableFuture<Optional<TransferInfo>>) requestFutures.remove(requestId);
        if (transferRequestFuture != null) {
            transferRequestFuture.complete(Optional.ofNullable(transferInfo));
        }
    }


    // USER METHODS ----------------------------------------------------------------------------------------------------

    public boolean isConnected(User user) {
        return clientStorage.isConnected(user);
    }

    public List<User> getConnectedUsers() {
        return clientStorage.getConnectedUsers();
    }

    public Optional<User> getUserByCharacterId(int characterId) {
        return clientStorage.getUserByCharacterId(characterId);
    }

    public void notifyUserConnect(User user) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userConnect(RemoteUser.from(user)));
    }

    public void notifyUserUpdate(User user) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userUpdate(RemoteUser.from(user)));
    }

    public void notifyUserDisconnect(User user) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userDisconnect(RemoteUser.from(user)));
    }

    public void submitUserPacketRequest(String characterName, OutPacket remotePacket) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userPacketRequest(characterName, remotePacket));
    }

    public void submitUserPacketReceive(int characterId, OutPacket remotePacket) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userPacketReceive(characterId, remotePacket));
    }

    public void submitUserPacketBroadcast(List<Integer> characterIds, OutPacket remotePacket) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.userPacketBroadcast(characterIds, remotePacket));
    }

    public void submitUserQueryRequest(List<String> characterNames, Consumer<List<RemoteUser>> consumer) {
        final CompletableFuture<List<RemoteUser>> userRequestFuture = new CompletableFuture<>();
        userRequestFuture.thenAccept(consumer).exceptionally(e -> {
            log.error("Exception caught while processing user query request", e);
            e.printStackTrace();
            return null;
        });
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, userRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.userQueryRequest(requestId, characterNames));
    }

    public void submitUserQueryRequestAll(Consumer<List<RemoteUser>> consumer) {
        final CompletableFuture<List<RemoteUser>> userRequestFuture = new CompletableFuture<>();
        userRequestFuture.thenAccept(consumer).exceptionally(e -> {
            log.error("Exception caught while processing user query request", e);
            e.printStackTrace();
            return null;
        });
        final int requestId = getNewRequestId();
        requestFutures.put(requestId, userRequestFuture);
        centralClientFuture.channel().writeAndFlush(CentralPacket.userQueryRequestAll(requestId));
    }

    @SuppressWarnings("unchecked")
    public void completeUserQueryRequest(int requestId, List<RemoteUser> remoteUsers) {
        final CompletableFuture<List<RemoteUser>> userRequestFuture = (CompletableFuture<List<RemoteUser>>) requestFutures.remove(requestId);
        if (userRequestFuture != null) {
            userRequestFuture.complete(remoteUsers);
        }
    }


    // BROADCAST METHODS -----------------------------------------------------------------------------------------------

    public boolean canSubmitAvatarSpeaker() {
        return speakerManager.canSubmitAvatarSpeaker();
    }

    public boolean canSubmitWorldSpeaker(int characterId) {
        return speakerManager.canSubmitWorldSpeaker(characterId);
    }

    public void submitWorldSpeakerRequest(int characterId, boolean avatar, OutPacket outPacket) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.worldSpeakerRequest(characterId, avatar, outPacket));
    }

    public void completeWorldSpeakerRequest(int characterId, boolean avatar, OutPacket outPacket) {
        speakerManager.registerWorldSpeaker(characterId, avatar, outPacket);
    }

    public void submitChannelPacketBroadcast(OutPacket outPacket) {
        for (User user : clientStorage.getConnectedUsers()) {
            user.write(outPacket);
        }
    }

    public void submitServerPacketBroadcast(OutPacket outPacket) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.serverPacketBroadcast(outPacket));
    }


    // OTHER CENTRAL REQUESTS ------------------------------------------------------------------------------------------

    public void submitMessengerRequest(User user, MessengerRequest messengerRequest) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.messengerRequest(user.getCharacterId(), messengerRequest));
    }

    public void submitPartyRequest(User user, PartyRequest partyRequest) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.partyRequest(user.getCharacterId(), partyRequest));
    }

    public void submitGuildRequest(User user, GuildRequest guildRequest) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.guildRequest(user.getCharacterId(), guildRequest));
    }

    public void submitBoardRequest(User user, GuildBoardRequest boardRequest) {
        centralClientFuture.channel().writeAndFlush(CentralPacket.boardRequest(user.getCharacterId(), boardRequest));
    }


    // FIELD METHODS ---------------------------------------------------------------------------------------------------

    public Optional<Field> getFieldById(int mapId) {
        return fieldStorage.getFieldById(mapId);
    }


    // INSTANCE METHODS ------------------------------------------------------------------------------------------------

    public Optional<Instance> createInstance(List<Integer> mapIds, int returnMap, int timeLimit) {
        return instanceStorage.createInstance(this, mapIds, returnMap, timeLimit);
    }

    public boolean removeInstance(Instance instance) {
        return instanceStorage.removeInstance(instance);
    }


    // EVENT METHODS ---------------------------------------------------------------------------------------------------

    public Optional<EventState> getEventState(EventType eventType) {
        return eventManager.getEventState(eventType);
    }


    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws InterruptedException, UnknownHostException {
        // Initialize channel server classes
        speakerManager.initialize(clientStorage);
        eventManager.initialize(fieldStorage);

        // Start channel server
        final ChannelServerNode self = this;
        try {
            channelServerFuture = startServer(new PacketChannelInitializer(new ChannelPacketHandler(), self), channelPort);
            channelServerFuture.sync();
        }
        catch(Exception e){
            log.error("Channel {} failed to bind to port {}", channelId + 1, channelPort);
            throw e;
        }
        log.info("Channel {} listening on port {}", channelId + 1, channelPort);

        // Start central client
        centralClientFuture = startClient(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new CentralPacketDecoder(), new ChannelServerHandler(self), new CentralPacketEncoder());
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

        // Clean up
        eventManager.shutdown();
        fieldStorage.clear();
        instanceStorage.clear();

        // Close channel server
        channelServerFuture.channel().close().sync();
        getShutdownFuture().orTimeout(ServerConfig.SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        log.info("Channel {} closed", channelId + 1);

        // Close central client
        centralClientFuture.channel().writeAndFlush(CentralPacket.shutdownResult(channelId, true));
        centralClientFuture.channel().close().sync();
        log.info("Central client {} closed", channelId + 1);
    }

    @Override
    public boolean isInitialized() {
        return true;
    }
}
