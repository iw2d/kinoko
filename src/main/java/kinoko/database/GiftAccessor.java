package kinoko.database;

import kinoko.server.cashshop.Gift;

import java.util.List;
import java.util.Optional;

public interface GiftAccessor {
    List<Gift> getGiftsByCharacterId(int characterId);

    Optional<Gift> getGiftByItemSn(long itemSn);

    boolean newGift(Gift gift, int receiverId);

    boolean deleteGift(Gift gift);
}
