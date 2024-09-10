package kinoko.packet.world;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendResultType;

import java.util.Collection;

public final class FriendPacket {
    // CWvsContext::OnFriendResult -------------------------------------------------------------------------------------

    public static OutPacket loadFriendDone(Collection<Friend> friends) {
        return FriendPacket.reset(FriendResultType.LoadFriend_Done, friends);
    }

    public static OutPacket notifyChangeFriendInfo(Friend friend) {
        final OutPacket outPacket = FriendPacket.of(FriendResultType.NotifyChange_FriendInfo);
        // CWvsContext::CFriend::UpdateFriend
        outPacket.encodeInt(friend.getFriendId()); // dwFriendID
        friend.encode(outPacket);
        return outPacket;
    }

    public static OutPacket invite(Friend friend) {
        final OutPacket outPacket = FriendPacket.of(FriendResultType.Invite);
        outPacket.encodeInt(friend.getFriendId()); // dwFriendID
        outPacket.encodeString(friend.getFriendName()); // sFriend
        outPacket.encodeInt(0); // nLevel - unused
        outPacket.encodeInt(0); // nJobCode - unused
        // CWvsContext::CFriend::Insert
        friend.encode(outPacket); // GW_Friend::Decode
        outPacket.encodeByte(friend.isInShop());
        return outPacket;
    }

    public static OutPacket setFriendDone(Collection<Friend> friends) {
        return FriendPacket.reset(FriendResultType.SetFriend_Done, friends);
    }

    public static OutPacket setFriendFullMe() {
        return FriendPacket.of(FriendResultType.SetFriend_FullMe);
    }

    public static OutPacket setFriendFullOther() {
        return FriendPacket.of(FriendResultType.SetFriend_FullOther);
    }

    public static OutPacket setFriendAlreadySet() {
        return FriendPacket.of(FriendResultType.SetFriend_AlreadySet);
    }

    public static OutPacket setFriendUnknownUser() {
        return FriendPacket.of(FriendResultType.SetFriend_UnknownUser);
    }

    public static OutPacket setFriendUnknown() {
        return FriendPacket.unknown(FriendResultType.SetFriend_Unknown, null);
    }

    public static OutPacket acceptFriendUnknown() {
        return FriendPacket.unknown(FriendResultType.AcceptFriend_Unknown, null);
    }

    public static OutPacket deleteFriendDone(Collection<Friend> friends) {
        return FriendPacket.reset(FriendResultType.DeleteFriend_Done, friends);
    }

    public static OutPacket deleteFriendUnknown() {
        return FriendPacket.unknown(FriendResultType.DeleteFriend_Unknown, null);
    }

    public static OutPacket notify(int friendId, int channelId, boolean inShop) {
        final OutPacket outPacket = FriendPacket.of(FriendResultType.Notify);
        outPacket.encodeInt(friendId); // dwFriendID
        outPacket.encodeByte(inShop); // aInShop
        outPacket.encodeInt(channelId); // nChannelID
        return outPacket;
    }

    public static OutPacket incMaxCountDone(int friendMax) {
        final OutPacket outPacket = FriendPacket.of(FriendResultType.IncMaxCount_Done);
        outPacket.encodeInt(friendMax); // nFriendMax
        return outPacket;
    }

    public static OutPacket incMaxCountUnknown() {
        return FriendPacket.unknown(FriendResultType.IncMaxCount_Unknown, null);
    }

    public static OutPacket reset(FriendResultType resultType, Collection<Friend> friends) {
        // CWvsContext::CFriend::Reset
        final OutPacket outPacket = FriendPacket.of(resultType);
        outPacket.encodeByte(friends.size());
        for (Friend friend : friends) {
            friend.encode(outPacket); // aFriend
        }
        for (Friend friend : friends) {
            outPacket.encodeInt(friend.isInShop()); // aInShop
        }
        return outPacket;
    }

    private static OutPacket unknown(FriendResultType resultType, String message) {
        final OutPacket outPacket = FriendPacket.of(resultType);
        outPacket.encodeByte(message != null && !message.isEmpty());
        outPacket.encodeString(message);
        return outPacket;
    }

    private static OutPacket of(FriendResultType resultType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FriendResult);
        outPacket.encodeByte(resultType.getValue());
        return outPacket;
    }
}
