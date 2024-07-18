package kinoko.server.friend;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

public final class FriendRequest implements Encodable {
    private final FriendRequestType requestType;
    private String targetName;
    private String friendGroup;
    private int friendId;
    private int friendMax;

    FriendRequest(FriendRequestType requestType) {
        this.requestType = requestType;
    }

    public FriendRequestType getRequestType() {
        return requestType;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getFriendGroup() {
        return friendGroup;
    }

    public int getFriendId() {
        return friendId;
    }

    public int getFriendMax() {
        return friendMax;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadFriend -> {
                // no encodes
            }
            case SetFriend -> {
                outPacket.encodeString(targetName);
                outPacket.encodeString(friendGroup);
                outPacket.encodeInt(friendMax);
            }
            case AcceptFriend, DeleteFriend -> {
                outPacket.encodeInt(friendId);
            }
        }
    }

    public static FriendRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final FriendRequest friendRequest = new FriendRequest(FriendRequestType.getByValue(type));
        switch (friendRequest.requestType) {
            case LoadFriend -> {
                // no decodes
            }
            case SetFriend -> {
                friendRequest.targetName = inPacket.decodeString();
                friendRequest.friendGroup = inPacket.decodeString();
                friendRequest.friendMax = inPacket.decodeInt();
            }
            case AcceptFriend, DeleteFriend -> {
                friendRequest.friendId = inPacket.decodeInt();
            }
        }
        return friendRequest;
    }

    public static FriendRequest loadFriend() {
        return new FriendRequest(FriendRequestType.LoadFriend);
    }

    public static FriendRequest setFriend(String targetName, String friendGroup, int friendMax) {
        final FriendRequest request = new FriendRequest(FriendRequestType.SetFriend);
        request.targetName = targetName;
        request.friendGroup = friendGroup;
        request.friendMax = friendMax;
        return request;
    }

    public static FriendRequest acceptFriend(int friendId) {
        final FriendRequest request = new FriendRequest(FriendRequestType.AcceptFriend);
        request.friendId = friendId;
        return request;
    }

    public static FriendRequest deleteFriend(int friendId) {
        final FriendRequest request = new FriendRequest(FriendRequestType.DeleteFriend);
        request.friendId = friendId;
        return request;
    }
}
