package kinoko.world.field;

import kinoko.packet.field.ReactorPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.reactor.Reactor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class ReactorPool extends FieldObjectPool<Reactor> {
    private final Map<Reactor, Instant> hitReactors = new HashMap<>(); // reactor, last hit time

    public ReactorPool(Field field) {
        super(field);
    }

    public void addReactor(Reactor reactor) {
        reactor.setField(field);
        lock.lock();
        try {
            reactor.setId(field.getNewObjectId());
            addObjectUnsafe(reactor);
            field.broadcastPacket(reactor.enterFieldPacket());
        } finally {
            lock.unlock();
        }
    }

    public void hitReactor(Reactor reactor, int delay) {
        lock.lock();
        try {
            if (reactor.getReactorTime() > 0) {
                hitReactors.put(reactor, Instant.now());
            }
            field.broadcastPacket(ReactorPacket.changeState(reactor, delay, 0, GameConstants.REACTOR_END_DELAY));
        } finally {
            lock.unlock();
        }
    }

    public void resetReactors() {
        lock.lock();
        try {
            final var iter = hitReactors.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<Reactor, Instant> entry = iter.next();
                try (var lockedReactor = entry.getKey().acquire()) {
                    final Reactor reactor = lockedReactor.get();
                    // Check reactor time and reset reactor
                    if (Instant.now().isBefore(entry.getValue().plus(reactor.getReactorTime(), ChronoUnit.SECONDS))) {
                        continue;
                    }
                    iter.remove();
                    reactor.reset(reactor.getX(), reactor.getY(), 0);
                    field.broadcastPacket(ReactorPacket.changeState(reactor, 0, 0, 0));
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
