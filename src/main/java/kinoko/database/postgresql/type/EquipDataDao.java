package kinoko.database.postgresql.type;

import kinoko.world.item.EquipData;
import kinoko.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class EquipDataDao {
    /**
     * Inserts or updates (upserts) an EquipData record into the item.equip_data table.
     * <p>
     * If the given item_sn already exists, the existing record will be updated
     * with the latest equip data values. Otherwise, a new record will be inserted.
     *
     * @param conn      the active SQL connection
     * @param itemSn    the unique item_sn associated with the equip
     * @param equipData the EquipData instance containing the stats to insert or update
     * @throws SQLException if any SQL error occurs
     */
    public static void upsertEquipData(Connection conn, long itemSn, EquipData equipData) throws SQLException {
        if (equipData == null) return;

        String sql = """
                INSERT INTO item.equip_data (
                    item_sn, inc_str, inc_dex, inc_int, inc_luk, inc_max_hp, inc_max_mp,
                    inc_pad, inc_mad, inc_pdd, inc_mdd, inc_acc, inc_eva, inc_craft,
                    inc_speed, inc_jump, ruc, cuc, iuc, chuc, grade,
                    option_1, option_2, option_3, socket_1, socket_2,
                    level_up_type, level, exp, durability
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (item_sn) DO UPDATE SET
                    inc_str = EXCLUDED.inc_str,
                    inc_dex = EXCLUDED.inc_dex,
                    inc_int = EXCLUDED.inc_int,
                    inc_luk = EXCLUDED.inc_luk,
                    inc_max_hp = EXCLUDED.inc_max_hp,
                    inc_max_mp = EXCLUDED.inc_max_mp,
                    inc_pad = EXCLUDED.inc_pad,
                    inc_mad = EXCLUDED.inc_mad,
                    inc_pdd = EXCLUDED.inc_pdd,
                    inc_mdd = EXCLUDED.inc_mdd,
                    inc_acc = EXCLUDED.inc_acc,
                    inc_eva = EXCLUDED.inc_eva,
                    inc_craft = EXCLUDED.inc_craft,
                    inc_speed = EXCLUDED.inc_speed,
                    inc_jump = EXCLUDED.inc_jump,
                    ruc = EXCLUDED.ruc,
                    cuc = EXCLUDED.cuc,
                    iuc = EXCLUDED.iuc,
                    chuc = EXCLUDED.chuc,
                    grade = EXCLUDED.grade,
                    option_1 = EXCLUDED.option_1,
                    option_2 = EXCLUDED.option_2,
                    option_3 = EXCLUDED.option_3,
                    socket_1 = EXCLUDED.socket_1,
                    socket_2 = EXCLUDED.socket_2,
                    level_up_type = EXCLUDED.level_up_type,
                    level = EXCLUDED.level,
                    exp = EXCLUDED.exp,
                    durability = EXCLUDED.durability
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idx = 1;
            stmt.setLong(idx++, itemSn);
            stmt.setShort(idx++, equipData.getIncStr());
            stmt.setShort(idx++, equipData.getIncDex());
            stmt.setShort(idx++, equipData.getIncInt());
            stmt.setShort(idx++, equipData.getIncLuk());
            stmt.setShort(idx++, equipData.getIncMaxHp());
            stmt.setShort(idx++, equipData.getIncMaxMp());
            stmt.setShort(idx++, equipData.getIncPad());
            stmt.setShort(idx++, equipData.getIncMad());
            stmt.setShort(idx++, equipData.getIncPdd());
            stmt.setShort(idx++, equipData.getIncMdd());
            stmt.setShort(idx++, equipData.getIncAcc());
            stmt.setShort(idx++, equipData.getIncEva());
            stmt.setShort(idx++, equipData.getIncCraft());
            stmt.setShort(idx++, equipData.getIncSpeed());
            stmt.setShort(idx++, equipData.getIncJump());
            stmt.setByte(idx++, equipData.getRuc());
            stmt.setByte(idx++, equipData.getCuc());
            stmt.setInt(idx++, equipData.getIuc());
            stmt.setByte(idx++, equipData.getChuc());
            stmt.setByte(idx++, equipData.getGrade());
            stmt.setShort(idx++, equipData.getOption1());
            stmt.setShort(idx++, equipData.getOption2());
            stmt.setShort(idx++, equipData.getOption3());
            stmt.setShort(idx++, equipData.getSocket1());
            stmt.setShort(idx++, equipData.getSocket2());
            stmt.setByte(idx++, equipData.getLevelUpType());
            stmt.setByte(idx++, equipData.getLevel());
            stmt.setInt(idx++, equipData.getExp());
            stmt.setInt(idx, equipData.getDurability());

            stmt.executeUpdate();
        }
    }

    /**
     * Batch upserts equip data for multiple items.
     * <p>
     * For each item, if the item has EquipData, it will be inserted or updated.
     * Existing equip_data rows are updated; missing ones are inserted.
     * Uses a single PreparedStatement batch for efficiency.
     *
     * @param conn  active SQL connection
     * @param items collection of items that may contain equip data
     * @throws SQLException if any SQL error occurs
     */
    public static void saveEquipDataBatch(Connection conn, Collection<Item> items) throws SQLException {
        if (items == null || items.isEmpty()) return;

        String sql = """
                INSERT INTO item.equip_data (
                    item_sn, inc_str, inc_dex, inc_int, inc_luk, inc_max_hp, inc_max_mp,
                    inc_pad, inc_mad, inc_pdd, inc_mdd, inc_acc, inc_eva, inc_craft,
                    inc_speed, inc_jump, ruc, cuc, iuc, chuc, grade,
                    option_1, option_2, option_3, socket_1, socket_2,
                    level_up_type, level, exp, durability
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (item_sn) DO UPDATE SET
                    inc_str = EXCLUDED.inc_str,
                    inc_dex = EXCLUDED.inc_dex,
                    inc_int = EXCLUDED.inc_int,
                    inc_luk = EXCLUDED.inc_luk,
                    inc_max_hp = EXCLUDED.inc_max_hp,
                    inc_max_mp = EXCLUDED.inc_max_mp,
                    inc_pad = EXCLUDED.inc_pad,
                    inc_mad = EXCLUDED.inc_mad,
                    inc_pdd = EXCLUDED.inc_pdd,
                    inc_mdd = EXCLUDED.inc_mdd,
                    inc_acc = EXCLUDED.inc_acc,
                    inc_eva = EXCLUDED.inc_eva,
                    inc_craft = EXCLUDED.inc_craft,
                    inc_speed = EXCLUDED.inc_speed,
                    inc_jump = EXCLUDED.inc_jump,
                    ruc = EXCLUDED.ruc,
                    cuc = EXCLUDED.cuc,
                    iuc = EXCLUDED.iuc,
                    chuc = EXCLUDED.chuc,
                    grade = EXCLUDED.grade,
                    option_1 = EXCLUDED.option_1,
                    option_2 = EXCLUDED.option_2,
                    option_3 = EXCLUDED.option_3,
                    socket_1 = EXCLUDED.socket_1,
                    socket_2 = EXCLUDED.socket_2,
                    level_up_type = EXCLUDED.level_up_type,
                    level = EXCLUDED.level,
                    exp = EXCLUDED.exp,
                    durability = EXCLUDED.durability
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Item item : items) {
                EquipData e = item.getEquipData();
                if (e == null) continue;

                int idx = 1;
                stmt.setLong(idx++, item.getItemSn());
                stmt.setShort(idx++, e.getIncStr());
                stmt.setShort(idx++, e.getIncDex());
                stmt.setShort(idx++, e.getIncInt());
                stmt.setShort(idx++, e.getIncLuk());
                stmt.setShort(idx++, e.getIncMaxHp());
                stmt.setShort(idx++, e.getIncMaxMp());
                stmt.setShort(idx++, e.getIncPad());
                stmt.setShort(idx++, e.getIncMad());
                stmt.setShort(idx++, e.getIncPdd());
                stmt.setShort(idx++, e.getIncMdd());
                stmt.setShort(idx++, e.getIncAcc());
                stmt.setShort(idx++, e.getIncEva());
                stmt.setShort(idx++, e.getIncCraft());
                stmt.setShort(idx++, e.getIncSpeed());
                stmt.setShort(idx++, e.getIncJump());
                stmt.setByte(idx++, e.getRuc());
                stmt.setByte(idx++, e.getCuc());
                stmt.setInt(idx++, e.getIuc());
                stmt.setByte(idx++, e.getChuc());
                stmt.setByte(idx++, e.getGrade());
                stmt.setShort(idx++, e.getOption1());
                stmt.setShort(idx++, e.getOption2());
                stmt.setShort(idx++, e.getOption3());
                stmt.setShort(idx++, e.getSocket1());
                stmt.setShort(idx++, e.getSocket2());
                stmt.setByte(idx++, e.getLevelUpType());
                stmt.setByte(idx++, e.getLevel());
                stmt.setInt(idx++, e.getExp());
                stmt.setInt(idx, e.getDurability());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}