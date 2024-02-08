package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import kinoko.database.CharacterAccessor;
import kinoko.database.cassandra.table.CharacterTable;
import kinoko.world.item.Inventory;
import kinoko.world.quest.QuestManager;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterInventory;
import kinoko.world.user.CharacterStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraCharacterAccessor extends CassandraAccessor implements CharacterAccessor {

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private CharacterData loadCharacterData(Row row) {
        final int accountId = row.getInt(CharacterTable.ACCOUNT_ID);
        final int characterId = row.getInt(CharacterTable.CHARACTER_ID);
        final CharacterData cd = new CharacterData(accountId, characterId);
        cd.setCharacterName(row.getString(CharacterTable.CHARACTER_NAME));
        cd.setCharacterStat(row.get(CharacterTable.CHARACTER_STAT, CharacterStat.class));

        final CharacterInventory ci = new CharacterInventory();
        ci.setEquipped(row.get(CharacterTable.CHARACTER_EQUIPPED, Inventory.class));
        ci.setEquipInventory(row.get(CharacterTable.EQUIP_INVENTORY, Inventory.class));
        ci.setConsumeInventory(row.get(CharacterTable.CONSUME_INVENTORY, Inventory.class));
        ci.setInstallInventory(row.get(CharacterTable.INSTALL_INVENTORY, Inventory.class));
        ci.setEtcInventory(row.get(CharacterTable.ETC_INVENTORY, Inventory.class));
        ci.setCashInventory(row.get(CharacterTable.CASH_INVENTORY, Inventory.class));
        ci.setMoney(row.getInt(CharacterTable.MONEY));
        cd.setCharacterInventory(ci);

        final SkillManager sm = new SkillManager();
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        final Set<QuestRecord> questRecords = row.getSet(CharacterTable.QUEST_RECORDS, QuestRecord.class);
        if (questRecords != null) {
            for (QuestRecord qr : questRecords) {
                qm.addQuestRecord(qr);
            }
        }
        cd.setQuestManager(qm);

        cd.setItemSnCounter(new AtomicInteger(row.getInt(CharacterTable.ITEM_SN_COUNTER)));
        cd.setFriendMax(row.getInt(CharacterTable.FRIEND_MAX));
        return cd;
    }

    private String lowerName(String name) {
        return name.toLowerCase();
    }

    @Override
    public Optional<Integer> nextCharacterId() {
        return getNextId(CharacterTable.getTableName());
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
    public List<AvatarData> getAvatarDataByAccount(int accountId) {
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
            final int characterId = row.getInt(CharacterTable.CHARACTER_ID);
            final String characterName = row.getString(CharacterTable.CHARACTER_NAME);
            final CharacterStat characterStat = row.get(CharacterTable.CHARACTER_STAT, CharacterStat.class);
            final Inventory equipped = row.get(CharacterTable.CHARACTER_EQUIPPED, Inventory.class);
            avatarDataList.add(AvatarData.from(characterId, characterName, characterStat, equipped));
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
                        .setColumn(CharacterTable.CHARACTER_EQUIPPED, literal(characterData.getCharacterInventory().getEquipped(), registry))
                        .setColumn(CharacterTable.EQUIP_INVENTORY, literal(characterData.getCharacterInventory().getEquipInventory(), registry))
                        .setColumn(CharacterTable.CONSUME_INVENTORY, literal(characterData.getCharacterInventory().getConsumeInventory(), registry))
                        .setColumn(CharacterTable.INSTALL_INVENTORY, literal(characterData.getCharacterInventory().getInstallInventory(), registry))
                        .setColumn(CharacterTable.ETC_INVENTORY, literal(characterData.getCharacterInventory().getEtcInventory(), registry))
                        .setColumn(CharacterTable.CASH_INVENTORY, literal(characterData.getCharacterInventory().getCashInventory(), registry))
                        .setColumn(CharacterTable.MONEY, literal(characterData.getCharacterInventory().getMoney()))
                        .setColumn(CharacterTable.QUEST_RECORDS, literal(characterData.getQuestManager().getQuestRecords(), registry))
                        .setColumn(CharacterTable.ITEM_SN_COUNTER, literal(characterData.getItemSnCounter().get()))
                        .setColumn(CharacterTable.FRIEND_MAX, literal(characterData.getFriendMax()))
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
}
