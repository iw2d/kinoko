package kinoko.world.field;

import kinoko.packet.field.ReactorPacket;
import kinoko.world.GameConstants;
import kinoko.world.field.reactor.Reactor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ReactorPool extends FieldObjectPool<Reactor> {
    private final ConcurrentHashMap<Reactor, Instant> hitReactors = new ConcurrentHashMap<>(); // reactor, next respawn time

    public ReactorPool(Field field) {
        super(field);
    }

    public void addReactor(Reactor reactor) {
        reactor.setField(field);
        reactor.setId(field.getNewObjectId());
        addObject(reactor);
        field.broadcastPacket(reactor.enterFieldPacket());
    }

    public void hitReactor(Reactor reactor, int delay) {
        if (reactor.getReactorTime() > 0) {
            hitReactors.put(reactor, Instant.now().plus(reactor.getReactorTime(), ChronoUnit.SECONDS));
        }
        field.broadcastPacket(ReactorPacket.changeState(reactor, delay, 0, GameConstants.REACTOR_END_DELAY));
    }

    public void expireReactors(Instant now) {
        final var iter = hitReactors.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<Reactor, Instant> entry = iter.next();
            try (var lockedReactor = entry.getKey().acquire()) {
                final Reactor reactor = lockedReactor.get();
                // Check reactor time and reset reactor
                if (now.isBefore(entry.getValue())) {
                    continue;
                }
                iter.remove();
                reactor.reset(reactor.getX(), reactor.getY(), 0);
                field.broadcastPacket(ReactorPacket.changeState(reactor, 0, 0, 0));
            }
        }
    }
}
