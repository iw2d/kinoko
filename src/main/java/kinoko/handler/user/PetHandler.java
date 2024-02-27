package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.PetPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PetHandler {
    private static final Logger log = LogManager.getLogger(PetHandler.class);

    @Handler(InHeader.PET_MOVE)
    public static void handlePetMove(User user, InPacket inPacket) {
        inPacket.decodeLong(); // liPetLockerSN
        final MovePath movePath = MovePath.decode(inPacket);
        user.getField().broadcastPacket(PetPacket.move(user, 0, movePath), user);
    }

    @Handler(InHeader.PET_ACTION)
    public static void handlePetAction(User user, InPacket inPacket) {
        inPacket.decodeLong(); // liPetLockerSN
        inPacket.decodeInt(); // update_time
        final byte type = inPacket.decodeByte(); // nType
        final byte action = inPacket.decodeByte(); // nAction
        final String chat = inPacket.decodeString(); // sChat
        user.getField().broadcastPacket(PetPacket.action(user, 0, type, action, chat), user);
    }
}
