package kinoko.database;

import kinoko.database.cassandra.CassandraConnector;

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

    public static AllianceAccessor allianceAccessor() {
        return connector.getAllianceAccessor();
    }

    public static GiftAccessor giftAccessor() {
        return connector.getGiftAccessor();
    }

    public static MemoAccessor memoAccessor() {
        return connector.getMemoAccessor();
    }

    public static void initialize() {
        connector = new CassandraConnector();
        connector.initialize();
    }

    public static void shutdown() {
        if (connector != null) {
            connector.shutdown();
        }
    }
}
