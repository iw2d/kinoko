package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.FriendAccessor;
import kinoko.database.postgresql.type.FriendDao;
import kinoko.world.user.friend.Friend;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public final class PostgresFriendAccessor extends PostgresAccessor implements FriendAccessor {
    public PostgresFriendAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves all friends for a given character ID.
     *
     * @param characterId the ID of the character
     * @return a list of Friend objects; empty list if none found or on error
     */
    @Override
    public List<Friend> getFriendsByCharacterId(int characterId) {
        try (Connection conn = getConnection()) {
            return FriendDao.getFriendsByCharacterId(conn, characterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves all friends where the given ID appears as the friend.
     *
     * @param friendId the ID of the friend
     * @return a list of Friend objects; empty list if none found or on error
     */
    @Override
    public List<Friend> getFriendsByFriendId(int friendId) {
        try (Connection conn = getConnection()) {
            return FriendDao.getFriendsByFriendId(conn, friendId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Saves a friend record to the database.
     * If 'force' is true, existing records will be updated.
     *
     * @param friend the Friend object to save
     * @param force whether to overwrite existing records
     * @return true if the save operation succeeded, false otherwise
     */
    public boolean saveFriend(Friend friend, boolean force) {
        return withTransaction(conn -> {
            return FriendDao.saveFriend(conn, friend, force);
        });
    }

    /**
     * Deletes a friend record from the database.
     *
     * @param characterId the ID of the character
     * @param friendId the ID of the friend to delete
     * @return true if the deletion succeeded, false otherwise
     */
    @Override
    public boolean deleteFriend(int characterId, int friendId) {
        return withTransaction(conn -> {
           return FriendDao.deleteFriend(conn, characterId, friendId);
        });
    }
}
