package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

public abstract class CassandraAccessor {
    private final CqlSession session;
    private final String keyspace;

    public CassandraAccessor(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
    }

    public final CqlSession getSession() {
        return session;
    }

    public final String getKeyspace() {
        return keyspace;
    }
}
