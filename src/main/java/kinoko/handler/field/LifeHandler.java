package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.field.MobPacket;
import kinoko.packet.field.NpcPacket;
import kinoko.provider.SkillProvider;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobSkill;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.life.Life;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.life.mob.Mob;
import kinoko.world.field.life.mob.MobActionType;
import kinoko.world.field.life.mob.MobAttackInfo;
import kinoko.world.field.life.npc.Npc;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LifeHandler {
    private static final Logger log = LogManager.getLogger(LifeHandler.class);

    @Handler(InHeader.MOB_MOVE)
    public static void handleMobMove(User user, InPacket inPacket) {
        // CMob::GenerateMovePath
        final int objectId = inPacket.decodeInt(); // dwMobID

        final Field field = user.getField();
        final Optional<Life> lifeResult = field.getLifePool().getById(objectId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Mob mob)) {
            log.error("Received MOB_MOVE for invalid life with ID : {}", objectId);
            return;
        }

        final short mobCtrlSn = inPacket.decodeShort(); // nMobCtrlSN
        final byte actionMask = inPacket.decodeByte(); // bDirLeft | (4 * (bRushMove | (2 * bRiseByToss | 2 * nMobCtrlState)))
        final byte actionAndDir = inPacket.decodeByte(); // nActionAndDir
        final int targetInfo = inPacket.decodeInt(); // CMob::TARGETINFO { short x, short y } || { short nSkillIDandLev, short nDelay }

        final List<Tuple<Integer, Integer>> multiTargetForBall = new ArrayList<>();
        final int multiTargetForBallCount = inPacket.decodeInt();
        for (int i = 0; i < multiTargetForBallCount; i++) {
            multiTargetForBall.add(new Tuple<>(
                    inPacket.decodeInt(), // aMultiTargetForBall[i].x
                    inPacket.decodeInt() // aMultiTargetForBall[i].y
            ));
        }
        final List<Integer> randTimeForAreaAttack = new ArrayList<>();
        final int randTimeForAreaAttackCount = inPacket.decodeInt();
        for (int i = 0; i < randTimeForAreaAttackCount; i++) {
            randTimeForAreaAttack.add(inPacket.decodeInt()); // aRandTimeforAreaAttack[i]
        }

        inPacket.decodeByte(); // (bActive == 0) | (16 * !(CVecCtrlMob::IsCheatMobMoveRand(pvcActive) == 0))
        inPacket.decodeInt(); // HackedCode
        inPacket.decodeInt(); // moveCtx.fc.ptTarget->x
        inPacket.decodeInt(); // moveCtx.fc.ptTarget->y
        inPacket.decodeInt(); // dwHackedCodeCRC

        final MovePath movePath = MovePath.decode(inPacket);

        inPacket.decodeByte(); // this->bChasing
        inPacket.decodeByte(); // pTarget != 0
        inPacket.decodeByte(); // pvcActive->bChasing
        inPacket.decodeByte(); // pvcActive->bChasingHack
        inPacket.decodeInt(); // pvcActive->tChaseDuration


        // handle mob attack / skill
        final MobAttackInfo mai = new MobAttackInfo();
        mai.actionMask = actionMask;
        mai.actionAndDir = actionAndDir;
        mai.targetInfo = targetInfo;
        mai.multiTargetForBall = multiTargetForBall;
        mai.randTimeForAreaAttack = randTimeForAreaAttack;

        final int action = mai.actionAndDir >> 1;
        mai.isAttack = action >= MobActionType.ATTACK1.getValue() && action <= MobActionType.ATTACKF.getValue();
        mai.isSkill = action >= MobActionType.SKILL1.getValue() && action <= MobActionType.SKILLF.getValue();

        try (var locked = mob.acquire()) {
            if (mai.isAttack) {
                final int attackIndex = action - MobActionType.ATTACK1.getValue();
                final Optional<MobAttack> mobAttackResult = mob.getAttack(attackIndex);
                if (mobAttackResult.isEmpty()) {
                    log.error("{} : Could not resolve mob attack for index : {}", mob, attackIndex);
                    return;
                }
                final MobAttack ma = mobAttackResult.get();

                log.debug("{} : Using mob attack index {}", mob, attackIndex);
                mob.setMp(Math.max(mob.getMp() - ma.getConMp(), 0));
                mob.setAttackCounter(Util.getRandom(
                        GameConstants.MOB_ATTACK_COOLTIME_MIN,
                        mob.isBoss() ? GameConstants.MOB_ATTACK_COOLTIME_MAX_BOSS : GameConstants.MOB_ATTACK_COOLTIME_MAX
                ));
            } else if (mai.isSkill) {
                final int skillIndex = action - MobActionType.SKILL1.getValue();
                final Optional<MobSkill> mobSkillResult = mob.getSkill(skillIndex);
                if (mobSkillResult.isEmpty()) {
                    log.error("{} : Could not resolve mob skill for index : {}", mob, skillIndex);
                    return;
                }
                final MobSkill ms = mobSkillResult.get();

                mai.skillId = targetInfo & 0xFF;
                mai.slv = (targetInfo >> 8) & 0xFF;
                mai.option = (targetInfo >> 16) & 0xFFFF;
                if (mai.skillId != ms.getSkillId() || mai.slv != ms.getLevel()) {
                    log.error("{} : Mismatching skill ID or level for mob skill index : {} ({}, {})", mob, skillIndex, mai.skillId, mai.slv);
                    return;
                }

                final Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillById(mai.skillId);
                if (skillInfoResult.isEmpty()) {
                    log.error("{} : Could not resolve skill info for mob skill : {}", mob, mai.skillId);
                    return;
                }
                final SkillInfo si = skillInfoResult.get();
                if (mob.isSkillAvailable(ms)) {
                    log.debug("{} : Using mob skill index {} ({}, {})", mob, skillIndex, mai.skillId, mai.slv);
                    mob.setMp(Math.max(mob.getMp() - si.getValue(SkillStat.mpCon, mai.slv), 0));
                    mob.setSkillOnCooltime(ms, Instant.now().plus(si.getValue(SkillStat.interval, mai.slv), ChronoUnit.SECONDS));
                    // TODO: apply effect
                } else {
                    log.error("{} : Mob skill ({}, {}) not available", mob, mai.skillId, mai.slv);
                    mai.skillId = 0;
                    mai.slv = 0;
                    mai.option = 0;
                }
            }

            // update mob position and write response
            final boolean nextAttackPossible = mob.getAndDecrementAttackCounter() <= 0 && Util.succeedProp(GameConstants.MOB_ATTACK_CHANCE);
            movePath.applyTo(mob);
            user.write(MobPacket.ctrlAck(mob, mobCtrlSn, nextAttackPossible, mai));
            field.broadcastPacket(MobPacket.move(mob, mai, movePath), user);
        }
    }

    @Handler(InHeader.MOB_APPLY_CTRL)
    public static void handleMobApplyCtrl(User user, InPacket inPacket) {
        // CMob::ApplyControl
        inPacket.decodeInt(); // dwMobID
        inPacket.decodeInt(); // unk
        // do nothing, since the controller logic is handled elsewhere
    }

    @Handler(InHeader.NPC_MOVE)
    public static void handleNpcMove(User user, InPacket inPacket) {
        final int objectId = inPacket.decodeInt(); // dwNpcId
        final byte oneTimeAction = inPacket.decodeByte(); // nOneTimeAction
        final byte chatIndex = inPacket.decodeByte(); // nChatIdx

        final Field field = user.getField();
        final Optional<Life> lifeResult = field.getLifePool().getById(objectId);
        if (lifeResult.isEmpty() || !(lifeResult.get() instanceof Npc npc)) {
            log.error("Received NPC_MOVE for invalid life with ID : {}", objectId);
            return;
        }

        final MovePath movePath = npc.isMove() ? MovePath.decode(inPacket) : null;
        if (movePath != null) {
            movePath.applyTo(npc);
        }
        field.broadcastPacket(NpcPacket.move(npc, oneTimeAction, chatIndex, movePath));
    }
}
