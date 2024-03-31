package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import kinoko.database.GiftAccessor;
import kinoko.database.cassandra.table.GiftTable;
import kinoko.server.cashshop.Gift;
import kinoko.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraGiftAccessor extends CassandraAccessor implements GiftAccessor {
    public CassandraGiftAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Gift loadGift(Row row) {
        final Item item = row.get(GiftTable.ITEM, Item.class);
        return new Gift(
                item,
                row.getString(GiftTable.SENDER_NAME),
                row.getString(GiftTable.MESSAGE)
        );
    }

    @Override
    public List<Gift> getGiftsByCharacterId(int characterId) {
        final List<Gift> gifts = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), GiftTable.getTableName())
                        .columns(
                                GiftTable.ITEM,
                                GiftTable.SENDER_NAME,
                                GiftTable.MESSAGE
                        )
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
                selectFrom(getKeyspace(), GiftTable.getTableName())
                        .columns(
                                GiftTable.ITEM,
                                GiftTable.SENDER_NAME,
                                GiftTable.MESSAGE
                        )
                        .whereColumn(GiftTable.ITEM_SN).isEqualTo(literal(itemSn))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadGift(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        final CodecRegistry registry = getSession().getContext().getCodecRegistry();
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), GiftTable.getTableName())
                        .value(GiftTable.ITEM_SN, literal(gift.getItemSn()))
                        .value(GiftTable.RECEIVER_ID, literal(receiverId))
                        .value(GiftTable.ITEM, literal(gift.getItem(), registry))
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
                        .whereColumn(GiftTable.ITEM_SN).isEqualTo(literal(gift.getItemSn()))
                        .build()
        );
        return updateResult.wasApplied();
    }
}
