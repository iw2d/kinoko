package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.GuildAccessor;
import kinoko.database.postgresql.type.GuildDao;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildRanking;

import java.sql.*;
import java.util.*;

public final class PostgresGuildAccessor extends PostgresAccessor implements GuildAccessor {

    public PostgresGuildAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves a guild from the database by its ID.
     *
     * Executes a query to fetch the guild record corresponding to the
     * provided guild ID. If a matching guild is found, it is loaded
     * into a {@link Guild} object using {@link GuildDao#loadGuild}.
     *
     * @param guildId the ID of the guild to retrieve
     * @return an {@link Optional} containing the guild if found, or
     *         {@link Optional#empty()} if no guild exists with the given ID
     */
    @Override
    public Optional<Guild> getGuildById(int guildId) {
        String sql = "SELECT * FROM guild.guilds WHERE guild_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, guildId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(GuildDao.loadGuild(conn, rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Checks if a guild name is available for use.
     *
     * Queries the database to determine whether the given guild name
     * already exists. Returns true if the name is not taken, false otherwise.
     *
     * @param name the guild name to check
     * @return true if the name is available, false if it is already in use
     */
    @Override
    public boolean checkGuildNameAvailable(String name) {
        try (Connection conn = getConnection();) {
            return GuildDao.checkGuildNameAvailable(conn, name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a new guild in the database within a transaction.
     *
     * This method wraps the insertion in a transaction to ensure atomicity.
     * It delegates the actual insertion to GuildDao.insertGuild.
     *
     * @param guild the Guild object to be inserted
     * @return true if the guild was successfully created, false otherwise
     */
    @Override
    public synchronized boolean newGuild(Guild guild) {
        return withTransaction(conn -> {
            return GuildDao.insertGuild(conn, guild);
        });
    }

    /**
     * Saves (updates) an existing guild in the database within a transaction.
     *
     * This method wraps the update in a transaction to ensure atomicity.
     * It delegates the actual update to GuildDao.updateGuild.
     *
     * @param guild the Guild object with updated data
     * @return true if the guild was successfully updated, false otherwise
     */
    @Override
    public boolean saveGuild(Guild guild) {
        return withTransaction(conn -> {
            return GuildDao.updateGuild(conn, guild);
        });
    }

    /**
     * Deletes a guild from the database within a transaction.
     *
     * This method wraps the deletion in a transaction to ensure atomicity.
     * It delegates the actual deletion to GuildDao.deleteGuild.
     *
     * @param guildId the ID of the guild to delete
     * @return true if the guild was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteGuild(int guildId) {
        return withTransaction(conn -> {
            return GuildDao.deleteGuild(conn, guildId);
        });
    }

    /**
     * Retrieves a list of guild rankings from the database.
     *
     * Guilds are ordered by their points in descending order,
     * so the guild with the highest points appears first.
     *
     * Each GuildRanking object contains the guild's name, points,
     * and visual mark information (mark, mark color, background, background color).
     *
     * @return a list of GuildRanking objects representing all guilds ordered by points
     */
    @Override
    public List<GuildRanking> getGuildRankings() {
        List<GuildRanking> rankings = new ArrayList<>();
        String sql = "SELECT name, points, mark, mark_color, mark_bg, mark_bg_color FROM guild.guilds ORDER BY points DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
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
