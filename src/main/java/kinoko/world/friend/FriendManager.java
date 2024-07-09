package kinoko.world.friend;

import kinoko.database.DatabaseManager;
import kinoko.packet.world.FriendPacket;
import kinoko.server.ServerConfig;
import kinoko.server.event.EventScheduler;
import kinoko.server.user.RemoteUser;
import kinoko.world.GameConstants;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * This class works as a cache for Friend classes and does not guarantee up-to-date information.
 */
public final class FriendManager {
    private static final Logger log = LogManager.getLogger(FriendManager.class);
    private final Map<Integer, Friend> friends = new HashMap<>(); // friend id -> friend
    private int friendMax;

    public FriendManager(int friendMax) {
        this.friendMax = friendMax;
    }

    public Optional<Friend> getFriend(int friendId) {
        return Optional.ofNullable(friends.get(friendId));
    }

    public Optional<Friend> getFriendByName(String friendName) {
        return friends.values().stream()
                .filter((friend) -> friend.getFriendName().equalsIgnoreCase(friendName))
                .findFirst();
    }

    public List<Friend> getRegisteredFriends() {
        return friends.values().stream()
                .filter((friend) -> friend.getStatus() == FriendStatus.NORMAL)
                .toList();
    }

    public List<Friend> getFriendRequests() {
        return friends.values().stream()
                .filter((friend) -> friend.getStatus() == FriendStatus.REQUEST)
                .toList();
    }

    public Set<Integer> getBroadcastTargets() {
        return getRegisteredFriends().stream()
                .filter(Friend::isOnline)
                .map(Friend::getFriendId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int getFriendMax() {
        return friendMax;
    }

    public void setFriendMax(int friendMax) {
        this.friendMax = friendMax;
    }

    public void updateFriends(List<Friend> newFriends) {
        for (Friend friend : newFriends) {
            friends.put(friend.getFriendId(), friend);
        }
    }

    public static void updateFriendsFromDatabase(User user) {
        final FriendManager fm = user.getFriendManager();
        fm.updateFriends(DatabaseManager.friendAccessor().getFriendsByCharacterId(user.getCharacterId()));
    }

    public static void updateFriendsFromCentralServer(User user, FriendResultType resultType, boolean isMigrateIn) {
        final FriendManager fm = user.getFriendManager();
        // Load mutual friends
        final Set<Friend> mutualFriends = new HashSet<>();
        for (Friend self : DatabaseManager.friendAccessor().getFriendsByFriendId(user.getCharacterId())) {
            if (self.getStatus() != FriendStatus.NORMAL) {
                continue;
            }
            final Optional<Friend> mutualFriendResult = fm.getFriend(self.getCharacterId());
            mutualFriendResult.ifPresent(mutualFriends::add);
        }
        EventScheduler.submit(() -> {
            // Query mutual friends' status
            final CompletableFuture<Set<RemoteUser>> userRequestFuture = user.getConnectedServer().submitUserQueryRequest(
                    mutualFriends.stream().map(Friend::getFriendName).collect(Collectors.toUnmodifiableSet())
            );
            try {
                final Set<RemoteUser> queryResult = userRequestFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
                // Update friend data and update client
                for (Friend friend : mutualFriends) {
                    final Optional<RemoteUser> userResult = queryResult.stream()
                            .filter((remoteUser) -> remoteUser.getCharacterId() == friend.getFriendId())
                            .findFirst();
                    if (userResult.isPresent()) {
                        friend.setChannelId(userResult.get().getChannelId());
                    } else {
                        friend.setChannelId(GameConstants.CHANNEL_OFFLINE);
                    }
                }
                user.write(FriendPacket.reset(resultType, fm.getRegisteredFriends()));
                if (isMigrateIn) {
                    // Notify friends
                    user.getConnectedServer().submitUserPacketBroadcast(
                            fm.getBroadcastTargets(),
                            FriendPacket.notify(user.getCharacterId(), user.getChannelId(), false)
                    );
                    // Process friend requests
                    for (Friend friend : user.getFriendManager().getFriendRequests()) {
                        user.write(FriendPacket.invite(friend));
                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("Exception caught while waiting for user query result", e);
                e.printStackTrace();
            }
        });
    }
}
