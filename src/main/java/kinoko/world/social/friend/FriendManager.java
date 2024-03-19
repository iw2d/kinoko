package kinoko.world.social.friend;

import java.util.ArrayList;
import java.util.List;

public final class FriendManager {
    private final List<Friend> friends = new ArrayList<>();
    private int friendMax;

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
}
