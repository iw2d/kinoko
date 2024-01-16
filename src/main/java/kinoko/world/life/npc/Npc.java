package kinoko.world.life.npc;

import kinoko.packet.life.NpcPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.ControlledObject;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.util.Optional;

public final class Npc extends Life implements ControlledObject {
    private final LifeInfo lifeInfo;
    private final NpcInfo npcInfo;
    private User controller;

    public Npc(LifeInfo lifeInfo, NpcInfo npcInfo) {
        this.lifeInfo = lifeInfo;
        this.npcInfo = npcInfo;

        // Initialization
        setX(lifeInfo.getX());
        setY(lifeInfo.getY());
        setFh(lifeInfo.getFh());
        setMoveAction(lifeInfo.isFlip() ? 1 : 0);
    }

    public int getTemplateId() {
        return lifeInfo.getTemplateId();
    }

    public boolean isMove() {
        return npcInfo.isMove();
    }

    public Optional<String> getScript() {
        if (npcInfo == null || npcInfo.getScript() == null || npcInfo.getScript().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(npcInfo.getScript());
    }

    @Override
    public User getController() {
        return controller;
    }

    @Override
    public void setController(User controller) {
        this.controller = controller;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return NpcPacket.npcEnterField(this);
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return NpcPacket.npcLeaveField(this);
    }

    @Override
    public OutPacket changeControllerPacket(boolean forController) {
        return NpcPacket.npcChangeController(this, forController);
    }

    public void encodeInit(OutPacket outPacket) {
        // CNpc::Init
        outPacket.encodeShort(getX()); // ptPos.x
        outPacket.encodeShort(getY()); // ptPos.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFh()); // Foothold
        outPacket.encodeShort(lifeInfo.getRx0()); // rgHorz.low
        outPacket.encodeShort(lifeInfo.getRx1()); // rgHorz.high
        outPacket.encodeByte(true); // bEnabled
    }
}
