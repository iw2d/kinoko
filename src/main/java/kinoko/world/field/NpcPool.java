package kinoko.world.field;

import kinoko.packet.field.NpcPacket;
import kinoko.world.field.npc.Npc;

public final class NpcPool extends FieldObjectPool<Npc> {
    public NpcPool(Field field) {
        super(field);
    }

    public void addNpc(Npc npc) {
        npc.setField(field);
        npc.setId(field.getNewObjectId());
        addObject(npc);
        field.broadcastPacket(NpcPacket.npcEnterField(npc));
        field.getUserPool().assignController(npc);
    }

    public boolean removeNpc(Npc npc) {
        if (!removeObject(npc)) {
            return false;
        }
        field.broadcastPacket(NpcPacket.npcLeaveField(npc));
        return true;
    }
}
