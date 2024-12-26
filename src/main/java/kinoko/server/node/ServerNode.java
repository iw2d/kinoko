package kinoko.server.node;

import kinoko.util.Util;
import kinoko.world.user.Account;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerNode extends Node {
    protected static final AtomicInteger requestIdCounter = new AtomicInteger(1);
    protected final ConcurrentHashMap<Integer, CompletableFuture<?>> requestFutures = new ConcurrentHashMap<>();
    protected final ClientStorage clientStorage = new ClientStorage();

    public int getNewRequestId() {
        return requestIdCounter.getAndIncrement();
    }

    public final boolean isConnected(Account account) {
        return clientStorage.isConnected(account);
    }

    public final void addClient(Client client) {
        if (isShutdown()) {
            throw new IllegalStateException("Tried to add client after shutdown");
        }
        clientStorage.addClient(client);
    }

    public final void removeClient(Client client) {
        clientStorage.removeClient(client);
        if (isShutdown() && clientStorage.isEmpty()) {
            getShutdownFuture().complete(null);
        }
    }

    protected static byte[] getNewIv() {
        final byte[] iv = new byte[4];
        Util.getRandom().nextBytes(iv);
        return iv;
    }

    protected static byte[] getNewClientKey() {
        final byte[] clientKey = new byte[8];
        Util.getRandom().nextBytes(clientKey);
        return clientKey;
    }
}
