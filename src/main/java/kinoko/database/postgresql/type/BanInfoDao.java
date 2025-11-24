package kinoko.database.postgresql.type;

import kinoko.world.user.BanInfo;

import java.sql.*;
import java.time.Instant;

public class BanInfoDao {

    /**
     * Loads the BanInfo for a specific account.
     *
     * Always returns a BanInfo object. If the account is not banned,
     * the returned BanInfo will have a null reason and no temporary ban.
     *
     * @param conn      the active SQL connection
     * @param accountId the ID of the account to load ban info for
     * @return a BanInfo object representing the account's ban status
     * @throws SQLException if a database error occurs
     */
    public static BanInfo load(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT reason, temp_ban_until FROM account.bans WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String reason = rs.getString("reason");
                    Timestamp ts = rs.getTimestamp("temp_ban_until");
                    Instant tempBanUntil = ts != null ? ts.toInstant() : null;
                    return new BanInfo(reason, tempBanUntil);
                }
            }
        }
        // Not banned → return empty BanInfo
        return new BanInfo(null, null);
    }

    /**
     * Saves or updates the BanInfo for an account.
     *
     * If the BanInfo indicates the account is banned, this method inserts
     * a new row or updates the existing row in the database.
     * If the account is not banned, any existing row is deleted.
     *
     * @param conn      the active SQL connection
     * @param accountId the ID of the account to save ban info for
     * @param banInfo   the BanInfo object containing ban data
     * @throws SQLException if a database error occurs
     */
    public static void save(Connection conn, int accountId, BanInfo banInfo) throws SQLException {
        if (banInfo.isBanned()) {
            String sql = """
                INSERT INTO account.bans (account_id, reason, temp_ban_until)
                VALUES (?, ?, ?)
                ON CONFLICT (account_id) DO UPDATE
                SET reason = EXCLUDED.reason,
                    temp_ban_until = EXCLUDED.temp_ban_until
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, banInfo.getReason());
                Instant tempBanUntil = banInfo.getTempBanUntil();
                if (tempBanUntil != null) {
                    stmt.setTimestamp(3, Timestamp.from(tempBanUntil));
                } else {
                    stmt.setNull(3, Types.TIMESTAMP);
                }
                stmt.executeUpdate();
            }
        } else {
            // Not banned → delete any existing ban row
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM account.bans WHERE account_id = ?")) {
                stmt.setInt(1, accountId);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Permanently bans an account with a given reason.
     *
     * @param conn      the active SQL connection
     * @param accountId the ID of the account to ban
     * @param reason    the reason for the ban
     * @throws SQLException if a database error occurs
     */
    public static void permanentBan(Connection conn, int accountId, String reason) throws SQLException {
        BanInfo banInfo = new BanInfo(reason, null);
        save(conn, accountId, banInfo);
    }

    /**
     * Temporarily bans an account for a specific duration in minutes.
     *
     * @param conn            the active SQL connection
     * @param accountId       the ID of the account to ban
     * @param reason          the reason for the temporary ban
     * @param durationMinutes the duration of the temporary ban in minutes
     * @throws SQLException if a database error occurs
     */
    public static void tempBan(Connection conn, int accountId, String reason, long durationMinutes) throws SQLException {
        BanInfo banInfo = new BanInfo(null, null);
        banInfo.setTempBan(reason, durationMinutes);
        save(conn, accountId, banInfo);
    }

    /**
     * Lifts any ban on the account.
     *
     * Deletes the row from the `account.bans` table if it exists.
     *
     * @param conn      the active SQL connection
     * @param accountId the ID of the account to unban
     * @throws SQLException if a database error occurs
     */
    public static void liftBan(Connection conn, int accountId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM account.bans WHERE account_id = ?")) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        }
    }

    /**
     * Alias for liftBan; removes any ban on the account.
     *
     * @param conn      the active SQL connection
     * @param accountId the ID of the account to unban
     * @throws SQLException if a database error occurs
     */
    public static void unBan(Connection conn, int accountId) throws SQLException {
        liftBan(conn, accountId);
    }
}
