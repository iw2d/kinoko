package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserRemote;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.world.job.JobConstants;
import kinoko.world.skill.*;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AttackHandler {
    private static final Logger log = LogManager.getLogger(AttackHandler.class);

    @Handler(InHeader.USER_MELEE_ATTACK)
    public static void handlerUserMeleeAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMeleeAttack, CUserLocal::TryDoingNormalAttack
        final Attack attack = new Attack(OutHeader.USER_MELEE_ATTACK);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        if (inPacket.getRemaining() == 60) {
            inPacket.decodeByte(); // extra byte is sent when reactor is hit, no other way to detect this
        }
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        attack.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        attack.skillId = inPacket.decodeInt(); // nSkillID
        attack.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        if (SkillConstants.isKeydownSkill(attack.skillId)) {
            attack.keyDown = inPacket.decodeInt(); // tKeyDown
        }
        attack.flag = inPacket.decodeByte();
        attack.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | bLeft << 15

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        attack.attackSpeed = inPacket.decodeByte(); // nAttackSpeed
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        decodeMobAttackInfo(inPacket, attack);

        attack.userX = inPacket.decodeShort(); // GetPos()->x
        attack.userY = inPacket.decodeShort(); // GetPos()->y
        if (attack.skillId == 14111006) {
            attack.grenadeX = inPacket.decodeShort(); // pGrenade->GetPos()->x
            attack.grenadeY = inPacket.decodeShort(); // pGrenade->GetPos()->y
        }

        try (var locked = user.acquire()) {
            SkillProcessor.processAttack(user, attack);
        }
    }

    @Handler(InHeader.USER_SHOOT_ATTACK)
    public static void handlerUserShootAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingShootAttack
        final Attack attack = new Attack(OutHeader.USER_SHOOT_ATTACK);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        attack.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        attack.skillId = inPacket.decodeInt(); // nSkillID
        attack.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        if (SkillConstants.isKeydownSkill(attack.skillId)) {
            attack.keyDown = inPacket.decodeInt(); // tKeyDown
        }
        attack.flag = inPacket.decodeByte();
        attack.exJablin = inPacket.decodeByte(); // bNextShootExJablin && CUserLocal::CheckApplyExJablin
        attack.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | (bLeft << 15)

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        attack.attackSpeed = inPacket.decodeByte(); // nAttackSpeed | (16 * nReduceCount)
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        attack.bulletPosition = inPacket.decodeShort(); // ProperBulletPosition
        inPacket.decodeShort(); // pnCashItemPos
        inPacket.decodeByte(); // nShootRange0a
        if (attack.isSpiritJavelin() && !SkillConstants.isShootSkillNotConsumingBullet(attack.skillId)) {
            attack.bulletItemId = inPacket.decodeInt(); // pnItemID
        }

        decodeMobAttackInfo(inPacket, attack);

        attack.userX = inPacket.decodeShort(); // GetPos()->x
        attack.userY = inPacket.decodeShort(); // GetPos()->y
        if (JobConstants.isWildHunterJob(user.getCharacterStat().getJob())) {
            inPacket.decodeShort(); // ptBodyRelMove.y
        }
        inPacket.decodeShort(); // pt0.x
        inPacket.decodeShort(); // pt0.y
        if (attack.skillId == 15111006) {
            inPacket.decodeInt(); // tReserveSpark
        }

        try (var locked = user.acquire()) {
            SkillProcessor.processAttack(user, attack);
        }
    }

    @Handler(InHeader.USER_MAGIC_ATTACK)
    public static void handlerUserMagicAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMagicAttack
        final Attack attack = new Attack(OutHeader.USER_MAGIC_ATTACK);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        attack.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        attack.skillId = inPacket.decodeInt(); // nSkillID
        attack.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeArray(16); // another DR_check
        inPacket.decodeInt(); // dwInit
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        if (SkillConstants.isMagicKeydownSkill(attack.skillId)) {
            attack.keyDown = inPacket.decodeInt(); // tKeyDown
        }
        attack.flag = inPacket.decodeByte(); // 0
        attack.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | (bLeft << 15)

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        attack.attackSpeed = inPacket.decodeByte(); // nAttackSpeed | (16 * nReduceCount)
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        decodeMobAttackInfo(inPacket, attack);

        attack.userX = inPacket.decodeShort(); // GetPos()->x
        attack.userY = inPacket.decodeShort(); // GetPos()->y
        if (inPacket.decodeBoolean()) {
            attack.dragonX = inPacket.decodeShort();
            attack.dragonY = inPacket.decodeShort();
        }

        try (var locked = user.acquire()) {
            SkillProcessor.processAttack(user, attack);
        }
    }

    @Handler(InHeader.USER_BODY_ATTACK)
    public static void handlerUserBodyAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingBodyAttack
        final Attack attack = new Attack(OutHeader.USER_BODY_ATTACK);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getField().getFieldKey() != fieldKey) {
            user.dispose();
            return;
        }
        inPacket.decodeInt(); // ~pDrInfo.dr0
        inPacket.decodeInt(); // ~pDrInfo.dr1
        attack.mask = inPacket.decodeByte(); // nDamagePerMob | (16 * nMobCount)
        inPacket.decodeInt(); // ~pDrInfo.dr2
        inPacket.decodeInt(); // ~pDrInfo.dr3
        attack.skillId = inPacket.decodeInt(); // nSkillID
        attack.combatOrders = inPacket.decodeByte(); // nCombatOrders
        inPacket.decodeInt(); // dwKey
        inPacket.decodeInt(); // Crc32

        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
        inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC

        attack.flag = inPacket.decodeByte();
        attack.actionAndDir = inPacket.decodeShort(); // nAttackAction & 0x7FFF | bLeft << 15

        inPacket.decodeInt(); // GETCRC32Svr
        inPacket.decodeByte(); // nAttackActionType
        attack.attackSpeed = inPacket.decodeByte(); // nAttackSpeed
        inPacket.decodeInt(); // tAttackTime
        inPacket.decodeInt(); // dwID

        decodeMobAttackInfo(inPacket, attack);

        attack.userX = inPacket.decodeShort(); // GetPos()->x
        attack.userY = inPacket.decodeShort(); // GetPos()->y

        try (var locked = user.acquire()) {
            SkillProcessor.processAttack(user, attack);
        }
    }

    @Handler(InHeader.USER_MOVING_SHOOT_ATTACK_PREPARE)
    public static void handleMovingShootAttackPrepare(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final short actionAndDir = inPacket.decodeShort(); // (nMoveAction & 1) << 15 | random_shoot_attack_action & 0x7FFF
        final byte attackSpeed = inPacket.decodeByte(); // nActionSpeed
        user.getField().broadcastPacket(UserRemote.movingShootAttackPrepare(user, skillId, 1, actionAndDir, attackSpeed), user); // TODO: slv
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
        try (var locked = user.acquire()) {
            SkillProcessor.processHit(user, hitInfo);
        }
    }

    private static void decodeMobAttackInfo(InPacket inPacket, Attack attack) {
        for (int i = 0; i < attack.getMobCount(); i++) {
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
            for (int j = 0; j < attack.getDamagePerMob(); j++) {
                ai.damage[j] = inPacket.decodeInt();
            }
            inPacket.decodeInt(); // CMob::GetCrc
            attack.getAttackInfo().add(ai);
        }
    }
}
