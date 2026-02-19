package kinoko.database.sqlite;

import kinoko.database.GiftAccessor;
import kinoko.server.cashshop.Gift;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public final class SqliteGiftAccessor extends SqliteAccessor implements GiftAccessor {
    public SqliteGiftAccessor(Connection connection) {
        super(connection);
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        return List.of();
    }

    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        return Optional.empty();
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        return false;
    }

    @Override
    public boolean deleteGift(Gift gift) {
        return false;
    }

    public static void createTable(Connection connection) {
        // TODO
    }
}
