package kinoko.database.sqlite;

import kinoko.database.GuildAccessor;
import kinoko.database.json.GuildSerializer;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRanking;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kinoko.database.schema.GuildSchema.*;

public final class SqliteGuildAccessor extends SqliteAccessor implements GuildAccessor {
    private static final String tableName = "guild_table";
    private final GuildSerializer guildSerializer = new GuildSerializer();

    public SqliteGuildAccessor(Connection connection) {
        super(connection);
    }

    private Guild loadGuild(ResultSet rs) throws SQLException {
        final int guildId = rs.getInt(GUILD_ID);
        final String guildName = rs.getString(GUILD_NAME);
        final Guild guild = new Guild(guildId, guildName);
        guild.setGradeNames(guildSerializer.deserializeGradeNames(getJsonArray(rs, GRADE_NAMES)));
        for (GuildMember member : guildSerializer.deserializeGuildMembers(getJsonArray(rs, MEMBERS))) {
            guild.addMember(member);
        }
        guild.setMemberMax(rs.getInt(MEMBER_MAX));
        guild.setMarkBg(rs.getShort(MARK_BG));
        guild.setMarkBgColor(rs.getByte(MARK_BG_COLOR));
        guild.setMark(rs.getShort(MARK));
        guild.setMarkColor(rs.getByte(MARK_COLOR));
        guild.setNotice(rs.getString(NOTICE));
        guild.setPoints(rs.getInt(POINTS));
        guild.setLevel(rs.getByte(LEVEL));

        guild.getBoardEntries().addAll(guildSerializer.deserializeBoardEntryList(getJsonArray(rs, BOARD_ENTRY_LIST)));
        guild.setBoardNoticeEntry(guildSerializer.deserializeBoardNoticeEntry(getJsonObject(rs, BOARD_ENTRY_NOTICE)));
        guild.setBoardEntryCounter(new AtomicInteger(rs.getInt(BOARD_ENTRY_COUNTER)));
        return guild;
    }


    @Override
    public Optional<Guild> getGuildById(int guildId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + GUILD_ID + " = ?"
        )) {
            ps.setInt(1, guildId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadGuild(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean checkGuildNameAvailable(String name) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT 1 FROM " + tableName + " WHERE " + GUILD_NAME + " = ?"
        )) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean newGuild(Guild guild) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        GUILD_ID + ", " +
                        GUILD_NAME + ", " +
                        GRADE_NAMES + ", " +
                        MEMBERS + ", " +
                        MEMBER_MAX + ", " +
                        MARK_BG + ", " +
                        MARK_BG_COLOR + ", " +
                        MARK + ", " +
                        MARK_COLOR + ", " +
                        NOTICE + ", " +
                        POINTS + ", " +
                        LEVEL + ", " +
                        BOARD_ENTRY_LIST + ", " +
                        BOARD_ENTRY_NOTICE + ", " +
                        BOARD_ENTRY_COUNTER + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            int i = 1;
            ps.setInt(i++, guild.getGuildId());
            ps.setString(i++, guild.getGuildName());
            setJsonArray(ps, i++, guildSerializer.serializeGradeNames(guild.getGradeNames()));
            setJsonArray(ps, i++, guildSerializer.serializeGuildMembers(guild.getGuildMembers()));
            ps.setInt(i++, guild.getMemberMax());
            ps.setShort(i++, guild.getMarkBg());
            ps.setByte(i++, guild.getMarkBgColor());
            ps.setShort(i++, guild.getMark());
            ps.setByte(i++, guild.getMarkColor());
            ps.setString(i++, guild.getNotice());
            ps.setInt(i++, guild.getPoints());
            ps.setByte(i++, guild.getLevel());
            setJsonArray(ps, i++, guildSerializer.serializeBoardEntryList(guild.getBoardEntries()));
            setJsonObject(ps, i++, guildSerializer.serializeBoardNoticeEntry(guild.getBoardNoticeEntry()));
            ps.setInt(i++, guild.getBoardEntryCounter().get());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveGuild(Guild guild) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "UPDATE " + tableName + " SET " +
                        GUILD_NAME + " = ?, " +
                        GRADE_NAMES + " = ?, " +
                        MEMBERS + " = ?, " +
                        MEMBER_MAX + " = ?, " +
                        MARK_BG + " = ?, " +
                        MARK_BG_COLOR + " = ?, " +
                        MARK + " = ?, " +
                        MARK_COLOR + " = ?, " +
                        NOTICE + " = ?, " +
                        POINTS + " = ?, " +
                        LEVEL + " = ?, " +
                        BOARD_ENTRY_LIST + " = ?, " +
                        BOARD_ENTRY_NOTICE + " = ?, " +
                        BOARD_ENTRY_COUNTER + " = ? WHERE " + GUILD_ID + " = ?"
        )) {
            int i = 1;
            ps.setString(i++, guild.getGuildName());
            setJsonArray(ps, i++, guildSerializer.serializeGradeNames(guild.getGradeNames()));
            setJsonArray(ps, i++, guildSerializer.serializeGuildMembers(guild.getGuildMembers()));
            ps.setInt(i++, guild.getMemberMax());
            ps.setShort(i++, guild.getMarkBg());
            ps.setByte(i++, guild.getMarkBgColor());
            ps.setShort(i++, guild.getMark());
            ps.setByte(i++, guild.getMarkColor());
            ps.setString(i++, guild.getNotice());
            ps.setInt(i++, guild.getPoints());
            ps.setByte(i++, guild.getLevel());
            setJsonArray(ps, i++, guildSerializer.serializeBoardEntryList(guild.getBoardEntries()));
            setJsonObject(ps, i++, guildSerializer.serializeBoardNoticeEntry(guild.getBoardNoticeEntry()));
            ps.setInt(i++, guild.getBoardEntryCounter().get());

            ps.setInt(i, guild.getGuildId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteGuild(int guildId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM " + tableName + " WHERE " + GUILD_ID + " = ?"
        )) {
            ps.setInt(1, guildId);
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<GuildRanking> getGuildRankings() {
        return List.of(); // TODO
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            GUILD_ID + " INTEGER PRIMARY KEY, " +
                            GUILD_NAME + " TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                            GRADE_NAMES + " " + JSON_TYPE + " NOT NULL, " +
                            MEMBERS + " " + JSON_TYPE + " NOT NULL, " +
                            MEMBER_MAX + " INTEGER NOT NULL, " +
                            MARK_BG + " SMALLINT NOT NULL, " +
                            MARK_BG_COLOR + " TINYINT NOT NULL, " +
                            MARK + " SMALLINT NOT NULL, " +
                            MARK_COLOR + " TINYINT NOT NULL, " +
                            NOTICE + " TEXT, " +
                            POINTS + " INTEGER NOT NULL, " +
                            LEVEL + " TINYINT NOT NULL, " +
                            BOARD_ENTRY_LIST + " " + JSON_TYPE + " NOT NULL, " +
                            BOARD_ENTRY_NOTICE + " " + JSON_TYPE + ", " + // NULLABLE
                            BOARD_ENTRY_COUNTER + " INTEGER NOT NULL)"
            );

            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_guild_name ON " + tableName + "(" + GUILD_NAME + ")"
            );
        }
    }
}
