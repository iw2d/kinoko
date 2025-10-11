package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardComment;
import kinoko.server.guild.GuildBoardEntry;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardEntryDao {

    /**
     * Synchronizes the guild's board entries in the database with the entries in memory.
     *
     * Deletes entries that are no longer present and inserts or updates
     * existing ones, minimizing redundant operations.
     *
     * Also synchronizes comments for each board entry via BoardEntryCommentDao.
     *
     * @param conn  the active SQL connection to use (part of a transaction if applicable)
     * @param guild the guild whose board entries should be synchronized
     * @throws SQLException if a database access error occurs
     */
    public static void saveBoardEntries(Connection conn, Guild guild) throws SQLException {
        List<GuildBoardEntry> entries = guild.getBoardEntries();

        if (entries == null || entries.isEmpty()) {
            deleteAllEntries(conn, guild.getGuildId());
            return;
        }

        // Delete board entries that have been removed.
        deleteRemovedBoardEntries(conn, guild, entries);

        // Update existing entries
        upsertBoardEntries(conn, guild.getGuildId(), entries);

        // Synchronize comments for each entry
        for (GuildBoardEntry entry : entries) {
            BoardEntryCommentDao.saveComments(conn, entry, guild);
        }
    }

    /**
     * Deletes all board entries for a given guild from the database.
     *
     * This is typically used when the guild has no entries in memory
     * and we want to remove all corresponding database records.
     *
     * @param conn    the active SQL connection
     * @param guildId the ID of the guild whose entries should be deleted
     * @throws SQLException if a database access error occurs
     */
    private static void deleteAllEntries(Connection conn, int guildId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM guild.board_entry WHERE guild_id = ?")) {
            stmt.setInt(1, guildId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes board entries from the database that are no longer present
     * in the provided in-memory list of entries.
     *
     * Only entries that exist in the database but not in the current list
     * will be deleted. If the list is empty, all entries for the guild
     * will be removed.
     *
     * @param conn    the active SQL connection
     * @param guild   the guild whose entries are being synchronized
     * @param entries the current list of board entries that should remain
     * @throws SQLException if a database access error occurs
     */
    private static void deleteRemovedBoardEntries(Connection conn, Guild guild, List<GuildBoardEntry> entries) throws SQLException {
        List<Integer> currentIds = entries.stream()
                .map(GuildBoardEntry::getEntryId)
                .toList();

        if (currentIds.isEmpty()) {
            deleteAllEntries(conn, guild.getGuildId());
            return;
        }

        String placeholders = currentIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM guild.board_entry WHERE guild_id = ? AND id NOT IN (" + placeholders + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, guild.getGuildId());
            for (Integer id : currentIds) stmt.setInt(idx++, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts or updates guild board entries in the database.
     *
     * If a board entry with the same guild_id and id already exists, this method updates
     * its character_id, title, message, and emoticon fields. Otherwise, a new row is inserted.
     *
     * Because id is a per-guild serial, callers should ensure that new entries have a valid
     * or newly assigned id value before invoking this method.
     *
     * @param conn     the SQL connection
     * @param guildId  the guild ID associated with the board entries
     * @param entries  list of board entries to insert or update
     * @throws SQLException if a database error occurs
     */
    private static void upsertBoardEntries(Connection conn, int guildId, List<GuildBoardEntry> entries) throws SQLException {
        if (entries.isEmpty()) return;

        String sql = """
                INSERT INTO guild.board_entry (guild_id, id, character_id, title, message, emoticon)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (guild_id, id)
                DO UPDATE SET
                    character_id = EXCLUDED.character_id,
                    title = EXCLUDED.title,
                    message = EXCLUDED.message,
                    emoticon = EXCLUDED.emoticon
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (GuildBoardEntry entry : entries) {
                stmt.setInt(1, guildId);
                stmt.setInt(2, entry.getEntryId());     // must already be set or generated
                stmt.setInt(3, entry.getCharacterId());
                stmt.setString(4, entry.getTitle());
                stmt.setString(5, entry.getText());
                stmt.setInt(6, entry.getEmoticon());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Loads all board entries for a given guild from the database.
     *
     * @param conn    the active SQL connection to use (part of a transaction if applicable)
     * @param guildId the guild ID whose board entries should be loaded
     * @return a list of GuildBoardEntry objects
     * @throws SQLException if a database access error occurs
     */
    public static List<GuildBoardEntry> loadBoardEntries(Connection conn, int guildId) throws SQLException {
        List<GuildBoardEntry> entries = new ArrayList<>();
        String sql = "SELECT id, character_id, title, message, timestamp, emoticon, notice " +
                "FROM guild.board_entry WHERE guild_id = ? AND notice IS FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GuildBoardEntry entry = new GuildBoardEntry(
                            rs.getInt("id"),
                            rs.getInt("character_id"),
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getTimestamp("timestamp").toInstant(),
                            rs.getInt("emoticon")
                    );
                    entries.add(entry);
                }
            }
        }


        java.util.Map<Integer, java.util.List<GuildBoardComment>> entryComments = BoardEntryCommentDao.loadComments(conn, guildId);

        // Attach comments to the corresponding entries
        for (GuildBoardEntry entry : entries) {
            List<GuildBoardComment> comments = entryComments.get(entry.getEntryId());
            if (comments != null) {
                for (GuildBoardComment comment : comments) {
                    entry.addComment(comment);
                }
            }
        }

        return entries;
    }
}
