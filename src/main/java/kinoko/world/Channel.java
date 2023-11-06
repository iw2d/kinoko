package kinoko.world;

import kinoko.server.netty.ChannelServer;
import kinoko.world.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Channel {
    private final int worldId;
    private final int channelId;
    private final int channelPort;
    private final String channelName;

    private final Map<Integer, User> users  = new ConcurrentHashMap<>();
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

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public int getUserNo() {
        return users.size();
    }

    public ChannelServer getChannelServer() {
        return channelServer;
    }

    public void setChannelServer(ChannelServer channelServer) {
        this.channelServer = channelServer;
    }
}
