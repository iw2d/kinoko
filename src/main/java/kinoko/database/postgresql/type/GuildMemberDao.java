package kinoko.database.postgresql.type;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GuildMemberDao {

    /**
     * Synchronizes the members of a guild in the database with the current list in memory.
     *
     * Deletes members that are no longer in the guild and inserts or updates members
     * that exist in the current guild member list, minimizing redundant database operations.
     *
     * @param conn  the active SQL connection to use (part of a transaction if applicable)
     * @param guild the guild whose members should be synchronized
     * @throws SQLException if a database access error occurs
     */
    public static void saveMembers(Connection conn, Guild guild) throws SQLException {
        List<GuildMember> members = guild.getGuildMembers();

        if (members == null || members.isEmpty()) {
            // No members at all — clear any remaining entries
            deleteAllMembers(conn, guild.getGuildId());
            return;
        }

        deleteRemovedMembers(conn, guild, members);
        upsertMembers(conn, guild, members);
    }

    /**
     * Deletes all members for a given guild from the database.
     *
     * @param conn    the active SQL connection
     * @param guildId the guild ID whose members should be deleted
     * @throws SQLException if a database access error occurs
     */
    public static void deleteAllMembers(Connection conn, int guildId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM guild.member WHERE guild_id = ?")) {
            stmt.setInt(1, guildId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes all guild members from the database that are no longer present
     * in the provided in-memory guild member list.
     *
     * Only members that exist in the database but not in the current list
     * will be deleted.
     *
     * @param conn    the SQL connection
     * @param guild   the guild whose members are being synchronized
     * @param members the current list of guild members that should remain
     * @throws SQLException if a database access error occurs
     */
    public static void deleteRemovedMembers(Connection conn, Guild guild, List<GuildMember> members) throws SQLException {
        List<Integer> currentIds = members.stream()
                .map(GuildMember::getCharacterId)
                .toList();

        // If there are no current members, remove all from DB
        if (currentIds.isEmpty()) {
            deleteAllMembers(conn, guild.getGuildId());
            return;
        }

        String placeholders = currentIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM guild.member WHERE guild_id = ? AND character_id NOT IN (" + placeholders + ")";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setInt(idx++, guild.getGuildId());
            for (Integer id : currentIds) {
                stmt.setInt(idx++, id);
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts or updates guild members in the database to match the provided in-memory list.
     * Existing members are updated with their new grade; new members are inserted.
     *
     * @param conn    the SQL connection
     * @param guild   the guild whose members are being synchronized
     * @param members the list of members to insert or update
     * @throws SQLException if a database access error occurs
     */
    public static void upsertMembers(Connection conn, Guild guild, List<GuildMember> members) throws SQLException {
        String sql = """
                INSERT INTO guild.member (guild_id, character_id, grade)
                VALUES (?, ?, ?)
                ON CONFLICT (guild_id, character_id) DO UPDATE SET grade = EXCLUDED.grade
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (GuildMember member : members) {
                stmt.setInt(1, guild.getGuildId());
                stmt.setInt(2, member.getCharacterId());
                stmt.setShort(3, (short) member.getGuildRank().getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Loads all members of a given guild from the database.
     *
     * Retrieves character information, stats, guild rank, and online status
     * for each member of the specified guild.
     *
     * @param conn    the active SQL connection to use (part of a transaction if applicable)
     * @param guildId the ID of the guild whose members should be loaded
     * @return a list of GuildMember objects representing the current members
     * @throws SQLException if a database access error occurs
     */
    public static List<GuildMember> loadMembers(Connection conn, int guildId) throws SQLException {
        List<GuildMember> members = new ArrayList<>();
        String sql = """
        SELECT c.character_id, c.character_name, s.job, s.level,
               m.grade AS guildRank, NULL AS allianceRank, c.online
        FROM guild.member m
        JOIN player.characters c ON c.character_id = m.character_id
        JOIN character.stats s ON s.character_id = c.character_id
        WHERE m.guild_id = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int charId = rs.getInt("character_id");
                    String charName = rs.getString("character_name");
                    int job = rs.getInt("job");
                    int level = rs.getInt("level");
                    boolean online = rs.getBoolean("online");
                    int guildRankInt = rs.getInt("guildRank");
                    Integer allianceRankInt = null; // no alliance rank yet

                    members.add(new GuildMember(
                            charId,
                            charName,
                            job,
                            level,
                            online,
                            GuildRank.getByValue(guildRankInt),
                            allianceRankInt != null ? GuildRank.getByValue(allianceRankInt) : null
                    ));
                }
            }
        }

        return members;
    }

}
