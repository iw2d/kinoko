package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.CharacterAccessor;
import kinoko.database.cassandra.model.CharacterStatModel;
import kinoko.world.item.Inventory;
import kinoko.world.job.WildHunterInfo;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterInventory;
import kinoko.world.user.CharacterStat;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public class CassandraCharacterAccessor extends CassandraAccessor implements CharacterAccessor {
    public static final String TABLE_NAME = "character";

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Inventory loadInventory(UdtValue udt) {
        final Inventory inventory = new Inventory(24);
        inventory.setSize(64);
        return inventory;
    }

    private CharacterData loadCharacterData(Row row) {
        final CharacterData cd = new CharacterData(
                row.getInt("id")
        );
        cd.setName(row.getString("name"));
        cd.setCharacterStat(row.get("stat", CharacterStat.class));

        final CharacterInventory inventory = new CharacterInventory();
        inventory.setMoney(row.getInt("money"));
        cd.setCharacterInventory(inventory);

        final SkillManager skillManager = new SkillManager();
        skillManager.setSkillRecords(new ConcurrentHashMap<>());
        skillManager.setSkillCooltimes(new ConcurrentHashMap<>());
        cd.setSkillManager(skillManager);

        final QuestManager questManager = new QuestManager();
        questManager.setStartedQuests(new ConcurrentHashMap<>());
        questManager.setCompletedQuests(new ConcurrentHashMap<>());
        questManager.setExQuests(new ConcurrentHashMap<>());
        cd.setQuestManager(questManager);

        final WildHunterInfo wildHunterInfo = new WildHunterInfo();
        cd.setWildHunterInfo(wildHunterInfo);

        cd.setFriendMax(row.getInt("friend_max"));
        return cd;
    }

    @Override
    public Optional<Integer> nextCharacterId() {
        return getNextId(TABLE_NAME);
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn("id").isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadCharacterData(row));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CharacterData> getCharacterByName(String characterName) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn("name").isEqualTo(literal(characterName))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadCharacterData(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean saveCharacter(CharacterData characterData) {
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), TABLE_NAME)
                        .setColumn("name", literal(characterData.getName()))
                        .setColumn("stat", literal(characterData.getCharacterStat()))
                        .setColumn("money", literal(characterData.getCharacterInventory().getMoney()))
                        .setColumn("friend_max", literal(characterData.getFriendMax()))
                        .whereColumn("id").isEqualTo(literal(characterData.getId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, TABLE_NAME)
                        .ifNotExists()
                        .withPartitionKey("id", DataTypes.INT)
                        .withClusteringColumn("name", DataTypes.TEXT)
                        .withColumn("stat", SchemaBuilder.udt(CharacterStatModel.TYPE_NAME, true))
                        .withColumn("money", DataTypes.INT)
                        .withColumn("friend_max", DataTypes.INT)
                        .build()
        );
    }
}
