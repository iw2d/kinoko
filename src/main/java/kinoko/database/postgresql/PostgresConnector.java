package kinoko.database.postgresql;

import kinoko.database.*;

import java.sql.Connection;
import java.util.TimeZone;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kinoko.server.ServerConstants;

public final class PostgresConnector implements DatabaseConnector {
    private HikariDataSource dataSource;
    private IdAccessor idAccessor;
    private AccountAccessor accountAccessor;
    private CharacterAccessor characterAccessor;
    private FriendAccessor friendAccessor;
    private GuildAccessor guildAccessor;
    private GiftAccessor giftAccessor;
    private MemoAccessor memoAccessor;
    private ItemAccessor itemAccessor;

    @Override
    public void initialize() {
        try {
            // Connect
            String DATABASE_URL = String.format(
                    "jdbc:postgresql://%s:%s/%s",
                    ServerConstants.DATABASE_HOST,
                    ServerConstants.DATABASE_PORT,
                    ServerConstants.DATABASE_NAME
            );
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));  // Set a custom timezone.
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DATABASE_URL);
            config.setUsername(ServerConstants.DATABASE_USER);
            config.setPassword(ServerConstants.DATABASE_PASSWORD);
            config.setMaximumPoolSize(10); // Adjust as needed
            config.setConnectionTimeout(5000); // 5s
            config.setIdleTimeout(60000); // 60s
            config.setMaxLifetime(1800000); // 30min
            config.setLeakDetectionThreshold(5000L);

            dataSource = new HikariDataSource(config);

//            Path initPath = Path.of("src/main/java/kinoko/database/postgresql/setup/init.sql");
//            if (Files.exists(initPath)) {
//                String sql = Files.readString(initPath);
//                try (Statement stmt = connection.createStatement()) {
//                    stmt.execute(sql);
//                }
//            }

            // Create Accessors
            idAccessor = new PostgresIdAccessor(dataSource);
            accountAccessor = new PostgresAccountAccessor(dataSource);
            characterAccessor = new PostgresCharacterAccessor(dataSource);
            friendAccessor = new PostgresFriendAccessor(dataSource);
            guildAccessor = new PostgresGuildAccessor(dataSource);
            giftAccessor = new PostgresGiftAccessor(dataSource);
            memoAccessor = new PostgresMemoAccessor(dataSource);
            itemAccessor = new PostgresItemAccessor(dataSource);



        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize PostgresConnector", e);
        }
    }

    @Override
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close(); // safely closes all pooled connections
            dataSource = null;
        }
    }

    @Override public IdAccessor getIdAccessor() { return idAccessor; }
    @Override public AccountAccessor getAccountAccessor() { return accountAccessor; }
    @Override public CharacterAccessor getCharacterAccessor() { return characterAccessor; }
    @Override public FriendAccessor getFriendAccessor() { return friendAccessor; }
    @Override public GuildAccessor getGuildAccessor() { return guildAccessor; }
    @Override public GiftAccessor getGiftAccessor() { return giftAccessor; }
    @Override public MemoAccessor getMemoAccessor() { return memoAccessor; }
    @Override public ItemAccessor getItemAccessor() {return itemAccessor; }
}
