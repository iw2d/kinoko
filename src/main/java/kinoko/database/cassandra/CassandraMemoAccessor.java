package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.MemoAccessor;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;

import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.MemoSchema.*;

public final class CassandraMemoAccessor extends CassandraAccessor implements MemoAccessor {
    private static final String tableName = "memo_table";

    public CassandraMemoAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        final List<Memo> memos = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                MEMO_ID,
                                MEMO_TYPE,
                                MEMO_CONTENT,
                                SENDER_NAME,
                                DATE_SENT
                        )
                        .whereColumn(RECEIVER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            final MemoType type = MemoType.getByValue(row.getInt(MEMO_TYPE));
            final Memo memo = new Memo(
                    type != null ? type : MemoType.DEFAULT,
                    row.getInt(MEMO_ID),
                    row.getString(SENDER_NAME),
                    row.getString(MEMO_CONTENT),
                    row.getInstant(DATE_SENT)
            );
            memos.add(memo);
        }
        return memos;
    }

    @Override
    public boolean hasMemo(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                RECEIVER_ID
                        )
                        .whereColumn(RECEIVER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            final int receiverId = row.getInt(RECEIVER_ID);
            if (receiverId == characterId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        final ResultSet updateResult = getSession().execute(
                insertInto(getKeyspace(), tableName)
                        .value(MEMO_ID, literal(memo.getMemoId()))
                        .value(RECEIVER_ID, literal(receiverId))
                        .value(MEMO_TYPE, literal(memo.getType().getValue()))
                        .value(MEMO_CONTENT, literal(memo.getContent()))
                        .value(SENDER_NAME, literal(memo.getSender()))
                        .value(DATE_SENT, literal(memo.getDateSent()))
                        .ifNotExists()
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), tableName)
                        .whereColumn(MEMO_ID).isEqualTo(literal(memoId))
                        .build()
        );
        return updateResult.wasApplied();
    }

    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(MEMO_ID, DataTypes.INT)
                        .withColumn(RECEIVER_ID, DataTypes.INT)
                        .withColumn(MEMO_TYPE, DataTypes.INT)
                        .withColumn(MEMO_CONTENT, DataTypes.TEXT)
                        .withColumn(SENDER_NAME, DataTypes.TEXT)
                        .withColumn(DATE_SENT, DataTypes.TIMESTAMP)
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
