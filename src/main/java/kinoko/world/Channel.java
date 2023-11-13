package kinoko.world;

import kinoko.server.netty.ChannelServer;

public final class Channel {
    private final byte worldId;
    private final byte channelId;
    private final byte[] channelAddress;
    private final int channelPort;
    private final String channelName;

    private ChannelServer channelServer;

    public Channel(byte worldId, byte channelId, byte[] channelAddress, int channelPort, String channelName) {
        this.worldId = worldId;
        this.channelId = channelId;
        this.channelAddress = channelAddress;
        this.channelPort = channelPort;
        this.channelName = channelName;
    }

    public byte getWorldId() {
        return worldId;
    }

    public byte getChannelId() {
        return channelId;
    }

    public byte[] getChannelAddress() {
        return channelAddress;
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

    public boolean isConnected(Account account) {
        // TODO
        return false;
    }
}
