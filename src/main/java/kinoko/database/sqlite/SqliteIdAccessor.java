package kinoko.database.sqlite;

import kinoko.database.IdAccessor;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static kinoko.database.schema.IdSchema.*;

public final class SqliteIdAccessor extends SqliteAccessor implements IdAccessor {
    private static final String tableName = "id_table";

    public SqliteIdAccessor(Connection connection) {
        super(connection);
    }

    private Optional<Integer> getNextId(String idType) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "UPDATE " + tableName + " SET " + NEXT_ID + " = " + NEXT_ID + " + 1 " +
                        "WHERE " + ID_TYPE + " = ? " +
                        "RETURNING " + NEXT_ID + " - 1 as id_result"
        )) {
            ps.setString(1, idType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("id_result"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Integer> nextAccountId() {
        return getNextId(ACCOUNT_ID);
    }

    @Override
    public synchronized Optional<Integer> nextCharacterId() {
        return getNextId(CHARACTER_ID);
    }

    @Override
    public synchronized Optional<Integer> nextPartyId() {
        return getNextId(PARTY_ID);
    }

    @Override
    public synchronized Optional<Integer> nextGuildId() {
        return getNextId(GUILD_ID);
    }

    @Override
    public synchronized Optional<Integer> nextMemoId() {
        return getNextId(MEMO_ID);
    }

    public static void createTable(Connection connection) throws SQLException {
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                            ID_TYPE + " TEXT PRIMARY KEY, " +
                            NEXT_ID + " INTEGER NOT NULL)"
            );
        }
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO " + tableName + " (" +
                        ID_TYPE + ", " +
                        NEXT_ID + ") VALUES(?, ?) ON CONFLICT (" + ID_TYPE + ") DO NOTHING"
        )) {
            for (String idType : List.of(
                    ACCOUNT_ID,
                    CHARACTER_ID,
                    PARTY_ID,
                    GUILD_ID,
                    MEMO_ID
            )) {
                ps.setString(1, idType);
                ps.setInt(2, 1);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
