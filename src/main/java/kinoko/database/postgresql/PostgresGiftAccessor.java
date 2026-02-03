package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.GiftAccessor;
import kinoko.database.postgresql.type.GiftDao;
import kinoko.server.cashshop.Gift;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class PostgresGiftAccessor extends PostgresAccessor implements GiftAccessor {
    public PostgresGiftAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }


    /**
     * Retrieves all gifts received by a specific character.
     *
     * @param characterId the ID of the character
     * @return a list of gifts for the given character, or an empty list if none exist or an error occurs
     */
    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        try (Connection conn = getConnection()) {
            return GiftDao.getGiftsByReceiverId(conn, characterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves a gift by its item serial number.
     *
     * @param itemSn the item serial number of the gift
     * @return an Optional containing the gift if found, or Optional.empty() if not found or an error occurs
     */
    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        try (Connection conn = getConnection()) {
            return GiftDao.getGiftByItemSn(conn, itemSn);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Creates a new gift for a specified receiver.
     * If the gift requires a new item to be created, it will be handled within the same transaction.
     *
     * @param gift the gift to be created
     * @param receiverId the ID of the receiver
     * @return true if the gift was successfully created, false otherwise
     */
    @Override
    public boolean newGift(Gift gift, int receiverId) {
        return withTransaction(conn -> {
            return GiftDao.insertGift(conn, gift, receiverId);
        });
    }

    /**
     * Deletes a specific gift from the database.
     *
     * @param gift the gift to delete
     * @return true if the gift was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteGift(Gift gift) {
        return withTransaction(conn -> {
            return GiftDao.deleteGift(conn, gift);
        });
    }
}
