package kinoko.packet.field;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;

public final class FieldPacket {
    public static OutPacket transferFieldReqIgnored(int failureType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRANSFER_FIELD_REQ_IGNORED);
        outPacket.encodeByte(failureType);
        // 1 : The portal is closed for now
        // 2 : You cannot go to that place
        // 3 : Unable to approach due to the force of the ground
        // 4 : You cannot teleport to or on this map
        // 5 : Unable to approach due to the force of the ground
        // 6 : This map can only be entered by party members
        // 7 : Only members of an expedition can enter this map
        // 8 : The Cash Shop is currently not available
        // default : no message
        return outPacket;
    }

    public static OutPacket transferChannelReqIgnored(int failureType) {
        final OutPacket outPacket = OutPacket.of(OutHeader.TRANSFER_CHANNEL_REQ_IGNORED);
        outPacket.encodeByte(failureType);
        // 1 : Cannot move to that Channel
        // 2 : Cannot go into Cash Shop
        // 3 : Item Trading Shop is currently unavailable
        // 4 : Cannot go into Trade Shop due to user count
        // 5 : Do not meet the minimum level requirement to access the Trade Shop
        // default : no message
        return outPacket;
    }
}