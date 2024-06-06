package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.MemoAccessor;
import kinoko.database.cassandra.table.IdTable;
import kinoko.database.cassandra.table.MemoTable;
import kinoko.world.social.memo.Memo;
import kinoko.world.social.memo.MemoType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraMemoAccessor extends CassandraAccessor implements MemoAccessor {
    public CassandraMemoAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    @Override
    public Optional<Integer> nextMemoId() {
        return getNextId(IdTable.MEMO_TABLE);
    }

    @Override
    public List<Memo> getMemosByCharacterId(int characterId) {
        final List<Memo> memos = new ArrayList<>();
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), MemoTable.getTableName())
                        .columns(
                                MemoTable.MEMO_ID,
                                MemoTable.MEMO_TYPE,
                                MemoTable.MEMO_CONTENT,
                                MemoTable.SENDER_NAME,
                                MemoTable.DATE_SENT
                        )
                        .whereColumn(MemoTable.RECEIVER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            final MemoType type = MemoType.getByValue(row.getInt(MemoTable.MEMO_TYPE));
            final Memo memo = new Memo(
                    type != null ? type : MemoType.DEFAULT,
                    row.getInt(MemoTable.MEMO_ID),
                    row.getString(MemoTable.SENDER_NAME),
                    row.getString(MemoTable.MEMO_CONTENT),
                    row.getInstant(MemoTable.DATE_SENT)
            );
            memos.add(memo);
        }
        return memos;
    }

    @Override
    public boolean hasMemo(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), MemoTable.getTableName())
                        .columns(
                                MemoTable.RECEIVER_ID
                        )
                        .whereColumn(MemoTable.RECEIVER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            final int receiverId = row.getInt(MemoTable.RECEIVER_ID);
            if (receiverId == characterId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean newMemo(Memo memo, int receiverId) {
        final ResultSet updateResult = getSession().execute(
                insertInto(getKeyspace(), MemoTable.getTableName())
                        .value(MemoTable.MEMO_ID, literal(memo.getMemoId()))
                        .value(MemoTable.RECEIVER_ID, literal(receiverId))
                        .value(MemoTable.MEMO_TYPE, literal(memo.getType().getValue()))
                        .value(MemoTable.MEMO_CONTENT, literal(memo.getContent()))
                        .value(MemoTable.SENDER_NAME, literal(memo.getSender()))
                        .value(MemoTable.DATE_SENT, literal(memo.getDateSent()))
                        .ifNotExists()
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteMemo(int memoId, int receiverId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), MemoTable.getTableName())
                        .whereColumn(MemoTable.MEMO_ID).isEqualTo(literal(memoId))
                        .build()
        );
        return updateResult.wasApplied();
    }
}
