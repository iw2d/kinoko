package kinoko.database.sqlite;

import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.database.json.*;
import kinoko.server.rank.CharacterRank;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryManager;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;
import kinoko.world.user.data.*;
import kinoko.world.user.stat.CharacterStat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kinoko.database.schema.CharacterDataSchema.*;

public final class SqliteCharacterAccessor extends SqliteAccessor implements CharacterAccessor {
    private static final String tableName = "character_table";

    private final CharacterDataSerializer characterDataSerializer = new CharacterDataSerializer();
    private final CharacterStatSerializer characterStatSerializer = new CharacterStatSerializer();
    private final InventorySerializer inventorySerializer = new InventorySerializer();


    public SqliteCharacterAccessor(Connection connection) {
        super(connection);
    }

    private CharacterData loadCharacterData(ResultSet rs) throws SQLException {
        final int accountId = rs.getInt(ACCOUNT_ID);
        final CharacterData cd = new CharacterData(accountId);

        final CharacterStat cs = characterStatSerializer.deserialize(getJsonObject(rs, CHARACTER_STAT));
        cs.setId(rs.getInt(CHARACTER_ID));
        cs.setName(rs.getString(CHARACTER_NAME));
        cd.setCharacterStat(cs);

        final InventoryManager im = new InventoryManager();
        im.setEquipped(inventorySerializer.deserialize(getJsonObject(rs, CHARACTER_EQUIPPED)));
        im.setEquipInventory(inventorySerializer.deserialize(getJsonObject(rs, EQUIP_INVENTORY)));
        im.setConsumeInventory(inventorySerializer.deserialize(getJsonObject(rs, CONSUME_INVENTORY)));
        im.setInstallInventory(inventorySerializer.deserialize(getJsonObject(rs, INSTALL_INVENTORY)));
        im.setEtcInventory(inventorySerializer.deserialize(getJsonObject(rs, ETC_INVENTORY)));
        im.setCashInventory(inventorySerializer.deserialize(getJsonObject(rs, CASH_INVENTORY)));
        im.setMoney(rs.getInt(MONEY));
        im.setExtSlotExpire(getInstant(rs, EXT_SLOT_EXPIRE));
        cd.setInventoryManager(im);

        final SkillManager sm = new SkillManager();
        for (var entry : characterDataSerializer.deserializeSkillCooltimes(getJsonObject(rs, SKILL_COOLTIMES)).entrySet()) {
            sm.setSkillCooltime(entry.getKey(), entry.getValue());
        }
        for (SkillRecord record : characterDataSerializer.deserializeSkillRecords(getJsonArray(rs, SKILL_RECORDS))) {
            sm.addSkill(record);
        }
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        for (var record : characterDataSerializer.deserializeQuestRecords(getJsonArray(rs, QUEST_RECORDS))) {
            qm.addQuestRecord(record);
        }
        cd.setQuestManager(qm);

        final ConfigManager cm = characterDataSerializer.deserializeConfigManager(getJsonObject(rs, CONFIG));
        cd.setConfigManager(cm);

        final PopularityRecord pr = characterDataSerializer.deserializePopularityRecord(getJsonObject(rs, POPULARITY_RECORD));
        cd.setPopularityRecord(pr);

        final MiniGameRecord mgr = characterDataSerializer.deserializeMiniGameRecord(getJsonObject(rs, MINIGAME_RECORD));
        cd.setMiniGameRecord(mgr);

        final CoupleRecord cr = CoupleRecord.from(im.getEquipped(), im.getEquipInventory());
        cd.setCoupleRecord(cr);

        final MapTransferInfo mti = characterDataSerializer.deserializeMapTransferInfo(getJsonObject(rs, MAP_TRANSFER_INFO));
        cd.setMapTransferInfo(mti);

        final WildHunterInfo whi = characterDataSerializer.deserializeWildHunterInfo(getJsonObject(rs, WILD_HUNTER_INFO));
        cd.setWildHunterInfo(whi);

        cd.setItemSnCounter(new AtomicInteger(rs.getInt(ITEM_SN_COUNTER)));
        cd.setFriendMax(rs.getInt(FRIEND_MAX));
        cd.setPartyId(rs.getInt(PARTY_ID));
        cd.setGuildId(rs.getInt(GUILD_ID));
        cd.setCreationTime(getInstant(rs, CREATION_TIME));
        cd.setMaxLevelTime(getInstant(rs, MAX_LEVEL_TIME));
        return cd;
    }

