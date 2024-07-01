package kinoko.server.migration;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class MigrationStorage {
    private final ConcurrentHashMap<Integer, MigrationInfo> migrationInfos = new ConcurrentHashMap<>(); // account id -> migration info

    public boolean isMigrating(int accountId) {
        final MigrationInfo existingInfo = migrationInfos.get(accountId);
        return existingInfo != null && !existingInfo.isExpired();
    }

    public synchronized boolean submitMigrationRequest(MigrationInfo migrationInfo) {
        if (isMigrating(migrationInfo.getAccountId())) {
            return false;
        }
        migrationInfos.put(migrationInfo.getAccountId(), migrationInfo);
        return true;
    }

    public synchronized Optional<MigrationInfo> completeMigrationRequest(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        final MigrationInfo existingInfo = migrationInfos.get(accountId);
        if (existingInfo == null || existingInfo.isExpired() ||
                !existingInfo.verify(channelId, accountId, characterId, machineId, clientKey)) {
            return Optional.empty();
        }
        migrationInfos.remove(accountId);
        return Optional.of(existingInfo);
    }
}
