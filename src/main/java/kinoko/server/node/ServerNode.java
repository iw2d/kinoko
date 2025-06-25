package kinoko.server.node;

import kinoko.world.user.Account;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ServerNode extends Node {
    protected static final AtomicInteger requestIdCounter = new AtomicInteger(1);
    protected final ConcurrentHashMap<Integer, CompletableFuture<?>> requestFutures = new ConcurrentHashMap<>();
    protected final ClientStorage clientStorage = new ClientStorage();

    public abstract boolean isInitialized();

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
        if (!client.getSocketChannel().isActive()) {
            throw new IllegalStateException("Tried to add client with inactive socket");
        }
        clientStorage.addClient(client);
    }

    public final void removeClient(Client client) {
        clientStorage.removeClient(client);
        if (isShutdown() && clientStorage.isEmpty()) {
            getShutdownFuture().complete(null);
        }
    }
}
