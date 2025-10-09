package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.FriendAccessor;
import kinoko.world.user.friend.Friend;
import kinoko.world.user.friend.FriendStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class PostgresFriendAccessor implements FriendAccessor {
    private final HikariDataSource dataSource;

    public PostgresFriendAccessor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Friend loadFriend(ResultSet rs) throws SQLException {
        int characterId = rs.getInt("character_id");
        int friendId = rs.getInt("friend_id");
        String friendName = rs.getString("friend_name");
        String friendGroup = rs.getString("friend_group");
        FriendStatus status = FriendStatus.getByValue(rs.getInt("friend_status"));
        return new Friend(characterId, friendId, friendName, friendGroup, status);
    }

    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT * FROM friend.friends WHERE character_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(loadFriend(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        List<Friend> friends = new ArrayList<>();
        String sql = "SELECT * FROM friend.friends WHERE friend_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, friendId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(loadFriend(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public boolean saveFriend(Friend friend, boolean force) {
        String sql;
        if (force) {
            sql = "INSERT INTO friend.friends (character_id, friend_id, friend_name, friend_group, friend_status) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON CONFLICT (character_id, friend_id) DO UPDATE SET friend_name = EXCLUDED.friend_name, " +
                    "friend_group = EXCLUDED.friend_group, friend_status = EXCLUDED.friend_status";
        } else {
            sql = "INSERT INTO friend.friends (character_id, friend_id, friend_name, friend_group, friend_status) " +
                    "VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, friend.getCharacterId());
            stmt.setInt(2, friend.getFriendId());
            stmt.setString(3, friend.getFriendName());
            stmt.setString(4, friend.getFriendGroup());
            stmt.setInt(5, friend.getStatus().getValue());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        String sql = "DELETE FROM friend.friends WHERE character_id = ? AND friend_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, friendId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
