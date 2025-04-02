package kinoko.handler.user;

import kinoko.database.CharacterInfo;
import kinoko.database.DatabaseManager;
import kinoko.handler.Handler;
import kinoko.packet.world.FriendPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.user.RemoteUser;
import kinoko.world.GameConstants;
import kinoko.world.user.User;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendRequestType;
import kinoko.world.user.friend.FriendStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public final class FriendHandler {
    private static final Logger log = LogManager.getLogger(FriendHandler.class);


    @Handler(InHeader.FriendRequest)
    public static void handleFriendRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final FriendRequestType requestType = FriendRequestType.getByValue(type);
        switch (requestType) {
            case LoadFriend -> {
                loadFriends(user, (friendMap) -> {
                    user.write(FriendPacket.loadFriendDone(friendMap.values()));
                });
            }
            case SetFriend -> {
                final String targetName = inPacket.decodeString(); // sTarget
                final String friendGroup = inPacket.decodeString(); // sFriendGroup
                loadFriends(user, (friendMap) -> {
                    final Optional<Friend> friendResult = friendMap.values().stream().filter((f) -> f.getFriendName().equals(targetName)).findFirst();
                    if (friendResult.isPresent() && friendResult.get().getStatus() == FriendStatus.NORMAL) {
                        // Update friend group
                        final Friend friend = friendResult.get();
                        friend.setFriendGroup(friendGroup);
                        if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                            user.write(FriendPacket.setFriendUnknown()); // The request was denied due to an unknown error.
                            return;
                        }
                        // Update client
                        user.write(FriendPacket.setFriendDone(friendMap.values()));
                    } else {
                        // Create new friend, resolve target info
                        final Optional<CharacterInfo> characterInfoResult = DatabaseManager.characterAccessor().getCharacterInfoByName(targetName);
                        if (characterInfoResult.isEmpty()) {
                            user.write(FriendPacket.setFriendUnknownUser()); // That character is not registered.
                            return;
                        }
                        final int targetCharacterId = characterInfoResult.get().getCharacterId();
                        final String targetCharacterName = characterInfoResult.get().getCharacterName();
                        // Check if target can be added as a friend
                        if (friendMap.size() >= user.getCharacterData().getFriendMax()) {
                            user.write(FriendPacket.setFriendFullMe()); // Your buddy list is full.
                            return;
                        }
                        if (friendMap.containsKey(targetCharacterId)) {
                            user.write(FriendPacket.setFriendAlreadySet()); // That character is already registered as your buddy.
                            return;
                        }
                        // Add target as friend, force creation
                        final Friend friendForUser = new Friend(user.getCharacterId(), targetCharacterId, targetCharacterName, friendGroup, FriendStatus.NORMAL);
                        if (!DatabaseManager.friendAccessor().saveFriend(friendForUser, true)) {
                            user.write(FriendPacket.setFriendUnknown()); // The request was denied due to an unknown error.
                            return;
                        }
                        // Update client
                        friendMap.put(targetCharacterId, friendForUser);
                        user.write(FriendPacket.setFriendDone(friendMap.values()));
                        // Add user as a friend for target, not forced - existing friends, requests, and refused records
                        final Friend friendForTarget = new Friend(targetCharacterId, user.getCharacterId(), user.getCharacterName(), GameConstants.DEFAULT_FRIEND_GROUP, FriendStatus.REQUEST);
                        if (DatabaseManager.friendAccessor().saveFriend(friendForTarget, false)) {
                            // Send invite to target if request was created
                            user.getConnectedServer().submitUserPacketReceive(targetCharacterId, FriendPacket.invite(friendForTarget));
                        }
                    }
                });
            }
            case AcceptFriend -> {
                final int friendId = inPacket.decodeInt();
                loadFriends(user, (friendMap) -> {
                    final Friend friend = friendMap.get(friendId);
                    if (friend == null) {
                        user.write(FriendPacket.acceptFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    friend.setStatus(FriendStatus.NORMAL);
                    if (!DatabaseManager.friendAccessor().saveFriend(friend, true)) {
                        user.write(FriendPacket.acceptFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    // Notify target if online
                    user.getConnectedServer().submitUserPacketReceive(friendId, FriendPacket.notify(user.getCharacterId(), user.getChannelId(), false));
                    // Update client
                    user.write(FriendPacket.setFriendDone(friendMap.values()));
                });
            }
            case DeleteFriend -> {
                final int friendId = inPacket.decodeInt();
                loadFriends(user, (friendMap) -> {
                    final Friend friend = friendMap.get(friendId);
                    if (friend == null) {
                        user.write(FriendPacket.deleteFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    // Delete friend
                    if (!DatabaseManager.friendAccessor().deleteFriend(user.getCharacterId(), friend.getFriendId())) {
                        user.write(FriendPacket.deleteFriendUnknown()); // The request was denied due to an unknown error.
                        return;
                    }
                    // Notify deleted friend if online
                    user.getConnectedServer().submitUserPacketReceive(friendId, FriendPacket.notify(user.getCharacterId(), GameConstants.CHANNEL_OFFLINE, false));
                    // Update client
                    friendMap.remove(friend.getFriendId());
                    user.write(FriendPacket.deleteFriendDone(friendMap.values()));
                });
            }
            case null -> {
                log.error("Unknown friend request type : {}", type);
            }
            default -> {
                log.error("Unhandled friend request type : {}", requestType);
            }
        }
    }

    public static void loadFriends(User user, Consumer<Map<Integer, Friend>> consumer) {
        final Map<Integer, Friend> friendMap = new HashMap<>();
        for (Friend friend : DatabaseManager.friendAccessor().getFriendsByCharacterId(user.getCharacterId())) {
            friendMap.put(friend.getFriendId(), friend);
        }
        final List<String> mutualFriends = new ArrayList<>();
        for (Friend mutualFriend : DatabaseManager.friendAccessor().getFriendsByFriendId(user.getCharacterId())) {
            if (mutualFriend.getStatus() != FriendStatus.NORMAL) {
                continue;
            }
            final Friend friend = friendMap.get(mutualFriend.getCharacterId());
            if (friend != null) {
                mutualFriends.add(friend.getFriendName());
            }
        }
        user.getConnectedServer().submitUserQueryRequest(mutualFriends, (queryResult) -> {
            for (RemoteUser remoteUser : queryResult) {
                final Friend friend = friendMap.get(remoteUser.getCharacterId());
                if (friend != null) {
                    friend.setChannelId(remoteUser.getChannelId());
                }
            }
            consumer.accept(friendMap);
        });
    }
}
