package kinoko.world;

import kinoko.server.netty.ChannelServer;

public final class Channel {
    private final int worldId;
    private final int channelId;
    private final int channelPort;
    private final String channelName;
    private ChannelServer channelServer;

    public Channel(int worldId, int channelId, int channelPort, String channelName) {
        this.worldId = worldId;
        this.channelId = channelId;
        this.channelPort = channelPort;
        this.channelName = channelName;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getChannelPort() {
        return channelPort;
    }

    public String getChannelName() {
        return channelName;
    }

    public int getUserNo() {
        // TODO
        return 0;
    }

    public ChannelServer getChannelServer() {
        return channelServer;
    }

    public void setChannelServer(ChannelServer channelServer) {
        this.channelServer = channelServer;
    }
}
