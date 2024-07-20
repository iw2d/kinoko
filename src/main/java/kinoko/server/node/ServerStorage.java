package kinoko.server.node;

import kinoko.server.ServerConfig;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class ServerStorage {
    private final ConcurrentHashMap<Integer, RemoteServerNode> channelServerNodes = new ConcurrentHashMap<>(); // channel id -> remote child node
    private final AtomicReference<RemoteServerNode> loginServerNode = new AtomicReference<>();

    public void addServerNode(RemoteServerNode serverNode) {
        final int channelId = serverNode.getChannelId();
        if (channelId == GameConstants.CHANNEL_LOGIN) {
            loginServerNode.set(serverNode);
        } else if (channelId >= 0 && channelId < ServerConfig.CHANNELS_PER_WORLD) {
            channelServerNodes.put(channelId, serverNode);
        } else {
            throw new IllegalStateException(String.format("Tried to add remote server node with channel ID : %d", channelId));
        }
    }

    public void removeServerNode(int channelId) {
        if (channelId == GameConstants.CHANNEL_LOGIN) {
            loginServerNode.set(null);
        } else {
            channelServerNodes.remove(channelId);
        }
    }

    public boolean isFull() {
        // Check if all channels are connected
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            if (!channelServerNodes.containsKey(i)) {
                return false;
            }
        }
        return loginServerNode.get() != null;
    }

    public boolean isEmpty() {
        // Check if all channels are disconnected
        return channelServerNodes.isEmpty();
    }

    public Optional<RemoteServerNode> getLoginServerNode() {
        return Optional.ofNullable(loginServerNode.get());
    }

    public Optional<RemoteServerNode> getChannelServerNodeById(int channelId) {
        return Optional.ofNullable(channelServerNodes.get(channelId));
    }

    public List<RemoteServerNode> getChannelServerNodes() {
        final List<RemoteServerNode> connectedNodes = new ArrayList<>();
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            getChannelServerNodeById(i).ifPresent(connectedNodes::add);
        }
        return connectedNodes;
    }
}
