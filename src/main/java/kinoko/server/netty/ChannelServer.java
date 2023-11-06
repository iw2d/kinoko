package kinoko.server.netty;

import kinoko.world.Channel;

public final class ChannelServer extends NettyServer {
    private final Channel channel;

    public ChannelServer(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public int getPort() {
        return channel.getChannelPort();
    }
}
