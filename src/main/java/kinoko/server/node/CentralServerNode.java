package kinoko.server.node;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import kinoko.database.DatabaseManager;
import kinoko.packet.CentralPacket;
import kinoko.server.family.FamilyStorage;
import kinoko.server.family.FamilyTree;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRank;
import kinoko.server.guild.GuildStorage;
import kinoko.server.messenger.Messenger;
import kinoko.server.messenger.MessengerStorage;
import kinoko.server.messenger.MessengerUser;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.MigrationStorage;
import kinoko.server.netty.CentralPacketDecoder;
import kinoko.server.netty.CentralPacketEncoder;
import kinoko.server.netty.CentralServerHandler;
import kinoko.server.netty.NettyContext;
import kinoko.server.party.Party;
import kinoko.server.party.PartyStorage;
import kinoko.server.user.RemoteUser;
import kinoko.server.user.UserStorage;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public final class CentralServerNode extends Node {
    private static final Logger log = LogManager.getLogger(CentralServerNode.class);
    private final ServerStorage serverStorage = new ServerStorage();
    private final MigrationStorage migrationStorage = new MigrationStorage();
    private final FamilyStorage familyStorage = new FamilyStorage();
    private final UserStorage userStorage = new UserStorage();
    private final MessengerStorage messengerStorage = new MessengerStorage();
    private final PartyStorage partyStorage = new PartyStorage();
    private final GuildStorage guildStorage = new GuildStorage();
    private final CompletableFuture<?> initializeFuture = new CompletableFuture<>();
    private final CompletableFuture<?> shutdownFuture = new CompletableFuture<>();
    private final int port;

    private ChannelFuture centralServerFuture;


    public CentralServerNode(int port) {
        this.port = port;
    }


    // CHANNEL METHODS -------------------------------------------------------------------------------------------------

    public synchronized void addServerNode(RemoteServerNode serverNode) {
        serverStorage.addServerNode(serverNode);
        if (serverStorage.isFull()) {
            initializeFuture.complete(null);
        }
    }

    public synchronized void addChannelServerNode(ChannelServerNode serverNode){
        serverStorage.addChannelServerNode(serverNode);
    }

    public synchronized void removeServerNode(int channelId) {
        serverStorage.removeServerNode(channelId);
        if (serverStorage.isEmpty()) {
            shutdownFuture.complete(null);
        }
    }

    public Optional<RemoteServerNode> getChannelServerNodeById(int channelId) {
        return serverStorage.getRemoteChannelServerNodeById(channelId);
    }

    public List<RemoteServerNode> getRemoteChannelServerNodes() {
        return serverStorage.getRemoteChannelServerNodes();
    }

    public List<ChannelServerNode> getChannelServerNodes() {
        return serverStorage.getChannelServerNodes();
    }


    // MIGRATION METHODS -----------------------------------------------------------------------------------------------


    public boolean isOnline(int accountId) {
        return migrationStorage.isMigrating(accountId) || userStorage.getByAccountId(accountId).isPresent();
    }

    public boolean isMigrating(int accountId) {
        return migrationStorage.isMigrating(accountId);
    }

    public boolean submitMigrationRequest(MigrationInfo migrationInfo) {
        return migrationStorage.submitMigrationRequest(migrationInfo);
    }

    public Optional<MigrationInfo> completeMigrationRequest(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        return migrationStorage.completeMigrationRequest(channelId, accountId, characterId, machineId, clientKey);
    }

    // USER METHODS ----------------------------------------------------------------------------------------------------

    /**
     * Returns a list of all users currently connected across all channel servers.
     */
    public List<User> getUsers() {
        List<User> allUsers = new ArrayList<>();
        for (ChannelServerNode channelNode : getChannelServerNodes()) {
            allUsers.addAll(channelNode.getConnectedUsers());
        }
        return allUsers;
    }

    /**
     * Returns the actual User object for a character ID, if connected.
     * Looks up the RemoteUser by ID, finds their channel server, then fetches the User from that channel.
     */
    public Optional<User> getUserByCharacterId(int characterId) {
        return getRemoteUserByCharacterId(characterId)
                .flatMap(remoteUser -> serverStorage
                        .getChannelServerNodeById(remoteUser.getChannelId())
                        .flatMap(channelNode -> channelNode.getUserByCharacterId(characterId))
                );
    }

    /**
     * Returns the actual User object for a character name, if connected.
     * Looks up the RemoteUser by name, finds their channel server, then fetches the User from that channel.
     */
    public Optional<User> getUserByCharacterName(String characterName) {
        return getRemoteUserByCharacterName(characterName)
                .flatMap(remoteUser -> serverStorage
                        .getChannelServerNodeById(remoteUser.getChannelId())
                        .flatMap(channelNode -> channelNode.getUserByCharacterId(remoteUser.getCharacterId()))
                );
    }


    // REMOTE USER METHODS ----------------------------------------------------------------------------------------------------

    public List<RemoteUser> getRemoteUsers() {
        return userStorage.getUsers();
    }

    public Optional<RemoteUser> getRemoteUserByCharacterId(int characterId) {
        return userStorage.getByCharacterId(characterId);
    }

    public Optional<RemoteUser> getRemoteUserByCharacterName(String characterName) {
        return userStorage.getByCharacterName(characterName);
    }

    public void addRemoteUser(RemoteUser remoteUser) {
        userStorage.putUser(remoteUser);
        getChannelServerNodeById(remoteUser.getChannelId()).ifPresent(RemoteServerNode::incrementUserCount);
    }

    public void updateRemoteUser(RemoteUser remoteUser) {
        userStorage.putUser(remoteUser);
    }

    public void removeRemoteUser(RemoteUser remoteUser) {
        userStorage.removeUser(remoteUser);
        getChannelServerNodeById(remoteUser.getChannelId()).ifPresent(RemoteServerNode::decrementUserCount);
    }

    public int getRemoteUserCount() {
        return userStorage.getUserCount();
    }

    // MESSENGER METHODS -----------------------------------------------------------------------------------------------

    public Messenger createNewMessenger(RemoteUser remoteUser, MessengerUser messengerUser) {
        final Messenger messenger = new Messenger(messengerStorage.getNewMessengerId());
        messenger.addUser(remoteUser, messengerUser);
        messengerStorage.addMessenger(messenger);
        return messenger;
    }

    public boolean removeMessenger(Messenger messenger) {
        return messengerStorage.removeMessenger(messenger);
    }

    public Optional<Messenger> getMessengerById(int messengerId) {
        if (messengerId == 0) {
            return Optional.empty();
        }
        return messengerStorage.getMessengerById(messengerId);
    }


    // PARTY METHODS ---------------------------------------------------------------------------------------------------

    public Party createNewParty(int partyId, RemoteUser remoteUser) {
        final Party party = new Party(partyId, remoteUser);
        partyStorage.addParty(party);
        return party;
    }

    public boolean removeParty(Party party) {
        return partyStorage.removeParty(party);
    }

    public Optional<Party> getPartyById(int partyId) {
        if (partyId == 0) {
            return Optional.empty();
        }
        return partyStorage.getPartyById(partyId);
    }


    // GUILD METHODS ---------------------------------------------------------------------------------------------------

    public Optional<Guild> createNewGuild(int guildId, String guildName, RemoteUser remoteUser) {
        final Guild guild = new Guild(guildId, guildName);
        final GuildMember member = GuildMember.from(remoteUser);
        member.setGuildRank(GuildRank.MASTER);
        if (!guild.addMember(member)) {
            throw new IllegalStateException("Could not add master to guild");
        }
        if (!guildStorage.addGuild(guild)) {
            return Optional.empty();
        }
        return Optional.of(guild);
    }

    public boolean removeGuild(Guild guild) {
        return guildStorage.removeGuild(guild);
    }

    public Optional<Guild> getGuildById(int guildId) {
        if (guildId == 0) {
            return Optional.empty();
        }
        return guildStorage.getGuildById(guildId);
    }


    // FAMILY METHODS --------------------------------------------------------------------------------------------------

    public ReentrantLock getGlobalFamilyLock(){
        return familyStorage.getGlobalLock();
    }

    /**
     * Loads all family trees from the database and adds them to the FamilyStorage.
     * Each family tree is stored in memory and keyed by its leader ID.
     */
    public void createAllFamilies() {
        Collection<FamilyTree> families = DatabaseManager.familyAccessor().getAllFamilies();

        for (FamilyTree tree : families) {
            familyStorage.addFamily(tree);
        }
    }

    /**
     * Retrieves a FamilyMember for the given character ID.
     * Returns FamilyMember.EMPTY if the character is not part of any family.
     *
     * @param characterId the ID of the character to look up
     * @return the FamilyMember instance or FamilyMember.EMPTY if not found
     */
    public FamilyMember getFamilyInfo(int characterId) {
        return familyStorage.getFamilyMember(characterId).orElse(FamilyMember.EMPTY);
    }

    /**
     * Retrieves the FamilyTree that contains the specified character.
     *
     * @param characterId the character ID whose family tree is being requested
     * @return an Optional containing the FamilyTree if found, otherwise empty
     */
    public Optional<FamilyTree> getFamilyTree(int characterId) {
        return familyStorage.getTreeByMemberId(characterId);
    }

    /**
     * Registers a new FamilyTree in storage, making it available for
     * lookups and relationship tracking.
     *
     * @param tree the FamilyTree to add
     */
    public void addFamilyTree(FamilyTree tree) {
        familyStorage.addFamily(tree);
    }

    /**
     * Updates the internal lookup mappings for all members in the given family tree.
     * Note: this method currently calls itself recursively and should be replaced
     * with the correct implementation (e.g., updating member lookup entries).
     *
     * @param family the FamilyTree whose member mappings should be refreshed
     */
    public void updateFamilyTree(FamilyTree family) {
        familyStorage.updateFamilyTree(family); // This is recursive and likely unintended.
    }

    /**
     * Removes a member from their family by delegating to the underlying FamilyStorage.
     * This will remove the member from their FamilyTree and clean up the lookup mapping.
     * Perfect for separation when the user has no juniors and is separating from their senior.
     *
     * @param characterId the ID of the member to remove
     */
    public void removeMemberFromFamily(int characterId) {
        familyStorage.removeMemberFromFamily(characterId);
    }

    // OVERRIDES -------------------------------------------------------------------------------------------------------

    @Override
    public void initialize() throws InterruptedException {
        // Start central server
        final CentralServerNode self = this;
        centralServerFuture = startServer(new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new CentralPacketDecoder(), new CentralServerHandler(self), new CentralPacketEncoder());
                ch.attr(NettyContext.CONTEXT_KEY).set(new NettyContext());
                ch.attr(RemoteServerNode.NODE_KEY).set(new RemoteServerNode(ch));
                ch.writeAndFlush(CentralPacket.initializeRequest());
            }
        }, port);
        centralServerFuture.sync();
        log.info("Central server listening on port {}", port);

        // Wait for child node connections
        final Instant start = Instant.now();
        initializeFuture.join();
        log.info("All servers connected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Complete initialization for login server node
        final RemoteServerNode loginServerNode = serverStorage.getLoginServerNode().orElseThrow();
        loginServerNode.write(CentralPacket.initializeComplete(serverStorage.getRemoteChannelServerNodes()));
    }

    @Override
    public void shutdown() throws InterruptedException {
        // Save All Guilds
        DatabaseManager.guildAccessor().saveAll(guildStorage.getAllGuilds());

        // Shutdown login server node
        final Instant start = Instant.now();
        serverStorage.getLoginServerNode().ifPresent((serverNode) -> serverNode.write(CentralPacket.shutdownRequest()));

        // Shutdown channel server nodes
        for (RemoteServerNode serverNode : serverStorage.getRemoteChannelServerNodes()) {
            serverNode.write(CentralPacket.shutdownRequest());
        }
        shutdownFuture.join();
        log.info("All servers disconnected in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Close central server
        centralServerFuture.channel().close().sync();
        log.info("Central server closed");
    }
}
