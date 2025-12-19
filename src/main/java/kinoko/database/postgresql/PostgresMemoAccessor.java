package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.MemoAccessor;
import kinoko.database.postgresql.type.MemoDao;
import kinoko.server.memo.Memo;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public final class PostgresMemoAccessor extends PostgresAccessor implements MemoAccessor {

    public PostgresMemoAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Retrieves all memos for a given character ID.
     *
     * @param characterId the ID of the character
     * @return list of memos for the character
     */
    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        try (Connection conn = getConnection()) {
            return MemoDao.getMemosByReceiverId(conn, characterId);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList(); // fallback
        }
    }

    /**
     * Checks if a character has any memos.
     *
     * @param characterId the ID of the character
     * @return true if the character has at least one memo, false otherwise
     */
    @Override
    public boolean hasMemo(int characterId) {
        try (Connection conn = getConnection()) {
            return MemoDao.hasMemo(conn, characterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // fallback
        }
    }

    /**
     * Creates a new memo for the given receiver.
     *
     * @param memo       the memo to be created
     * @param receiverId the ID of the receiver
     * @return true if the memo was successfully created, false otherwise
     */
    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        return withTransaction(conn -> {
            return MemoDao.insertMemo(conn, memo, receiverId);
        });
    }

    /**
     * Deletes a memo by its ID and receiver ID.
     *
     * @param memoId     the ID of the memo
     * @param receiverId the ID of the receiver
     * @return true if the memo was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        return withTransaction(conn -> {
            return MemoDao.deleteMemo(conn, memoId, receiverId);
        });
    }
}
