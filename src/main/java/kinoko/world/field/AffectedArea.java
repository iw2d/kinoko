package kinoko.world.field;

import kinoko.server.packet.OutPacket;

public final class AffectedArea extends FieldObjectImpl implements UserObject {
    private final int ownerId;

    public AffectedArea(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public OutPacket enterFieldPacket() {
        return null;
    }

    @Override
    public OutPacket leaveFieldPacket() {
        return null;
    }
}
