package kinoko.server.node;

import kinoko.server.ServerConfig;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class ServerStorage {
    private final ConcurrentHashMap<Integer, RemoteServerNode> remoteChannelServerNodes = new ConcurrentHashMap<>(); // channel id -> remote child node
    private final ConcurrentHashMap<Integer, ChannelServerNode> channelServerNodes = new ConcurrentHashMap<>(); // channel id -> channel server node
    private final AtomicReference<RemoteServerNode> loginServerNode = new AtomicReference<>();

    public void addServerNode(RemoteServerNode serverNode) {
        final int channelId = serverNode.getChannelId();
        if (channelId == GameConstants.CHANNEL_LOGIN) {
            loginServerNode.set(serverNode);
        } else if (channelId >= 0 && channelId < ServerConfig.CHANNELS_PER_WORLD) {
            remoteChannelServerNodes.put(channelId, serverNode);
        } else {
            throw new IllegalStateException(String.format("Tried to add remote server node with channel ID : %d", channelId));
        }
    }

    /**
     * Registers a live ChannelServerNode in the server storage.
     *
     * Unlike RemoteServerNode, which only contains metadata and a Netty connection,
     * a ChannelServerNode holds all the live User objects and server state for that channel.
     *
     * Storing it here allows the central server or other components to:
     *  - Access the live users on a specific channel
     *  - Submit packets or requests directly to a ChannelServerNode
     *  - Perform operations like warping a player within the same channel
     *  - Perform cross channel operations
     *  - Get all live Users across all channels.
     *  - and much more...
     *
     * Essentially, this provides a way to map channel IDs to their full server instances,
     * enabling operations that cannot be done with just RemoteServerNode metadata.
     */
    public void addChannelServerNode(ChannelServerNode serverNode){
        channelServerNodes.put(serverNode.getChannelId(), serverNode);
    }

    public void removeServerNode(int channelId) {
        if (channelId == GameConstants.CHANNEL_LOGIN) {
            loginServerNode.set(null);
        } else {
            remoteChannelServerNodes.remove(channelId);
        }
    }

    public boolean isFull() {
        // Check if all channels are connected
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            if (!remoteChannelServerNodes.containsKey(i)) {
                return false;
            }
        }
        return loginServerNode.get() != null;
    }

    public boolean isEmpty() {
        // Check if all channels are disconnected
        return remoteChannelServerNodes.isEmpty();
    }

    public Optional<RemoteServerNode> getLoginServerNode() {
        return Optional.ofNullable(loginServerNode.get());
    }

    public Optional<RemoteServerNode> getRemoteChannelServerNodeById(int channelId) {
        return Optional.ofNullable(remoteChannelServerNodes.get(channelId));
    }

    public List<RemoteServerNode> getRemoteChannelServerNodes() {
        final List<RemoteServerNode> connectedNodes = new ArrayList<>();
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            getRemoteChannelServerNodeById(i).ifPresent(connectedNodes::add);
        }
        return connectedNodes;
    }

    public Optional<ChannelServerNode> getChannelServerNodeById(int channelId) {
        return Optional.ofNullable(channelServerNodes.get(channelId));
    }

    public List<ChannelServerNode> getChannelServerNodes() {
        final List<ChannelServerNode> connectedNodes = new ArrayList<>();
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            getChannelServerNodeById(i).ifPresent(connectedNodes::add);
        }
        return connectedNodes;
    }
}
