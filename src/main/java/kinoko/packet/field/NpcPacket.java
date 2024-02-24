package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.life.npc.Npc;

public final class NpcPacket {
    // CNpcPool::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket npcEnterField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_ENTER_FIELD);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
        npc.encode(outPacket);
        return outPacket;
    }

    public static OutPacket npcLeaveField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_LEAVE_FIELD);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        return outPacket;
    }

    public static OutPacket npcChangeController(Npc npc, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_CHANGE_CONTROLLER);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        if (forController) {
            outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
            npc.encode(outPacket);
        }
        return outPacket;
    }


    // CNpcPool::OnNpcPacket -------------------------------------------------------------------------------------------

    public static OutPacket move(Npc npc, byte oneTimeAction, byte chatIndex, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_MOVE);
        outPacket.encodeInt(npc.getId());
        outPacket.encodeByte(oneTimeAction);
        outPacket.encodeByte(chatIndex);
        if (movePath != null) {
            movePath.encode(outPacket);
        }
        return outPacket;
    }
}
