package kinoko.database.sqlite;

import kinoko.database.MemoAccessor;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static kinoko.database.schema.MemoSchema.*;

public final class SqliteMemoAccessor extends SqliteAccessor implements MemoAccessor {
    private static final String tableName = "memo_table";

    public SqliteMemoAccessor(Connection connection) {
        super(connection);
    }

    private Memo loadMemo(ResultSet rs) throws SQLException {
        final MemoType memoType = MemoType.getByValue(rs.getInt(MEMO_TYPE));
        return new Memo(
                memoType != null ? memoType : MemoType.DEFAULT,
                rs.getInt(MEMO_ID),
                rs.getString(SENDER_NAME),
                rs.getString(MEMO_CONTENT),
                getInstant(rs, DATE_SENT)
        );
    }

    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        final List<Memo> memos = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName +
                        " WHERE " + RECEIVER_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    memos.add(loadMemo(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memos;
    }

    @Override
    public boolean hasMemo(int characterId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT 1 FROM " + tableName +
                        " WHERE " + RECEIVER_ID + " = ? LIMIT 1"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        MEMO_ID + ", " +
                        RECEIVER_ID + ", " +
                        MEMO_TYPE + ", " +
                        MEMO_CONTENT + ", " +
                        SENDER_NAME + ", " +
                        DATE_SENT + ") VALUES (?, ?, ?, ?, ?, ?)"
        )) {
            ps.setInt(1, memo.getMemoId());
            ps.setInt(2, receiverId);
            ps.setInt(3, memo.getType().getValue());
            ps.setString(4, memo.getContent());
            ps.setString(5, memo.getSender());
            setInstant(ps, 6, memo.getDateSent());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM " + tableName +
                        " WHERE " + MEMO_ID + " = ? AND " + RECEIVER_ID + " = ?"
        )) {
            ps.setInt(1, memoId);
            ps.setInt(2, receiverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            MEMO_ID + " INTEGER PRIMARY KEY, " +
                            RECEIVER_ID + " INTEGER NOT NULL, " +
                            MEMO_TYPE + " INTEGER NOT NULL, " +
                            MEMO_CONTENT + " TEXT NOT NULL, " +
                            SENDER_NAME + " TEXT NOT NULL, " +
                            DATE_SENT + " " + INSTANT_TYPE + ")"
            );

            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_memo_receiver_id ON " +
                            tableName + "(" + RECEIVER_ID + ")"
            );
        }
    }
}
