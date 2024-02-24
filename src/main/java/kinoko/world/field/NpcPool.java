package kinoko.world.field;

import kinoko.world.field.life.npc.Npc;

public final class NpcPool extends FieldObjectPool<Npc> {
    public NpcPool(Field field) {
        super(field);
    }

    public void addNpc(Npc npc) {
        npc.setField(field);
        lock.lock();
        try {
            npc.setId(field.getNewObjectId());
            addObjectUnsafe(npc);
            field.broadcastPacket(npc.enterFieldPacket());
            field.getUserPool().assignController(npc);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeNpc(Npc npc) {
        lock.lock();
        try {
            if (!removeObjectUnsafe(npc)) {
                return false;
            }
            field.broadcastPacket(npc.leaveFieldPacket());
            return true;
        } finally {
            lock.unlock();
        }
    }
}
