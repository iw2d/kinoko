package kinoko.database;

import kinoko.world.friend.Friend;

import java.util.List;

public interface FriendAccessor {
    List<Friend> getFriendsByCharacterId(int characterId);

    List<Friend> getFriendsByFriendId(int friendId);

    boolean saveFriend(Friend friend, boolean force);

    boolean deleteFriend(int characterId, int friendId);
}
