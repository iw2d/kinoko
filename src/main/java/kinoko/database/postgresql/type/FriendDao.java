package kinoko.database.postgresql.type;

import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class FriendDao {
    /**
     * Creates a Friend object from the current row of a ResultSet.
     *
     * Extracts the character ID, friend ID, friend name, friend group,
     * and friend status from the ResultSet and constructs a corresponding
     * Friend instance.
     *
     * @param rs the ResultSet positioned at the row to load
     * @return a Friend object representing the data in the current row
     * @throws SQLException if a database access error occurs
     */
    private static Friend loadFriend(ResultSet rs) throws SQLException {
        int characterId = rs.getInt("character_id");
        int friendId = rs.getInt("friend_id");
        String friendName = rs.getString("friend_name");
        String friendGroup = rs.getString("friend_group");
        FriendStatus status = FriendStatus.getByValue(rs.getInt("friend_status"));
        return new Friend(characterId, friendId, friendName, friendGroup, status);
    }

    /**
     * Retrieves all friends for a specific character ID.
     *
     * @param conn the active SQL connection
     * @param characterId the character's ID
     * @return a list of Friend objects
     * @throws SQLException if a database error occurs
     */
    public static List<Friend> getFriendsByCharacterId(Connection conn, int characterId) throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT * FROM friend.friends WHERE character_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        }
        return friends;
    }

    /**
     * Retrieves all friends where the given friend ID appears.
     *
     * @param conn the active SQL connection
     * @param friendId the friend's ID
     * @return a list of Friend objects
     * @throws SQLException if a database error occurs
     */
    public static List<Friend> getFriendsByFriendId(Connection conn, int friendId) throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT * FROM friend.friends WHERE friend_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, friendId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(loadFriend(rs));
                }
            }
        }
        return friends;
    }

    /**
     * Inserts or updates a friend record in the database.
     *
     * @param conn the active SQL connection
     * @param friend the Friend object to save
     * @param force if true, update existing record; if false, do nothing on conflict
     * @return true if the record was inserted or updated, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean saveFriend(Connection conn, Friend friend, boolean force) throws SQLException {
        String sql;
        if (force) {
            sql = "INSERT INTO friend.friends (character_id, friend_id, friend_name, friend_group, friend_status) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (character_id, friend_id) DO UPDATE SET " +
                    "friend_name = EXCLUDED.friend_name, " +
                    "friend_group = EXCLUDED.friend_group, " +
                    "friend_status = EXCLUDED.friend_status";
        } else {
            sql = "INSERT INTO friend.friends (character_id, friend_id, friend_name, friend_group, friend_status) " +
                    "VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, friend.getCharacterId());
            stmt.setInt(2, friend.getFriendId());
            stmt.setString(3, friend.getFriendName());
            stmt.setString(4, friend.getFriendGroup());
            stmt.setInt(5, friend.getStatus().getValue());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a friend record from the database.
     *
     * @param conn the active SQL connection
     * @param characterId the character's ID
     * @param friendId the friend's ID
     * @return true if the record was deleted, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean deleteFriend(Connection conn, int characterId, int friendId) throws SQLException {
        String sql = "DELETE FROM friend.friends WHERE character_id = ? AND friend_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, friendId);
            return stmt.executeUpdate() > 0;
        }
    }
}
