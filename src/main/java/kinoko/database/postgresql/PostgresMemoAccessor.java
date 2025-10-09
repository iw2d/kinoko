package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.MemoAccessor;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class PostgresMemoAccessor extends PostgresAccessor implements MemoAccessor {

    public PostgresMemoAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        List<Memo> memos = new ArrayList<>();
        String sql = "SELECT id, memo_type, memo_content, sender_name, date_sent " +
                "FROM memo.memo WHERE receiver_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, characterId);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memos;
    }

    @Override
    public boolean hasMemo(int characterId) {
        String sql = "SELECT 1 FROM memo.memo WHERE receiver_id = ? LIMIT 1";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        // `id` is SERIAL, no need to provide it manually
        String sql = "INSERT INTO memo.memo (receiver_id, memo_type, memo_content, sender_name, date_sent) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, memo.getType().getValue());
            stmt.setString(3, memo.getContent());
            stmt.setString(4, memo.getSender());
            stmt.setTimestamp(5, memo.getDateSent() != null ? Timestamp.from(memo.getDateSent()) : Timestamp.from(java.time.Instant.now()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        String sql = "DELETE FROM memo.memo WHERE id = ? AND receiver_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memoId);
            stmt.setInt(2, receiverId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
