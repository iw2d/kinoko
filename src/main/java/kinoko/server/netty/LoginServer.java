package kinoko.server.netty;

import kinoko.server.Client;
import kinoko.util.Tuple;
import kinoko.world.Account;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LoginServer extends NettyServer {
    private final Map<Integer, Tuple<Client, Account>> connectedAccounts = new ConcurrentHashMap<>();
    private final int port;

    public LoginServer(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    public boolean isConnected(Account account) {
        return connectedAccounts.containsKey(account.getId());
    }

    public void removeAccount(Account account) {
        connectedAccounts.remove(account.getId());
    }

    public boolean isAuthenticated(Client c, Account account) {
        final var tuple = connectedAccounts.get(account.getId());
        if (tuple == null) {
            return false;
        }
        return c.getMachineId() != null &&
                Arrays.equals(c.getMachineId(), tuple.getLeft().getMachineId());
    }

    public void setAuthenticated(Client c, Account account) {
        connectedAccounts.put(account.getId(), new Tuple<>(c, account));
    }
}
