package kinoko.server;

import kinoko.database.DatabaseManager;
import kinoko.handler.Dispatch;
import kinoko.provider.EtcProvider;
import kinoko.provider.MapProvider;
import kinoko.server.crypto.MapleCrypto;
import kinoko.server.netty.ChannelServer;
import kinoko.server.netty.LoginServer;
import kinoko.world.Channel;
import kinoko.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public enum Server {
    INSTANCE;

    private static final Logger log = LogManager.getLogger(Server.class);
    private LoginServer loginServer;
    private List<World> worlds;

    public static void main(String[] args) {
        Server.getInstance().start();
    }

    public static Server getInstance() {
        return INSTANCE;
    }

    public List<World> getWorlds() {
        return worlds;
    }

    public void start() {
        // Load Providers
        Instant start = Instant.now();
        MapProvider.initialize();
        EtcProvider.initialize();
        log.info("Loaded providers in {} milliseconds.", Duration.between(start, Instant.now()).toMillis());

        // Load Database
        start = Instant.now();
        DatabaseManager.initialize();
        log.info("Loaded database connection in {} milliseconds.", Duration.between(start, Instant.now()).toMillis());

        // Load World
        start = Instant.now();
        MapleCrypto.initialize();
        Dispatch.registerHandlers();
        loginServer = new LoginServer(ServerConstants.LOGIN_PORT);
        loginServer.start().join();
        log.info("Login server listening on port {}", loginServer.getPort());
        final List<Channel> channels = new ArrayList<>();
        for (int channelId = 0; channelId < ServerConfig.CHANNELS_PER_WORLD; channelId++) {
            final Channel channel = new Channel(
                    ServerConfig.WORLD_ID,
                    channelId,
                    ServerConstants.CHANNEL_PORT + channelId,
                    String.format("%s - %d", ServerConfig.WORLD_NAME, channelId + 1)
            );
            final ChannelServer channelServer = new ChannelServer(channel);
            channelServer.start().join();
            log.info("Channel {} listening on port {}", channelId + 1, channel.getChannelPort());
            channel.setChannelServer(channelServer);
            channels.add(channel);
        }
        worlds = List.of(new World(ServerConfig.WORLD_ID, ServerConfig.WORLD_NAME, Collections.unmodifiableList(channels)));
        log.info("Loaded world in {} milliseconds.", Duration.between(start, Instant.now()).toMillis());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                stop();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void stop() throws ExecutionException, InterruptedException {
        log.info("Shutting down Server");
        loginServer.stop().join();
        for (World world : getWorlds()) {
            for (Channel channel : world.getChannels()) {
                channel.getChannelServer().stop().join();
            }
        }
        log.info("Shutting down DatabaseManager");
        DatabaseManager.shutdown();
    }
}
