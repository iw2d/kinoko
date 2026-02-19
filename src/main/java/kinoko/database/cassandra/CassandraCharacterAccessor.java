package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.database.CharacterRankData;
import kinoko.database.json.CharacterDataSerializer;
import kinoko.database.json.CharacterStatSerializer;
import kinoko.database.json.InventorySerializer;
import kinoko.server.rank.CharacterRank;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryManager;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;
import kinoko.world.user.data.*;
import kinoko.world.user.stat.CharacterStat;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.CharacterDataSchema.*;

public final class CassandraCharacterAccessor extends CassandraAccessor implements CharacterAccessor {
    private static final String tableName = "character_table";
    private static final String characterNameIndex = "character_name_index";
    private final CharacterDataSerializer characterDataSerializer = new CharacterDataSerializer();
    private final CharacterStatSerializer characterStatSerializer = new CharacterStatSerializer();
    private final InventorySerializer inventorySerializer = new InventorySerializer();

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private CharacterData loadCharacterData(Row row) {
        final int accountId = row.getInt(ACCOUNT_ID);

        final CharacterData cd = new CharacterData(accountId);

        final CharacterStat cs = characterStatSerializer.deserialize(getJsonObject(row, CHARACTER_STAT));
        cs.setId(row.getInt(CHARACTER_ID));
        cs.setName(row.getString(CHARACTER_NAME));
        cd.setCharacterStat(cs);

        final InventoryManager im = new InventoryManager();
        im.setEquipped(inventorySerializer.deserialize(getJsonObject(row, CHARACTER_EQUIPPED)));
        im.setEquipInventory(inventorySerializer.deserialize(getJsonObject(row, EQUIP_INVENTORY)));
        im.setConsumeInventory(inventorySerializer.deserialize(getJsonObject(row, CONSUME_INVENTORY)));
        im.setInstallInventory(inventorySerializer.deserialize(getJsonObject(row, INSTALL_INVENTORY)));
        im.setEtcInventory(inventorySerializer.deserialize(getJsonObject(row, ETC_INVENTORY)));
        im.setCashInventory(inventorySerializer.deserialize(getJsonObject(row, CASH_INVENTORY)));
        im.setMoney(row.getInt(MONEY));
        im.setExtSlotExpire(row.getInstant(EXT_SLOT_EXPIRE));
        cd.setInventoryManager(im);

        final SkillManager sm = new SkillManager();
        for (var entry : characterDataSerializer.deserializeSkillCooltimes(getJsonObject(row, SKILL_COOLTIMES)).entrySet()) {
            sm.setSkillCooltime(entry.getKey(), entry.getValue());
        }
        for (SkillRecord record : characterDataSerializer.deserializeSkillRecords(getJsonArray(row, SKILL_RECORDS))) {
            sm.addSkill(record);
        }
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        for (var record : characterDataSerializer.deserializeQuestRecords(getJsonArray(row, QUEST_RECORDS))) {
            qm.addQuestRecord(record);
        }
        cd.setQuestManager(qm);

        final ConfigManager cm = characterDataSerializer.deserializeConfigManager(getJsonObject(row, CONFIG));
        cd.setConfigManager(cm);

        final PopularityRecord pr = characterDataSerializer.deserializePopularityRecord(getJsonObject(row, POPULARITY_RECORD));
        cd.setPopularityRecord(pr);

        final MiniGameRecord mgr = characterDataSerializer.deserializeMiniGameRecord(getJsonObject(row, MINIGAME_RECORD));
        cd.setMiniGameRecord(mgr);

        final CoupleRecord cr = CoupleRecord.from(im.getEquipped(), im.getEquipInventory());
        cd.setCoupleRecord(cr);

        final MapTransferInfo mti = characterDataSerializer.deserializeMapTransferInfo(getJsonObject(row, MAP_TRANSFER_INFO));
        cd.setMapTransferInfo(mti);

        final WildHunterInfo whi = characterDataSerializer.deserializeWildHunterInfo(getJsonObject(row, WILD_HUNTER_INFO));
        cd.setWildHunterInfo(whi);

        cd.setItemSnCounter(new AtomicInteger(row.getInt(ITEM_SN_COUNTER)));
        cd.setFriendMax(row.getInt(FRIEND_MAX));
        cd.setPartyId(row.getInt(PARTY_ID));
        cd.setGuildId(row.getInt(GUILD_ID));
        cd.setCreationTime(row.getInstant(CREATION_TIME));
        cd.setMaxLevelTime(row.getInstant(MAX_LEVEL_TIME));
        return cd;
    }

