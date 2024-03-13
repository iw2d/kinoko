package kinoko.server.node;

import kinoko.server.ServerConfig;

import java.util.List;
import java.util.Optional;

public final class ChannelStorage {
    private final RemoteChildNode[] nodeArray = new RemoteChildNode[ServerConfig.CHANNELS_PER_WORLD];

    public void addChildNode(RemoteChildNode childNode) {
        assert childNode.getChannelId() < ServerConfig.CHANNELS_PER_WORLD;
        nodeArray[childNode.getChannelId()] = childNode;
    }

    public void removeChildNode(int channelId) {
        nodeArray[channelId] = null;
    }

    public boolean isFull() {
        // Check if all channels are connected
        for (RemoteChildNode childNode : nodeArray) {
            if (childNode == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        // Check if all channels are disconnected
        for (RemoteChildNode childNode : nodeArray) {
            if (childNode != null) {
                return false;
            }
        }
        return true;
    }

    public Optional<RemoteChildNode> getChildNodeByChannelId(int channelId) {
        return Optional.ofNullable(nodeArray[channelId]);
    }

    public List<RemoteChildNode> getConnectedNodes() {
        return List.of(nodeArray);
    }
}
