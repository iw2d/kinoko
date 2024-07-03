package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class ContiMovePacket {
    // CField_ContiMove::OnPacket --------------------------------------------------------------------------------------

    public static OutPacket enterShipMove() {
        // CShip::EnterShipMove
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTISTATE);
        outPacket.encodeByte(ContiMoveType.DORMANT.getValue());
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

    public static OutPacket appearShip() {
        // CShip::AppearShip
        final OutPacket outPacket = OutPacket.of(OutHeader.CONTISTATE);
        outPacket.encodeByte(ContiMoveType.MOVE.getValue());
        outPacket.encodeByte(1);
        return outPacket;
    }
}
