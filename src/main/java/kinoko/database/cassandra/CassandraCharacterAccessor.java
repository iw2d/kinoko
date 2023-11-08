package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.CharacterAccessor;
import kinoko.database.cassandra.model.CharacterStatModel;
import kinoko.database.cassandra.model.InventoryModel;
import kinoko.world.item.Inventory;
import kinoko.world.quest.QuestManager;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.AvatarData;
import kinoko.world.user.CharacterData;
import kinoko.world.user.CharacterInventory;
import kinoko.world.user.CharacterStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.udt;
import static kinoko.database.cassandra.model.CharacterDataModel.*;

public class CassandraCharacterAccessor extends CassandraAccessor implements CharacterAccessor {
    public static final String TABLE_NAME = "character";

    public CassandraCharacterAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private CharacterData loadCharacterData(Row row) {
        final int accountId = row.getInt(ACCOUNT_ID.getName());
        final int characterId = row.getInt(CHARACTER_ID.getName());
        final CharacterData cd = new CharacterData(accountId, characterId);
        cd.setCharacterName(row.getString(CHARACTER_NAME.getName()));
        cd.setCharacterStat(row.get(CHARACTER_STAT.getName(), CharacterStat.class));

        final CharacterInventory ci = new CharacterInventory();
        ci.setEquipped(row.get(CHARACTER_EQUIPPED.getName(), Inventory.class));
        ci.setEquipInventory(row.get(EQUIP_INVENTORY.getName(), Inventory.class));
        ci.setConsumeInventory(row.get(CONSUME_INVENTORY.getName(), Inventory.class));
        ci.setInstallInventory(row.get(INSTALL_INVENTORY.getName(), Inventory.class));
        ci.setEtcInventory(row.get(ETC_INVENTORY.getName(), Inventory.class));
        ci.setCashInventory(row.get(CASH_INVENTORY.getName(), Inventory.class));
        ci.setMoney(row.getInt(MONEY.getName()));

        final SkillManager sm = new SkillManager();
        cd.setSkillManager(sm);

        final QuestManager qm = new QuestManager();
        cd.setQuestManager(qm);

        cd.setFriendMax(row.getInt(FRIEND_MAX.getName()));
        return cd;
    }

    @Override
    public Optional<Integer> nextCharacterId() {
        return getNextId(TABLE_NAME);
    }

    @Override
    public Optional<CharacterData> getCharacterById(int characterId) {
        final ResultSet selectResult = getSession().execute(
                QueryBuilder.selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn(CHARACTER_ID.getName()).isEqualTo(literal(characterId))
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
                QueryBuilder.selectFrom(getKeyspace(), TABLE_NAME).all()
                        .whereColumn(CHARACTER_NAME.getName()).isEqualTo(literal(characterName))
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
                QueryBuilder.selectFrom(getKeyspace(), TABLE_NAME)
                        .columns(CHARACTER_STAT.getName(), CHARACTER_EQUIPPED.getName())
                        .whereColumn(ACCOUNT_ID.getName()).isEqualTo(literal(accountId))
                        .build()
        );
        for (Row row : selectResult) {
            final CharacterStat characterStat = row.get(CHARACTER_STAT.getName(), CharacterStat.class);
            final Inventory equipped = row.get(CHARACTER_EQUIPPED.getName(), Inventory.class);
            avatarDataList.add(new AvatarData(characterStat, equipped));
        }
        return avatarDataList;
    }

    @Override
    public boolean saveCharacter(CharacterData characterData) {
        final ResultSet updateResult = getSession().execute(
                QueryBuilder.update(getKeyspace(), TABLE_NAME)
                        .setColumn(ACCOUNT_ID.getName(), literal(characterData.getAccountId()))
                        .setColumn(CHARACTER_NAME.getName(), literal(characterData.getCharacterName()))
                        .setColumn(CHARACTER_STAT.getName(), literal(characterData.getCharacterStat()))
                        .setColumn(CHARACTER_EQUIPPED.getName(), literal(characterData.getCharacterInventory().getEquipped()))
                        .setColumn(EQUIP_INVENTORY.getName(), literal(characterData.getCharacterInventory().getEquipInventory()))
                        .setColumn(CONSUME_INVENTORY.getName(), literal(characterData.getCharacterInventory().getConsumeInventory()))
                        .setColumn(INSTALL_INVENTORY.getName(), literal(characterData.getCharacterInventory().getInstallInventory()))
                        .setColumn(CASH_INVENTORY.getName(), literal(characterData.getCharacterInventory().getEtcInventory()))
                        .setColumn(ETC_INVENTORY.getName(), literal(characterData.getCharacterInventory().getCashInventory()))
                        .setColumn(MONEY.getName(), literal(characterData.getCharacterInventory().getMoney()))
                        .setColumn(FRIEND_MAX.getName(), literal(characterData.getFriendMax()))
                        .whereColumn(CHARACTER_ID.getName()).isEqualTo(literal(characterData.getCharacterId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, TABLE_NAME)
                        .ifNotExists()
                        .withPartitionKey(CHARACTER_ID.getName(), DataTypes.INT)
                        .withClusteringColumn(ACCOUNT_ID.getName(), DataTypes.INT)
                        .withClusteringColumn(CHARACTER_NAME.getName(), DataTypes.TEXT)
                        .withColumn(CHARACTER_STAT.getName(), udt(CharacterStatModel.TYPE_NAME, true))
                        .withColumn(CHARACTER_EQUIPPED.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(EQUIP_INVENTORY.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(CONSUME_INVENTORY.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(INSTALL_INVENTORY.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(ETC_INVENTORY.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(CASH_INVENTORY.getName(), udt(InventoryModel.TYPE_NAME, true))
                        .withColumn(MONEY.getName(), DataTypes.INT)
                        .withColumn(FRIEND_MAX.getName(), DataTypes.INT)
                        .build()
        );
    }
}
