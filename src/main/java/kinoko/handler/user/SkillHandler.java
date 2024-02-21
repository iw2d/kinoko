package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemote;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.USER_MOVING_SHOOT_ATTACK_PREPARE)
    public static void handleMovingShootAttackPrepare(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final short actionAndDir = inPacket.decodeShort(); // (nMoveAction & 1) << 15 | random_shoot_attack_action & 0x7FFF
        final byte attackSpeed = inPacket.decodeByte(); // nActionSpeed
        user.getField().broadcastPacket(UserRemote.movingShootAttackPrepare(user, skillId, 1, actionAndDir, attackSpeed), user); // TODO: slv
    }

    @Handler(InHeader.USER_SKILL_UP_REQUEST)
    public static void handleUserSkillUpRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final int skillId = inPacket.decodeInt(); // nSkillID
        // TODO
    }

    @Handler(InHeader.USER_SKILL_USE_REQUEST)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        // TODO
    }

    @Handler(InHeader.USER_SKILL_CANCEL_REQUEST)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        user.getField().broadcastPacket(UserRemote.skillCancel(user, skillId), user);
    }

    @Handler(InHeader.USER_SKILL_PREPARE_REQUEST)
    public static void handleUserSkillPrepareRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        final short actionAndDir = inPacket.decodeShort(); // nOneTimeAction & 0x7FFF | (nMoveAction << 15)
        final byte attackSpeed = inPacket.decodeByte(); // attack_speed_degree
        if (skillId == 33101005) {
            inPacket.decodeInt(); // dwSwallowMobID
        }
        user.getField().broadcastPacket(UserRemote.skillPrepare(user, skillId, slv, actionAndDir, attackSpeed), user);
    }
}
