package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.npc.Npc;

public final class NpcPacket {
    // CNpcPool::OnPacket ----------------------------------------------------------------------------------------------

    public static OutPacket npcEnterField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NpcEnterField);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
        npc.encode(outPacket);
        return outPacket;
    }

    public static OutPacket npcLeaveField(Npc npc) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NpcLeaveField);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        return outPacket;
    }

    public static OutPacket npcChangeController(Npc npc, boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NpcChangeController);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(npc.getId()); // dwNpcID
        if (forController) {
            outPacket.encodeInt(npc.getTemplateId()); // dwTemplateID
            npc.encode(outPacket);
        }
        return outPacket;
    }


    // CNpcPool::OnNpcPacket -------------------------------------------------------------------------------------------

    public static OutPacket npcMove(Npc npc, byte oneTimeAction, byte chatIndex, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NpcMove);
        outPacket.encodeInt(npc.getId());
        outPacket.encodeByte(oneTimeAction);
        outPacket.encodeByte(chatIndex);
        if (movePath != null) {
            movePath.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket npcSpecialAction(Npc npc, String action) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NpcSpecialAction);
        outPacket.encodeInt(npc.getId());
        outPacket.encodeString(action);
        return outPacket;
    }
}
