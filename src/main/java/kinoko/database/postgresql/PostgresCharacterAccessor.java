package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.server.rank.CharacterRank;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;
import kinoko.world.user.data.*;
import kinoko.world.user.stat.CharacterStat;
import org.postgresql.util.PGobject;

import java.io.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class PostgresCharacterAccessor implements CharacterAccessor {
    private final HikariDataSource dataSource;

    public PostgresCharacterAccessor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return clazz.cast(ois.readObject());
        }
    }

    private CharacterData loadCharacterData(ResultSet rs) throws SQLException, IOException, ClassNotFoundException {
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
                rs.getLong("pet_3")
        );
        cd.setCharacterStat(cs);

        InventoryManager im = loadInventory(characterID);
        cd.setInventoryManager(im);
        im.setMoney(rs.getInt("money"));


        Timestamp extSlotExpireTs = rs.getTimestamp("ext_slot_expire");
        im.setExtSlotExpire(extSlotExpireTs != null ? extSlotExpireTs.toInstant() : null);
        cd.setInventoryManager(im);

        SkillManager sm = loadSkillCooltimesAndRecords(characterID);

        cd.setSkillManager(sm);

        QuestManager qm = loadQuestRecords(characterID);

        cd.setQuestManager(qm);

        ConfigManager cm = loadConfig(characterID);
        cd.setConfigManager(cm);

        PopularityRecord pr = loadPopularityRecord(characterID);
        cd.setPopularityRecord(pr);

        MiniGameRecord mgr = loadMiniGameRecord(characterID);
        cd.setMiniGameRecord(mgr);


        cd.setCoupleRecord(CoupleRecord.from(
                im.getEquipped(), im.getEquipInventory()
        ));

        MapTransferInfo mto = loadMapTransferInfo(characterID);
        cd.setMapTransferInfo(mto);

        WildHunterInfo whi = loadWildHunterInfo(characterID);
        cd.setWildHunterInfo(whi);

        cd.setItemSnCounter(new AtomicInteger(-1));  // Let Postgres handle item sn

        cd.setFriendMax(rs.getInt("friend_max"));
        cd.setPartyId(rs.getInt("party_id"));
        cd.setGuildId(rs.getInt("guild_id"));

        Timestamp creationTs = rs.getTimestamp("creation_time");
        cd.setCreationTime(creationTs != null ? creationTs.toInstant() : null);
        Timestamp maxLevelTs = rs.getTimestamp("max_level_time");
        cd.setMaxLevelTime(maxLevelTs != null ? maxLevelTs.toInstant() : null);
        return cd;
    }

    private WildHunterInfo loadWildHunterInfo(int characterId) throws SQLException {
        WildHunterInfo wh = new WildHunterInfo();

        String sqlRiding = "SELECT riding_type FROM player.wild_hunter WHERE character_id = ?";
        String sqlMobs = "SELECT mob_id FROM player.wild_hunter_mob WHERE character_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            // Load riding_type
            try (PreparedStatement stmt = conn.prepareStatement(sqlRiding)) {
                stmt.setInt(1, characterId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        wh.setRidingType(rs.getInt("riding_type"));
                    }
                }
            }

            // Load captured mobs
            try (PreparedStatement stmt = conn.prepareStatement(sqlMobs)) {
                stmt.setInt(1, characterId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        wh.getCapturedMobs().add(rs.getInt("mob_id"));
                        if (wh.getCapturedMobs().size() >= 5) break; // enforce max 5
                    }
                }
            }
        }

        return wh;
    }


    private MapTransferInfo loadMapTransferInfo(int characterId) throws SQLException {
        MapTransferInfo mti = new MapTransferInfo();

        // Query the new table for this character
        String sql = "SELECT map_id, old_map_id FROM player.map_transfer WHERE character_id = ?";
        try (PreparedStatement stmt = dataSource.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            try (ResultSet mapRs = stmt.executeQuery()) {
                if (mapRs.next()) {
                    int mapId = mapRs.getInt("map_id");
                    int oldMapId = mapRs.getInt("old_map_id");

                    mti.getMapTransfer().add(mapId);       // main list
                    mti.getMapTransferEx().add(oldMapId);   // legacy/old map
                }
            }
        }

        return mti;
    }

    private MiniGameRecord loadMiniGameRecord(int characterId) throws SQLException {
        MiniGameRecord record = new MiniGameRecord();

        String sql = """
        SELECT omok_wins, omok_ties, omok_losses, omok_score,
               memory_wins, memory_ties, memory_losses, memory_score
        FROM player.minigame
        WHERE character_id = ?
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    record.setOmokGameWins(rs.getInt("omok_wins"));
                    record.setOmokGameTies(rs.getInt("omok_ties"));
                    record.setOmokGameLosses(rs.getInt("omok_losses"));
                    record.setOmokGameScore(rs.getDouble("omok_score"));

                    record.setMemoryGameWins(rs.getInt("memory_wins"));
                    record.setMemoryGameTies(rs.getInt("memory_ties"));
                    record.setMemoryGameLosses(rs.getInt("memory_losses"));
                    record.setMemoryGameScore(rs.getDouble("memory_score"));
                }
            }
        }

        return record;
    }


    private PopularityRecord loadPopularityRecord(int characterId) throws SQLException {
        PopularityRecord pr = new PopularityRecord();

        String sql = "SELECT other_character_id, timestamp FROM player.popularity WHERE character_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int otherCharId = rs.getInt("other_character_id");
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) {
                        pr.getRecords().put(otherCharId, ts.toInstant());
                    }
                }
            }
        }

        return pr;
    }


    private ConfigManager loadConfig(int characterId) throws SQLException {
        String sql = """
        SELECT pet_consume_item, pet_consume_mp_item, pet_exception_list,
               func_key_types, func_key_ids, quickslot_key_map
        FROM player.config
        WHERE character_id = ?
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return ConfigManager.defaults();
                }

                int petConsumeItem = rs.getInt("pet_consume_item");
                int petConsumeMpItem = rs.getInt("pet_consume_mp_item");

                // --- Pet exception list ---
                List<Integer> petExceptionList;
                var petExArr = rs.getArray("pet_exception_list");
                if (petExArr != null) {
                    Integer[] arr = (Integer[]) petExArr.getArray();
                    petExceptionList = Arrays.asList(arr);
                } else {
                    petExceptionList = List.of();
                }

                // --- Function key map ---
                FuncKeyMapped[] funcKeyMap = new FuncKeyMapped[GameConstants.FUNC_KEY_MAP_SIZE];
                var funcTypeArr = rs.getArray("func_key_types");
                var funcIdArr = rs.getArray("func_key_ids");

                if (funcTypeArr != null && funcIdArr != null) {
                    Short[] typeValues = (Short[]) funcTypeArr.getArray();
                    Integer[] idValues = (Integer[]) funcIdArr.getArray();

                    for (int i = 0; i < funcKeyMap.length; i++) {
                        FuncKeyType type = FuncKeyType.getByValue(typeValues[i].byteValue());
                        int id = idValues[i];
                        funcKeyMap[i] = FuncKeyMapped.of(type, id);
                    }
                } else {
                    funcKeyMap = Arrays.copyOf(GameConstants.DEFAULT_FUNC_KEY_MAP, GameConstants.FUNC_KEY_MAP_SIZE);
                }

                // --- Quickslot key map ---
                int[] quickslotKeyMap;
                var quickArr = rs.getArray("quickslot_key_map");
                if (quickArr != null) {
                    Integer[] arr = (Integer[]) quickArr.getArray();
                    quickslotKeyMap = Arrays.stream(arr).mapToInt(Integer::intValue).toArray();
                } else {
                    quickslotKeyMap = Arrays.copyOf(GameConstants.DEFAULT_QUICKSLOT_KEY_MAP, GameConstants.QUICKSLOT_KEY_MAP_SIZE);
                }

                return new ConfigManager(petConsumeItem, petConsumeMpItem, petExceptionList, funcKeyMap, quickslotKeyMap);
            }
        }
    }


    private QuestManager loadQuestRecords(int characterId) throws SQLException {
        QuestManager qm = new QuestManager();
        String sql = "SELECT quest_id, status, progress, completed_time FROM player.quest_record WHERE character_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int questId = rs.getInt("quest_id");
                    int statusInt = rs.getInt("status");
                    QuestState state = QuestState.getByValue(statusInt); // map int -> QuestState
                    String value = rs.getString("progress");
                    Timestamp completedTs = rs.getTimestamp("completed_time");
                    Instant completedTime = completedTs != null ? completedTs.toInstant() : null;
                    QuestRecord record = new QuestRecord(questId, state, value, completedTime);
                    qm.addQuestRecord(record);
                }
            }
        }

        return qm;
    }


    private SkillManager loadSkillCooltimesAndRecords(int characterId) throws SQLException {
        SkillManager sm = new SkillManager();

        // Load skill cooldowns
        String cooldownSql = "SELECT skill_id, cooldown_end FROM player.skill_cooltime WHERE character_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(cooldownSql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    Timestamp cooldownEnd = rs.getTimestamp("cooldown_end");
                    if (cooldownEnd != null) {
                        sm.getSkillCooltimes().put(skillId, cooldownEnd.toInstant());
                    }
                }
            }
        }

        // Load skill records
        String recordSql = "SELECT skill_id, level, master_level FROM player.skill_record WHERE character_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(recordSql)) {
            stmt.setInt(1, characterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int skillId = rs.getInt("skill_id");
                    int level = rs.getInt("level");
                    int masterLevel = rs.getInt("master_level");
                    SkillRecord record = new SkillRecord(skillId, level, masterLevel);
                    sm.addSkill(record);
                }
            }
        }

        return sm;
    }



    private String lowerName(String name) {
        return name.toLowerCase();
    }

    @Override
    public boolean checkCharacterNameAvailable(String name) {
        String sql = "SELECT COUNT(*) > 0 AS exists FROM player.characters WHERE name ILIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name); // original name
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getBoolean("exists");
                    return !exists; // available if it does NOT exist
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private InventoryManager loadInventory(int characterId) throws SQLException {
        InventoryManager im = new InventoryManager();

        String sql = """
        SELECT inv.inventory_type, inv.slot, fi.*
        FROM player.inventory inv
        JOIN item.full_item fi ON inv.item_sn = fi.item_sn
        WHERE inv.character_id = ?
        ORDER BY inv.inventory_type, inv.slot
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long itemSn = rs.getLong("item_sn");
                int slot = rs.getInt("slot");
                int itemId = rs.getInt("item_id");
                short quantity = rs.getShort("quantity");
                short attribute = rs.getShort("attribute");
                String title = rs.getString("title");
                Timestamp dateExpireTs = rs.getTimestamp("date_expire");

                // EquipData
                EquipData equipData; // declare once
                if (rs.getObject("inc_str") != null) {
                    equipData = new EquipData(
                            rs.getShort("inc_str"),
                            rs.getShort("inc_dex"),
                            rs.getShort("inc_int"),
                            rs.getShort("inc_luk"),
                            rs.getShort("inc_max_hp"),
                            rs.getShort("inc_max_mp"),
                            rs.getShort("inc_pad"),
                            rs.getShort("inc_mad"),
                            rs.getShort("inc_pdd"),
                            rs.getShort("inc_mdd"),
                            rs.getShort("inc_acc"),
                            rs.getShort("inc_eva"),
                            rs.getShort("inc_craft"),
                            rs.getShort("inc_speed"),
                            rs.getShort("inc_jump"),
                            rs.getByte("ruc"),
                            rs.getByte("cuc"),
                            rs.getInt("iuc"),
                            rs.getByte("chuc"),
                            rs.getByte("grade"),
                            rs.getShort("option_1"),
                            rs.getShort("option_2"),
                            rs.getShort("option_3"),
                            rs.getShort("socket_1"),
                            rs.getShort("socket_2"),
                            rs.getByte("level_up_type"),
                            rs.getByte("level"),
                            rs.getInt("exp"),
                            rs.getInt("durability")
                    );
                }
                else{
                    equipData = new EquipData();
                }

                // PetData
                PetData petData = null;
                if (rs.getObject("pet_name") != null) {
                    petData = new PetData(
                            rs.getString("pet_name"),
                            rs.getByte("pet_level"),
                            rs.getByte("fullness"),
                            rs.getShort("tameness"),
                            rs.getShort("pet_skill"),
                            rs.getShort("pet_attribute"),
                            rs.getInt("remain_life")
                    );
                }

                // RingData
                RingData ringData = null;
                if (rs.getObject("pair_character_id") != null) {
                    ringData = new RingData(
                            rs.getInt("pair_character_id"),
                            rs.getString("pair_character_name"),
                            rs.getLong("pair_item_sn")
                    );
                }

                Item item = new Item(
                        itemId,
                        quantity,
                        itemSn,
                        false, // cash flag
                        attribute,
                        title,
                        dateExpireTs != null ? dateExpireTs.toInstant() : null,
                        equipData,
                        petData,
                        ringData
                );

                String invType = rs.getString("inventory_type");
                switch (invType.toUpperCase()) {
                    case "EQUIPPED" -> im.getEquipped().addItem(slot, item);
                    case "EQUIP" -> im.getEquipInventory().addItem(slot, item);
                    case "CONSUME" -> im.getConsumeInventory().addItem(slot, item);
                    case "INSTALL" -> im.getInstallInventory().addItem(slot, item);
                    case "ETC" -> im.getEtcInventory().addItem(slot, item);
                    case "CASH" -> im.getCashInventory().addItem(slot, item);
                    default -> throw new IllegalArgumentException("Unknown inventory type: " + invType);
                }
            }
        }

        return im;
    }


    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        String sql = """
        SELECT c.*, s.*
        FROM player.characters c
        LEFT JOIN player.stats s ON c.id = s.character_id
        WHERE c.id = ?
    """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(loadCharacterData(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        String sql = "SELECT * FROM player.characters WHERE name ILIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lowerName(name));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(loadCharacterData(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterInfo> getCharacterInfoByName(String name) {
        String sql = "SELECT account_id, id, name FROM player.characters WHERE name ILIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lowerName(name));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new CharacterInfo(
                        rs.getInt("account_id"),
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        String sql = "SELECT account_id FROM player.characters WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return Optional.of(rs.getInt("account_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
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

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

                    // For inventory, query normalized player.inventory table separately
                    Inventory equipped = loadEquippedInventory(cs.getId());

                    list.add(AvatarData.from(cs, equipped));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Inventory loadEquippedInventory(int characterId) throws SQLException {
        Inventory equipped = new Inventory(24); // default equipped size

        String sql = """
        SELECT f.*, i.slot
        FROM player.inventory i
        JOIN item.full_item f ON i.item_sn = f.item_sn
        WHERE i.character_id = ? AND i.inventory_type = ?
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);

            PGobject enumValue = new PGobject();
            enumValue.setType("inventory_type_enum");
            enumValue.setValue(InventoryType.EQUIPPED.name());
            stmt.setObject(2, enumValue);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long itemSn = rs.getLong("item_sn");
                    int slot = rs.getInt("slot");
                    int itemId = rs.getInt("item_id");
                    short quantity = rs.getShort("quantity");
                    short attribute = rs.getShort("attribute");
                    String title = rs.getString("title");
                    Timestamp dateExpireTs = rs.getTimestamp("date_expire");

                    // Build EquipData if applicable
                    EquipData equipData = null;
                    if (rs.getObject("inc_str") != null) {
                        equipData = new EquipData(
                                rs.getShort("inc_str"),
                                rs.getShort("inc_dex"),
                                rs.getShort("inc_int"),
                                rs.getShort("inc_luk"),
                                rs.getShort("inc_max_hp"),
                                rs.getShort("inc_max_mp"),
                                rs.getShort("inc_pad"),
                                rs.getShort("inc_mad"),
                                rs.getShort("inc_pdd"),
                                rs.getShort("inc_mdd"),
                                rs.getShort("inc_acc"),
                                rs.getShort("inc_eva"),
                                rs.getShort("inc_craft"),
                                rs.getShort("inc_speed"),
                                rs.getShort("inc_jump"),
                                rs.getByte("ruc"),
                                rs.getByte("cuc"),
                                rs.getInt("iuc"),
                                rs.getByte("chuc"),
                                rs.getByte("grade"),
                                rs.getShort("option_1"),
                                rs.getShort("option_2"),
                                rs.getShort("option_3"),
                                rs.getShort("socket_1"),
                                rs.getShort("socket_2"),
                                rs.getByte("level_up_type"),
                                rs.getByte("level"),
                                rs.getInt("exp"),
                                rs.getInt("durability")
                        );
                    }

                    // Build PetData if applicable
                    PetData petData = null;
                    if (rs.getObject("pet_name") != null) {
                        petData = new PetData(
                                rs.getString("pet_name"),
                                rs.getByte("pet_level"),
                                rs.getByte("fullness"),
                                rs.getShort("tameness"),
                                rs.getShort("pet_skill"),
                                rs.getShort("pet_attribute"),
                                rs.getInt("remain_life")
                        );
                    }

                    // Build RingData if applicable
                    RingData ringData = null;
                    if (rs.getObject("pair_character_id") != null) {
                        ringData = new RingData(
                                rs.getInt("pair_character_id"),
                                rs.getString("pair_character_name"),
                                rs.getLong("pair_item_sn")
                        );
                    }

                    Item item = new Item(
                            itemId,
                            quantity,
                            itemSn,
                            false, // cash flag, adjust if you have it
                            attribute,
                            title,
                            dateExpireTs != null ? dateExpireTs.toInstant() : null,
                            equipData,
                            petData,
                            ringData
                    );

                    equipped.putItem(slot, item);
                }
            }
        }

        return equipped;
    }


    @Override
    public synchronized boolean newCharacter(CharacterData characterData) {
        if (!checkCharacterNameAvailable(characterData.getCharacterName())) return false;

        String sql = """
        INSERT INTO player.characters 
        (account_id, name, money, ext_slot_expire, friend_max, party_id, guild_id, creation_time, max_level_time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
    """;

        Connection conn = null;
        boolean success = false;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

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

            // Pass the same connection to all dependent methods
            saveCharacterStats(conn, characterData);
            saveCharacterInventory(conn, characterData);
            saveCharacterSkills(conn, characterData);
            saveCharacterQuests(conn, characterData);
            saveCharacterConfig(conn, characterData);
            saveCharacterMacros(conn, characterData);
            saveCharacterPopularity(conn, characterData);

            conn.commit();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }

        return success;
    }


    private void saveCharacterConfig(Connection conn, CharacterData characterData) throws SQLException {
        ConfigManager config = characterData.getConfigManager();

        String sql = """
    INSERT INTO player.config 
    (character_id, pet_consume_item, pet_consume_mp_item, pet_exception_list, func_key_types, func_key_ids, quickslot_key_map)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT (character_id) DO UPDATE 
    SET pet_consume_item = EXCLUDED.pet_consume_item,
        pet_consume_mp_item = EXCLUDED.pet_consume_mp_item,
        pet_exception_list = EXCLUDED.pet_exception_list,
        func_key_types = EXCLUDED.func_key_types,
        func_key_ids = EXCLUDED.func_key_ids,
        quickslot_key_map = EXCLUDED.quickslot_key_map
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterData.getCharacterId());
            stmt.setInt(2, config.getPetConsumeItem());
            stmt.setInt(3, config.getPetConsumeMpItem());

            // pet_exception_list -> List<Integer> -> Integer[]
            List<Integer> exceptionList = config.getPetExceptionList();
            Integer[] exceptionArray = exceptionList != null ? exceptionList.toArray(new Integer[0]) : new Integer[0];
            stmt.setArray(4, conn.createArrayOf("integer", exceptionArray));

            // func_key_types & func_key_ids from FuncKeyMapped[]
            FuncKeyMapped[] funcKeyMap = config.getFuncKeyMap();
            if (funcKeyMap == null) {
                funcKeyMap = Arrays.copyOf(GameConstants.DEFAULT_FUNC_KEY_MAP, GameConstants.FUNC_KEY_MAP_SIZE);
            }

            Short[] funcTypes = Arrays.stream(funcKeyMap)
                    .map(f -> (short) f.getType().getValue())
                    .toArray(Short[]::new);
            Integer[] funcIds = Arrays.stream(funcKeyMap)
                    .map(FuncKeyMapped::getId)
                    .toArray(Integer[]::new);

            stmt.setArray(5, conn.createArrayOf("smallint", funcTypes)); // func_key_types
            stmt.setArray(6, conn.createArrayOf("integer", funcIds));   // func_key_ids

            // quickslot_key_map -> int[] -> Integer[]
            int[] quickslot = config.getQuickslotKeyMap();
            Integer[] quickslotKeys = quickslot != null
                    ? Arrays.stream(quickslot).boxed().toArray(Integer[]::new)
                    : new Integer[0];
            stmt.setArray(7, conn.createArrayOf("integer", quickslotKeys));

            stmt.executeUpdate();
        }
    }



    private void saveCharacterMacros(Connection conn, CharacterData characterData) throws SQLException {
        List<SingleMacro> macros = characterData.getConfigManager().getMacroSysData();

        String sql = """
        INSERT INTO player.character_macro 
        (character_id, macro_index, name, mute, skills)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (character_id, macro_index) DO UPDATE
        SET name = EXCLUDED.name,
            mute = EXCLUDED.mute,
            skills = EXCLUDED.skills
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < macros.size(); i++) {
                SingleMacro macro = macros.get(i);
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, i); // macro_index
                stmt.setString(3, macro.getName());
                stmt.setBoolean(4, macro.isMute());

                // skills array -> Integer[]
                int[] skills = macro.getSkills();
                Integer[] skillArray = Arrays.stream(skills).boxed().toArray(Integer[]::new);
                stmt.setArray(5, conn.createArrayOf("INT", skillArray));

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }


    private void saveCharacterPopularity(Connection conn, CharacterData characterData) throws SQLException {
        String sql = """
        INSERT INTO player.popularity (character_id, other_character_id, timestamp)
        VALUES (?, ?, ?)
        ON CONFLICT (character_id, other_character_id)
        DO UPDATE SET timestamp = EXCLUDED.timestamp
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            PopularityRecord pr = characterData.getPopularityRecord();
            int charId = characterData.getCharacterId();

            for (var entry : pr.getRecords().entrySet()) {
                stmt.setInt(1, charId);
                stmt.setInt(2, entry.getKey());
                stmt.setTimestamp(3, Timestamp.from(entry.getValue()));
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }



    private void saveCharacterSkills(Connection conn, CharacterData characterData) throws SQLException {
        String skillRecordSql = """
        INSERT INTO player.skill_record (character_id, skill_id, level, master_level)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (character_id, skill_id) 
        DO UPDATE SET level = EXCLUDED.level, master_level = EXCLUDED.master_level
        """;

        try (PreparedStatement stmt = conn.prepareStatement(skillRecordSql)) {
            for (SkillRecord sr : characterData.getSkillManager().getSkillRecords()) {
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, sr.getSkillId());
                stmt.setInt(3, sr.getSkillLevel());
                if (sr.getMasterLevel() > 0) {
                    stmt.setInt(4, sr.getMasterLevel());
                } else {
                    stmt.setNull(4, java.sql.Types.INTEGER);
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        // Save skill cooltimes
        String skillCooltimeSql = """
        INSERT INTO player.skill_cooltime (character_id, skill_id, cooldown_end)
        VALUES (?, ?, ?)
        ON CONFLICT (character_id, skill_id)
        DO UPDATE SET cooldown_end = EXCLUDED.cooldown_end
        """;

        try (PreparedStatement stmt = conn.prepareStatement(skillCooltimeSql)) {
            for (var entry : characterData.getSkillManager().getSkillCooltimes().entrySet()) {
                int skillId = entry.getKey();
                Instant endTime = entry.getValue();
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, skillId);
                stmt.setTimestamp(3, Timestamp.from(endTime));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void saveCharacterQuests(Connection conn, CharacterData characterData) throws SQLException {
        String sql = """
        INSERT INTO player.quest_record (character_id, quest_id, status, progress, completed_time)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (character_id, quest_id)
        DO UPDATE SET status = EXCLUDED.status,
                      progress = EXCLUDED.progress,
                      completed_time = EXCLUDED.completed_time
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (QuestRecord qr : characterData.getQuestManager().getQuestRecords()) {
                stmt.setInt(1, characterData.getCharacterId());
                stmt.setInt(2, qr.getQuestId());
                stmt.setInt(3, qr.getState().getValue());
                stmt.setString(4, qr.getValue());
                stmt.setTimestamp(5, qr.getCompletedTime() != null ? Timestamp.from(qr.getCompletedTime()) : null);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public boolean saveCharacter(CharacterData characterData) {
        String sql = "UPDATE player.characters SET account_id=?, name=?, money=?, ext_slot_expire=?, " +
                "friend_max=?, party_id=?, guild_id=?, creation_time=?, max_level_time=? " +
                "WHERE id=?";
        Connection conn = null;
        boolean previousAutoCommit = true;

        try {
            conn = dataSource.getConnection();

            // save previous auto-commit state
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

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
                stmt.setInt(10, characterData.getCharacterId());

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // Save dependent tables using the same connection
            saveCharacterStats(conn, characterData);
            saveCharacterInventory(conn, characterData);
            saveCharacterSkills(conn, characterData);
            saveCharacterQuests(conn, characterData);
            saveCharacterConfig(conn, characterData);
            saveCharacterMacros(conn, characterData);
            saveCharacterPopularity(conn, characterData);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(previousAutoCommit);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    private void saveCharacterStats(Connection conn, CharacterData characterData) throws SQLException {
        String sql = """
        INSERT INTO player.stats (
            character_id, gender, skin, face, hair, level, job, sub_job,
            base_str, base_dex, base_int, base_luk, hp, max_hp, mp, max_mp,
            ap, exp, pop, pos_map, portal, pet_1, pet_2, pet_3
        ) VALUES (
            ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
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
            pet_3 = EXCLUDED.pet_3
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

            stmt.executeUpdate();
        }
    }


    private void saveCharacterInventory(Connection conn, CharacterData characterData) throws SQLException {
        String sqlInventory = """
        INSERT INTO player.inventory (character_id, inventory_type, slot, item_sn)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (character_id, item_sn)
        DO UPDATE SET slot = EXCLUDED.slot, inventory_type = EXCLUDED.inventory_type
    """;

        String sqlItems = """
        INSERT INTO item.items (item_sn, item_id, quantity, attribute, title, date_expire)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement stmtInventory = conn.prepareStatement(sqlInventory);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {

            InventoryManager inv = characterData.getInventoryManager();

            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.EQUIPPED, inv.getEquipped().getItems());
            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.EQUIP, inv.getEquipInventory().getItems());
            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.CONSUME, inv.getConsumeInventory().getItems());
            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.INSTALL, inv.getInstallInventory().getItems());
            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.ETC, inv.getEtcInventory().getItems());
            saveInventoryBatch(conn, stmtItems, stmtInventory, characterData.getCharacterId(), InventoryType.CASH, inv.getCashInventory().getItems());

            stmtItems.executeBatch();
            stmtInventory.executeBatch();
        }
    }



    private void saveInventoryBatch(
            Connection conn,
            PreparedStatement stmtItems,
            PreparedStatement stmtInventory,
            int charId,
            InventoryType type,
            Map<Integer, Item> items
    ) throws SQLException {

        PGobject enumValue = new PGobject();
        enumValue.setType("inventory_type_enum");
        enumValue.setValue(type.name());

        // --- Delete inventory items that no longer exist ---
        if (!items.isEmpty()) {
            Long[] itemSnArray = items.values().stream()
                    .map(Item::getItemSn)
                    .toArray(Long[]::new);

            try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM player.inventory WHERE character_id = ? AND item_sn <> ALL (?)")) {
                deleteStmt.setInt(1, charId);
                Array sqlArray = conn.createArrayOf("bigint", itemSnArray);
                deleteStmt.setArray(2, sqlArray);
                deleteStmt.executeUpdate();
            }
        } else {  // delete all items.
            try (PreparedStatement deleteStmt = conn.prepareStatement(
                    "DELETE FROM player.inventory WHERE character_id = ?")) {
                deleteStmt.setInt(1, charId);
                deleteStmt.executeUpdate();
            }
        }

        // --- Insert/update items ---
        for (var entry : items.entrySet()) {
            Item item = entry.getValue();
            long itemSn = item.getItemSn();

            if (itemSn <= 0) {  // DNE
                try (PreparedStatement seqStmt = conn.prepareStatement(
                        "SELECT nextval(pg_get_serial_sequence('item.items', 'item_sn'))");
                     ResultSet rs = seqStmt.executeQuery()) {
                    rs.next();
                    itemSn = rs.getLong(1);
                    item.setItemSn(itemSn);
                }

                stmtItems.setLong(1, itemSn);
                stmtItems.setInt(2, item.getItemId());
                stmtItems.setInt(3, item.getQuantity());
                stmtItems.setShort(4, item.getAttribute());
                stmtItems.setString(5, item.getTitle());
                stmtItems.setTimestamp(6, item.getDateExpire() != null ? Timestamp.from(item.getDateExpire()) : null);
                stmtItems.addBatch();
            } else {
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE item.items SET quantity = ?, attribute = ?, title = ?, date_expire = ? WHERE item_sn = ?")) {
                    updateStmt.setInt(1, item.getQuantity());
                    updateStmt.setShort(2, item.getAttribute());
                    updateStmt.setString(3, item.getTitle());
                    updateStmt.setTimestamp(4, item.getDateExpire() != null ? Timestamp.from(item.getDateExpire()) : null);
                    updateStmt.setLong(5, itemSn);
                    updateStmt.executeUpdate();
                }
            }

            stmtInventory.setInt(1, charId);
            stmtInventory.setObject(2, enumValue);
            stmtInventory.setInt(3, entry.getKey());
            stmtInventory.setLong(4, itemSn);
            stmtInventory.addBatch();
        }
    }

    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        String sql = "DELETE FROM player.characters WHERE id=? AND account_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, characterId);
            stmt.setInt(2, accountId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        Map<Integer, CharacterRank> ranks = new HashMap<>();
        String sql = """
        SELECT c.id, c.max_level_time, s.job, s.exp
        FROM player.characters c
        JOIN player.stats s ON c.id = s.character_id
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<CharacterRankData> rankDataList = new ArrayList<>();

            while (rs.next()) {
                int characterId = rs.getInt("id");
                int jobId = rs.getInt("job");
                long cumulativeExp = rs.getLong("exp");
                Timestamp ts = rs.getTimestamp("max_level_time");

                // skip admin/manager characters
                if (JobConstants.isAdminJob(jobId) || JobConstants.isManagerJob(jobId)) {
                    continue;
                }

                rankDataList.add(new CharacterRankData(
                        characterId,
                        JobConstants.getJobCategory(jobId),
                        cumulativeExp,
                        ts != null ? ts.toInstant() : Instant.MAX
                ));
            }

            // Sort by EXP (descending) and then by earliest max level time
            rankDataList.sort(
                    Comparator.comparingLong(CharacterRankData::getCumulativeExp).reversed()
                            .thenComparing(CharacterRankData::getMaxLevelTime)
            );

            // Compute world rank and job rank
            Map<Integer, Integer> jobRanks = new HashMap<>();
            for (CharacterRankData data : rankDataList) {
                int worldRank = ranks.size() + 1;
                int jobRank = jobRanks.getOrDefault(data.getJobCategory(), 0) + 1;
                jobRanks.put(data.getJobCategory(), jobRank);

                ranks.put(data.getCharacterId(), new CharacterRank(
                        data.getCharacterId(),
                        worldRank,
                        jobRank
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ranks;
    }


    private static class CharacterRankData {
        private final int characterId;
        private final int jobCategory;
        private final long cumulativeExp;
        private final Instant maxLevelTime;

        private CharacterRankData(int characterId, int jobCategory, long cumulativeExp, Instant maxLevelTime) {
            this.characterId = characterId;
            this.jobCategory = jobCategory;
            this.cumulativeExp = cumulativeExp;
            this.maxLevelTime = maxLevelTime;
        }

        public int getCharacterId() { return characterId; }
        public int getJobCategory() { return jobCategory; }
        public long getCumulativeExp() { return cumulativeExp; }
        public Instant getMaxLevelTime() { return maxLevelTime != null ? maxLevelTime : Instant.MAX; }
    }
}
