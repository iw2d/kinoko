package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class PostgresAccessor {
    private final HikariDataSource dataSource;

    public PostgresAccessor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a connection from the pool for a single operation.
     * Use try-with-resources when calling this!
     */
    protected final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Helper to lowercase strings (like usernames)
     */
    protected final String lowerName(String name) {
        return name.toLowerCase();
    }
}
