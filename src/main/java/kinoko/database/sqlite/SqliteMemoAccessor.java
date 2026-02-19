package kinoko.database.sqlite;

import kinoko.database.MemoAccessor;
import kinoko.server.memo.Memo;

import java.sql.Connection;
import java.util.List;

public final class SqliteMemoAccessor extends SqliteAccessor implements MemoAccessor {
    public SqliteMemoAccessor(Connection connection) {
        super(connection);
    }

    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        return List.of();
    }

    @Override
    public boolean hasMemo(int characterId) {
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        return false;
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        return false;
    }

    public static void createTable(Connection connection) {
        // TODO
    }
}
