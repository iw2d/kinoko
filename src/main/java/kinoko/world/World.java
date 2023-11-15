package kinoko.world;

import kinoko.server.ChannelServer;

import java.util.List;

public final class World {
    private final int id;
    private final String name;
    private final List<ChannelServer> channelServers;

    public World(int id, String name, List<ChannelServer> channelServers) {
        this.id = id;
        this.name = name;
        this.channelServers = channelServers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ChannelServer> getChannels() {
        return channelServers;
    }
}
