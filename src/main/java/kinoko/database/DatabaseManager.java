package kinoko.database;

import kinoko.database.cassandra.CassandraConnector;
import kinoko.database.sqlite.SqliteConnector;
import kinoko.server.ServerConstants;

public final class DatabaseManager {
    private static DatabaseConnector connector;

    public static IdAccessor idAccessor() {
        return connector.getIdAccessor();
    }

    public static AccountAccessor accountAccessor() {
        return connector.getAccountAccessor();
    }

    public static CharacterAccessor characterAccessor() {
        return connector.getCharacterAccessor();
    }

    public static FriendAccessor friendAccessor() {
        return connector.getFriendAccessor();
    }

    public static GuildAccessor guildAccessor() {
        return connector.getGuildAccessor();
    }

    public static GiftAccessor giftAccessor() {
        return connector.getGiftAccessor();
    }

    public static MemoAccessor memoAccessor() {
        return connector.getMemoAccessor();
    }

    public static void initialize() {
        switch (ServerConstants.DATABASE_PROVIDER) {
            case "cassandra" -> {
                connector = new CassandraConnector();
            }
            case "sqlite" -> {
                connector = new SqliteConnector();
            }
            default -> {
                throw new IllegalArgumentException("Unknown database provider : " + ServerConstants.DATABASE_PROVIDER);
            }
        }
        connector.initialize();
    }

    public static void shutdown() {
        if (connector != null) {
            connector.shutdown();
        }
    }
}
