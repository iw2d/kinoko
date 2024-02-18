package kinoko.server.client;

import kinoko.world.Account;
import kinoko.world.user.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ClientStorage {
    private final Map<Integer, Client> connectedAccounts = new ConcurrentHashMap<>(); // accountId -> Client
    private final Map<Integer, Client> connectedUsers = new ConcurrentHashMap<>(); // characterId -> Client

    public boolean isConnected(Account account) {
        return connectedAccounts.containsKey(account.getId());
    }

    public boolean isConnected(User user) {
        return connectedUsers.containsKey(user.getId());
    }

    public void addClient(Client client) {
        final Account account = client.getAccount();
        if (account != null) {
            connectedAccounts.put(account.getId(), client);
        }
        final User user = client.getUser();
        if (user != null) {
            connectedUsers.put(user.getId(), client);
        }
    }

    public void removeClient(Client client) {
        final Account account = client.getAccount();
        if (account != null) {
            connectedAccounts.remove(account.getId());
        }
        final User user = client.getUser();
        if (user != null) {
            connectedUsers.remove(user.getId());
        }
    }

    public Set<Client> getConnectedClients() {
        return connectedAccounts.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public int getUserCount() {
        return connectedUsers.size();
    }
}
