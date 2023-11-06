package kinoko.database.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kinoko.server.ServerConfig;

import java.sql.Connection;
import java.sql.SQLException;

public final class PostgresConnectionPool implements kinoko.database.ConnectionPool {
    private final HikariDataSource dataSource;
    public PostgresConnectionPool(HikariConfig config) {
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {
        dataSource.close();
    }

    public static PostgresConnectionPool useDefault() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ServerConfig.DATABASE_URL);
        config.setUsername(ServerConfig.DATABASE_USER);
        config.setPassword(ServerConfig.DATABASE_PASS);
        return new PostgresConnectionPool(config);
    }

    public static PostgresConnectionPool useProperties(String propertiesPath) {
        return new PostgresConnectionPool(new HikariConfig(propertiesPath));
    }
}
