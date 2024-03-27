package kinoko.world.social.friend;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;

public final class FriendResult implements Encodable {
    private final FriendResultType resultType;

    private List<Friend> friends;
    private Friend friend;
    private String string1;
    private int int1;
    private int int2;
    private int int3;

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
                outPacket.encodeInt(int1); // dwFriendID
                outPacket.encodeString(string1); // sFriend
                outPacket.encodeInt(int2); // nLevel
                outPacket.encodeInt(int3); // nJobCode
            }
            case SET_FRIEND_UNKNOWN, ACCEPT_FRIEND_UNKNOWN, DELETE_FRIEND_UNKNOWN, INC_MAX_COUNT_UNKNOWN -> {
                outPacket.encodeByte(string1 != null && !string1.isEmpty());
                outPacket.encodeString(string1);
            }
            case NOTIFY -> {
                outPacket.encodeInt(int1); // dwFriendID
                outPacket.encodeByte(int2); // aInShop
                outPacket.encodeByte(int3); // nChannelID
            }
            case INC_MAX_COUNT_DONE -> {
                outPacket.encodeByte(int1); // nFriendMax
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

    public static FriendResult notify(int characterId, int channelId) {
        final FriendResult result = new FriendResult(FriendResultType.NOTIFY);
        result.int1 = characterId;
        result.int2 = 0;
        result.int3 = channelId;
        return result;
    }
}
