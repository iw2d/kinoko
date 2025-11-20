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

/**
 * Represents the central server node responsible for coordinating all channel servers,
 * user data, parties, guilds, messengers, and family information.
 *
 * This class provides **high-level, thread-safe access** to shared server data via
 * wrapper methods. All internal storage objects (FamilyStorage, UserStorage, GuildStorage,
 * MessengerStorage, PartyStorage, MigrationStorage, ServerStorage) are **private** and
 * should **not be accessed directly** by external classes.
 *
 * Instead, external code should always interact with data through the provided
 * CentralServerNode methods, which handle synchronization, validation, and safe
 * concurrent access. This design ensures thread safety and encapsulates the
 * internal implementation details, keeping the storage classes simple and fast.
 *
 * High-level operations, such as modifying families, creating parties, or
 * updating guilds, acquire the necessary locks internally and expose only safe
 * interfaces for external use.
 */
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
    // High-level family operations for CentralServerNode
    //
    // These methods provide thread-safe, high-level access to family data, such as
    // retrieving members or adding/updating family trees
    // Each method acquires the global family lock to ensure safe concurrent access.
    //
    // Note: The underlying FamilyStorage methods themselves are **not** thread-safe.
    // Only these high-level wrapper methods acquire the lock. Direct calls to
    // familyStorage should not be made without proper synchronization.
    //
    // This design ensures that all external access to family data through CentralServerNode
    // is safe, while keeping the internal FamilyStorage implementation simple and fast.

    /**
     * Returns the global lock used to synchronize access to the family storage.
     *
     * This lock protects all operations on the shared family data structures,
     * such as adding, removing, or retrieving FamilyMember instances.
     * Since the underlying familyStorage is mutable and may be accessed concurrently
     * by multiple threads, any read or write operation should acquire this lock
     * to avoid race conditions or inconsistent data.
     *
     * Important usage notes:
     * 1. **Use only for family data operations** – do not hold this lock for network
     *    writes, database I/O, or other slow tasks. Keep the critical section short.
     * 2. **Avoid combining with other locks** – acquiring this lock alongside
     *    other locks can lead to deadlocks; follow a consistent lock acquisition order.
     * 3. **External responsibility** – the underlying FamilyStorage methods do not
     *    acquire this lock themselves. Any multi-step operation that reads or
     *    modifies family data should acquire the lock externally.
     *
     * Example usage:
     * getGlobalFamilyLock().lock();
     * try {
     *     FamilyMember member = familyStorage.getFamilyMember(characterId).orElse(FamilyMember.EMPTY);
     *     FamilyTree tree = familyStorage.getTreeByMemberId(characterId).orElse(null);
     * } finally {
     *     getGlobalFamilyLock().unlock();
     * }
     *
     * Note: Even if some call sites already hold this lock before calling
     * methods like getFamilyInfo(), acquiring the lock here ensures safety and
     * prevents accidental concurrent access elsewhere in the code.
     *
     * @return the ReentrantLock protecting all family operations
     */
    public ReentrantLock getGlobalFamilyLock(){
        return familyStorage.getGlobalLock();
    }

    /**
     * Loads all family trees from the database and stores them in memory.
     *
     * This method retrieves all families via the DatabaseManager and adds each
     * FamilyTree to the FamilyStorage, keyed by the family leader's ID.
     *
     * The operation is performed under the global family lock to ensure thread-safe
     * access to the shared FamilyStorage. This prevents concurrent modifications
     * that could lead to inconsistent or corrupted family data.
     *
     * Usage of the global lock ensures that any other thread accessing or modifying
     * family data will be properly synchronized during this operation.
     */
    public void createAllFamilies() {
        getGlobalFamilyLock().lock();
        try {
            Collection<FamilyTree> families = DatabaseManager.familyAccessor().getAllFamilies();

            for (FamilyTree tree : families) {
                familyStorage.addFamily(tree);
            }
        }
        finally {
            getGlobalFamilyLock().unlock();
        }
    }

    /**
     * Retrieves the FamilyMember associated with the given character ID.
     *
     * This method returns FamilyMember.EMPTY if the character is not part of any family.
     * Access to the underlying familyStorage is synchronized using the global family lock
     * to ensure thread safety, preventing race conditions or inconsistent reads when
     * other threads may be modifying the family data concurrently.
     *
     * Even if some callers already hold the global lock, acquiring the lock here ensures
     * that this method is safe to call from anywhere without requiring external synchronization.
     *
     * @param characterId the ID of the character to look up
     * @return the FamilyMember instance corresponding to the characterId, or FamilyMember.EMPTY if not found
     */
    public FamilyMember getFamilyInfo(int characterId) {
        getGlobalFamilyLock().lock();
        try {
            return familyStorage.getFamilyMember(characterId).orElse(FamilyMember.EMPTY);
        }
        finally {
            getGlobalFamilyLock().unlock();
        }
    }

    /**
     * Retrieves the FamilyTree that contains the specified character.
     *
     * This method looks up the family tree associated with the given character ID.
     * Access to the underlying familyStorage is synchronized using the global family lock
     * to ensure thread-safe reads, preventing race conditions if other threads
     * are concurrently modifying the family data.
     *
     * @param characterId the character ID whose family tree is being requested
     * @return an Optional containing the FamilyTree if the character is part of a family,
     *         or an empty Optional if not found
     */
    public Optional<FamilyTree> getFamilyTree(int characterId) {
        getGlobalFamilyLock().lock();
        try {
            return familyStorage.getTreeByMemberId(characterId);
        }
        finally {
            getGlobalFamilyLock().unlock();
        }
    }

    /**
     * Registers a new FamilyTree in storage, making it available for lookups
     * and family relationship tracking.
     *
     * This method adds the given FamilyTree to the shared familyStorage.
     * The operation is performed under the global family lock to ensure thread-safe
     * modification, preventing concurrent access issues when other threads are
     * reading or modifying family data.
     *
     * @param tree the FamilyTree to add
     */
    public void addFamilyTree(FamilyTree tree) {
        getGlobalFamilyLock().lock();
        try {
            familyStorage.addFamily(tree);
        }
        finally {
            getGlobalFamilyLock().unlock();
        }
    }

    /**
     * Updates the shared family storage to reflect the latest state of the given FamilyTree.
     *
     * This method registers each member of the FamilyTree in the internal lookup table,
     * allowing fast retrieval of a FamilyTree by any character ID. Access is synchronized
     * with the global family lock to ensure thread-safe updates while other threads
     * may be reading or modifying family data.
     *
     * @param family the FamilyTree whose members should be registered in storage
     */
    public void updateFamilyTree(FamilyTree family) {
        getGlobalFamilyLock().lock();
        try {
            familyStorage.updateFamilyTree(family); // This is recursive and likely unintended.
        }
        finally {
            getGlobalFamilyLock().unlock();
        }
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
        logDuration("Connecting All Servers", initializeFuture::join);

        // Complete initialization for login server node
        final RemoteServerNode loginServerNode = serverStorage.getLoginServerNode().orElseThrow();
        loginServerNode.write(CentralPacket.initializeComplete(serverStorage.getRemoteChannelServerNodes()));
    }

    @Override
    public void shutdown() throws InterruptedException {
        final Instant start = Instant.now();
        logDuration("Saving all guilds", () -> {
                    DatabaseManager.guildAccessor().saveAll(guildStorage.getAllGuilds());
                }
        );

        logDuration("Saving all families", () -> {
            DatabaseManager.familyAccessor().saveAll(familyStorage.getAllFamilyTrees());
        });

        logDuration("Disconnecting All Servers", () -> {
            // Shutdown login server node
            serverStorage.getLoginServerNode().ifPresent((serverNode) -> serverNode.write(CentralPacket.shutdownRequest()));

            // Shutdown channel server nodes
            for (RemoteServerNode serverNode : serverStorage.getRemoteChannelServerNodes()) {
                serverNode.write(CentralPacket.shutdownRequest());
            }
            shutdownFuture.join();
        });

        // Close central server
        centralServerFuture.channel().close().sync();
        log.info("Central server closed");
    }

    /**
     * Executes the given action and logs the time it took to complete.
     *
     * This is a utility method to measure and report the duration of a specific task.
     * The elapsed time is calculated in milliseconds from the start to the end of the action.
     * The action itself is executed synchronously in the current thread.
     *
     * Example usage:
     *   logDuration("Saving all guilds", () -> guildAccessor.saveAll(guilds));
     *
     * @param taskName a descriptive name for the task being measured; used in the log message
     * @param action a Runnable representing the code block whose duration is to be measured
     */
    public void logDuration(String taskName, Runnable action) {
        Instant start = Instant.now();
        action.run();
        long millis = Duration.between(start, Instant.now()).toMillis();
        log.info("{} completed in {} milliseconds", taskName, millis);
    }
}
