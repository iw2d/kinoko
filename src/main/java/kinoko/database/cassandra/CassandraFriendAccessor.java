package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import kinoko.database.FriendAccessor;
import kinoko.database.cassandra.table.FriendTable;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;

import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraFriendAccessor extends CassandraAccessor implements FriendAccessor {
    public CassandraFriendAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Friend loadFriend(Row row) {
        final int characterId = row.getInt(FriendTable.CHARACTER_ID);
        final int friendId = row.getInt(FriendTable.FRIEND_ID);
        final String friendName = row.getString(FriendTable.FRIEND_NAME);
        final String friendGroup = row.getString(FriendTable.FRIEND_GROUP);
        final FriendStatus status = FriendStatus.getByValue(row.getInt(FriendTable.FRIEND_STATUS));
        return new Friend(characterId, friendId, friendName, friendGroup, status);
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        final List<Friend> friends = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), FriendTable.getTableName()).all()
                        .whereColumn(FriendTable.CHARACTER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            friends.add(loadFriend(row));
        }
        return friends;
    }

    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        final List<Friend> friends = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), FriendTable.getTableName()).all()
                        .whereColumn(FriendTable.FRIEND_ID).isEqualTo(literal(friendId))
                        .build()
        );
        for (Row row : selectResult) {
            friends.add(loadFriend(row));
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        Insert insert = insertInto(getKeyspace(), FriendTable.getTableName())
                .value(FriendTable.CHARACTER_ID, literal(friend.getCharacterId()))
                .value(FriendTable.FRIEND_ID, literal(friend.getFriendId()))
                .value(FriendTable.FRIEND_NAME, literal(friend.getFriendName()))
                .value(FriendTable.FRIEND_GROUP, literal(friend.getFriendGroup()))
                .value(FriendTable.FRIEND_STATUS, literal(friend.getStatus().getValue()));
        if (!force) {
            insert = insert.ifNotExists();
        }
        final ResultSet insertResult = getSession().execute(insert.build());
        return insertResult.wasApplied();
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        final ResultSet deleteResult = getSession().execute(
                deleteFrom(getKeyspace(), FriendTable.getTableName())
                        .whereColumn(FriendTable.CHARACTER_ID).isEqualTo(literal(characterId))
                        .whereColumn(FriendTable.FRIEND_ID).isEqualTo(literal(friendId))
                        .build()
        );
        return deleteResult.wasApplied();
    }
}
