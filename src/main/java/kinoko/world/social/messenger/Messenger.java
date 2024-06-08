package kinoko.world.social.messenger;

import kinoko.server.node.RemoteUser;
import kinoko.util.Lockable;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class Messenger implements Lockable<Messenger> {
    private final Lock lock = new ReentrantLock();
    private final int messengerId;
    private final Map<Integer, Tuple<RemoteUser, MessengerUser>> users = new HashMap<>();

    public Messenger(int messengerId) {
        this.messengerId = messengerId;
    }

    public int getMessengerId() {
        return messengerId;
    }

    public Map<Integer, MessengerUser> getMessengerUsers() {
        final Map<Integer, MessengerUser> messengerUsers = new HashMap<>();
        for (var entry : users.entrySet()) {
            messengerUsers.put(entry.getKey(), entry.getValue().getRight());
        }
        return messengerUsers;
    }

    public boolean addUser(RemoteUser remoteUser, MessengerUser messengerUser) {
        for (int i = 0; i < GameConstants.MESSENGER_MAX; i++) {
            if (!users.containsKey(i)) {
                users.put(i, new Tuple<>(remoteUser, messengerUser));
                return true;
            }
        }
        return false;
    }

    public int removeUser(RemoteUser remoteUser) {
        final int userIndex = getUserIndex(remoteUser);
        users.remove(userIndex);
        return userIndex;
    }

    public void updateUser(RemoteUser remoteUser) {
        for (int i = 0; i < GameConstants.MESSENGER_MAX; i++) {
            final var tuple = users.get(i);
            if (tuple != null && tuple.getLeft().getCharacterId() == remoteUser.getCharacterId()) {
                users.put(i, new Tuple<>(remoteUser, tuple.getRight()));
            }
        }
    }

    public int getUserIndex(RemoteUser remoteUser) {
        for (var entry : users.entrySet()) {
            if (entry.getValue().getLeft().getCharacterId() == remoteUser.getCharacterId()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public void forEachUser(Consumer<RemoteUser> consumer) {
        for (var tuple : users.values()) {
            consumer.accept(tuple.getLeft());
        }
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
