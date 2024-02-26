package kinoko.world.field;

import kinoko.world.field.reactor.Reactor;

public final class ReactorPool extends FieldObjectPool<Reactor> {
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

    public boolean removeReactor(Reactor reactor) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(reactor)) {
                return false;
            }
            field.broadcastPacket(reactor.leaveFieldPacket());
            return true;
        } finally {
            lock.unlock();
        }
    }
}