    @Override
    public boolean checkCharacterNameAvailable(String name) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT 1 FROM " + tableName + " WHERE " + CHARACTER_NAME + " = ?"
        )) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + CHARACTER_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadCharacterData(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + CHARACTER_NAME + " = ?"
        )) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(loadCharacterData(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterInfo> getCharacterInfoByName(String name) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT " +
                        ACCOUNT_ID + ", " +
                        CHARACTER_ID + ", " +
                        CHARACTER_NAME + " FROM " + tableName + " WHERE " + CHARACTER_NAME + " = ?"
        )) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CharacterInfo(
                            rs.getInt(ACCOUNT_ID),
                            rs.getInt(CHARACTER_ID),
                            rs.getString(CHARACTER_NAME)
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT " + ACCOUNT_ID + " FROM " + tableName + " WHERE " + CHARACTER_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt(ACCOUNT_ID));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
        final List<AvatarData> avatarDataList = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT " +
                        CHARACTER_ID + ", " +
                        CHARACTER_NAME + ", " +
                        CHARACTER_STAT + ", " +
                        CHARACTER_EQUIPPED + " FROM " + tableName + " WHERE " + ACCOUNT_ID + " = ?"
        )) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final CharacterStat characterStat = characterStatSerializer.deserialize(getJsonObject(rs, CHARACTER_STAT));
                    characterStat.setId(rs.getInt(CHARACTER_ID));
                    characterStat.setName(rs.getString(CHARACTER_NAME));
                    final Inventory equipped = inventorySerializer.deserialize(getJsonObject(rs, CHARACTER_EQUIPPED));
                    avatarDataList.add(AvatarData.from(characterStat, equipped));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avatarDataList;
    }

    @Override
    public synchronized boolean newCharacter(CharacterData cd) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        CHARACTER_ID + ", " +
                        ACCOUNT_ID + ", " +
                        CHARACTER_NAME + ", " +
                        CHARACTER_STAT + ", " +
                        CHARACTER_EQUIPPED + ", " +
                        EQUIP_INVENTORY + ", " +
                        CONSUME_INVENTORY + ", " +
                        INSTALL_INVENTORY + ", " +
                        ETC_INVENTORY + ", " +
                        CASH_INVENTORY + ", " +
                        MONEY + ", " +
                        EXT_SLOT_EXPIRE + ", " +
                        SKILL_COOLTIMES + ", " +
                        SKILL_RECORDS + ", " +
                        QUEST_RECORDS + ", " +
                        CONFIG + ", " +
                        POPULARITY_RECORD + ", " +
                        MINIGAME_RECORD + ", " +
                        MAP_TRANSFER_INFO + ", " +
                        WILD_HUNTER_INFO + ", " +
                        ITEM_SN_COUNTER + ", " +
                        FRIEND_MAX + ", " +
                        PARTY_ID + ", " +
                        GUILD_ID + ", " +
                        CREATION_TIME + ", " +
                        MAX_LEVEL_TIME + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        )) {
            int i = 1;
            ps.setInt(i++, cd.getCharacterId());
            ps.setInt(i++, cd.getAccountId());
            ps.setString(i++, cd.getCharacterName());
            setJsonObject(ps, i++, characterStatSerializer.serialize(cd.getCharacterStat()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEquipped()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEquipInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getConsumeInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getInstallInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEtcInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getCashInventory()));
            ps.setInt(i++, cd.getInventoryManager().getMoney());
            setInstant(ps, i++, cd.getInventoryManager().getExtSlotExpire());
            setJsonObject(ps, i++, characterDataSerializer.serializeSkillCooltimes(cd.getSkillManager().getSkillCooltimes()));
            setJsonArray(ps, i++, characterDataSerializer.serializeSkillRecords(cd.getSkillManager().getSkillRecords()));
            setJsonArray(ps, i++, characterDataSerializer.serializeQuestRecords(cd.getQuestManager().getQuestRecords()));
            setJsonObject(ps, i++, characterDataSerializer.serializeConfigManager(cd.getConfigManager()));
            setJsonObject(ps, i++, characterDataSerializer.serializePopularityRecord(cd.getPopularityRecord()));
            setJsonObject(ps, i++, characterDataSerializer.serializeMiniGameRecord(cd.getMiniGameRecord()));
            setJsonObject(ps, i++, characterDataSerializer.serializeMapTransferInfo(cd.getMapTransferInfo()));
            setJsonObject(ps, i++, characterDataSerializer.serializeWildHunterInfo(cd.getWildHunterInfo()));
            ps.setInt(i++, cd.getItemSnCounter().get());
            ps.setInt(i++, cd.getFriendMax());
            ps.setInt(i++, cd.getPartyId());
            ps.setInt(i++, cd.getGuildId());
            setInstant(ps, i++, cd.getCreationTime());
            setInstant(ps, i++, cd.getMaxLevelTime());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean saveCharacter(CharacterData cd) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "UPDATE " + tableName + " SET " +
                        ACCOUNT_ID + " = ?," +
                        CHARACTER_NAME + " = ?," +
                        CHARACTER_STAT + " = ?," +
                        CHARACTER_EQUIPPED + " = ?," +
                        EQUIP_INVENTORY + " = ?," +
                        CONSUME_INVENTORY + " = ?," +
                        INSTALL_INVENTORY + " = ?," +
                        ETC_INVENTORY + " = ?," +
                        CASH_INVENTORY + " = ?," +
                        MONEY + " = ?," +
                        EXT_SLOT_EXPIRE + " = ?," +
                        SKILL_COOLTIMES + " = ?," +
                        SKILL_RECORDS + " = ?," +
                        QUEST_RECORDS + " = ?," +
                        CONFIG + " = ?," +
                        POPULARITY_RECORD + " = ?," +
                        MINIGAME_RECORD + " = ?," +
                        MAP_TRANSFER_INFO + " = ?," +
                        WILD_HUNTER_INFO + " = ?," +
                        ITEM_SN_COUNTER + " = ?," +
                        FRIEND_MAX + " = ?," +
                        PARTY_ID + " = ?," +
                        GUILD_ID + " = ?," +
                        CREATION_TIME + " = ?," +
                        MAX_LEVEL_TIME + " = ? WHERE " + CHARACTER_ID + " = ?"
        )) {
            int i = 1;
            ps.setInt(i++, cd.getAccountId());
            ps.setString(i++, cd.getCharacterName());
            setJsonObject(ps, i++, characterStatSerializer.serialize(cd.getCharacterStat()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEquipped()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEquipInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getConsumeInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getInstallInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getEtcInventory()));
            setJsonObject(ps, i++, inventorySerializer.serialize(cd.getInventoryManager().getCashInventory()));
            ps.setInt(i++, cd.getInventoryManager().getMoney());
            setInstant(ps, i++, cd.getInventoryManager().getExtSlotExpire());
            setJsonObject(ps, i++, characterDataSerializer.serializeSkillCooltimes(cd.getSkillManager().getSkillCooltimes()));
            setJsonArray(ps, i++, characterDataSerializer.serializeSkillRecords(cd.getSkillManager().getSkillRecords()));
            setJsonArray(ps, i++, characterDataSerializer.serializeQuestRecords(cd.getQuestManager().getQuestRecords()));
            setJsonObject(ps, i++, characterDataSerializer.serializeConfigManager(cd.getConfigManager()));
            setJsonObject(ps, i++, characterDataSerializer.serializePopularityRecord(cd.getPopularityRecord()));
            setJsonObject(ps, i++, characterDataSerializer.serializeMiniGameRecord(cd.getMiniGameRecord()));
            setJsonObject(ps, i++, characterDataSerializer.serializeMapTransferInfo(cd.getMapTransferInfo()));
            setJsonObject(ps, i++, characterDataSerializer.serializeWildHunterInfo(cd.getWildHunterInfo()));
            ps.setInt(i++, cd.getItemSnCounter().get());
            ps.setInt(i++, cd.getFriendMax());
            ps.setInt(i++, cd.getPartyId());
            ps.setInt(i++, cd.getGuildId());
            setInstant(ps, i++, cd.getCreationTime());
            setInstant(ps, i++, cd.getMaxLevelTime());

            ps.setInt(i, cd.getCharacterId());
            if (ps.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM " + tableName + " WHERE " + CHARACTER_ID + " = ? AND " + ACCOUNT_ID + " = ?"
        )) {
            ps.setInt(1, characterId);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        return Map.of(); // TODO
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            CHARACTER_ID + " INTEGER PRIMARY KEY, " +
                            ACCOUNT_ID + " INTEGER NOT NULL, " +
                            CHARACTER_NAME + " TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                            CHARACTER_STAT + " " + JSON_TYPE + ", " +
                            CHARACTER_EQUIPPED + " " + JSON_TYPE + ", " +
                            EQUIP_INVENTORY + " " + JSON_TYPE + ", " +
                            CONSUME_INVENTORY + " " + JSON_TYPE + ", " +
                            INSTALL_INVENTORY + " " + JSON_TYPE + ", " +
                            ETC_INVENTORY + " " + JSON_TYPE + ", " +
                            CASH_INVENTORY + " " + JSON_TYPE + ", " +
                            MONEY + " INTEGER NOT NULL, " +
                            EXT_SLOT_EXPIRE + " " + INSTANT_TYPE + ", " +
                            SKILL_COOLTIMES + " " + JSON_TYPE + ", " +
                            SKILL_RECORDS + " " + JSON_TYPE + ", " +
                            QUEST_RECORDS + " " + JSON_TYPE + ", " +
                            CONFIG + " " + JSON_TYPE + ", " +
                            POPULARITY_RECORD + " " + JSON_TYPE + ", " +
                            MINIGAME_RECORD + " " + JSON_TYPE + ", " +
                            MAP_TRANSFER_INFO + " " + JSON_TYPE + ", " +
                            WILD_HUNTER_INFO + " " + JSON_TYPE + ", " +
                            ITEM_SN_COUNTER + " INTEGER NOT NULL, " +
                            FRIEND_MAX + " INTEGER NOT NULL, " +
                            PARTY_ID + " INTEGER NOT NULL, " +
                            GUILD_ID + " INTEGER NOT NULL, " +
                            CREATION_TIME + " " + INSTANT_TYPE + ", " +
                            MAX_LEVEL_TIME + " " + INSTANT_TYPE + ")"
            );

            s.executeUpdate(
                    "CREATE INDEX IF NOT EXISTS idx_character_account_id ON " +
                            tableName + "(" + ACCOUNT_ID + ")"
            );
        }
    }

}
