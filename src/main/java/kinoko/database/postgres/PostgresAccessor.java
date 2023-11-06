package kinoko.database.postgres;

import kinoko.database.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class PostgresAccessor {
    protected static final Logger log = LogManager.getLogger(PostgresAccessor.class);
    private final ConnectionPool connectionPool;

    public PostgresAccessor(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    protected Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
}
