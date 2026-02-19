package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.database.CharacterRankData;
import kinoko.database.cassandra.type.*;
import kinoko.server.rank.CharacterRank;
import kinoko.world.item.Inventory;
import kinoko.world.item.InventoryManager;
import kinoko.world.job.JobConstants;
import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
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

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private CharacterData loadCharacterData(Row row) {
        final int accountId = row.getInt(ACCOUNT_ID);

        final CharacterData cd = new CharacterData(accountId);

        final CharacterStat cs = row.get(CHARACTER_STAT, CharacterStat.class);
        cs.setId(row.getInt(CHARACTER_ID));
        cs.setName(row.getString(CHARACTER_NAME));
        cd.setCharacterStat(cs);

        final InventoryManager im = new InventoryManager();
        im.setEquipped(row.get(CHARACTER_EQUIPPED, Inventory.class));
        im.setEquipInventory(row.get(EQUIP_INVENTORY, Inventory.class));
        im.setConsumeInventory(row.get(CONSUME_INVENTORY, Inventory.class));
        im.setInstallInventory(row.get(INSTALL_INVENTORY, Inventory.class));
        im.setEtcInventory(row.get(ETC_INVENTORY, Inventory.class));
        im.setCashInventory(row.get(CASH_INVENTORY, Inventory.class));
        im.setMoney(row.getInt(MONEY));
        im.setExtSlotExpire(row.getInstant(EXT_SLOT_EXPIRE));
        cd.setInventoryManager(im);

        final SkillManager sm = new SkillManager();
        final Map<Integer, Instant> skillCooltimes = row.getMap(SKILL_COOLTIMES, Integer.class, Instant.class);
        if (skillCooltimes != null) {
            sm.getSkillCooltimes().putAll(skillCooltimes);
        }
        final List<SkillRecord> skillRecords = row.getList(SKILL_RECORDS, SkillRecord.class);
        if (skillRecords != null) {
            for (SkillRecord sr : skillRecords) {
                sm.addSkill(sr);
            }
        }
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        final List<QuestRecord> questRecords = row.getList(QUEST_RECORDS, QuestRecord.class);
        if (questRecords != null) {
            for (QuestRecord qr : questRecords) {
                qm.addQuestRecord(qr);
            }
        }
        cd.setQuestManager(qm);

        final ConfigManager cm = row.get(CONFIG, ConfigManager.class);
        cd.setConfigManager(cm);

        final PopularityRecord pr = new PopularityRecord();
        final Map<Integer, Instant> popularityRecords = row.getMap(POPULARITY_RECORD, Integer.class, Instant.class);
        if (popularityRecords != null) {
            pr.getRecords().putAll(popularityRecords);
        }
        cd.setPopularityRecord(pr);

        final MiniGameRecord mgr = row.get(MINIGAME_RECORD, MiniGameRecord.class);
        cd.setMiniGameRecord(mgr);

        final CoupleRecord cr = CoupleRecord.from(im.getEquipped(), im.getEquipInventory());
        cd.setCoupleRecord(cr);

        final MapTransferInfo mti = row.get(MAP_TRANSFER_INFO, MapTransferInfo.class);
        cd.setMapTransferInfo(mti);

        final WildHunterInfo whi = row.get(WILD_HUNTER_INFO, WildHunterInfo.class);
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
            final CharacterStat characterStat = row.get(CHARACTER_STAT, CharacterStat.class);
            characterStat.setId(row.getInt(CHARACTER_ID));
            characterStat.setName(row.getString(CHARACTER_NAME));
            final Inventory equipped = row.get(CHARACTER_EQUIPPED, Inventory.class);
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
    public boolean saveCharacter(CharacterData characterData) {
        final CodecRegistry registry = getSession().getContext().getCodecRegistry();
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), tableName)
                        .setColumn(ACCOUNT_ID, literal(characterData.getAccountId()))
                        .setColumn(CHARACTER_NAME, literal(characterData.getCharacterName()))
                        .setColumn(characterNameIndex, literal(lowerName(characterData.getCharacterName())))
                        .setColumn(CHARACTER_STAT, literal(characterData.getCharacterStat(), registry))
                        .setColumn(CHARACTER_EQUIPPED, literal(characterData.getInventoryManager().getEquipped(), registry))
                        .setColumn(EQUIP_INVENTORY, literal(characterData.getInventoryManager().getEquipInventory(), registry))
                        .setColumn(CONSUME_INVENTORY, literal(characterData.getInventoryManager().getConsumeInventory(), registry))
                        .setColumn(INSTALL_INVENTORY, literal(characterData.getInventoryManager().getInstallInventory(), registry))
                        .setColumn(ETC_INVENTORY, literal(characterData.getInventoryManager().getEtcInventory(), registry))
                        .setColumn(CASH_INVENTORY, literal(characterData.getInventoryManager().getCashInventory(), registry))
                        .setColumn(MONEY, literal(characterData.getInventoryManager().getMoney()))
                        .setColumn(EXT_SLOT_EXPIRE, literal(characterData.getInventoryManager().getExtSlotExpire()))
                        .setColumn(SKILL_COOLTIMES, literal(characterData.getSkillManager().getSkillCooltimes()))
                        .setColumn(SKILL_RECORDS, literal(characterData.getSkillManager().getSkillRecords(), registry))
                        .setColumn(QUEST_RECORDS, literal(characterData.getQuestManager().getQuestRecords(), registry))
                        .setColumn(CONFIG, literal(characterData.getConfigManager(), registry))
                        .setColumn(POPULARITY_RECORD, literal(characterData.getPopularityRecord().getRecords(), registry))
                        .setColumn(MINIGAME_RECORD, literal(characterData.getMiniGameRecord(), registry))
                        .setColumn(MAP_TRANSFER_INFO, literal(characterData.getMapTransferInfo(), registry))
                        .setColumn(WILD_HUNTER_INFO, literal(characterData.getWildHunterInfo(), registry))
                        .setColumn(ITEM_SN_COUNTER, literal(characterData.getItemSnCounter().get()))
                        .setColumn(FRIEND_MAX, literal(characterData.getFriendMax()))
                        .setColumn(PARTY_ID, literal(characterData.getPartyId()))
                        .setColumn(GUILD_ID, literal(characterData.getGuildId()))
                        .setColumn(CREATION_TIME, literal(characterData.getCreationTime()))
                        .setColumn(MAX_LEVEL_TIME, literal(characterData.getMaxLevelTime()))
                        .whereColumn(CHARACTER_ID).isEqualTo(literal(characterData.getCharacterId()))
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
            final CharacterStat characterStat = row.get(CHARACTER_STAT, CharacterStat.class);
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
                        .withColumn(CHARACTER_STAT, SchemaBuilder.udt(CharacterStatUDT.getTypeName(), false)) // to allow fixing characters in DB
                        .withColumn(CHARACTER_EQUIPPED, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(EQUIP_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(CONSUME_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(INSTALL_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(ETC_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(CASH_INVENTORY, SchemaBuilder.udt(InventoryUDT.getTypeName(), true))
                        .withColumn(MONEY, DataTypes.INT)
                        .withColumn(EXT_SLOT_EXPIRE, DataTypes.TIMESTAMP)
                        .withColumn(SKILL_COOLTIMES, DataTypes.frozenMapOf(DataTypes.INT, DataTypes.TIMESTAMP))
                        .withColumn(SKILL_RECORDS, DataTypes.frozenListOf(SchemaBuilder.udt(SkillRecordUDT.getTypeName(), true)))
                        .withColumn(QUEST_RECORDS, DataTypes.frozenListOf(SchemaBuilder.udt(QuestRecordUDT.getTypeName(), true)))
                        .withColumn(POPULARITY_RECORD, DataTypes.frozenMapOf(DataTypes.INT, DataTypes.TIMESTAMP))
                        .withColumn(MINIGAME_RECORD, SchemaBuilder.udt(MiniGameRecordUDT.getTypeName(), true))
                        .withColumn(MAP_TRANSFER_INFO, SchemaBuilder.udt(MapTransferInfoUDT.getTypeName(), true))
                        .withColumn(WILD_HUNTER_INFO, SchemaBuilder.udt(WildHunterInfoUDT.getTypeName(), true))
                        .withColumn(CONFIG, SchemaBuilder.udt(ConfigUDT.getTypeName(), true))
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
