package kinoko.world;

import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.server.netty.NettyServer;

public final class ChannelServer extends NettyServer {
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
