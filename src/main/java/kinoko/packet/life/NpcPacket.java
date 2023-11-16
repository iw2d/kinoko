package kinoko.packet.life;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.life.MovePath;
import kinoko.world.life.Npc;

public final class NpcPacket {

    public static OutPacket npcEnterField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_ENTER_FIELD);
        outPacket.encodeInt(npc.getLifeId()); // dwNpcID
        outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
        npc.encodeInit(outPacket);
        return outPacket;
    }

    public static OutPacket npcLeaveField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_LEAVE_FIELD);
        outPacket.encodeInt(npc.getLifeId()); // dwNpcID
        return outPacket;
    }

    public static OutPacket npcChangeController(Npc npc, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_CHANGE_CONTROLLER);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(npc.getLifeId()); // dwNpcID
        if (forController) {
            outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
            npc.encodeInit(outPacket);
        }
        return outPacket;
    }

    public static OutPacket npcMove(int lifeId, byte oneTimeAction, byte chatIndex, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_MOVE);
        outPacket.encodeInt(lifeId);
        outPacket.encodeByte(oneTimeAction);
        outPacket.encodeByte(chatIndex);
        if (movePath != null) {
            movePath.encode(outPacket);
        }
        return outPacket;
    }
}
