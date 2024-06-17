package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.field.life.MovePath;
import kinoko.world.user.Dragon;
import kinoko.world.user.User;

public final class DragonPacket {
    // CUser::OnDragonPacket -------------------------------------------------------------------------------------------

    public static OutPacket dragonEnterField(User user, Dragon dragon) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DragonEnterField);
        outPacket.encodeInt(user.getCharacterId());
        dragon.encode(outPacket);
        return outPacket;
    }

    public static OutPacket dragonMove(User user, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.DragonMove);
        outPacket.encodeInt(user.getCharacterId());
        movePath.encode(outPacket);
        return outPacket;
    }
}
