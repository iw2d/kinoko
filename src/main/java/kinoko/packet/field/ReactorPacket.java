package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.reactor.Reactor;

public final class ReactorPacket {
    // CReactorPool::OnPacket ------------------------------------------------------------------------------------------

    public static OutPacket reactorEnterField(Reactor reactor) {
        final OutPacket outPacket = OutPacket.of(OutHeader.REACTOR_ENTER_FIELD);
        outPacket.encodeInt(reactor.getId()); // dwID
        outPacket.encodeInt(reactor.getTemplateId()); // dwTemplateID
        outPacket.encodeByte(reactor.getState()); // nState
        outPacket.encodeShort(reactor.getX()); // ptPos.x
        outPacket.encodeShort(reactor.getY()); // ptPos.y
        outPacket.encodeByte(reactor.isFlip()); // bFlip
        outPacket.encodeString(reactor.getName()); // sName
        return outPacket;
    }

    public static OutPacket reactorLeaveField(Reactor reactor) {
        final OutPacket outPacket = OutPacket.of(OutHeader.REACTOR_ENTER_FIELD);
        outPacket.encodeInt(reactor.getId());
        outPacket.encodeByte(reactor.getState()); // nState
        outPacket.encodeShort(reactor.getX()); // ptPos.x
        outPacket.encodeShort(reactor.getY()); // ptPos.y
        return outPacket;
    }

    public static OutPacket changeState(int reactorId, int state, short x, short y, short delay, byte properEventIndex, byte endDelay) {
        final OutPacket outPacket = OutPacket.of(OutHeader.REACTOR_CHANGE_STATE);
        outPacket.encodeInt(reactorId); // reactorId
        outPacket.encodeByte(state); // nState
        outPacket.encodeShort(x); // ptPos.x
        outPacket.encodeShort(y); // ptPos.y
        outPacket.encodeShort(delay);
        outPacket.encodeByte(properEventIndex);
        outPacket.encodeByte(endDelay); // tStateEnd = update_time + 100 * byte
        return outPacket;
    }
}
