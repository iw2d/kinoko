package kinoko.server.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class UserStorage {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, RemoteUser> mapByAccountId = new HashMap<>();
    private final Map<Integer, RemoteUser> mapByCharacterId = new HashMap<>();
    private final Map<String, RemoteUser> mapByCharacterName = new HashMap<>();

    public void putUser(RemoteUser remoteUser) {
        lock.lock();
        try {
            mapByAccountId.put(remoteUser.getAccountId(), remoteUser);
            mapByCharacterId.put(remoteUser.getCharacterId(), remoteUser);
            mapByCharacterName.put(normalizeName(remoteUser.getCharacterName()), remoteUser);
        } finally {
            lock.unlock();
        }
    }

    public void removeUser(RemoteUser remoteUser) {
        lock.lock();
        try {
            mapByAccountId.remove(remoteUser.getAccountId());
            mapByCharacterId.remove(remoteUser.getCharacterId());
            mapByCharacterName.remove(normalizeName(remoteUser.getCharacterName()));
        } finally {
            lock.unlock();
        }
    }

    public List<RemoteUser> getUsers() {
        lock.lock();
        try {
            return mapByAccountId.values().stream().toList();
        } finally {
            lock.unlock();
        }
    }

    public Optional<RemoteUser> getByAccountId(int accountId) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByAccountId.get(accountId));
        } finally {
            lock.unlock();
        }
    }

    public Optional<RemoteUser> getByCharacterId(int characterId) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByCharacterId.get(characterId));
        } finally {
            lock.unlock();
        }
    }

    public Optional<RemoteUser> getByCharacterName(String characterName) {
        lock.lock();
        try {
            return Optional.ofNullable(mapByCharacterName.get(normalizeName(characterName)));
        } finally {
            lock.unlock();
        }
    }

    private static String normalizeName(String name) {
        return name.toLowerCase();
    }
}
