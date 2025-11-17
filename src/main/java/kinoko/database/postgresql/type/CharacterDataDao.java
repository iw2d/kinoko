package kinoko.database.postgresql.type;

import kinoko.world.item.InventoryManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.quest.QuestManager;
import kinoko.world.user.CharacterData;
import kinoko.world.user.data.*;
import kinoko.world.user.stat.AdminLevel;
import kinoko.world.user.stat.CharacterStat;

import java.sql.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CharacterDataDao {
    /**
     * Constructs and loads a CharacterData object from the given ResultSet and database connection.
     *
     * This method initializes a CharacterData instance for a specific character, populates its
     * CharacterStat, InventoryManager, SkillManager, QuestManager, ConfigManager, PopularityRecord,
     * MiniGameRecord, MapTransferInfo, WildHunterInfo, and CoupleRecord. It also sets the item SN
     * counter, friend limit, party ID, guild ID, and timestamps for creation and max level.
     *
     * All required additional data is loaded via DAOs or helper methods using the provided Connection.
     *
     * @param conn the database connection to use for loading related character data
     * @param rs the ResultSet containing the character row data
     * @return a fully populated CharacterData object
     * @throws SQLException if a database access error occurs
     */
    public static CharacterData loadCharacterData(Connection conn, ResultSet rs) throws SQLException {
        int accountId = rs.getInt("account_id");
        CharacterData cd = new CharacterData(accountId);
        int characterID = rs.getInt("id");

        CharacterStat cs = new CharacterStat(
                characterID,
                rs.getString("name"),
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
                rs.getLong("pet_3"),
                AdminLevel.fromValue(rs.getShort("admin_level"))
        );
        cd.setCharacterStat(cs);

        cs.setSp(ExtendSpDao.loadExtendSp(conn, characterID));

        InventoryManager im = InventoryDao.loadInventoryManager(conn, characterID);
        im.setMoney(rs.getInt("money"));

        Timestamp extSlotExpireTs = rs.getTimestamp("ext_slot_expire");
        im.setExtSlotExpire(extSlotExpireTs != null ? extSlotExpireTs.toInstant() : null);

        cd.setInventoryManager(im);
        cd.setCoupleRecord(CoupleRecord.from(im.getEquipped(), im.getEquipInventory()));

        SkillManager sm = SkillManagerDao.loadSkillCooltimesAndRecords(conn, characterID);
        cd.setSkillManager(sm);

        QuestManager qm = QuestManagerDao.loadQuestRecords(conn, characterID);
        cd.setQuestManager(qm);

        ConfigManager cm = ConfigManagerDao.loadConfig(conn, characterID);
        cd.setConfigManager(cm);

        PopularityRecord pr = PopularityRecordDao.loadPopularityRecord(conn, characterID);
        cd.setPopularityRecord(pr);

        MiniGameRecord mgr = MiniGameRecordDao.loadMiniGameRecord(conn, characterID);
        cd.setMiniGameRecord(mgr);

        MapTransferInfo mto = MapTransferInfoDao.loadMapTransferInfo(conn, characterID);
        cd.setMapTransferInfo(mto);

        WildHunterInfo whi = WildHunterInfoDao.loadWildHunterInfo(conn, characterID);
        cd.setWildHunterInfo(whi);

        cd.setItemSnCounter(new AtomicInteger(-1));

        cd.setFriendMax(rs.getInt("friend_max"));
        cd.setPartyId(rs.getInt("party_id"));
        cd.setGuildId(rs.getInt("guild_id"));

        Timestamp creationTs = rs.getTimestamp("creation_time");
        cd.setCreationTime(creationTs != null ? creationTs.toInstant() : null);

        Timestamp maxLevelTs = rs.getTimestamp("max_level_time");
        cd.setMaxLevelTime(maxLevelTs != null ? maxLevelTs.toInstant() : null);

        return cd;
    }

    /**
     * Retrieves a CharacterData object for the given character ID.
     *
     * This method fetches the character's basic data, stats, and guild information,
     * then delegates loading of inventory, skills, quests, configs, popularity,
     * mini-games, map transfer, and wild hunter info to the appropriate DAOs.
     *
     * @param conn the database connection to use
     * @param characterId the ID of the character
     * @return an Optional containing the CharacterData if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    public static Optional<CharacterData> getCharacterById(Connection conn, int characterId) throws SQLException {
        String sql = """
            SELECT c.*, s.*, m.guild_id, m.grade
            FROM player.characters c
            LEFT JOIN player.stats s ON c.id = s.character_id
            LEFT JOIN guild.member m ON m.character_id = c.id
            WHERE c.id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadCharacterData(conn, rs));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Retrieves a CharacterData object by character name (case-insensitive).
     *
     * This method queries the characters table using ILIKE for case-insensitive matching,
     * then delegates to loadCharacterData to populate all associated managers and records.
     *
     * @param conn the database connection to use
     * @param name the name of the character
     * @return an Optional containing the CharacterData if found, otherwise empty
     * @throws SQLException if a database access error occurs
     */
    public static Optional<CharacterData> getCharacterByName(Connection conn, String name) throws SQLException {
        String sql = """
            SELECT c.*, s.*, m.guild_id, m.grade
            FROM player.characters c
            LEFT JOIN player.stats s ON c.id = s.character_id
            LEFT JOIN guild.member m ON m.character_id = c.id
            WHERE c.name ILIKE ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadCharacterData(conn, rs));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Creates a new character in the database along with all dependent data.
     *
     * This includes stats, inventory, skills, quests, config, popularity,
     * and extended SP. The operation is performed within a single transaction.
     *
     * @param conn          the database connection
     * @param characterData the CharacterData to insert
     * @return true if the character was successfully created, false otherwise
     * @throws SQLException if a database error occurs
     */
    public static boolean newCharacter(Connection conn, CharacterData characterData) throws SQLException {
        if (!checkCharacterNameAvailable(conn, characterData.getCharacterName())) return false;

        String sql = """
            INSERT INTO player.characters 
            (account_id, name, money, ext_slot_expire, friend_max, party_id, guild_id, creation_time, max_level_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterData.getAccountId());
            stmt.setString(2, characterData.getCharacterName());
            stmt.setInt(3, characterData.getInventoryManager().getMoney());
            stmt.setTimestamp(4, characterData.getInventoryManager().getExtSlotExpire() != null ?
                    Timestamp.from(characterData.getInventoryManager().getExtSlotExpire()) : null);
            stmt.setInt(5, characterData.getFriendMax());
            stmt.setInt(6, characterData.getPartyId());
            stmt.setInt(7, characterData.getGuildId());
            stmt.setTimestamp(8, characterData.getCreationTime() != null ? Timestamp.from(characterData.getCreationTime()) : null);
            stmt.setTimestamp(9, characterData.getMaxLevelTime() != null ? Timestamp.from(characterData.getMaxLevelTime()) : null);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int newCharacterId = rs.getInt(1);
                    characterData.getCharacterStat().setId(newCharacterId);
                } else {
                    throw new SQLException("Failed to insert new character");
                }
            }
        }

        // Save all dependent data using the same connection
        saveCharacterStats(conn, characterData);
        InventoryDao.saveCharacter(conn, characterData);
        SkillManagerDao.saveCharacterSkills(conn, characterData);
        QuestManagerDao.saveCharacterQuests(conn, characterData);
        ConfigManagerDao.saveCharacterConfig(conn, characterData);
        PopularityRecordDao.saveCharacterPopularity(conn, characterData);
        MapTransferInfoDao.saveMapTransferInfo(conn, characterData);
        MiniGameRecordDao.saveMiniGameRecord(conn, characterData);
        WildHunterInfoDao.saveWildHunterInfo(conn, characterData);
        ExtendSpDao.upsertExtendSp(conn, characterData.getCharacterId(), characterData.getCharacterStat().getSp());

        return true;
    }

    /**
     * Checks whether a character name is available (not already in use).
     *
     * Uses ILIKE to perform a case-insensitive check.
     *
     * @param conn the database connection
     * @param name the character name to check
     * @return true if the name is available, false if it already exists
     * @throws SQLException if a database access error occurs
     */
    public static boolean checkCharacterNameAvailable(Connection conn, String name) throws SQLException {
        String sql = "SELECT COUNT(*) > 0 AS exists FROM player.characters WHERE name ILIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name); // original name
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean("exists");
                    return !exists; // available if it does NOT exist
                }
            }
        }
        return false;
    }

    /**
     * Inserts or updates a character’s base statistics in the database.
     * Uses UPSERT logic to ensure that stats are either created or updated as needed.
     *
     * @param conn the active database connection
     * @param characterData the character whose stats should be saved
     * @throws SQLException if a database access error occurs
     */
    private static void saveCharacterStats(Connection conn, CharacterData characterData) throws SQLException {
        String sql = """
        INSERT INTO player.stats (
            character_id, gender, skin, face, hair, level, job, sub_job,
            base_str, base_dex, base_int, base_luk, hp, max_hp, mp, max_mp,
            ap, exp, pop, pos_map, portal, pet_1, pet_2, pet_3, admin_level
        ) VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
        )
        ON CONFLICT (character_id) DO UPDATE SET
            gender = EXCLUDED.gender,
            skin = EXCLUDED.skin,
            face = EXCLUDED.face,
            hair = EXCLUDED.hair,
            level = EXCLUDED.level,
            job = EXCLUDED.job,
            sub_job = EXCLUDED.sub_job,
            base_str = EXCLUDED.base_str,
            base_dex = EXCLUDED.base_dex,
            base_int = EXCLUDED.base_int,
            base_luk = EXCLUDED.base_luk,
            hp = EXCLUDED.hp,
            max_hp = EXCLUDED.max_hp,
            mp = EXCLUDED.mp,
            max_mp = EXCLUDED.max_mp,
            ap = EXCLUDED.ap,
            exp = EXCLUDED.exp,
            pop = EXCLUDED.pop,
            pos_map = EXCLUDED.pos_map,
            portal = EXCLUDED.portal,
            pet_1 = EXCLUDED.pet_1,
            pet_2 = EXCLUDED.pet_2,
            pet_3 = EXCLUDED.pet_3,
            admin_level = EXCLUDED.admin_level
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            CharacterStat cs = characterData.getCharacterStat();
            stmt.setInt(1, characterData.getCharacterId());
            stmt.setInt(2, cs.getGender());
            stmt.setInt(3, cs.getSkin());
            stmt.setInt(4, cs.getFace());
            stmt.setInt(5, cs.getHair());
            stmt.setInt(6, cs.getLevel());
            stmt.setInt(7, cs.getJob());
            stmt.setInt(8, cs.getSubJob());
            stmt.setInt(9, cs.getBaseStr());
            stmt.setInt(10, cs.getBaseDex());
            stmt.setInt(11, cs.getBaseInt());
            stmt.setInt(12, cs.getBaseLuk());
            stmt.setInt(13, cs.getHp());
            stmt.setInt(14, cs.getMaxHp());
            stmt.setInt(15, cs.getMp());
            stmt.setInt(16, cs.getMaxMp());
            stmt.setInt(17, cs.getAp());
            stmt.setInt(18, cs.getExp());
            stmt.setInt(19, cs.getPop());
            stmt.setInt(20, cs.getPosMap());
            stmt.setInt(21, cs.getPortal());
            stmt.setLong(22, cs.getPetSn1());
            stmt.setLong(23, cs.getPetSn2());
            stmt.setLong(24, cs.getPetSn3());
            stmt.setShort(25, cs.getAdminLevel().getValue());

            stmt.executeUpdate();
        }
    }

    /**
     * Saves/updates a CharacterData object to the database.
     *
     * Updates the main character row and all dependent tables (stats, inventory, skills,
     * quests, config, popularity, extend SP) using the same connection and transaction.
     *
     * @param conn          the database connection
     * @param characterData the CharacterData to save
     * @return true if the save succeeded, false if the update failed
     * @throws SQLException if a database access error occurs
     */
    public static boolean saveCharacter(Connection conn, CharacterData characterData) throws SQLException {
        String sql = "UPDATE player.characters SET account_id=?, name=?, money=?, ext_slot_expire=?, " +
                "friend_max=?, party_id=?, guild_id=?, creation_time=?, max_level_time=? " +
                "WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterData.getAccountId());
            stmt.setString(2, characterData.getCharacterName());
            stmt.setInt(3, characterData.getInventoryManager().getMoney());
            stmt.setTimestamp(4, characterData.getInventoryManager().getExtSlotExpire() != null ?
                    Timestamp.from(characterData.getInventoryManager().getExtSlotExpire()) : null);
            stmt.setInt(5, characterData.getFriendMax());
            stmt.setInt(6, characterData.getPartyId());
            stmt.setInt(7, characterData.getGuildId());
            stmt.setTimestamp(8, characterData.getCreationTime() != null ?
                    Timestamp.from(characterData.getCreationTime()) : null);
            stmt.setTimestamp(9, characterData.getMaxLevelTime() != null ?
                    Timestamp.from(characterData.getMaxLevelTime()) : null);
            stmt.setInt(10, characterData.getCharacterId());

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                return false;  // will rollback in a transaction.
            }
        }

        // Save dependent tables using the same connection
        saveCharacterStats(conn, characterData);
        InventoryDao.saveCharacter(conn, characterData);
        SkillManagerDao.saveCharacterSkills(conn, characterData);
        QuestManagerDao.saveCharacterQuests(conn, characterData);
        ConfigManagerDao.saveCharacterConfig(conn, characterData);
        PopularityRecordDao.saveCharacterPopularity(conn, characterData);
        MapTransferInfoDao.saveMapTransferInfo(conn, characterData);
        MiniGameRecordDao.saveMiniGameRecord(conn, characterData);
        WildHunterInfoDao.saveWildHunterInfo(conn, characterData);
        ExtendSpDao.upsertExtendSp(conn, characterData.getCharacterId(), characterData.getCharacterStat().getSp());

        return true;
    }
}
