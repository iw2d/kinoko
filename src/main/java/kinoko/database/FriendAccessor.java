package kinoko.database;

import kinoko.world.social.friend.Friend;

import java.util.List;

public interface FriendAccessor {
    List<Friend> getFriendsByCharacterId(int characterId);

    boolean newFriend(int characterId, Friend friend);
}
