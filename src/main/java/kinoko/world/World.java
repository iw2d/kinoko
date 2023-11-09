package kinoko.world;

import java.util.List;

public final class World {
    private final int id;
    private final String name;
    private final List<Channel> channels;

    public World(int id, String name, List<Channel> channels) {
        this.id = id;
        this.name = name;
        this.channels = channels;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Channel> getChannels() {
        return channels;
    }
}
