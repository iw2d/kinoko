package kinoko.server.messenger;

import kinoko.world.social.messenger.Messenger;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class MessengerStorage {
    private static final AtomicInteger messengerIdCounter = new AtomicInteger(1);

    private final ConcurrentHashMap<Integer, Messenger> messengerMap = new ConcurrentHashMap<>();

    public void addMessenger(Messenger messenger) {
        messengerMap.put(messenger.getMessengerId(), messenger);
    }

    public boolean removeMessenger(Messenger messenger) {
        return messengerMap.remove(messenger.getMessengerId(), messenger);
    }

    public Optional<Messenger> getMessengerById(int messengerId) {
        return Optional.ofNullable(messengerMap.get(messengerId));
    }

    public int getNewMessengerId() {
        return messengerIdCounter.getAndIncrement();
    }
}
