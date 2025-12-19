package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardComment;
import kinoko.server.guild.GuildBoardEntry;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class BoardEntryCommentDao {

    /**
     * Synchronizes the comments of a given guild board entry in the database
     * with the comments currently in memory.
     *
     * Deletes comments that have been removed, inserts new comments,
     * and ensures that the database matches the in-memory list.
     *
     * @param conn  the active SQL connection to use (part of a transaction if applicable)
     * @param entry the board entry whose comments should be synchronized
     * @throws SQLException if a database access error occurs
     */
    public static void saveComments(Connection conn, GuildBoardEntry entry, Guild guild) throws SQLException {
        List<GuildBoardComment> comments = entry.getComments();

        if (comments == null || comments.isEmpty()) {
            deleteAllComments(conn, entry.getEntryId(), guild.getGuildId());
            return;
        }

        deleteRemovedComments(conn, entry, comments, guild.getGuildId());
        insertNewComments(conn, entry, comments, guild.getGuildId());
    }

    /**
     * Deletes all comments for a given board entry from the database.
     *
     * This is typically used when there are no comments in memory,
     * and the database should be cleared accordingly.
     *
     * @param conn    the active SQL connection
     * @param entryId the ID of the board entry whose comments should be deleted
     * @throws SQLException if a database access error occurs
     */
    private static void deleteAllComments(Connection conn, int entryId, int guildId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM guild.board_entry_comment WHERE entry_id = ? AND guild_id = ?")) {
            stmt.setInt(1, entryId);
            stmt.setInt(2, guildId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes comments that exist in the database but no longer exist
     * in the current in-memory list of comments for a given entry.
     *
     * @param conn     the active SQL connection
     * @param entry    the board entry whose comments are being synchronized
     * @param comments the current list of comments that should remain
     * @throws SQLException if a database access error occurs
     */
    private static void deleteRemovedComments(Connection conn, GuildBoardEntry entry, List<GuildBoardComment> comments, int guildId) throws SQLException {
        List<Integer> currentIds = comments.stream()
                .filter(c -> !c.hasNoSN())
                .map(GuildBoardComment::getCommentSn)
                .toList();

        if (currentIds.isEmpty()) return;

        String placeholders = currentIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM guild.board_entry_comment WHERE entry_id = ? AND guild_id = ? AND id NOT IN (" + placeholders + ")";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, entry.getEntryId());
            stmt.setInt(idx++, guildId);
            for (Integer id : currentIds) stmt.setInt(idx++, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts new comments for a given board entry into the database
     * and sets their generated IDs.
     *
     * Only comments without a serial number (new comments) are inserted.
     *
     * @param conn     the active SQL connection
     * @param entry    the board entry to which the comments belong
     * @param comments the list of comments to insert
     * @throws SQLException if a database access error occurs or
     *                      the generated keys cannot be retrieved
     */
    private static void insertNewComments(Connection conn, GuildBoardEntry entry, List<GuildBoardComment> comments, int guildId) throws SQLException {
        String insertSql = """
                INSERT INTO guild.board_entry_comment (entry_id, guild_id, character_id, text, timestamp)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            List<GuildBoardComment> newComments = comments.stream().filter(GuildBoardComment::hasNoSN).toList();
            for (GuildBoardComment comment : newComments) {
                stmt.setInt(1, entry.getEntryId());
                stmt.setInt(2, guildId);
                stmt.setInt(3, comment.getCharacterId());
                stmt.setString(4, comment.getText());
                stmt.setTimestamp(5, Timestamp.from(comment.getDate()));
                stmt.addBatch();
            }

            stmt.executeBatch();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                for (GuildBoardComment comment : newComments) {
                    if (generatedKeys.next()) {
                        comment.setCommentSn(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Failed to retrieve generated commentSn for a new comment.");
                    }
                }
            }
        }
    }

    /**
     * Loads all comments for a given guild from the database, grouped by board entry.
     *
     * @param conn    the active SQL connection
     * @param guildId the ID of the guild whose comments should be loaded
     * @return a mapping from entryId to a list of GuildBoardComment objects
     * @throws SQLException if a database access error occurs
     */
    public static java.util.Map<Integer, java.util.List<GuildBoardComment>> loadComments(Connection conn, int guildId) throws SQLException {
        String sql = """
                SELECT id, entry_id, guild_id, character_id, text, timestamp
                FROM guild.board_entry_comment
                WHERE guild_id = ?
                ORDER BY entry_id ASC, timestamp ASC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                java.util.Map<Integer, java.util.List<GuildBoardComment>> commentsByEntry = new java.util.HashMap<>();

                while (rs.next()) {
                    int entryId = rs.getInt("entry_id");
                    GuildBoardComment comment = new GuildBoardComment(
                            rs.getInt("id"),
                            rs.getInt("character_id"),
                            rs.getString("text"),
                            rs.getTimestamp("timestamp").toInstant()
                    );

                    commentsByEntry.computeIfAbsent(entryId, k -> new java.util.ArrayList<>()).add(comment);
                }

                return commentsByEntry;
            }
        }
    }
}
