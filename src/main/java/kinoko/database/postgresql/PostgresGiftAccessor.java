package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.GiftAccessor;
import kinoko.database.postgresql.type.ItemDao;
import kinoko.server.cashshop.Gift;
import kinoko.world.item.Item;

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
                rs.getLong("item_sn"),
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
        String sql = """
        SELECT g.item_sn, fi.item_id, g.commodity_id, g.sender_id,
            g.sender_name, g.sender_message, fi.pair_item_sn 
        FROM gift.gifts g
        JOIN item.full_item fi ON fi.item_sn = g.item_sn
        WHERE g.receiver_id = ?
        """;
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
        String sql = """
        SELECT g.item_sn, fi.item_id, g.commodity_id, g.sender_id,
            g.sender_name, g.sender_message, fi.pair_item_sn FROM gift.gifts g
        JOIN item.full_item fi ON fi.item_sn = g.item_sn
        WHERE g.item_sn = ?
        """;

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
        String sql = """
        INSERT INTO gift.gifts (item_sn, receiver_id, commodity_id, sender_id, sender_name, sender_message)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (item_sn) DO NOTHING
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // We need a new item created.
            Item basicItem = new Item(gift.getItemId(), (short) 1);
            ItemDao.createNewItem(conn, basicItem);

            stmt.setLong(1, basicItem.getItemSn());          // item_sn is now the primary key
            stmt.setInt(2, receiverId);
            stmt.setInt(3, gift.getCommodityId());
            stmt.setInt(4, gift.getSenderId());
            stmt.setString(5, gift.getSenderName());
            stmt.setString(6, gift.getSenderMessage());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteGift(Gift gift) {
        String sql = "DELETE FROM gift.gifts WHERE item_sn = ?";
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
