package kinoko.database;

import kinoko.database.postgres.PostgresAccountAccessor;
import kinoko.database.postgres.PostgresCharacterAccessor;
import kinoko.database.postgres.PostgresConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DatabaseManager {
    private static final Logger log = LogManager.getLogger(DatabaseManager.class);
    private static ConnectionPool connectionPool;
    private static AccountAccessor accountAccessor;
    private static CharacterAccessor characterAccessor;

    public static AccountAccessor accountAccessor() {
        return accountAccessor;
    }

    public static CharacterAccessor characterAccessor() {
        return characterAccessor;
    }

    public static void initialize() {
        connectionPool = PostgresConnectionPool.useDefault();
        accountAccessor = new PostgresAccountAccessor(connectionPool);
        characterAccessor = new PostgresCharacterAccessor(connectionPool);
    }

    public static void shutdown() {
        connectionPool.shutdown();
    }
}
