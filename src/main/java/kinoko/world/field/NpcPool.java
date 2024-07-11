package kinoko.world.field;

import kinoko.packet.field.NpcPacket;
import kinoko.world.field.npc.Npc;

import java.util.Optional;

public final class NpcPool extends FieldObjectPool<Npc> {
    public NpcPool(Field field) {
        super(field);
    }

    public Optional<Npc> getByTemplateId(int templateId) {
        for (Npc npc : getObjects()) {
            if (npc.getTemplateId() == templateId) {
                return Optional.of(npc);
            }
        }
        return Optional.empty();
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
