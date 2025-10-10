package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;

import java.sql.*;
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

        // Split entries into new inserts and existing updates
        List<GuildBoardEntry> newEntries = entries.stream()
                .filter(GuildBoardEntry::hasNoSN)
                .toList();

        List<GuildBoardEntry> existingEntries = entries.stream()
                .filter(entry -> !entry.hasNoSN())
                .toList();

        // Insert new entries and assign generated SNs
        insertBoardEntries(conn, guild.getGuildId(), newEntries);

        // Update existing entries
        updateBoardEntries(conn, guild.getGuildId(), existingEntries);

        // Synchronize comments for each entry
        for (GuildBoardEntry entry : entries) {
            BoardEntryCommentDao.saveComments(conn, entry);
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
     * Inserts new board entries into the database and sets their generated IDs.
     *
     * @param conn      the SQL connection
     * @param guildId   the guild ID
     * @param newEntries list of entries to insert
     * @throws SQLException if a database error occurs
     */
    private static void insertBoardEntries(Connection conn, int guildId, List<GuildBoardEntry> newEntries) throws SQLException {
        if (newEntries.isEmpty()) return;

        String sql = """
                INSERT INTO guild.board_entry (guild_id, character_id, title, message, emoticon)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (GuildBoardEntry entry : newEntries) {
                stmt.setInt(1, guildId);
                stmt.setInt(2, entry.getCharacterId());
                stmt.setString(3, entry.getTitle());
                stmt.setString(4, entry.getText());
                stmt.setInt(5, entry.getEmoticon());
                stmt.addBatch();
            }

            stmt.executeBatch();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                int i = 0;
                while (rs.next()) {
                    newEntries.get(i++).setEntryId(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Updates existing board entries in the database.
     *
     * @param conn           the SQL connection
     * @param guildId        the guild ID
     * @param existingEntries list of entries to update
     * @throws SQLException if a database error occurs
     */
    private static void updateBoardEntries(Connection conn, int guildId, List<GuildBoardEntry> existingEntries) throws SQLException {
        if (existingEntries.isEmpty()) return;

        String sql = """
                UPDATE guild.board_entry
                SET character_id = ?, title = ?, message = ?, emoticon = ?
                WHERE guild_id = ? AND id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (GuildBoardEntry entry : existingEntries) {
                stmt.setInt(1, entry.getCharacterId());
                stmt.setString(2, entry.getTitle());
                stmt.setString(3, entry.getText());
                stmt.setInt(4, entry.getEmoticon());
                stmt.setInt(5, guildId);
                stmt.setInt(6, entry.getEntryId());
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
                "FROM guild.board_entry WHERE guild_id = ?";

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

        return entries;
    }
}
