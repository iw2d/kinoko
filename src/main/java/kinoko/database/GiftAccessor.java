package kinoko.database;

import kinoko.server.cashshop.Gift;

import java.util.List;

public interface GiftAccessor {
    List<Gift> getGiftsByCharacterId(int characterId);

    boolean newGift(Gift gift, int receiverId);

    boolean deleteGift(Gift gift);
}
