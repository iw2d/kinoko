package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BoardNoticeDao {

    /**
     * Saves or updates the guild's notice entry in the database.
     *
     * Sets the `notice` column to TRUE for the given board entry and
     * automatically ensures that all other entries for the guild are set to FALSE.
     *
     * @param conn  the active SQL connection to use (part of a transaction if applicable)
     * @param guild the guild whose board notice should be saved
     * @throws SQLException if a database access error occurs
     */
    public static void saveBoardNotice(Connection conn, Guild guild) throws SQLException {
        GuildBoardEntry notice = guild.getBoardNoticeEntry();

        // Always unset the notice flag for all entries first
        try (PreparedStatement unsetStmt = conn.prepareStatement(
                "UPDATE guild.board_entry SET notice = FALSE WHERE guild_id = ?"
        )) {
            unsetStmt.setInt(1, guild.getGuildId());
            unsetStmt.executeUpdate();
        }

        // If there’s no notice to save, we’re done
        if (notice == null) return;

        String sql = """
                INSERT INTO guild.board_entry (guild_id, id, character_id, title, message, emoticon, notice)
                VALUES (?, ?, ?, ?, ?, ?, TRUE)
                ON CONFLICT (guild_id, id)
                DO UPDATE SET
                    character_id = EXCLUDED.character_id,
                    title = EXCLUDED.title,
                    message = EXCLUDED.message,
                    emoticon = EXCLUDED.emoticon,
                    notice = TRUE
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guild.getGuildId());
            stmt.setInt(2, notice.getEntryId());
            stmt.setInt(3, notice.getCharacterId());
            stmt.setString(4, notice.getTitle());
            stmt.setString(5, notice.getText());
            stmt.setInt(6, notice.getEmoticon());
            stmt.executeUpdate();
        }
    }

    /**
     * Loads the guild's current board notice from the database.
     *
     * Queries the `guild.board_entry` table for the entry marked as a notice.
     * Assumes there is at most one notice per guild.
     *
     * @param conn    the active SQL connection to use (part of a transaction if applicable)
     * @param guildId the guild ID whose notice should be loaded
     * @return the GuildBoardEntry marked as notice, or null if none exists
     * @throws SQLException if a database access error occurs
     */
    public static GuildBoardEntry loadBoardNotice(Connection conn, int guildId) throws SQLException {
        String sql = """
            SELECT id, character_id, title, message, timestamp, emoticon
            FROM guild.board_entry
            WHERE guild_id = ? AND notice IS TRUE
            LIMIT 1
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GuildBoardEntry(
                            rs.getInt("id"),
                            rs.getInt("character_id"),
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getTimestamp("timestamp").toInstant(),
                            rs.getInt("emoticon")
                    );
                }
            }
        }

        return null;
    }
}
