package kinoko.world.social.messenger;

import kinoko.util.Lockable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Messenger implements Lockable<Messenger> {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, MessengerUser> users = new HashMap<>();

    public Map<Integer, MessengerUser> getUsers() {
        return users;
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
