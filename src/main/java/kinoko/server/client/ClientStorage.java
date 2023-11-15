package kinoko.server.client;

import kinoko.database.DatabaseManager;
import kinoko.world.Account;
import kinoko.world.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientStorage {
    private final Map<Integer, Client> connectedAccounts = new ConcurrentHashMap<>(); // getAccountId -> Client
    private final Map<Integer, Client> connectedUsers = new ConcurrentHashMap<>(); // getCharacterId -> Client

    public boolean isConnected(Account account) {
        return connectedAccounts.containsKey(account.getId());
    }

    public boolean isConnected(User user) {
        return connectedUsers.containsKey(user.getId());
    }

    public void addPlayer(Client client) {
        final Account account = client.getAccount();
        if (account != null) {
            connectedAccounts.put(account.getId(), client);
        }
        final User user = client.getUser();
        if (user != null) {
            connectedUsers.put(user.getId(), client);
        }
    }

    public void removePlayer(Client client) {
        final Account account = client.getAccount();
        if (account != null) {
            connectedAccounts.remove(account.getId());
            DatabaseManager.accountAccessor().saveAccount(account);
        }
        final User user = client.getUser();
        if (user != null) {
            connectedUsers.remove(user.getId());
            DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());
        }
    }

    public int getUserCount() {
        return connectedUsers.size();
    }
}
