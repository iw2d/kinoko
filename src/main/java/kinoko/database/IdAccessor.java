package kinoko.database;

import java.util.Optional;

public interface IdAccessor {
    Optional<Integer> nextAccountId();

    Optional<Integer> nextCharacterId();

    Optional<Integer> nextPartyId();

    Optional<Integer> nextGuildId();

    Optional<Integer> nextAllianceId();

    Optional<Integer> nextMemoId();
}
