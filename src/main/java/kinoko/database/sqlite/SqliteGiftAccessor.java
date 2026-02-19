package kinoko.database.sqlite;

import kinoko.database.GiftAccessor;
import kinoko.server.cashshop.Gift;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kinoko.database.schema.GiftSchema.*;

public final class SqliteGiftAccessor extends SqliteAccessor implements GiftAccessor {
    private static final String tableName = "gift_table";

    public SqliteGiftAccessor(Connection connection) {
        super(connection);
    }

    public Gift loadGift(ResultSet rs) throws SQLException {
        return new Gift(
                rs.getLong(GIFT_SN),
                rs.getInt(ITEM_ID),
                rs.getInt(COMMODITY_ID),
                rs.getInt(SENDER_ID),
                rs.getString(SENDER_NAME),
                rs.getString(SENDER_MESSAGE),
                rs.getLong(PAIR_ITEM_SN)
        );
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        final List<Gift> gifts = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName +
                        " WHERE " + RECEIVER_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    gifts.add(loadGift(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gifts;
    }

    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName +
                        " WHERE " + GIFT_SN + " = ?"
        )) {
            ps.setLong(1, itemSn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadGift(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        GIFT_SN + ", " +
                        RECEIVER_ID + ", " +
                        ITEM_ID + ", " +
                        COMMODITY_ID + ", " +
                        SENDER_ID + ", " +
                        SENDER_NAME + ", " +
                        SENDER_MESSAGE + ", " +
                        PAIR_ITEM_SN + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            ps.setLong(1, gift.getGiftSn());
            ps.setInt(2, receiverId);
            ps.setInt(3, gift.getItemId());
            ps.setInt(4, gift.getCommodityId());
            ps.setInt(5, gift.getSenderId());
            ps.setString(6, gift.getSenderName());
            ps.setString(7, gift.getSenderMessage());
            ps.setLong(8, gift.getPairItemSn());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteGift(Gift gift) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM " + tableName +
                        " WHERE " + GIFT_SN + " = ?"
        )) {
            ps.setLong(1, gift.getGiftSn());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            GIFT_SN + " BIGINT PRIMARY KEY, " +
                            RECEIVER_ID + " INTEGER NOT NULL, " +
                            ITEM_ID + " INTEGER NOT NULL, " +
                            COMMODITY_ID + " INTEGER NOT NULL, " +
                            SENDER_ID + " INTEGER NOT NULL, " +
                            SENDER_NAME + " TEXT NOT NULL, " +
                            SENDER_MESSAGE + " TEXT NOT NULL, " +
                            PAIR_ITEM_SN + " BIGINT NOT NULL)"
            );

            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_gift_receiver_id ON " +
                            tableName + "(" + RECEIVER_ID + ")"
            );
        }
    }
}
