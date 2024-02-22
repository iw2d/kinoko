package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ReactorPacket {
    // CReactorPool::OnPacket ------------------------------------------------------------------------------------------

    public static OutPacket reactorChangeState(int reactorId, int state, short x, short y, short delay, byte properEventIndex, byte endDelay) {
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
