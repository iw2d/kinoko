package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.GiftAccessor;
import kinoko.database.cassandra.table.GiftTable;
import kinoko.server.cashshop.Gift;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraGiftAccessor extends CassandraAccessor implements GiftAccessor {
    public CassandraGiftAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Gift loadGift(Row row) {
        return new Gift(
                row.getLong(GiftTable.GIFT_SN),
                row.getInt(GiftTable.ITEM_ID),
                row.getInt(GiftTable.COMMODITY_ID),
                row.getString(GiftTable.SENDER_NAME),
                row.getString(GiftTable.MESSAGE)
        );
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        final List<Gift> gifts = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), GiftTable.getTableName()).all()
                        .whereColumn(GiftTable.RECEIVER_ID).isEqualTo(literal(characterId))
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
                selectFrom(getKeyspace(), GiftTable.getTableName()).all()
                        .whereColumn(GiftTable.GIFT_SN).isEqualTo(literal(itemSn))
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
                insertInto(getKeyspace(), GiftTable.getTableName())
                        .value(GiftTable.GIFT_SN, literal(gift.getGiftSn()))
                        .value(GiftTable.RECEIVER_ID, literal(receiverId))
                        .value(GiftTable.ITEM_ID, literal(gift.getItemId()))
                        .value(GiftTable.COMMODITY_ID, literal(gift.getCommodityId()))
                        .value(GiftTable.SENDER_NAME, literal(gift.getSender()))
                        .value(GiftTable.MESSAGE, literal(gift.getMessage()))
                        .ifNotExists()
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public boolean deleteGift(Gift gift) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), GiftTable.getTableName())
                        .whereColumn(GiftTable.GIFT_SN).isEqualTo(literal(gift.getGiftSn()))
                        .build()
        );
        return updateResult.wasApplied();
    }
}
