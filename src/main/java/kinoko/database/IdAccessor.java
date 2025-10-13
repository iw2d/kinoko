package kinoko.database;

import kinoko.world.item.Item;

import java.util.Optional;

public interface IdAccessor {
    Optional<Integer> nextAccountId();

    Optional<Integer> nextCharacterId();

    Optional<Integer> nextPartyId();

    Optional<Integer> nextGuildId();

    Optional<Integer> nextMemoId();

    default public boolean generateItemSn(Item item){
        if (DatabaseManager.isRelational()){
            throw new UnsupportedOperationException("generateItemSn() needs to be implemented for this database.");
        }
        return true;
    }
}
