package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.GiftAccessor;
import kinoko.server.cashshop.Gift;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.GiftSchema.*;

public final class CassandraGiftAccessor extends CassandraAccessor implements GiftAccessor {
    private static final String tableName = "gift_table";

    public CassandraGiftAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Gift loadGift(Row row) {
        return new Gift(
                row.getLong(GIFT_SN),
                row.getInt(ITEM_ID),
                row.getInt(COMMODITY_ID),
                row.getInt(SENDER_ID),
                row.getString(SENDER_NAME),
                row.getString(SENDER_MESSAGE),
                row.getLong(PAIR_ITEM_SN)
        );
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        final List<Gift> gifts = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(RECEIVER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            gifts.add(loadGift(row));
        }
        return gifts;
    }

    @Override
    public Optional<Gift> getGiftByItemSn(long itemSn) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(GIFT_SN).isEqualTo(literal(itemSn))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadGift(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), tableName)
                        .value(GIFT_SN, literal(gift.getGiftSn()))
                        .value(RECEIVER_ID, literal(receiverId))
                        .value(ITEM_ID, literal(gift.getItemId()))
                        .value(COMMODITY_ID, literal(gift.getCommodityId()))
                        .value(SENDER_NAME, literal(gift.getSenderName()))
                        .value(SENDER_MESSAGE, literal(gift.getSenderMessage()))
                        .value(PAIR_ITEM_SN, literal(gift.getPairItemSn()))
                        .ifNotExists()
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean deleteGift(Gift gift) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), tableName)
                        .whereColumn(GIFT_SN).isEqualTo(literal(gift.getGiftSn()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(GIFT_SN, DataTypes.BIGINT)
                        .withColumn(RECEIVER_ID, DataTypes.INT)
                        .withColumn(ITEM_ID, DataTypes.INT)
                        .withColumn(COMMODITY_ID, DataTypes.INT)
                        .withColumn(SENDER_ID, DataTypes.INT)
                        .withColumn(SENDER_NAME, DataTypes.TEXT)
                        .withColumn(SENDER_MESSAGE, DataTypes.TEXT)
                        .withColumn(PAIR_ITEM_SN, DataTypes.BIGINT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(RECEIVER_ID)
                        .build()
        );
    }
}
