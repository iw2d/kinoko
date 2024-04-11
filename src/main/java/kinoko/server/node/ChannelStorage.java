package kinoko.server.node;

import kinoko.server.ServerConfig;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ChannelStorage {
    private final ConcurrentHashMap<Integer, RemoteChildNode> remoteChildNodes = new ConcurrentHashMap<>(); // channel id -> remote child node

    public void addChildNode(RemoteChildNode childNode) {
        final int channelId = childNode.getChannelId();
        assert channelId == GameConstants.CHANNEL_SHOP || (channelId >= 0 && channelId < ServerConfig.CHANNELS_PER_WORLD);
        remoteChildNodes.put(channelId, childNode);
    }

    public void removeChildNode(int channelId) {
        remoteChildNodes.remove(channelId);
    }

    public boolean isFull() {
        // Check if all channels are connected
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            if (!remoteChildNodes.containsKey(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        // Check if all channels are disconnected
        return remoteChildNodes.isEmpty();
    }

    public Optional<RemoteChildNode> getChildNodeByChannelId(int channelId) {
        return Optional.ofNullable(remoteChildNodes.get(channelId));
    }

    public List<RemoteChildNode> getConnectedNodes() {
        final List<RemoteChildNode> connectedNodes = new ArrayList<>();
        for (int i = 0; i < ServerConfig.CHANNELS_PER_WORLD; i++) {
            getChildNodeByChannelId(i).ifPresent(connectedNodes::add);
        }
        return connectedNodes;
    }
}
