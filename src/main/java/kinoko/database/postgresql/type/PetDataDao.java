package kinoko.database.postgresql.type;

import kinoko.world.item.PetData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class PetDataDao {
    /**
     * Inserts or updates PetData for a given item_sn.
     * If an entry already exists, it will be updated instead.
     */
    public static void upsertPetData(Connection conn, long itemSn, PetData petData) throws SQLException {
        if (petData == null) {
            return;
        }

        String sql = """
            INSERT INTO item.pet_data (
                item_sn, pet_name, level, fullness, tameness, pet_skill, pet_attribute, remain_life
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (item_sn)
            DO UPDATE SET
                pet_name = EXCLUDED.pet_name,
                level = EXCLUDED.level,
                fullness = EXCLUDED.fullness,
                tameness = EXCLUDED.tameness,
                pet_skill = EXCLUDED.pet_skill,
                pet_attribute = EXCLUDED.pet_attribute,
                remain_life = EXCLUDED.remain_life
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, itemSn);
            stmt.setString(2, petData.getPetName());
            stmt.setByte(3, petData.getLevel());
            stmt.setByte(4, petData.getFullness());
            stmt.setShort(5, petData.getTameness());
            stmt.setShort(6, petData.getPetSkill());
            stmt.setShort(7, petData.getPetAttribute());
            stmt.setInt(8, petData.getRemainLife());
            stmt.executeUpdate();
        }
    }
}
