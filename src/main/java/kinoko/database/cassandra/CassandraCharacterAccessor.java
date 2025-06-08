package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import kinoko.database.CharacterAccessor;
import kinoko.database.CharacterInfo;
import kinoko.database.cassandra.table.CharacterTable;
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

public final class CassandraCharacterAccessor extends CassandraAccessor implements CharacterAccessor {

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private CharacterData loadCharacterData(Row row) {
        final int accountId = row.getInt(CharacterTable.ACCOUNT_ID);

        final CharacterData cd = new CharacterData(accountId);

        final CharacterStat cs = row.get(CharacterTable.CHARACTER_STAT, CharacterStat.class);
        cs.setId(row.getInt(CharacterTable.CHARACTER_ID));
        cs.setName(row.getString(CharacterTable.CHARACTER_NAME));
        cd.setCharacterStat(cs);

        final InventoryManager im = new InventoryManager();
        im.setEquipped(row.get(CharacterTable.CHARACTER_EQUIPPED, Inventory.class));
        im.setEquipInventory(row.get(CharacterTable.EQUIP_INVENTORY, Inventory.class));
        im.setConsumeInventory(row.get(CharacterTable.CONSUME_INVENTORY, Inventory.class));
        im.setInstallInventory(row.get(CharacterTable.INSTALL_INVENTORY, Inventory.class));
        im.setEtcInventory(row.get(CharacterTable.ETC_INVENTORY, Inventory.class));
        im.setCashInventory(row.get(CharacterTable.CASH_INVENTORY, Inventory.class));
        im.setMoney(row.getInt(CharacterTable.MONEY));
        im.setExtSlotExpire(row.getInstant(CharacterTable.EXT_SLOT_EXPIRE));
        cd.setInventoryManager(im);

        final SkillManager sm = new SkillManager();
        final Map<Integer, Instant> skillCooltimes = row.getMap(CharacterTable.SKILL_COOLTIMES, Integer.class, Instant.class);
        if (skillCooltimes != null) {
            sm.getSkillCooltimes().putAll(skillCooltimes);
        }
        final List<SkillRecord> skillRecords = row.getList(CharacterTable.SKILL_RECORDS, SkillRecord.class);
        if (skillRecords != null) {
            for (SkillRecord sr : skillRecords) {
                sm.addSkill(sr);
            }
        }
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        final List<QuestRecord> questRecords = row.getList(CharacterTable.QUEST_RECORDS, QuestRecord.class);
        if (questRecords != null) {
            for (QuestRecord qr : questRecords) {
                qm.addQuestRecord(qr);
            }
        }
        cd.setQuestManager(qm);

        final ConfigManager cm = row.get(CharacterTable.CONFIG, ConfigManager.class);
        cd.setConfigManager(cm);

        final PopularityRecord pr = new PopularityRecord();
        final Map<Integer, Instant> popularityRecords = row.getMap(CharacterTable.POPULARITY_RECORD, Integer.class, Instant.class);
        if (popularityRecords != null) {
            pr.getRecords().putAll(popularityRecords);
        }
        cd.setPopularityRecord(pr);

        final MiniGameRecord mgr = row.get(CharacterTable.MINIGAME_RECORD, MiniGameRecord.class);
        cd.setMiniGameRecord(mgr);

        final CoupleRecord cr = CoupleRecord.from(im.getEquipped(), im.getEquipInventory());
        cd.setCoupleRecord(cr);

        final MapTransferInfo mti = row.get(CharacterTable.MAP_TRANSFER_INFO, MapTransferInfo.class);
        cd.setMapTransferInfo(mti);

        final WildHunterInfo whi = row.get(CharacterTable.WILD_HUNTER_INFO, WildHunterInfo.class);
        cd.setWildHunterInfo(whi);

        cd.setItemSnCounter(new AtomicInteger(row.getInt(CharacterTable.ITEM_SN_COUNTER)));
        cd.setFriendMax(row.getInt(CharacterTable.FRIEND_MAX));
        cd.setPartyId(row.getInt(CharacterTable.PARTY_ID));
        cd.setGuildId(row.getInt(CharacterTable.GUILD_ID));
        cd.setCreationTime(row.getInstant(CharacterTable.CREATION_TIME));
        cd.setMaxLevelTime(row.getInstant(CharacterTable.MAX_LEVEL_TIME));
        return cd;
    }

