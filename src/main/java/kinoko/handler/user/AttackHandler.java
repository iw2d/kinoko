package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemotePacket;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.skill.Attack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AttackHandler {
    private static final Logger log = LogManager.getLogger(AttackHandler.class);

    @Handler(InHeader.USER_MELEE_ATTACK)
    public static void handlerUserMeleeAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMeleeAttack, CUserLocal::TryDoingNormalAttack
        final Attack a = new Attack(OutHeader.USER_MELEE_ATTACK);
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        a.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        a.skillId = inPacket.decodeInt(); // nSkillID
        a.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        if (SkillConstants.isKeydownSkill(a.skillId)) {
            a.keyDown = inPacket.decodeInt(); // tKeyDown
        }
        a.flag = inPacket.decodeByte();
        a.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | bLeft << 15

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        a.attackSpeed = inPacket.decodeByte(); // nAttackSpeed
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        decodeMobAttackInfo(inPacket, a);

        a.userX = inPacket.decodeShort(); // GetPos()->x
        a.userY = inPacket.decodeShort(); // GetPos()->y
        if (a.skillId == 14111006) {
            a.grenadeX = inPacket.decodeShort(); // pGrenade->GetPos()->x
            a.grenadeY = inPacket.decodeShort(); // pGrenade->GetPos()->y
        }

        // TODO: handle skills, apply damage
        user.getField().broadcastPacket(UserRemotePacket.userAttack(user, a));
    }

    @Handler(InHeader.USER_SHOOT_ATTACK)
    public static void handlerUserShootAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingShootAttack

    }

    @Handler(InHeader.USER_MAGIC_ATTACK)
    public static void handlerUserMagicAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMagicAttack
        final Attack a = new Attack(OutHeader.USER_MAGIC_ATTACK);
        inPacket.decodeByte(); // bFieldKey
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        a.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        a.skillId = inPacket.decodeInt(); // nSkillID
        a.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeArray(16); // another DR_check
        inPacket.decodeInt(); // dwInit
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        if (SkillConstants.isMagicKeydownSkill(a.skillId)) {
            a.keyDown = inPacket.decodeInt(); // tKeyDown
        }
        a.flag = inPacket.decodeByte(); // 0
        a.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | (bLeft << 15)

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        a.attackSpeed = inPacket.decodeByte(); // nAttackSpeed | (16 * nReduceCount)
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        decodeMobAttackInfo(inPacket, a);

        a.userX = inPacket.decodeShort(); // GetPos()->x
        a.userY = inPacket.decodeShort(); // GetPos()->y
        if (inPacket.decodeBoolean()) {
            a.dragonX = inPacket.decodeShort();
            a.dragonY = inPacket.decodeShort();
        }
    }

    private static void decodeMobAttackInfo(InPacket inPacket, Attack a) {
        for (int i = 0; i < a.getMobCount(); i++) {
            final AttackInfo ai = new AttackInfo();
            ai.mobId = inPacket.decodeInt(); // mobID
            ai.hitAction = inPacket.decodeByte(); // nHitAction
            ai.actionAndDir = inPacket.decodeByte(); // nForeAction & 0x7F | (bLeft << 7)
            inPacket.decodeByte(); // nFrameIdx
            inPacket.decodeByte(); // CalcDamageStatIndex & 0x7F | (bCurTemplate << 7)
            ai.hitX = inPacket.decodeShort(); // ptHit.x
            ai.hitY = inPacket.decodeShort(); // ptHit.y
            inPacket.decodeShort();
            inPacket.decodeShort();
            inPacket.decodeShort(); // tDelay
            for (int j = 0; j < a.getDamagePerMob(); j++) {
                ai.damage[j] = inPacket.decodeInt();
            }
            inPacket.decodeInt(); // CMob::GetCrc
            a.getAttackInfo().add(ai);
        }
    }
}
