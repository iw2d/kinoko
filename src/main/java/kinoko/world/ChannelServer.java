package kinoko.world;

import kinoko.handler.ClientHandler;
import kinoko.handler.field.FieldHandler;
import kinoko.handler.stage.MigrationHandler;
import kinoko.handler.user.UserHandler;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.header.InHeader;
import kinoko.server.netty.NettyServer;

import java.lang.reflect.Method;
import java.util.Map;

public final class ChannelServer extends NettyServer {
    private static final Map<InHeader, Method> handlerMap = loadHandlers(
            ClientHandler.class,
            MigrationHandler.class,
            FieldHandler.class,
            UserHandler.class
    );
    private final int worldId;
    private final int channelId;
    private final int port;

    public ChannelServer(int worldId, int channelId, int port) {
        this.worldId = worldId;
        this.channelId = channelId;
        this.port = port;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getChannelId() {
        return channelId;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Method getHandler(InHeader header) {
        return handlerMap.get(header);
    }

    public byte[] getAddress() {
        return ServerConstants.SERVER_ADDRESS;
    }

    public String getName() {
        return String.format("%s - %d", ServerConfig.WORLD_NAME, channelId + 1);
    }

    public int getUserNo() {
        return 0;
    }
}
