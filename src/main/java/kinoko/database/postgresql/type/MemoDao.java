package kinoko.database.postgresql.type;

import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public final class MemoDao {
    /**
     * Inserts a new memo for a specific receiver.
     *
     * @param conn       active SQL connection
     * @param memo       the memo object containing content and sender info
     * @param receiverId the ID of the character receiving the memo
     * @throws SQLException if any SQL error occurs
     */
    public static boolean insertMemo(Connection conn, Memo memo, int receiverId) throws SQLException {
        String sql = """
            INSERT INTO memo.memo (receiver_id, memo_type, memo_content, sender_name, date_sent)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, memo.getType().getValue());
            stmt.setString(3, memo.getContent());
            stmt.setString(4, memo.getSender());
            stmt.setTimestamp(5, memo.getDateSent() != null ? Timestamp.from(memo.getDateSent()) : Timestamp.from(java.time.Instant.now()));
            stmt.executeUpdate();
        }
        return true;
    }


    /**
     * Retrieves all memos for a given receiver ID.
     *
     * @param conn       active SQL connection
     * @param receiverId the character ID to fetch memos for
     * @return list of memos belonging to the receiver
     * @throws SQLException if any SQL error occurs
     */
    public static List<Memo> getMemosByReceiverId(Connection conn, int receiverId) throws SQLException {
        List<Memo> memos = new ArrayList<>();
        String sql = """
            SELECT id, memo_type, memo_content, sender_name, date_sent
            FROM memo.memo
            WHERE receiver_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MemoType type = MemoType.getByValue(rs.getInt("memo_type"));
                    Memo memo = new Memo(
                            type != null ? type : MemoType.DEFAULT,
                            rs.getInt("id"),
                            rs.getString("sender_name"),
                            rs.getString("memo_content"),
                            rs.getTimestamp("date_sent").toInstant()
                    );
                    memos.add(memo);
                }
            }
        }
        return memos;
    }

    /**
     * Deletes a memo by its ID and receiver ID.
     *
     * @param conn       active SQL connection
     * @param memoId     the memo ID to delete
     * @param receiverId the receiver ID to verify ownership
     * @return true if the memo was deleted; false otherwise
     * @throws SQLException if any SQL error occurs
     */
    public static boolean deleteMemo(Connection conn, int memoId, int receiverId) throws SQLException {
        String sql = "DELETE FROM memo.memo WHERE id = ? AND receiver_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memoId);
            stmt.setInt(2, receiverId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Checks whether a receiver has at least one memo.
     *
     * @param conn       active SQL connection
     * @param receiverId the receiver ID to check
     * @return true if at least one memo exists for the receiver; false otherwise
     * @throws SQLException if any SQL error occurs
     */
    public static boolean hasMemo(Connection conn, int receiverId) throws SQLException {
        String sql = "SELECT 1 FROM memo.memo WHERE receiver_id = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
