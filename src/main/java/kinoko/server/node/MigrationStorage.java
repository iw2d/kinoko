package kinoko.server.node;

import kinoko.world.user.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MigrationStorage {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, MigrationInfo> mapByAccountId = new HashMap<>();
    private final Map<Integer, MigrationInfo> mapByCharacterId = new HashMap<>();

    public boolean isMigrating(Account account) {
        lock.lock();
        try {
            final MigrationInfo existingForAccountId = mapByAccountId.get(account.getId());
            return existingForAccountId != null && !existingForAccountId.isExpired();
        } finally {
            lock.unlock();
        }
    }

    public boolean submitMigrationRequest(MigrationInfo migrationInfo) {
        lock.lock();
        try {
            // Confirm that there are no unexpired migration requests
            final MigrationInfo existingForAccountId = mapByAccountId.get(migrationInfo.getAccountId());
            if (existingForAccountId != null && !existingForAccountId.isExpired()) {
                return false;
            }
            final MigrationInfo existingForCharacterId = mapByCharacterId.get(migrationInfo.getCharacterId());
            if (existingForCharacterId != null && !existingForCharacterId.isExpired()) {
                return false;
            }
            // Submit migration request
            mapByAccountId.put(migrationInfo.getAccountId(), migrationInfo);
            mapByCharacterId.put(migrationInfo.getCharacterId(), migrationInfo);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Optional<MigrationInfo> completeMigrationRequest(MigrationInfo migrationInfo) {
        lock.lock();
        try {
            // Confirm migration request is present, unexpired, and matches the request
            final MigrationInfo existingInfo = mapByCharacterId.get(migrationInfo.getCharacterId());
            if (existingInfo == null || existingInfo.isExpired() ||
                    !existingInfo.verify(migrationInfo.getChannelId(), migrationInfo.getAccountId(), migrationInfo.getCharacterId(), migrationInfo.getMachineId(), migrationInfo.getClientKey())) {
                return Optional.empty();
            }
            // Remove migration request
            mapByAccountId.remove(migrationInfo.getAccountId());
            mapByCharacterId.remove(migrationInfo.getCharacterId());
            return Optional.of(existingInfo);
        } finally {
            lock.unlock();
        }
    }
}
