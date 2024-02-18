package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserCommonPacket;
import kinoko.packet.user.UserLocalPacket;
import kinoko.packet.user.UserRemotePacket;
import kinoko.server.ServerConfig;
import kinoko.server.command.CommandProcessor;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.life.MovePath;
import kinoko.world.skill.HitInfo;
import kinoko.world.skill.HitType;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UserHandler {
    private static final Logger log = LogManager.getLogger(UserHandler.class);

    @Handler(InHeader.USER_MOVE)
    public static void handleUserMove(User user, InPacket inPacket) {
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // dwCrc
        inPacket.decodeInt(); // 0
        inPacket.decodeInt(); // Crc32

        final MovePath movePath = MovePath.decode(inPacket);
        movePath.applyTo(user);
        user.getField().broadcastPacket(UserRemotePacket.userMove(user, movePath), user);
    }

    @Handler(InHeader.USER_SIT_REQUEST)
    public static void handleUserSitRequest(User user, InPacket inPacket) {
        // CUserLocal::HandleXKeyDown, CWvsContext::SendGetUpFromChairRequest
        final short fieldSeatId = inPacket.decodeShort();
        user.write(UserLocalPacket.userSitResult(fieldSeatId != -1, fieldSeatId)); // broadcast not required
    }

    @Handler(InHeader.USER_PORTABLE_CHAIR_SIT_REQUEST)
    public static void handleUserPortableChairSitRequest(User user, InPacket inPacket) {
        // CWvsContext::SendSitOnPortableChairRequest
        final int itemId = inPacket.decodeInt(); // nItemID
        user.getField().broadcastPacket(UserRemotePacket.userSetActivePortableChair(user, itemId), user); // self-cast not required
    }

    @Handler(InHeader.USER_HIT)
    public static void handleUserHit(User user, InPacket inPacket) {
        // CUserLocal::SetDamaged, CUserLocal::Update
        inPacket.decodeInt(); // get_update_time()

        final int attackIndex = inPacket.decodeByte(); // nAttackIdx
        final HitType hitType = HitType.getByValue(attackIndex);
        if (hitType == null) {
            log.error("Unknown hit type (attack index) received : {}", attackIndex);
            return;
        }
        final HitInfo hitInfo = new HitInfo(hitType);
        switch (hitType) {
            case MOB_PHYSICAL, MOB_MAGIC -> {
                hitInfo.magicElemAttr = inPacket.decodeByte(); // nMagicElemAttr
                hitInfo.damage = inPacket.decodeInt(); // nDamage
                hitInfo.templateId = inPacket.decodeInt(); // dwTemplateID
                hitInfo.mobId = inPacket.decodeInt(); // MobID
                hitInfo.dir = inPacket.decodeByte(); // nDir
                hitInfo.reflect = inPacket.decodeByte(); // nX = 0
                hitInfo.guard = inPacket.decodeByte(); // bGuard
                final byte knockback = inPacket.decodeByte(); // (bKnockback != 0) + 1
                if (knockback > 0 || hitInfo.reflect != 0) {
                    hitInfo.powerGuard = inPacket.decodeByte(); // nX != 0 && nPowerGuard != 0
                    hitInfo.reflectMobId = inPacket.decodeInt(); // reflectMobID
                    hitInfo.reflectMobAction = inPacket.decodeByte(); // hitAction
                    hitInfo.reflectMobX = inPacket.decodeShort(); // ptHit.x
                    hitInfo.reflectMobY = inPacket.decodeShort(); // ptHit.y
                    inPacket.decodeShort(); // this->GetPos()->x
                    inPacket.decodeShort(); // this->GetPos()->y
                }
                hitInfo.stance = inPacket.decodeByte(); // bStance | (nSkillID_Stance == 33101006 ? 2 : 0)
            }
            case COUNTER, OBSTACLE -> {
                inPacket.decodeByte(); // 0
                hitInfo.damage = inPacket.decodeInt(); // nDamage
                hitInfo.obstacleData = inPacket.decodeShort(); // dwObstacleData
                inPacket.decodeByte(); // 0
            }
            case STAT -> {
                hitInfo.magicElemAttr = inPacket.decodeByte(); // nElemAttr
                hitInfo.damage = inPacket.decodeInt(); // nDamage
                hitInfo.diseaseData = inPacket.decodeShort(); // dwDiseaseData = (nSkillID << 8) | nSLV
                hitInfo.diseaseType = inPacket.decodeByte(); // 1 : Poison, 2 : AffectedArea, 3 : Shadow of Darkness
            }
        }

        // TODO: update stats
        user.getField().broadcastPacket(UserRemotePacket.userHit(user.getId(), hitInfo), user);
    }

    @Handler(InHeader.USER_CHAT)
    public static void handleUserChat(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final String text = inPacket.decodeString(); // sText
        final boolean onlyBalloon = inPacket.decodeBoolean(); // bOnlyBalloon

        if (text.startsWith(ServerConfig.COMMAND_PREFIX) && CommandProcessor.tryProcessCommand(user, text)) {
            return;
        }

        user.getField().broadcastPacket(UserCommonPacket.userChat(user, 0, text, onlyBalloon));
    }

    @Handler(InHeader.USER_EMOTION)
    public static void handleUserEmotion(User user, InPacket inPacket) {
        final int emotion = inPacket.decodeInt(); // nEmotion
        final int duration = inPacket.decodeInt(); // nDuration
        final boolean isByItemOption = inPacket.decodeBoolean(); // bByItemOption

        user.getField().broadcastPacket(UserRemotePacket.userEmotion(user, emotion, duration, isByItemOption), user);
    }

    @Handler(InHeader.USER_PORTAL_TELEPORT_REQUEST)
    public static void handleUserPortalTeleportRequest(User user, InPacket inPacket) {
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        final String portalName = inPacket.decodeString(); // sPortalName
        final short x = inPacket.decodeShort(); // GetPos()->x
        final short y = inPacket.decodeShort(); // GetPos()->x
        inPacket.decodeShort(); // portal x
        inPacket.decodeShort(); // portal y
    }
}
