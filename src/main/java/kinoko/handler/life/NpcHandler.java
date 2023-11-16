package kinoko.handler.life;

import kinoko.handler.Handler;
import kinoko.packet.life.NpcPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.field.Field;
import kinoko.world.life.Life;
import kinoko.world.life.MovePath;
import kinoko.world.life.Npc;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class NpcHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.NPC_MOVE)
    public static void handleNpcMove(User user, InPacket inPacket) {
        final int lifeId = inPacket.decodeInt(); // dwNpcId
        final byte oneTimeAction = inPacket.decodeByte(); // nOneTimeAction
        final byte chatIndex = inPacket.decodeByte(); // nChatIdx

        final Field field = user.getField();
        final Optional<Life> lifeResult = field.getLifeById(lifeId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Npc npc)) {
            log.error("[NpcHandler] Received NPC_MOVE for invalid life with ID : {}", lifeId);
            return;
        }

        final MovePath movePath = npc.isMove() ? MovePath.decode(inPacket) : null;
        field.broadcastPacket(NpcPacket.npcMove(lifeId, oneTimeAction, chatIndex, movePath));
    }
}
