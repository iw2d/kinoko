package kinoko.server.node;

import kinoko.server.ServerConfig;

public final class ChannelInfo {
    private final String channelName;
    private final int channelId;
    private final int userCount;

    public ChannelInfo(String channelName, int channelId, int userCount) {
        this.channelName = channelName;
        this.channelId = channelId;
        this.userCount = userCount;
    }

    public String getName() {
        return channelName;
    }

    public int getId() {
        return channelId;
    }

    public int getUserCount() {
        return userCount;
    }

    public static ChannelInfo from(int channelId, int userCount) {
        return new ChannelInfo(
                String.format("%s - %d", ServerConfig.WORLD_NAME, channelId + 1),
                channelId,
                userCount
        );
    }
}
