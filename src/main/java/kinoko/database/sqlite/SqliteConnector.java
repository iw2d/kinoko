package kinoko.database.sqlite;

import kinoko.database.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SqliteConnector implements DatabaseConnector {

    public static final String DATABASE_FILE = "kinoko.db";

    private Connection connection;
    private IdAccessor idAccessor;
    private AccountAccessor accountAccessor;
    private CharacterAccessor characterAccessor;
    private FriendAccessor friendAccessor;
    private GuildAccessor guildAccessor;
    private GiftAccessor giftAccessor;
    private MemoAccessor memoAccessor;

    @Override
    public IdAccessor getIdAccessor() {
        return idAccessor;
    }

    @Override
    public AccountAccessor getAccountAccessor() {
        return accountAccessor;
    }

    @Override
    public CharacterAccessor getCharacterAccessor() {
        return characterAccessor;
    }

    @Override
    public FriendAccessor getFriendAccessor() {
        return friendAccessor;
    }

    @Override
    public GuildAccessor getGuildAccessor() {
        return guildAccessor;
    }

    @Override
    public GiftAccessor getGiftAccessor() {
        return giftAccessor;
    }

    @Override
    public MemoAccessor getMemoAccessor() {
        return memoAccessor;
    }

    @Override
    public void initialize() {
        try {
            // Connect to SQLite database (creates file if it does not exist)
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Create Tables
            SqliteIdAccessor.createTable(connection);
            SqliteAccountAccessor.createTable(connection);
            SqliteCharacterAccessor.createTable(connection);
            SqliteFriendAccessor.createTable(connection);
            SqliteGuildAccessor.createTable(connection);
            SqliteGiftAccessor.createTable(connection);
            SqliteMemoAccessor.createTable(connection);

            // Create Accessors
            idAccessor = new SqliteIdAccessor(connection);
            accountAccessor = new SqliteAccountAccessor(connection);
            characterAccessor = new SqliteCharacterAccessor(connection);
            friendAccessor = new SqliteFriendAccessor(connection);
            guildAccessor = new SqliteGuildAccessor(connection);
            giftAccessor = new SqliteGiftAccessor(connection);
            memoAccessor = new SqliteMemoAccessor(connection);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite database", e);
        }
    }

    @Override
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
