package kinoko.server;

import kinoko.world.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class UserStorage {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, UserProxy> mapByAccountId = new HashMap<>();
    private final Map<Integer, UserProxy> mapByCharacterId = new HashMap<>();
    private final Map<String, UserProxy> mapByCharacterName = new HashMap<>();

    public boolean isConnected(Account account) {
        lock.lock();
        try {
            return mapByAccountId.containsKey(account.getId());
        } finally {
            lock.unlock();
        }
    }

    public void addUser(UserProxy userProxy) {
        lock.lock();
        try {
            mapByAccountId.put(userProxy.getAccountId(), userProxy);
            mapByCharacterId.put(userProxy.getCharacterId(), userProxy);
            mapByCharacterName.put(normalizeName(userProxy.getCharacterName()), userProxy);
        } finally {
            lock.unlock();
        }
    }

    public void removeUser(UserProxy userProxy) {
        lock.lock();
        try {
            mapByAccountId.remove(userProxy.getAccountId());
            mapByCharacterId.remove(userProxy.getCharacterId());
            mapByCharacterName.remove(normalizeName(userProxy.getCharacterName()));
        } finally {
            lock.unlock();
        }
    }

    public Optional<UserProxy> getByAccountId(int accountId) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByAccountId.get(accountId));
        } finally {
            lock.unlock();
        }
    }

    public Optional<UserProxy> getByCharacterId(int characterId) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByCharacterId.get(characterId));
        } finally {
            lock.unlock();
        }
    }

    public Optional<UserProxy> getByCharacterName(String characterName) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByCharacterName.get(normalizeName(characterName)));
        } finally {
            lock.unlock();
        }
    }

    public static String normalizeName(String name) {
        return name.toLowerCase();
    }
}
