package kinoko.world.life.npc;

import kinoko.packet.field.NpcPacket;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.npc.NpcTemplate;
import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.field.ControlledObject;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.util.Optional;

public final class Npc extends Life implements ControlledObject, Encodable {
    private final NpcTemplate template;
    private final int rx0;
    private final int rx1;
    private User controller;

    public Npc(NpcTemplate template, int x, int y, int rx0, int rx1, int fh, boolean isFlip) {
        this.template = template;
        this.rx0 = rx0;
        this.rx1 = rx1;

        // Life initialization
        setX(x);
        setY(y);
        setFoothold(fh);
        setMoveAction(isFlip ? 0 : 1);
    }

    public int getTemplateId() {
        return template.getId();
    }

    public boolean isMove() {
        return template.isMove();
    }

    public Optional<String> getScript() {
        if (template == null || template.getScript() == null || template.getScript().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(template.getScript());
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

    @Override
    public String toString() {
        return String.format("Npc { %d, oid : %d, script : %s }", getTemplateId(), getId(), getScript().orElse("-"));
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CNpc::Init
        outPacket.encodeShort(getX()); // ptPos.x
        outPacket.encodeShort(getY()); // ptPos.y
        outPacket.encodeByte(getMoveAction()); // nMoveAction
        outPacket.encodeShort(getFoothold()); // Foothold
        outPacket.encodeShort(rx0); // rgHorz.low
        outPacket.encodeShort(rx1); // rgHorz.high
        outPacket.encodeByte(true); // bEnabled
    }

    public static Npc from(NpcTemplate template, LifeInfo lifeInfo) {
        return new Npc(
                template,
                lifeInfo.getX(),
                lifeInfo.getY(),
                lifeInfo.getRx0(),
                lifeInfo.getRx1(),
                lifeInfo.getFh(),
                lifeInfo.isFlip()
        );
    }
}
