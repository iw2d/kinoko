package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import kinoko.database.FriendAccessor;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;

import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.FriendSchema.*;

public final class CassandraFriendAccessor extends CassandraAccessor implements FriendAccessor {
    private static final String tableName = "friend_table";

    public CassandraFriendAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Friend loadFriend(Row row) {
        final int characterId = row.getInt(CHARACTER_ID);
        final int friendId = row.getInt(FRIEND_ID);
        final String friendName = row.getString(FRIEND_NAME);
        final String friendGroup = row.getString(FRIEND_GROUP);
        final FriendStatus status = FriendStatus.getByValue(row.getInt(FRIEND_STATUS));
        return new Friend(characterId, friendId, friendName, friendGroup, status);
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        final List<Friend> friends = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterId))
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
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(FRIEND_ID).isEqualTo(literal(friendId))
                        .build()
        );
        for (Row row : selectResult) {
            friends.add(loadFriend(row));
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        Insert insert = insertInto(getKeyspace(), tableName)
                .value(CHARACTER_ID, literal(friend.getCharacterId()))
                .value(FRIEND_ID, literal(friend.getFriendId()))
                .value(FRIEND_NAME, literal(friend.getFriendName()))
                .value(FRIEND_GROUP, literal(friend.getFriendGroup()))
                .value(FRIEND_STATUS, literal(friend.getStatus().getValue()));
        if (!force) {
            insert = insert.ifNotExists();
        }
        final ResultSet insertResult = getSession().execute(insert.build());
        return insertResult.wasApplied();
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        final ResultSet deleteResult = getSession().execute(
                deleteFrom(getKeyspace(), tableName)
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterId))
                        .whereColumn(FRIEND_ID).isEqualTo(literal(friendId))
                        .build()
        );
        return deleteResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(CHARACTER_ID, DataTypes.INT)
                        .withClusteringColumn(FRIEND_ID, DataTypes.INT)
                        .withColumn(FRIEND_NAME, DataTypes.TEXT)
                        .withColumn(FRIEND_GROUP, DataTypes.TEXT)
                        .withColumn(FRIEND_STATUS, DataTypes.INT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(FRIEND_ID)
                        .build()
        );
    }
}