    @Override
    public boolean checkCharacterNameAvailable(String name) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(characterNameIndex).isEqualTo(literal(lowerName(name)))
                        .build()
        );
        for (Row row : selectResult) {
            final String existingName = row.getString(CHARACTER_NAME);
            if (existingName != null && existingName.equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadCharacterData(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String name) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(characterNameIndex).isEqualTo(literal(lowerName(name)))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadCharacterData(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterInfo> getCharacterInfoByName(String name) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                ACCOUNT_ID,
                                CHARACTER_ID,
                                CHARACTER_NAME
                        )
                        .whereColumn(characterNameIndex).isEqualTo(literal(lowerName(name)))
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        for (Row row : selectResult) {
            return Optional.of(new CharacterInfo(
                    row.getInt(ACCOUNT_ID),
                    row.getInt(CHARACTER_ID),
                    row.getString(CHARACTER_NAME)
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                ACCOUNT_ID
                        )
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterId))
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        for (Row row : selectResult) {
            return Optional.of(row.getInt(ACCOUNT_ID));
        }
        return Optional.empty();
    }

    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
        final List<AvatarData> avatarDataList = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                CHARACTER_ID,
                                CHARACTER_NAME,
                                CHARACTER_STAT,
                                CHARACTER_EQUIPPED
                        )
                        .whereColumn(ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        for (Row row : selectResult) {
            final CharacterStat characterStat = characterStatSerializer.deserialize(getJsonObject(row, CHARACTER_STAT));
            characterStat.setId(row.getInt(CHARACTER_ID));
            characterStat.setName(row.getString(CHARACTER_NAME));
            final Inventory equipped = inventorySerializer.deserialize(getJsonObject(row, CHARACTER_EQUIPPED));
            avatarDataList.add(AvatarData.from(characterStat, equipped));
        }
        return avatarDataList;
    }

    @Override
    public synchronized boolean newCharacter(CharacterData characterData) {
        if (!checkCharacterNameAvailable(characterData.getCharacterName())) {
            return false;
        }
        return saveCharacter(characterData);
    }

    @Override
    public boolean saveCharacter(CharacterData cd) {
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), tableName)
                        .setColumn(ACCOUNT_ID, literal(cd.getAccountId()))
                        .setColumn(CHARACTER_NAME, literal(cd.getCharacterName()))
                        .setColumn(characterNameIndex, literal(lowerName(cd.getCharacterName())))
                        .setColumn(CHARACTER_STAT, literalJsonObject(characterStatSerializer.serialize(cd.getCharacterStat())))
                        .setColumn(CHARACTER_EQUIPPED, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getEquipped())))
                        .setColumn(EQUIP_INVENTORY, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getEquipInventory())))
                        .setColumn(CONSUME_INVENTORY, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getConsumeInventory())))
                        .setColumn(INSTALL_INVENTORY, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getInstallInventory())))
                        .setColumn(ETC_INVENTORY, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getEtcInventory())))
                        .setColumn(CASH_INVENTORY, literalJsonObject(inventorySerializer.serialize(cd.getInventoryManager().getCashInventory())))
                        .setColumn(MONEY, literal(cd.getInventoryManager().getMoney()))
                        .setColumn(EXT_SLOT_EXPIRE, literal(cd.getInventoryManager().getExtSlotExpire()))
                        .setColumn(SKILL_COOLTIMES, literalJsonObject(characterDataSerializer.serializeSkillCooltimes(cd.getSkillManager().getSkillCooltimes())))
                        .setColumn(SKILL_RECORDS, literalJsonArray(characterDataSerializer.serializeSkillRecords(cd.getSkillManager().getSkillRecords())))
                        .setColumn(QUEST_RECORDS, literalJsonArray(characterDataSerializer.serializeQuestRecords(cd.getQuestManager().getQuestRecords())))
                        .setColumn(CONFIG, literalJsonObject(characterDataSerializer.serializeConfigManager(cd.getConfigManager())))
                        .setColumn(POPULARITY_RECORD, literalJsonObject(characterDataSerializer.serializePopularityRecord(cd.getPopularityRecord())))
                        .setColumn(MINIGAME_RECORD, literalJsonObject(characterDataSerializer.serializeMiniGameRecord(cd.getMiniGameRecord())))
                        .setColumn(MAP_TRANSFER_INFO, literalJsonObject(characterDataSerializer.serializeMapTransferInfo(cd.getMapTransferInfo())))
                        .setColumn(WILD_HUNTER_INFO, literalJsonObject(characterDataSerializer.serializeWildHunterInfo(cd.getWildHunterInfo())))
                        .setColumn(ITEM_SN_COUNTER, literal(cd.getItemSnCounter().get()))
                        .setColumn(FRIEND_MAX, literal(cd.getFriendMax()))
                        .setColumn(PARTY_ID, literal(cd.getPartyId()))
                        .setColumn(GUILD_ID, literal(cd.getGuildId()))
                        .setColumn(CREATION_TIME, literal(cd.getCreationTime()))
                        .setColumn(MAX_LEVEL_TIME, literal(cd.getMaxLevelTime()))
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(cd.getCharacterId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), tableName)
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterId))
                        .ifColumn(ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                CHARACTER_ID,
                                CHARACTER_STAT,
                                MAX_LEVEL_TIME
                        )
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        final List<CharacterRankData> rankDataList = new ArrayList<>();
        for (Row row : selectResult) {
            final int characterId = row.getInt(CHARACTER_ID);
            final CharacterStat characterStat = characterStatSerializer.deserialize(getJsonObject(row, CHARACTER_STAT));
            final Instant maxLevelTime = row.getInstant(MAX_LEVEL_TIME);
            final int jobId = characterStat.getJob();
            if (JobConstants.isAdminJob(jobId) || JobConstants.isManagerJob(jobId)) {
                continue;
            }
            rankDataList.add(new CharacterRankData(
                    characterId,
                    JobConstants.getJobCategory(jobId),
                    characterStat.getCumulativeExp(),
                    maxLevelTime
            ));
        }
        // Sort and process rank data
        rankDataList.sort(Comparator.comparing(CharacterRankData::getCumulativeExp).reversed().thenComparing(CharacterRankData::getMaxLevelTime));
        final Map<Integer, Integer> jobRanks = new HashMap<>(); // job rank counter
        final Map<Integer, CharacterRank> characterRanks = new HashMap<>(); // character id -> character rank
        for (CharacterRankData rankData : rankDataList) {
            final int characterId = rankData.getCharacterId();
            final int jobCategory = rankData.getJobCategory();
            final int worldRank = characterRanks.size() + 1;
            final int jobRank = jobRanks.getOrDefault(jobCategory, 0) + 1;
            jobRanks.put(jobCategory, jobRank);
            characterRanks.put(characterId, new CharacterRank(
                    characterId,
                    worldRank,
                    jobRank
            ));
        }
        return characterRanks;
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(CHARACTER_ID, DataTypes.INT)
                        .withColumn(ACCOUNT_ID, DataTypes.INT)
                        .withColumn(CHARACTER_NAME, DataTypes.TEXT)
                        .withColumn(characterNameIndex, DataTypes.TEXT)
                        .withColumn(CHARACTER_STAT, JSON_TYPE)
                        .withColumn(CHARACTER_EQUIPPED, JSON_TYPE)
                        .withColumn(EQUIP_INVENTORY, JSON_TYPE)
                        .withColumn(CONSUME_INVENTORY, JSON_TYPE)
                        .withColumn(INSTALL_INVENTORY, JSON_TYPE)
                        .withColumn(ETC_INVENTORY, JSON_TYPE)
                        .withColumn(CASH_INVENTORY, JSON_TYPE)
                        .withColumn(MONEY, DataTypes.INT)
                        .withColumn(EXT_SLOT_EXPIRE, DataTypes.TIMESTAMP)
                        .withColumn(SKILL_COOLTIMES, JSON_TYPE)
                        .withColumn(SKILL_RECORDS, JSON_TYPE)
                        .withColumn(QUEST_RECORDS, JSON_TYPE)
                        .withColumn(POPULARITY_RECORD, JSON_TYPE)
                        .withColumn(MINIGAME_RECORD, JSON_TYPE)
                        .withColumn(MAP_TRANSFER_INFO, JSON_TYPE)
                        .withColumn(WILD_HUNTER_INFO, JSON_TYPE)
                        .withColumn(CONFIG, JSON_TYPE)
                        .withColumn(ITEM_SN_COUNTER, DataTypes.INT)
                        .withColumn(FRIEND_MAX, DataTypes.INT)
                        .withColumn(PARTY_ID, DataTypes.INT)
                        .withColumn(GUILD_ID, DataTypes.INT)
                        .withColumn(CREATION_TIME, DataTypes.TIMESTAMP)
                        .withColumn(MAX_LEVEL_TIME, DataTypes.TIMESTAMP)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(ACCOUNT_ID)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(characterNameIndex)
                        .build()
        );
    }
}
