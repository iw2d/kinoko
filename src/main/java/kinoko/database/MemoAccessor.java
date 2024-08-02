package kinoko.database;

import kinoko.server.memo.Memo;

import java.util.List;

public interface MemoAccessor {
    List<Memo> getMemosByCharacterId(int characterId);

    boolean hasMemo(int characterId);

    boolean newMemo(Memo memo, int receiverId);

    boolean deleteMemo(int memoId, int receiverId);
}
