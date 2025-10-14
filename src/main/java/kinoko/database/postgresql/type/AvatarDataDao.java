package kinoko.database.postgresql.type;

import kinoko.world.item.Inventory;
import kinoko.world.user.AvatarData;
import kinoko.world.user.stat.CharacterStat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class AvatarDataDao {

    /**
     * Retrieves all AvatarData for characters belonging to a given account.
     *
     * This includes character stats and equipped inventory.
     *
     * @param conn      the database connection to use
     * @param accountId the ID of the account
     * @return a list of AvatarData for each character under the account
     * @throws SQLException if a database access error occurs
     */
    public static List<AvatarData> getAvatarDataByAccountId(Connection conn, int accountId) throws SQLException {
        List<AvatarData> list = new ArrayList<>();
        String sql = """
            SELECT c.id AS character_id,
                   c.name AS character_name,
                   c.money,
                   s.gender,
                   s.skin,
                   s.face,
                   s.hair,
                   s.level,
                   s.job,
                   s.sub_job,
                   s.base_str,
                   s.base_dex,
                   s.base_int,
                   s.base_luk,
                   s.hp,
                   s.max_hp,
                   s.mp,
                   s.max_mp,
                   s.ap,
                   s.exp,
                   s.pop,
                   s.pos_map,
                   s.portal,
                   s.pet_1,
                   s.pet_2,
                   s.pet_3
            FROM player.characters c
            JOIN player.stats s ON c.id = s.character_id
            WHERE c.account_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CharacterStat cs = new CharacterStat(
                            rs.getInt("character_id"),
                            rs.getString("character_name"),
                            rs.getByte("gender"),
                            rs.getByte("skin"),
                            rs.getInt("face"),
                            rs.getInt("hair"),
                            rs.getShort("level"),
                            rs.getShort("job"),
                            rs.getShort("sub_job"),
                            rs.getShort("base_str"),
                            rs.getShort("base_dex"),
                            rs.getShort("base_int"),
                            rs.getShort("base_luk"),
                            rs.getInt("hp"),
                            rs.getInt("max_hp"),
                            rs.getInt("mp"),
                            rs.getInt("max_mp"),
                            rs.getShort("ap"),
                            rs.getInt("exp"),
                            rs.getShort("pop"),
                            rs.getInt("pos_map"),
                            rs.getByte("portal"),
                            rs.getLong("pet_1"),
                            rs.getLong("pet_2"),
                            rs.getLong("pet_3")
                    );

                    Inventory equipped = InventoryDao.loadEquippedInventory(conn, cs.getId());

                    list.add(AvatarData.from(cs, equipped));
                }
            }
        }

        return list;
    }
}
