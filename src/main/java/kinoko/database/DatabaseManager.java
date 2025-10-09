package kinoko.database;

import kinoko.database.postgresql.PostgresConnector;
import kinoko.database.cassandra.CassandraConnector;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.world.GameConstants;

import java.util.List;
import java.util.Objects;

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


    public static boolean isRelational() {
        // Get whether the database connection is a relational database.
        if (connector != null){
            return connector instanceof PostgresConnector;
        }
        return false;
    };


    public static void initialize() {
        if (Objects.equals(ServerConstants.DATABASE_HOST, "cassandra_kinoko")
                || Objects.equals(ServerConstants.DATABASE_TYPE, "cassandra")) {
            connector = new CassandraConnector();
        }
        else if (Objects.equals(ServerConstants.DATABASE_HOST, "postgres_kinoko")
                || (List.of("psql", "postgres", "postgresql").contains(ServerConstants.DATABASE_TYPE))){
            connector = new PostgresConnector();
        }
        else {  // Your choice, defaulting to cassandra.
            connector = new CassandraConnector();
//            connector = new PostgresConnector();
        }
        connector.initialize();
    }

    public static void shutdown() {
        if (connector != null) {
            connector.shutdown();
        }
    }
}
