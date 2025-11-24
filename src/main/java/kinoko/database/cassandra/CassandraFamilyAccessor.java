package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.FamilyAccessor;
import kinoko.database.MemoAccessor;
import kinoko.database.cassandra.table.MemoTable;
import kinoko.server.memo.Memo;
import kinoko.server.memo.MemoType;

import java.util.ArrayList;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraFamilyAccessor extends CassandraAccessor implements FamilyAccessor {
    public CassandraFamilyAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }
}
