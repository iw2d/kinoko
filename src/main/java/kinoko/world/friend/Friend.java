package kinoko.world.friend;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class Friend implements Encodable {
    public static final int CHANNEL_OFFLINE = -2;
    public static final int CHANNEL_SHOP = -1;

    private int friendId;
    private String friendName;
    private String friendGroup;
    private FriendStatus status;
    private int channelId;

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendGroup() {
        return friendGroup;
    }

    public void setFriendGroup(String friendGroup) {
        this.friendGroup = friendGroup;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public boolean isInShop() {
        return getChannelId() == Friend.CHANNEL_SHOP;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_Friend struct (39)
        outPacket.encodeInt(friendId); // dwFriendID
        outPacket.encodeString(friendName, 13); // sFriendName
        outPacket.encodeByte(status.getValue()); // nFlag
        outPacket.encodeInt(channelId); // nChannelId
        outPacket.encodeString(friendGroup, 17); // sFriendGroup
    }
}
