package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.GuildAccessor;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRanking;
import kinoko.server.guild.GuildRank;

import java.sql.*;
import java.util.*;

public final class PostgresGuildAccessor extends PostgresAccessor implements GuildAccessor {

    public PostgresGuildAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    // ---------------------------------------------
    // LOAD A GUILD
    // ---------------------------------------------
    private Guild loadGuild(ResultSet rs) throws SQLException {
        final int guildId = rs.getInt("guild_id");
        final String guildName = rs.getString("guild_name");
        Guild guild = new Guild(guildId, guildName);

        guild.setMemberMax(rs.getInt("member_max"));
        guild.setMarkBg(rs.getShort("mark_bg"));
        guild.setMarkBgColor(rs.getByte("mark_bg_color"));
        guild.setMark(rs.getShort("mark"));
        guild.setMarkColor(rs.getByte("mark_color"));
        guild.setNotice(rs.getString("notice"));
        guild.setPoints(rs.getInt("points"));
        guild.setLevel(rs.getByte("level"));

        final List<GuildMember> members = loadMembers(guildId);
        for (GuildMember member : members) {
            guild.addMember(member);
        }

        guild.setGradeNames(loadGrades(guild.getGuildId()));

        final List<GuildBoardEntry> boardEntries = loadBoardEntries(guildId);
        guild.getBoardEntries().addAll(boardEntries);

        guild.setBoardNoticeEntry(loadBoardNotice(guildId));

        return guild;
    }

