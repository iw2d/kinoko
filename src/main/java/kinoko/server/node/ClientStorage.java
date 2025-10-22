package kinoko.server.node;

import kinoko.world.user.Account;
import kinoko.world.user.User;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ClientStorage {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Client> mapByAccountId = new HashMap<>();
    private final Map<Integer, Client> mapByCharacterId = new HashMap<>();

    public boolean isConnected(Account account) {
        lock.lock();
        try {
            return mapByAccountId.containsKey(account.getId());
        } finally {
            lock.unlock();
        }
    }

    public boolean isConnected(User user) {
        lock.lock();
        try {
            return mapByCharacterId.containsKey(user.getCharacterId());
        } finally {
            lock.unlock();
        }
    }

    public Optional<User> getUserByCharacterId(int characterId) {
        lock.lock();
        try {
            final Client client = mapByCharacterId.get(characterId);
            if (client == null || client.getUser() == null) {
                return Optional.empty();
            }
            return Optional.of(client.getUser());
        } finally {
            lock.unlock();
        }
    }

    public Optional<User> getUserByCharacterName(String characterName) {
        lock.lock();
        try {
            return mapByCharacterId.values().stream()
                    .map(Client::getUser)
                    .filter(Objects::nonNull)
                    .filter(user -> user.getCharacterName().equalsIgnoreCase(characterName))
                    .findFirst();
        } finally {
            lock.unlock();
        }
    }

    public void addClient(Client client) {
        lock.lock();
        try {
            if (client.getAccount() instanceof Account account) {
                mapByAccountId.put(account.getId(), client);
            }
            if (client.getUser() instanceof User user) {
                mapByCharacterId.put(user.getCharacterId(), client);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeClient(Client client) {
        lock.lock();
        try {
            if (client.getAccount() instanceof Account account) {
                mapByAccountId.remove(account.getId());
            }
            if (client.getUser() instanceof User user) {
                mapByCharacterId.remove(user.getCharacterId());
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Client> getConnectedClients() {
        lock.lock();
        try {
            return mapByAccountId.values().stream().toList();
        } finally {
            lock.unlock();
        }
    }

    public List<User> getConnectedUsers() {
        lock.lock();
        try {
            return mapByCharacterId.values().stream()
                    .map(Client::getUser)
                    .filter(Objects::nonNull)
                    .toList();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return mapByAccountId.isEmpty();
        } finally {
            lock.unlock();
        }
    }
}