    @Override
    public boolean checkCharacterNameAvailable(String name) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), CharacterTable.getTableName()).all()
                        .whereColumn(CharacterTable.CHARACTER_NAME_INDEX).isEqualTo(literal(lowerName(name)))
                        .build()
        );
        for (Row row : selectResult) {
            final String existingName = row.getString(CharacterTable.CHARACTER_NAME);
            if (existingName != null && existingName.equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), CharacterTable.getTableName()).all()
                        .whereColumn(CharacterTable.CHARACTER_ID).isEqualTo(literal(characterId))
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
                selectFrom(getKeyspace(), CharacterTable.getTableName()).all()
                        .whereColumn(CharacterTable.CHARACTER_NAME_INDEX).isEqualTo(literal(lowerName(name)))
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
                selectFrom(getKeyspace(), CharacterTable.getTableName())
                        .columns(
                                CharacterTable.ACCOUNT_ID,
                                CharacterTable.CHARACTER_ID,
                                CharacterTable.CHARACTER_NAME
                        )
                        .whereColumn(CharacterTable.CHARACTER_NAME_INDEX).isEqualTo(literal(lowerName(name)))
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        for (Row row : selectResult) {
            return Optional.of(new CharacterInfo(
                    row.getInt(CharacterTable.ACCOUNT_ID),
                    row.getInt(CharacterTable.CHARACTER_ID),
                    row.getString(CharacterTable.CHARACTER_NAME)
            ));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getAccountIdByCharacterId(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), CharacterTable.getTableName())
                        .columns(
                                CharacterTable.ACCOUNT_ID
                        )
                        .whereColumn(CharacterTable.CHARACTER_ID).isEqualTo(literal(characterId))
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        for (Row row : selectResult) {
            return Optional.of(row.getInt(CharacterTable.ACCOUNT_ID));
        }
        return Optional.empty();
    }

    @Override
    public List<AvatarData> getAvatarDataByAccountId(int accountId) {
        final List<AvatarData> avatarDataList = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), CharacterTable.getTableName())
                        .columns(
                                CharacterTable.CHARACTER_ID,
                                CharacterTable.CHARACTER_NAME,
                                CharacterTable.CHARACTER_STAT,
                                CharacterTable.CHARACTER_EQUIPPED
                        )
                        .whereColumn(CharacterTable.ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        for (Row row : selectResult) {
            final CharacterStat characterStat = row.get(CharacterTable.CHARACTER_STAT, CharacterStat.class);
            characterStat.setId(row.getInt(CharacterTable.CHARACTER_ID));
            characterStat.setName(row.getString(CharacterTable.CHARACTER_NAME));
            final Inventory equipped = row.get(CharacterTable.CHARACTER_EQUIPPED, Inventory.class);
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
                update(getKeyspace(), CharacterTable.getTableName())
                        .setColumn(CharacterTable.ACCOUNT_ID, literal(characterData.getAccountId()))
                        .setColumn(CharacterTable.CHARACTER_NAME, literal(characterData.getCharacterName()))
                        .setColumn(CharacterTable.CHARACTER_NAME_INDEX, literal(lowerName(characterData.getCharacterName())))
                        .setColumn(CharacterTable.CHARACTER_STAT, literal(characterData.getCharacterStat(), registry))
                        .setColumn(CharacterTable.CHARACTER_EQUIPPED, literal(characterData.getInventoryManager().getEquipped(), registry))
                        .setColumn(CharacterTable.EQUIP_INVENTORY, literal(characterData.getInventoryManager().getEquipInventory(), registry))
                        .setColumn(CharacterTable.CONSUME_INVENTORY, literal(characterData.getInventoryManager().getConsumeInventory(), registry))
                        .setColumn(CharacterTable.INSTALL_INVENTORY, literal(characterData.getInventoryManager().getInstallInventory(), registry))
                        .setColumn(CharacterTable.ETC_INVENTORY, literal(characterData.getInventoryManager().getEtcInventory(), registry))
                        .setColumn(CharacterTable.CASH_INVENTORY, literal(characterData.getInventoryManager().getCashInventory(), registry))
                        .setColumn(CharacterTable.MONEY, literal(characterData.getInventoryManager().getMoney()))
                        .setColumn(CharacterTable.EXT_SLOT_EXPIRE, literal(characterData.getInventoryManager().getExtSlotExpire()))
                        .setColumn(CharacterTable.SKILL_COOLTIMES, literal(characterData.getSkillManager().getSkillCooltimes()))
                        .setColumn(CharacterTable.SKILL_RECORDS, literal(characterData.getSkillManager().getSkillRecords(), registry))
                        .setColumn(CharacterTable.QUEST_RECORDS, literal(characterData.getQuestManager().getQuestRecords(), registry))
                        .setColumn(CharacterTable.CONFIG, literal(characterData.getConfigManager(), registry))
                        .setColumn(CharacterTable.POPULARITY_RECORD, literal(characterData.getPopularityRecord().getRecords(), registry))
                        .setColumn(CharacterTable.MINIGAME_RECORD, literal(characterData.getMiniGameRecord(), registry))
                        .setColumn(CharacterTable.MAP_TRANSFER_INFO, literal(characterData.getMapTransferInfo(), registry))
                        .setColumn(CharacterTable.WILD_HUNTER_INFO, literal(characterData.getWildHunterInfo(), registry))
                        .setColumn(CharacterTable.ITEM_SN_COUNTER, literal(characterData.getItemSnCounter().get()))
                        .setColumn(CharacterTable.FRIEND_MAX, literal(characterData.getFriendMax()))
                        .setColumn(CharacterTable.PARTY_ID, literal(characterData.getPartyId()))
                        .setColumn(CharacterTable.GUILD_ID, literal(characterData.getGuildId()))
                        .setColumn(CharacterTable.CREATION_TIME, literal(characterData.getCreationTime()))
                        .setColumn(CharacterTable.MAX_LEVEL_TIME, literal(characterData.getMaxLevelTime()))
                        .whereColumn(CharacterTable.CHARACTER_ID).isEqualTo(literal(characterData.getCharacterId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteCharacter(int accountId, int characterId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), CharacterTable.getTableName())
                        .whereColumn(CharacterTable.CHARACTER_ID).isEqualTo(literal(characterId))
                        .ifColumn(CharacterTable.ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public Map<Integer, CharacterRank> getCharacterRanks() {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), CharacterTable.getTableName())
                        .columns(
                                CharacterTable.CHARACTER_ID,
                                CharacterTable.CHARACTER_STAT,
                                CharacterTable.MAX_LEVEL_TIME
                        )
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        final List<CharacterRankData> rankDataList = new ArrayList<>();
        for (Row row : selectResult) {
            final int characterId = row.getInt(CharacterTable.CHARACTER_ID);
            final CharacterStat characterStat = row.get(CharacterTable.CHARACTER_STAT, CharacterStat.class);
            final Instant maxLevelTime = row.getInstant(CharacterTable.MAX_LEVEL_TIME);
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

        public int getCharacterId() {
            return characterId;
        }

        public int getJobCategory() {
            return jobCategory;
        }

        public long getCumulativeExp() {
            return cumulativeExp;
        }

        public Instant getMaxLevelTime() {
            return maxLevelTime != null ? maxLevelTime : Instant.MAX;
        }
    }
}
