package kinoko.database.sqlite;

import kinoko.database.FriendAccessor;
import kinoko.world.user.friend.Friend;

import java.sql.Connection;
import java.util.List;

public final class SqliteFriendAccessor extends SqliteAccessor implements FriendAccessor {
    public SqliteFriendAccessor(Connection connection) {
        super(connection);
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        return List.of();
    }

    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        return List.of();
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        return false;
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        return false;
    }

    public static void createTable(Connection connection) {
        // TODO
    }
}
