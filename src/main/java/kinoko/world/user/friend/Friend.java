package kinoko.world.user.friend;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

public final class Friend implements Encodable {
    private final int characterId;
    private final int friendId;
    private final String friendName;
    private String friendGroup;
    private FriendStatus status;
    private int channelId;

    public Friend(int characterId, int friendId, String friendName, String friendGroup, FriendStatus status) {
        this.characterId = characterId;
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendGroup = friendGroup;
        this.status = status;
        this.channelId = GameConstants.CHANNEL_OFFLINE;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
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

    public boolean isOnline() {
        return getChannelId() != GameConstants.CHANNEL_OFFLINE;
    }

    public boolean isInShop() {
        return getChannelId() == GameConstants.CHANNEL_LOGIN;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // GW_Friend struct (39)
        outPacket.encodeInt(friendId); // dwFriendID
        outPacket.encodeString(friendName, 13); // sFriendName
        outPacket.encodeByte(status.getValue()); // nFlag
        outPacket.encodeInt(status == FriendStatus.NORMAL ? channelId : GameConstants.CHANNEL_OFFLINE); // nChannelId
        outPacket.encodeString(friendGroup, 17); // sFriendGroup
    }
}
