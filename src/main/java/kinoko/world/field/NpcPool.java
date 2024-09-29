package kinoko.world.field;

import kinoko.packet.field.NpcPacket;
import kinoko.provider.npc.NpcImitateData;
import kinoko.world.field.npc.Npc;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class NpcPool extends FieldObjectPool<Npc> {
    private final ConcurrentHashMap<Integer, NpcImitateData> npcImitateDataMap = new ConcurrentHashMap<>();

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

    public void addNpcImitateData(NpcImitateData npcImitateData) {
        npcImitateDataMap.put(npcImitateData.getNpcId(), npcImitateData);
    }

    public boolean hasNpcImitateData() {
        return !npcImitateDataMap.isEmpty();
    }

    public List<NpcImitateData> getNpcImitateData() {
        return npcImitateDataMap.values().stream().toList();
    }
}
