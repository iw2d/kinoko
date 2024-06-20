package kinoko.world.field;

import kinoko.packet.field.FieldPacket;
import kinoko.world.user.User;

import java.time.Instant;

public final class OpenGate extends FieldObjectImpl {
    private final User owner;
    private final boolean first;
    private final Instant expireTime;

    private OpenGate secondGate;

    public OpenGate(User owner, boolean first, Instant expireTime) {
        this.owner = owner;
        this.first = first;
        this.expireTime = expireTime;
    }

    public User getOwner() {
        return owner;
    }

    public boolean isFirst() {
        return first;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public OpenGate getSecondGate() {
        return secondGate;
    }

    public void setSecondGate(OpenGate secondGate) {
        assert this.first;
        this.secondGate = secondGate;
    }

    public void setPosition(Field field, int x, int y) {
        setField(field);
        setX(x);
        setY(y);
    }

    public void destroy() {
        if (getSecondGate() != null) {
            getField().broadcastPacket(FieldPacket.openGateRemoved(getOwner(), false, false));
            setSecondGate(null);
        }
        getField().broadcastPacket(FieldPacket.openGateRemoved(getOwner(), true, true));
    }

    @Override
    public int getId() {
        return owner.getCharacterId();
    }

    @Override
    public String toString() {
        return "OpenGate{" +
                "owner=" + owner +
                ", first=" + first +
                ", expireTime=" + expireTime +
                ", secondGate=" + secondGate +
                '}';
    }
}
