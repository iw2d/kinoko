package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.GiftAccessor;
import kinoko.server.cashshop.Gift;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PostgresGiftAccessor implements GiftAccessor {
    private final HikariDataSource dataSource;

    public PostgresGiftAccessor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Gift loadGift(ResultSet rs) throws SQLException {
        return new Gift(
                rs.getLong("gift_sn"),
                rs.getInt("item_id"),
                rs.getInt("commodity_id"),
                rs.getInt("sender_id"),
                rs.getString("sender_name"),
                rs.getString("sender_message"),
                rs.getLong("pair_item_sn")
        );
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        List<Gift> gifts = new ArrayList<>();
        String sql = "SELECT * FROM gifts WHERE receiver_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gifts.add(loadGift(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gifts;
    }

    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        String sql = "SELECT * FROM gifts WHERE gift_sn = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, itemSn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(loadGift(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        String sql = "INSERT INTO gifts (gift_sn, receiver_id, item_id, commodity_id, sender_id, sender_name, sender_message, pair_item_sn) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (gift_sn) DO NOTHING";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gift.getGiftSn());
            stmt.setInt(2, receiverId);
            stmt.setInt(3, gift.getItemId());
            stmt.setInt(4, gift.getCommodityId());
            stmt.setInt(5, gift.getSenderId());
            stmt.setString(6, gift.getSenderName());
            stmt.setString(7, gift.getSenderMessage());
            stmt.setLong(8, gift.getPairItemSn());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteGift(Gift gift) {
        String sql = "DELETE FROM gifts WHERE gift_sn = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, gift.getGiftSn());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
