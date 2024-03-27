package kinoko.world.social.friend;

import kinoko.packet.world.WvsContext;
import kinoko.server.ServerConfig;
import kinoko.server.node.RemoteUser;
import kinoko.util.Locked;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public final class FriendManager {
    private static final Logger log = LogManager.getLogger(FriendManager.class);
    private final List<Friend> friends = new ArrayList<>();
    private int friendMax;

    public FriendManager(int friendMax) {
        this.friendMax = friendMax;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public boolean addFriend(Friend friend) {
        if (friends.size() >= friendMax) {
            return false;
        }
        friends.add(friend);
        return true;
    }

    public boolean removeFriend(Friend toRemove) {
        return friends.removeIf((friend) -> friend.getFriendId() == toRemove.getFriendId());
    }

    public int getFriendMax() {
        return friendMax;
    }

    public void setFriendMax(int friendMax) {
        this.friendMax = friendMax;
    }

    public void updateFriends(Set<RemoteUser> remoteUsers) {
        for (Friend friend : getFriends()) {
            final Optional<RemoteUser> userResult = remoteUsers.stream().filter((remoteUser) -> remoteUser.getCharacterId() == friend.getFriendId()).findAny();
            if (userResult.isPresent()) {
                friend.setChannelId(userResult.get().getChannelId());
            } else {
                friend.setChannelId(Friend.CHANNEL_OFFLINE);
            }
        }
    }

    public static void loadFriends(Locked<User> locked) {
        final User user = locked.get();
        final FriendManager fm = user.getFriendManager();
        final Set<String> friendNames = fm.getFriends().stream().map(Friend::getFriendName).collect(Collectors.toUnmodifiableSet());
        final CompletableFuture<Set<RemoteUser>> userRequestFuture = user.getConnectedServer().submitUserQueryRequest(friendNames);
        try {
            final Set<RemoteUser> queryResult = userRequestFuture.get(ServerConfig.CENTRAL_REQUEST_TTL, TimeUnit.SECONDS);
            // Update friend data and update client
            fm.updateFriends(queryResult);
            user.write(WvsContext.friendResult(FriendResult.loadFriendDone(fm.getFriends())));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Exception caught while waiting for user query result", e);
            e.printStackTrace();
        }
    }
}
