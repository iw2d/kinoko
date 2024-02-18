package kinoko.packet.life;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.life.drop.Drop;
import kinoko.world.life.drop.DropEnterType;
import kinoko.world.life.drop.DropLeaveType;

public final class DropPacket {
    public static OutPacket dropEnterField(Drop drop, DropEnterType enterType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DROP_ENTER_FIELD);
        outPacket.encodeByte(enterType.getValue()); // nEnterType
        outPacket.encodeInt(drop.getObjectId()); // DROP->dwId
        outPacket.encodeByte(drop.isMoney()); // DROP->bIsMoney
        outPacket.encodeInt(drop.isMoney() ? drop.getMoney() : drop.getItem().getItemId()); // DROP->nInfo
        outPacket.encodeInt(drop.getOwnerId()); // DROP->dwOwnerID
        outPacket.encodeByte(drop.getOwnType().getValue()); // DROP->nOwnType
        outPacket.encodeShort(drop.getX());
        outPacket.encodeShort(drop.getY());
        outPacket.encodeInt(drop.getSource().getObjectId()); // DROP->dwSourceID

        if (enterType != DropEnterType.ON_THE_FOOTHOLD) {
            outPacket.encodeShort(drop.getSource().getX()); // source x
            outPacket.encodeShort(drop.getSource().getY()); // source y
            outPacket.encodeShort(0); // tDelay
        }
        if (!drop.isMoney()) {
            outPacket.encodeFT(drop.getItem().getDateExpire()); // m_dateExpire
        }
        outPacket.encodeByte(!drop.isUserDrop()); // bByPet
        outPacket.encodeByte(false); // bool -> IWzGr2DLayer::Putz(0xC0041F15)
        return outPacket;
    }

    public static OutPacket dropLeaveField(Drop drop, DropLeaveType leaveType, int pickUpId, int petId) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DROP_LEAVE_FIELD);
        outPacket.encodeByte(leaveType.getValue());
        outPacket.encodeInt(drop.getObjectId());
        switch (leaveType) {
            case PICKED_UP_BY_USER, PICKED_UP_BY_MOB, PICKED_UP_BY_PET -> {
                outPacket.encodeInt(pickUpId); // dwPickUpID
                if (leaveType == DropLeaveType.PICKED_UP_BY_PET) {
                    outPacket.encodeInt(petId);
                }
            }
            case EXPLODE -> {
                outPacket.encodeShort(0); // delay
            }
        }
        return outPacket;
    }
}
