package kinoko.database.sqlite;

import kinoko.database.FriendAccessor;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static kinoko.database.schema.FriendSchema.*;

public final class SqliteFriendAccessor extends SqliteAccessor implements FriendAccessor {
    private static final String tableName = "friend_table";

    public SqliteFriendAccessor(Connection connection) {
        super(connection);
    }

    private Friend loadFriend(ResultSet rs) throws SQLException {
        final int characterId = rs.getInt(CHARACTER_ID);
        final int friendId = rs.getInt(FRIEND_ID);
        final String friendName = rs.getString(FRIEND_NAME);
        final String friendGroup = rs.getString(FRIEND_GROUP);
        final FriendStatus status = FriendStatus.getByValue(rs.getInt(FRIEND_STATUS));
        return new Friend(characterId, friendId, friendName, friendGroup, status);
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        final List<Friend> friends = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + CHARACTER_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }


    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        final List<Friend> friends = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + FRIEND_ID + " = ?"
        )) {
            ps.setInt(1, friendId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        final String onConflict = force ? "DO UPDATE SET " +
                FRIEND_NAME + " = excluded." + FRIEND_NAME + ", " +
                FRIEND_GROUP + " = excluded." + FRIEND_GROUP + ", " +
                FRIEND_STATUS + " = excluded." + FRIEND_STATUS
                : "DO NOTHING";
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        CHARACTER_ID + ", " +
                        FRIEND_ID + ", " +
                        FRIEND_NAME + ", " +
                        FRIEND_GROUP + ", " +
                        FRIEND_STATUS + ") VALUES (?, ?, ?, ?, ?) " +
                        "ON CONFLICT(" + CHARACTER_ID + ", " + FRIEND_ID + ")" + onConflict
        )) {
            ps.setInt(1, friend.getCharacterId());
            ps.setInt(2, friend.getFriendId());
            ps.setString(3, friend.getFriendName());
            ps.setString(4, friend.getFriendGroup());
            ps.setInt(5, friend.getStatus().getValue());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM " + tableName + " WHERE " + CHARACTER_ID + " = ? AND " + FRIEND_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            ps.setInt(2, friendId);
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            CHARACTER_ID + " INTEGER NOT NULL, " +
                            FRIEND_ID + " INTEGER NOT NULL, " +
                            FRIEND_NAME + " TEXT NOT NULL, " +
                            FRIEND_GROUP + " TEXT NOT NULL, " +
                            FRIEND_STATUS + " INTEGER NOT NULL, " +
                            "PRIMARY KEY (" + CHARACTER_ID + ", " + FRIEND_ID + "))"
            );

            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_friend_character_id ON " +
                            tableName + "(" + CHARACTER_ID + ")"
            );
            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_friend_friend_id ON " +
                            tableName + "(" + FRIEND_ID + ")"
            );
        }
    }
}
