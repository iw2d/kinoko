package kinoko.world.social.friend;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;

public final class FriendResult implements Encodable {
    private final FriendResultType resultType;

    private List<Friend> friends;
    private Friend friend;
    private String reason;
    private int friendMax;

    public FriendResult(FriendResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(resultType.getValue());
        switch (resultType) {
            case LOAD_FRIEND_DONE, SET_FRIEND_DONE, DELETE_FRIEND_DONE -> {
                // CWvsContext::CFriend::Reset
                outPacket.encodeByte(friends.size());
                // aFriend
                for (Friend friend : friends) {
                    friend.encode(outPacket);
                }
                // aInShop
                for (Friend friend : friends) {
                    outPacket.encodeInt(friend.isInShop());
                }
            }
            case NOTIFY_CHANGE_FRIEND_INFO -> {
                // CWvsContext::CFriend::UpdateFriend
                outPacket.encodeInt(friend.getFriendId()); // dwFriendID
                friend.encode(outPacket);
            }
            case INVITE -> {
                outPacket.encodeInt(0); // dwFriendID
                outPacket.encodeString(""); // sFriend
                outPacket.encodeInt(0); // nLevel
                outPacket.encodeInt(0); // nJobCode
            }
            case SET_FRIEND_UNKNOWN, ACCEPT_FRIEND_UNKNOWN, DELETE_FRIEND_UNKNOWN, INC_MAX_COUNT_UNKNOWN -> {
                outPacket.encodeByte(reason != null && !reason.isEmpty());
                outPacket.encodeString(reason);
            }
            case NOTIFY -> {
                outPacket.encodeInt(friend.getFriendId()); // dwFriendID
                outPacket.encodeByte(friend.isInShop()); // aInShop
                outPacket.encodeByte(friend.getChannelId()); // nChannelID
            }
            case INC_MAX_COUNT_DONE -> {
                outPacket.encodeByte(friendMax); // nFriendMax
            }
            case SET_FRIEND_FULL_ME, SET_FRIEND_FULL_OTHER, SET_FRIEND_ALREADY_SET, SET_FRIEND_MASTER, SET_FRIEND_UNKNOWN_USER, PLEASE_WAIT -> {
                // no encodes
            }
            default -> {
                throw new IllegalArgumentException("Unsupported friend result type : " + resultType);
            }
        }
    }

    public static FriendResult loadFriendDone(List<Friend> friends) {
        final FriendResult result = new FriendResult(FriendResultType.LOAD_FRIEND_DONE);
        result.friends = friends;
        return result;
    }
}
