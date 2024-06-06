package kinoko.database;

import kinoko.world.social.memo.Memo;

import java.util.List;
import java.util.Optional;

public interface MemoAccessor {
    Optional<Integer> nextMemoId();

    List<Memo> getMemosByCharacterId(int characterId);

    boolean hasMemo(int characterId);

    boolean newMemo(Memo memo, int receiverId);

    boolean deleteMemo(int memoId, int receiverId);
}