    @Override
    public Optional<Guild> getGuildById(int guildId) {
        String sql = "SELECT * FROM guild.guilds WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadGuild(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private List<String> loadGrades(int guildId) throws SQLException {
        List<String> grades = new ArrayList<>();
        String sql = "SELECT grade_name FROM guild.grade WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(rs.getString("grade_name"));
                }
            }
        }
        return grades;
    }

    // ---------------------------------------------
    // MEMBERS
    // ---------------------------------------------
    private List<GuildMember> loadMembers(int guildId) {
        List<GuildMember> members = new ArrayList<>();
        String sql = """
        SELECT c.character_id, c.character_name, s.job, s.level,
               m.grade AS guildRank, NULL AS allianceRank, c.online
        FROM guild.member m
        JOIN player.characters c ON c.character_id = m.character_id
        JOIN character.stats s ON s.character_id = c.character_id
        WHERE m.guild_id = ?
        """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int charId = rs.getInt("character_id");
                    String charName = rs.getString("character_name");
                    int job = rs.getInt("job");
                    int level = rs.getInt("level");
                    boolean online = rs.getBoolean("online"); // now works with the new column
                    int guildRankInt = rs.getInt("guildRank");
                    Integer allianceRankInt = null; // no alliance rank yet

                    members.add(new GuildMember(
                            charId,
                            charName,
                            job,
                            level,
                            online,
                            GuildRank.getByValue(guildRankInt),
                            allianceRankInt != null ? GuildRank.getByValue(allianceRankInt) : null  // setting to null for now.
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }




    // ---------------------------------------------
    // BOARD ENTRIES
    // ---------------------------------------------
    private List<GuildBoardEntry> loadBoardEntries(int guildId) {
        List<GuildBoardEntry> entries = new ArrayList<>();
        String sql = "SELECT entry_id, character_id, title, message, timestamp, 0 AS emoticon " +
                "FROM guild.board_entry WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(new GuildBoardEntry(
                            rs.getInt("entry_id"),
                            rs.getInt("character_id"),
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getTimestamp("timestamp").toInstant(),
                            rs.getInt("emoticon")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private GuildBoardEntry loadBoardNotice(int guildId) {
        String sql = "SELECT entry_id FROM guild.notice WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int entryId = rs.getInt("entry_id");
                    // Load full entry
                    String entrySql = "SELECT entry_id, character_id, title, message, timestamp, 0 AS emoticon " +
                            "FROM guild.board_entry WHERE entry_id = ?";
                    try (PreparedStatement entryStmt = getConnection().prepareStatement(entrySql)) {
                        entryStmt.setInt(1, entryId);
                        try (ResultSet ers = entryStmt.executeQuery()) {
                            if (ers.next()) {
                                return new GuildBoardEntry(
                                        ers.getInt("entry_id"),
                                        ers.getInt("character_id"),
                                        ers.getString("title"),
                                        ers.getString("message"),
                                        ers.getTimestamp("timestamp").toInstant(),
                                        ers.getInt("emoticon")
                                );
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------------------------------------------
    // CHECK NAME
    // ---------------------------------------------
    @Override
    public boolean checkGuildNameAvailable(String name) {
        String sql = "SELECT 1 FROM guild.guilds WHERE LOWER(guild_name) = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------
    // SAVE / CREATE
    // ---------------------------------------------
    @Override
    public synchronized boolean newGuild(Guild guild) {
        if (!checkGuildNameAvailable(guild.getGuildName())) return false;
        return saveGuild(guild);
    }

    @Override
    public boolean saveGuild(Guild guild) {
        String sql = "INSERT INTO guild.guilds (guild_id, guild_name, grade_names, member_max, mark_bg, mark_bg_color, mark, mark_color, notice, points, level, board_entry_counter) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (guild_id) DO UPDATE SET " +
                "guild_name = EXCLUDED.guild_name, " +
                "grade_names = EXCLUDED.grade_names, " +
                "member_max = EXCLUDED.member_max, " +
                "mark_bg = EXCLUDED.mark_bg, " +
                "mark_bg_color = EXCLUDED.mark_bg_color, " +
                "mark = EXCLUDED.mark, " +
                "mark_color = EXCLUDED.mark_color, " +
                "notice = EXCLUDED.notice, " +
                "points = EXCLUDED.points, " +
                "level = EXCLUDED.level, " +
                "board_entry_counter = EXCLUDED.board_entry_counter";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guild.getGuildId());
            stmt.setString(2, guild.getGuildName());
            stmt.setArray(3, getConnection().createArrayOf("text", guild.getGradeNames().toArray()));
            stmt.setInt(4, guild.getMemberMax());
            stmt.setShort(5, guild.getMarkBg());
            stmt.setByte(6, guild.getMarkBgColor());
            stmt.setShort(7, guild.getMark());
            stmt.setByte(8, guild.getMarkColor());
            stmt.setString(9, guild.getNotice());
            stmt.setInt(10, guild.getPoints());
            stmt.setByte(11, guild.getLevel());
            stmt.setInt(12, guild.getBoardEntryCounter().get());
            stmt.executeUpdate();

            saveMembers(guild);
            saveBoardEntries(guild);
            saveBoardNotice(guild);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveMembers(Guild guild) throws SQLException {
        String deleteSql = "DELETE FROM guild.member WHERE guild_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, guild.getGuildId());
            stmt.executeUpdate();
        }

        String insertSql = "INSERT INTO guild.member (guild_id, character_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (GuildMember member : guild.getGuildMembers()) {
                stmt.setInt(1, guild.getGuildId());
                stmt.setInt(2, member.getCharacterId());
                stmt.setShort(3, (short) member.getGuildRank().getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void saveBoardEntries(Guild guild) throws SQLException {
        String deleteSql = "DELETE FROM guild.board_entry WHERE guild_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, guild.getGuildId());
            stmt.executeUpdate();
        }

        String insertSql = "INSERT INTO guild.board_entry (entry_id, guild_id, character_id, title, message, timestamp, emoticon) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (GuildBoardEntry entry : guild.getBoardEntries()) {
                stmt.setInt(1, entry.getEntryId());
                stmt.setInt(2, guild.getGuildId());
                stmt.setInt(3, entry.getCharacterId());
                stmt.setString(4, entry.getTitle());
                stmt.setString(5, entry.getText());
                stmt.setTimestamp(6, Timestamp.from(entry.getDate()));
                stmt.setInt(7, entry.getEmoticon());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void saveBoardNotice(Guild guild) throws SQLException {
        String sql = "INSERT INTO guild.notice (guild_id, entry_id) VALUES (?, ?) " +
                "ON CONFLICT (guild_id) DO UPDATE SET entry_id = EXCLUDED.entry_id";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            GuildBoardEntry notice = guild.getBoardNoticeEntry();
            if (notice != null) {
                stmt.setInt(1, guild.getGuildId());
                stmt.setInt(2, notice.getEntryId());
                stmt.executeUpdate();
            }
        }
    }

    // ---------------------------------------------
    // DELETE
    // ---------------------------------------------
    @Override
    public boolean deleteGuild(int guildId) {
        String sql = "DELETE FROM guild.guilds WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------------------------------------------
    // RANKINGS
    // ---------------------------------------------
    @Override
    public List<GuildRanking> getGuildRankings() {
        List<GuildRanking> rankings = new ArrayList<>();
        String sql = "SELECT name, points, mark, mark_color, mark_bg, mark_bg_color FROM guild.guilds ORDER BY points DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rankings.add(new GuildRanking(
                        rs.getString("name"),
                        rs.getInt("points"),
                        rs.getShort("mark"),
                        rs.getByte("mark_color"),
                        rs.getShort("mark_bg"),
                        rs.getByte("mark_bg_color")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rankings;
    }
}
