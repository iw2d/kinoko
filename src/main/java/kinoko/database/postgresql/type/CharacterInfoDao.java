package kinoko.database.postgresql.type;

import kinoko.database.CharacterInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class CharacterInfoDao {
    /**
     * Retrieves CharacterInfo by character name.
     *
     * Performs a case-insensitive search using ILIKE.
     * Returns an Optional containing CharacterInfo if found, otherwise empty.
     *
     * @param conn the database connection to use
     * @param name the name of the character
     * @return Optional containing CharacterInfo if the character exists
     * @throws SQLException if a database access error occurs
     */
    public static Optional<CharacterInfo> getCharacterInfoByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT account_id, id, name FROM player.characters WHERE name ILIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CharacterInfo(
                            rs.getInt("account_id"),
                            rs.getInt("id"),
                            rs.getString("name")
                    ));
                }
            }
        }
        return Optional.empty();
    }
}
