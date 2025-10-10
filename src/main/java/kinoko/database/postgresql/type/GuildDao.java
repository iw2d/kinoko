package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;
import kinoko.server.guild.GuildMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuildDao {

    /**
     * Checks if a guild name is available for use by verifying that it does not already exist in the database.
     *
     * Performs a case-insensitive lookup in the `guild.guilds` table using the provided connection.
     * Returns true if no guild with the same name exists, false otherwise.
     *
     * @param conn the active SQL connection to use for the query
     * @param name the guild name to check for availability
     * @return true if the guild name is available; false if it already exists or an error occurs
     */
    public static boolean checkGuildNameAvailable(Connection conn, String name) throws SQLException {
        String sql = "SELECT 1 FROM guild.guilds WHERE LOWER(name) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        }
    }

    /**
     * Inserts a new guild record and all related data into the database within the provided transaction connection.
     *
     * This method first verifies that the guild name is available before inserting the guild into the `guild.guilds` table.
     * After the main guild record is inserted, it saves the associated members, board entries, and notice using the same connection.
     * If the guild name is already taken, the method returns false without modifying the database.
     *
     * @param conn  the active SQL connection used for the transaction
     * @param guild the guild object containing all information to insert
     * @return true if the guild was successfully inserted; false if the guild name was unavailable
     * @throws SQLException if a database error occurs during insertion or related saves
     */
    public static synchronized boolean insertGuild(Connection conn, Guild guild) throws SQLException {
        if (!checkGuildNameAvailable(conn, guild.getGuildName())) return false;

        String sql = "INSERT INTO guild.guilds (name, grade_names, member_max, mark_bg, mark_bg_color, mark, mark_color, notice, points, level, board_entry_counter) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guild.getGuildName());
            stmt.setArray(2, conn.createArrayOf("text", guild.getGradeNames().toArray()));
            stmt.setInt(3, guild.getMemberMax());
            stmt.setShort(4, guild.getMarkBg());
            stmt.setByte(5, guild.getMarkBgColor());
            stmt.setShort(6, guild.getMark());
            stmt.setByte(7, guild.getMarkColor());
            stmt.setString(8, guild.getNotice());
            stmt.setInt(9, guild.getPoints());
            stmt.setByte(10, guild.getLevel());
            stmt.setInt(11, guild.getBoardEntryCounter().get());

            stmt.executeUpdate();

            GuildMemberDao.saveMembers(conn, guild);
            BoardEntryDao.saveBoardEntries(conn, guild);
            BoardNoticeDao.saveBoardNotice(conn, guild);
            return true;
        }
    }

    /**
     * Updates an existing guild's information in the database using the provided connection.
     *
     * Modifies all relevant guild fields such as name, grade names, emblems, notice, points, and level.
     * Also updates related members, board entries, and board notice after the main record update.
     *
     * @param conn the active SQL connection to use for the update
     * @param guild the guild object containing updated data
     * @return true if the update affected at least one row; false otherwise
     * @throws SQLException if a database error occurs during the update
     */
    public static boolean updateGuild(Connection conn, Guild guild) throws SQLException {
        String sql = "UPDATE guild.guilds SET " +
                "name = ?, " +
                "grade_names = ?, " +
                "member_max = ?, " +
                "mark_bg = ?, " +
                "mark_bg_color = ?, " +
                "mark = ?, " +
                "mark_color = ?, " +
                "notice = ?, " +
                "points = ?, " +
                "level = ?, " +
                "board_entry_counter = ? " +
                "WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guild.getGuildName());
            stmt.setArray(2, conn.createArrayOf("text", guild.getGradeNames().toArray()));
            stmt.setInt(3, guild.getMemberMax());
            stmt.setShort(4, guild.getMarkBg());
            stmt.setByte(5, guild.getMarkBgColor());
            stmt.setShort(6, guild.getMark());
            stmt.setByte(7, guild.getMarkColor());
            stmt.setString(8, guild.getNotice());
            stmt.setInt(9, guild.getPoints());
            stmt.setByte(10, guild.getLevel());
            stmt.setInt(11, guild.getBoardEntryCounter().get());
            stmt.setInt(12, guild.getGuildId());

            int rows = stmt.executeUpdate();

            GuildMemberDao.saveMembers(conn, guild);
            BoardEntryDao.saveBoardEntries(conn, guild);
            BoardNoticeDao.saveBoardNotice(conn, guild);

            return rows > 0;
        }
    }

    /**
     * Loads a Guild object from the provided ResultSet.
     *
     * Populates the guild's basic info, members, grades, board entries,
     * and the board notice entry. Assumes the ResultSet is already positioned
     * at the correct row.
     *
     * @param conn the active SQL connection to use (part of a transaction if applicable)
     * @param rs   the ResultSet containing guild data
     * @return a fully populated Guild object
     * @throws SQLException if a database access error occurs
     */
    public static Guild loadGuild(Connection conn, ResultSet rs) throws SQLException {
        final int guildId = rs.getInt("id");
        final String guildName = rs.getString("name");
        Guild guild = new Guild(guildId, guildName);

        guild.setMemberMax(rs.getInt("member_max"));
        guild.setMarkBg(rs.getShort("mark_bg"));
        guild.setMarkBgColor(rs.getByte("mark_bg_color"));
        guild.setMark(rs.getShort("mark"));
        guild.setMarkColor(rs.getByte("mark_color"));
        guild.setNotice(rs.getString("notice"));
        guild.setPoints(rs.getInt("points"));
        guild.setLevel(rs.getByte("level"));

        // Load members
        List<GuildMember> members = GuildMemberDao.loadMembers(conn, guildId);
        for (GuildMember member : members) {
            guild.addMember(member);
        }

        // Load grade names
        guild.setGradeNames(loadGrades(conn, guildId));

        // Load board entries
        List<GuildBoardEntry> boardEntries = BoardEntryDao.loadBoardEntries(conn, guildId);
        guild.getBoardEntries().addAll(boardEntries);

        // Load board notice entry
        GuildBoardEntry noticeEntry = BoardNoticeDao.loadBoardNotice(conn, guildId);
        guild.setBoardNoticeEntry(noticeEntry);

        return guild;
    }

    /**
     * Deletes a guild from the database along with all related data.
     *
     * This method expects an active SQL connection, allowing it to be part
     * of a larger transaction.
     *
     * @param conn    the active SQL connection (part of a transaction)
     * @param guildId the ID of the guild to delete
     * @return true if the guild was deleted, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean deleteGuild(Connection conn, int guildId) throws SQLException {
        String sql = "DELETE FROM guild.guilds WHERE guild_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Loads the grade names for a specific guild from the database.
     *
     * Retrieves all grade names associated with the given guild ID
     * and returns them as a list of strings. The order of the grades
     * is determined by the database query.
     *
     * @param conn    the active SQL connection to use
     * @param guildId the ID of the guild whose grades should be loaded
     * @return a list of grade names for the specified guild
     * @throws SQLException if a database access error occurs
     */
    private static List<String> loadGrades(Connection conn, int guildId) throws SQLException {
        List<String> grades = new ArrayList<>();
        String sql = "SELECT grade_name FROM guild.grade WHERE guild_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(rs.getString("grade_name"));
                }
            }
        }
        return grades;
    }
}