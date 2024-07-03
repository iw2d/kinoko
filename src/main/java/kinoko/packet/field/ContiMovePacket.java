package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ContiMovePacket {
    // CField_ContiMove::OnPacket --------------------------------------------------------------------------------------

    public static OutPacket mobGen() {
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTIMOVE);
        outPacket.encodeByte(ContiMoveType.TARGET_MOVEFIELD.getValue());
        outPacket.encodeByte(ContiMoveType.MOBGEN.getValue());
        return outPacket;
    }

    public static OutPacket mobDestroy() {
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTIMOVE);
        outPacket.encodeByte(ContiMoveType.TARGET_MOVEFIELD.getValue());
        outPacket.encodeByte(ContiMoveType.MOBDESTROY.getValue());
        return outPacket;
    }

    public static OutPacket enterShipMove() {
        // CShip::EnterShipMove
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTISTATE);
        outPacket.encodeByte(ContiMoveType.WAIT.getValue());
        outPacket.encodeByte(0);
        return outPacket;
    }

    public static OutPacket leaveShipMove() {
        // CShip::LeaveShipMove
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTISTATE);
        outPacket.encodeByte(ContiMoveType.START.getValue());
        outPacket.encodeByte(0);
        return outPacket;
    }
}
