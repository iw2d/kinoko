package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import kinoko.database.MigrationAccessor;
import kinoko.database.cassandra.table.MigrationTable;
import kinoko.server.MigrationRequest;
import kinoko.server.ServerConfig;

import java.nio.ByteBuffer;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraMigrationAccessor extends CassandraAccessor implements MigrationAccessor {
    public CassandraMigrationAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private MigrationRequest loadMigrationRequest(Row row) {
        final byte[] machineId = new byte[16];
        final byte[] remoteAddress = new byte[4];
        row.getByteBuffer(MigrationTable.MACHINE_ID).get(machineId);
        row.getByteBuffer(MigrationTable.REMOTE_ADDRESS).get(remoteAddress);
        return new MigrationRequest(
                row.getInt(MigrationTable.ACCOUNT_ID),
                row.getInt(MigrationTable.CHANNEL_ID),
                row.getInt(MigrationTable.CHARACTER_ID),
                machineId,
                remoteAddress
        );
    }

    @Override
    public boolean hasMigrationRequest(int accountId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), MigrationTable.getTableName()).all()
                        .whereColumn(MigrationTable.ACCOUNT_ID).isEqualTo(literal(accountId))
                        .build()
        );
        return selectResult.one() != null;
    }

    @Override
    public boolean submitMigrationRequest(MigrationRequest mr) {
        final ResultSet insertResult = getSession().execute(
                insertInto(getKeyspace(), MigrationTable.getTableName())
                        .value(MigrationTable.ACCOUNT_ID, literal(mr.accountId()))
                        .value(MigrationTable.CHANNEL_ID, literal(mr.channelId()))
                        .value(MigrationTable.CHARACTER_ID, literal(mr.characterId()))
                        .value(MigrationTable.MACHINE_ID, literal(ByteBuffer.wrap(mr.machineId())))
                        .value(MigrationTable.REMOTE_ADDRESS, literal(ByteBuffer.wrap(mr.remoteAddress())))
                        .usingTtl(ServerConfig.MIGRATION_REQUEST_TTL)
                        .build()
        );
        return insertResult.wasApplied();
    }

    @Override
    public Optional<MigrationRequest> fetchMigrationRequest(int characterId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), MigrationTable.getTableName()).all()
                        .whereColumn(MigrationTable.CHARACTER_ID).isEqualTo(literal(characterId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadMigrationRequest(row));
        }
        return Optional.empty();
    }
}
