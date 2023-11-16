package kinoko.world.life;

import kinoko.provider.map.LifeInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.ControlledObject;

import java.util.Optional;

public final class Npc extends Life implements ControlledObject {
    private final LifeInfo lifeInfo;
    private final NpcInfo npcInfo;

    public Npc(LifeInfo lifeInfo, NpcInfo npcInfo) {
        this.lifeInfo = lifeInfo;
        this.npcInfo = npcInfo;
    }

    public Optional<String> getScript() {
        if (npcInfo == null || npcInfo.script() == null || npcInfo.script().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(npcInfo.script());
    }

    @Override
    public OutPacket enterFieldPacket() {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_ENTER_FIELD);
        outPacket.encodeInt(getLifeId()); // dwNpcID
        outPacket.encodeInt(lifeInfo.id()); // dwTemplateID
        encodeInit(outPacket);
        return outPacket;
    }

    @Override
    public OutPacket leaveFieldPacket() {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_LEAVE_FIELD);
        outPacket.encodeInt(getLifeId()); // dwNpcID
        return outPacket;
    }

    @Override
    public OutPacket changeControllerPacket(boolean forController) {
        final OutPacket outPacket = OutPacket.of(OutHeader.NPC_CHANGE_CONTROLLER);
        outPacket.encodeByte(forController);
        outPacket.encodeInt(getLifeId()); // dwNpcID
        if (forController) {
            outPacket.encodeInt(lifeInfo.id()); // dwTemplateID
            encodeInit(outPacket);
        }
        return outPacket;
    }

    private void encodeInit(OutPacket outPacket) {
        // CNpc::Init
        outPacket.encodeShort(lifeInfo.x()); // ptPos.x
        outPacket.encodeShort(lifeInfo.y()); // ptPos.y
        outPacket.encodeByte(lifeInfo.f()); // nMoveAction
        outPacket.encodeShort(lifeInfo.fh()); // Foothold
        outPacket.encodeShort(lifeInfo.rx0()); // rgHorz.low
        outPacket.encodeShort(lifeInfo.rx1()); // rgHorz.high
        outPacket.encodeByte(true);
    }
}
