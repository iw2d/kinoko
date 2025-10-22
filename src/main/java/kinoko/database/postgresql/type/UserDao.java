package kinoko.database.postgresql.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class UserDao {

    /**
     * Deletes a character for the given account.
     *
     * @param conn        the database connection
     * @param accountId   the account ID owning the character
     * @param characterId the character ID to delete
     * @return true if a row was deleted, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean deleteCharacter(Connection conn, int accountId, int characterId) throws SQLException {
        String sql = "DELETE FROM player.characters WHERE id=? AND account_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, accountId);
            return stmt.executeUpdate() > 0;
        }
    }
}
