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
            case LoadFriend_Done, SetFriend_Done, DeleteFriend_Done -> {
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
            case NotifyChange_FriendInfo -> {
                // CWvsContext::CFriend::UpdateFriend
                outPacket.encodeInt(friend.getFriendId()); // dwFriendID
                friend.encode(outPacket);
            }
            case Invite -> {
                outPacket.encodeInt(friend.getFriendId()); // dwFriendID
                outPacket.encodeString(friend.getFriendName()); // sFriend
                outPacket.encodeInt(0); // nLevel - unused
                outPacket.encodeInt(0); // nJobCode - unused
                // CWvsContext::CFriend::Insert
                friend.encode(outPacket); // GW_Friend::Decode
                outPacket.encodeByte(friend.isInShop());
            }
            case SetFriend_Unknown, AcceptFriend_Unknown, DeleteFriend_Unknown, INcMaxCount_Unknown -> {
                outPacket.encodeByte(string1 != null && !string1.isEmpty());
                outPacket.encodeString(string1);
            }
            case Notify -> {
                outPacket.encodeInt(int1); // dwFriendID
                outPacket.encodeByte(int2); // aInShop
                outPacket.encodeInt(int3); // nChannelID
            }
            case IncMaxCount_Done -> {
                outPacket.encodeByte(int1); // nFriendMax
            }
            case SetFriend_FullMe, SetFriend_FullOther, SetFriend_AlreadySet, SetFriend_Master, SetFriend_UnknownUser, PleaseWait -> {
                // no encodes
            }
            default -> {
                throw new IllegalArgumentException("Unsupported friend result type : " + resultType);
            }
        }
    }

    public static FriendResult of(FriendResultType resultType) {
        return new FriendResult(resultType);
    }

    public static FriendResult reset(FriendResultType resultType, List<Friend> friends) {
        final FriendResult result = new FriendResult(resultType);
        result.friends = friends;
        return result;
    }

    public static FriendResult invite(Friend friend) {
        final FriendResult result = new FriendResult(FriendResultType.Invite);
        result.friend = friend;
        return result;
    }

    public static FriendResult notify(int characterId, int channelId) {
        final FriendResult result = new FriendResult(FriendResultType.Notify);
        result.int1 = characterId;
        result.int2 = 0;
        result.int3 = channelId;
        return result;
    }
}
