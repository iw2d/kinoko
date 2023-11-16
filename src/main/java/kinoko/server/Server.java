package kinoko.server;

import kinoko.database.DatabaseManager;
import kinoko.provider.*;
import kinoko.server.client.Client;
import kinoko.server.client.MigrationRequest;
import kinoko.server.command.CommandProcessor;
import kinoko.server.crypto.MapleCrypto;
import kinoko.world.Account;
import kinoko.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public final class Server {
    private static final Logger log = LogManager.getLogger(Server.class);
    private static LoginServer loginServer;
    private static List<World> worlds;

    public static LoginServer loginServer() {
        return loginServer;
    }

    public static List<World> getWorlds() {
        return worlds;
    }

    public static Optional<World> getWorldById(int worldId) {
        return getWorlds().stream()
                .filter(w -> w.getId() == worldId)
                .findFirst();
    }

    public static Optional<ChannelServer> getChannelServerById(int worldId, int channelId) {
        final Optional<World> worldResult = Server.getWorldById(worldId);
        if (worldResult.isEmpty()) {
            return Optional.empty();
        }
        return worldResult.get().getChannels().stream()
                .filter(ch -> ch.getWorldId() == worldId && ch.getChannelId() == channelId)
                .findFirst();
    }

    /**
     * Check whether an {@link Account} instance is associated with a client. In order to prevent multiple clients
     * logging into the same account, this should return true if:
     * <ul>
     *     <li>{@link Account} is authenticated on the {@link LoginServer}, or</li>
     *     <li>{@link MigrationRequest} exists for the account, or</li>
     *     <li>{@link Account} is connected to a {@link ChannelServer} instance.</li>
     * </ul>
     *
     * @param account {@link Account} instance to check.
     * @return true if {@link Account} is currently associated with a client.
     */
    public static boolean isConnected(Account account) {
        if (loginServer.getClientStorage().isConnected(account)) {
            return true;
        }
        if (DatabaseManager.migrationAccessor().hasMigrationRequest(account.getId())) {
            return true;
        }
        for (World world : getWorlds()) {
            for (ChannelServer channelServer : world.getChannels()) {
                if (channelServer.getClientStorage().isConnected(account)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Start migration process, an empty result is returned if migration cannot be performed due to incorrect
     * initialization or due to existing migrations.
     *
     * @param c             {@link Client} instance attempting to start migration.
     * @param channelServer The target channel to migrate to.
     * @param characterId   The target character for migration.
     * @return Empty result is returned if migration cannot be performed, result with {@link MigrationRequest} if
     * migration was successfully queued.
     */
    public static Optional<MigrationRequest> submitMigrationRequest(Client c, ChannelServer channelServer, int characterId) {
        // Account not initialized
        if (c == null || c.getAccount() == null) {
            return Optional.empty();
        }
        // Account not authenticated
        final Account account = c.getAccount();
        if (!c.getConnectedServer().getClientStorage().isConnected(account)) {
            return Optional.empty();
        }
        // Create and Submit MigrationRequest
        final MigrationRequest migrationRequest = new MigrationRequest(
                account.getId(), channelServer.getChannelId(), characterId, c.getMachineId(), c.getRemoteAddress()
        );
        if (!DatabaseManager.migrationAccessor().submitMigrationRequest(migrationRequest)) {
            return Optional.empty();
        }
        return Optional.of(migrationRequest);
    }

    /**
     * Check whether a client migration is valid. There should be a {@link MigrationRequest} that matches the requested
     * channel ID, character ID, the client's machine ID, and remote address.
     *
     * @param client      {@link Client} instance attempting to migrate to channel server.
     * @param characterId Target character ID attempting to migrate to channel server.
     * @return {@link MigrationRequest} instance that matches the request.
     */
    public static Optional<MigrationRequest> fetchMigrationRequest(Client client, int characterId) {
        final Optional<MigrationRequest> mrResult = DatabaseManager.migrationAccessor().fetchMigrationRequest(characterId);
        if (mrResult.isEmpty() || !mrResult.get().strictMatch(client, characterId)) {
            return Optional.empty();
        }
        return mrResult;
    }

    public static void main(String[] args) {
        Server.start();
    }

    private static void start() {
        // Load Providers
        Instant start = Instant.now();
        ItemProvider.initialize();
        MapProvider.initialize();
        MobProvider.initialize();
        NpcProvider.initialize();
        SkillProvider.initialize();
        QuestProvider.initialize();
        EtcProvider.initialize();
        System.gc();
        log.info("Loaded providers in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Load Database
        start = Instant.now();
        DatabaseManager.initialize();
        log.info("Loaded database connection in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        // Load Commands
        CommandProcessor.initialize();

        // Load World
        start = Instant.now();
        MapleCrypto.initialize();
        loginServer = new LoginServer();
        loginServer.start().join();
        log.info("Login server listening on port {}", loginServer.getPort());
        final List<ChannelServer> channelServers = new ArrayList<>();
        for (int channelId = 0; channelId < ServerConfig.CHANNELS_PER_WORLD; channelId++) {
            final ChannelServer channelServer = new ChannelServer(
                    ServerConfig.WORLD_ID,
                    channelId,
                    ServerConstants.CHANNEL_PORT + channelId
            );
            channelServer.start().join();
            channelServers.add(channelServer);
            log.info("Channel {} listening on port {}", channelId + 1, channelServer.getPort());
        }
        worlds = List.of(new World(ServerConfig.WORLD_ID, ServerConfig.WORLD_NAME, Collections.unmodifiableList(channelServers)));
        log.info("Loaded world in {} milliseconds", Duration.between(start, Instant.now()).toMillis());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Server.stop();
            } catch (ExecutionException | InterruptedException e) {
                log.error("Exception caught while shutting down Server", e);
                throw new RuntimeException(e);
            }
        }));
    }

    private static void stop() throws ExecutionException, InterruptedException {
        log.info("Shutting down Server");
        loginServer.stop().join();
        for (World world : getWorlds()) {
            for (ChannelServer channelServer : world.getChannels()) {
                channelServer.stop().join();
            }
        }
        DatabaseManager.shutdown();
    }
}
