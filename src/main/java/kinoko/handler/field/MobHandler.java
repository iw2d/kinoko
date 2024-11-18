package kinoko.handler.field;

import kinoko.handler.Handler;
import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.world.BroadcastPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.provider.MobProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.map.Foothold;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobSkill;
import kinoko.provider.mob.MobSkillType;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.provider.skill.SummonInfo;
import kinoko.script.party.HenesysPQ;
import kinoko.script.quest.EvanQuest;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Rect;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.life.MovePath;
import kinoko.world.field.mob.*;
import kinoko.world.job.explorer.Thief;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillConstants;
import kinoko.world.user.User;
import kinoko.world.user.stat.CalcDamage;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.DefenseStateStat;
import kinoko.world.user.stat.TemporaryStatOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class MobHandler {
    private static final Logger log = LogManager.getLogger(MobHandler.class);

    @Handler(InHeader.MobMove)
    public static void handleMobMove(User user, InPacket inPacket) {
        // CMob::GenerateMovePath
        final int objectId = inPacket.decodeInt(); // dwMobID

        final Field field = user.getField();
        final Optional<Mob> mobResult = field.getMobPool().getById(objectId);
        if (mobResult.isEmpty()) {
            // log.error("Received MobMove for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedMob = mobResult.get().acquire()) {
            final Mob mob = lockedMob.get();
            if (mob.getController() != user) {
                field.getUserPool().setController(mob, user);
            }

            final short mobCtrlSn = inPacket.decodeShort(); // nMobCtrlSN
            final byte actionMask = inPacket.decodeByte(); // bDirLeft | (4 * (bRushMove | (2 * bRiseByToss | 2 * nMobCtrlState)))
            final byte actionAndDir = inPacket.decodeByte(); // nActionAndDir
            final int targetInfo = inPacket.decodeInt(); // CMob::TARGETINFO { short x, short y } || { short nSkillIDandLev, short nDelay }

            final List<Tuple<Integer, Integer>> multiTargetForBall = new ArrayList<>();
            final int multiTargetForBallCount = inPacket.decodeInt();
            for (int i = 0; i < multiTargetForBallCount; i++) {
                multiTargetForBall.add(Tuple.of(
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
            movePath.applyTo(mob);

            inPacket.decodeByte(); // this->bChasing
            inPacket.decodeByte(); // pTarget != 0
            inPacket.decodeByte(); // pvcActive->bChasing
            inPacket.decodeByte(); // pvcActive->bChasingHack
            inPacket.decodeInt(); // pvcActive->tChaseDuration

            // Handle mob attack / skill
            final MobAttackInfo mai = new MobAttackInfo();
            mai.actionMask = actionMask;
            mai.actionAndDir = actionAndDir;
            mai.targetInfo = targetInfo;
            mai.multiTargetForBall = multiTargetForBall;
            mai.randTimeForAreaAttack = randTimeForAreaAttack;
            handleMobAttack(mob, mai);

            // Update client
            final boolean nextAttackPossible = mob.getAndDecrementAttackCounter() <= 0 && Util.succeedProp(GameConstants.MOB_ATTACK_CHANCE);
            final Optional<MobSkill> nextSkillResult = nextAttackPossible ? mob.getNextSkill() : Optional.empty();
            user.write(MobPacket.mobCtrlAck(mob, mobCtrlSn, nextAttackPossible, nextSkillResult.orElse(null)));
            field.broadcastPacket(MobPacket.mobMove(mob, mai, movePath), user);
        }
    }

    @Handler(InHeader.MobApplyCtrl)
    public static void handleMobApplyCtrl(User user, InPacket inPacket) {
        // CMob::ApplyControl
        final int objectId = inPacket.decodeInt(); // dwMobID
        inPacket.decodeInt(); // crc?

        final Field field = user.getField();
        final Optional<Mob> mobResult = field.getMobPool().getById(objectId);
        if (mobResult.isEmpty()) {
            log.error("Received MobApplyCtrl for invalid object with ID : {}", objectId);
            return;
        }
        try (var lockedMob = mobResult.get().acquire()) {
            final Mob mob = lockedMob.get();
            if (!mob.getTemplate().isPickUpDrop() && !mob.getTemplate().isFirstAttack()) {
                log.error("Received invalid MobApplyCtrl request for mob template ID : {}", mob.getTemplateId());
            }
            // Assign controller
            if (mob.getController() == null) {
                field.getUserPool().setController(mob, user);
            } else if (mob.getController() != user) {
                final double userDistance = Util.distance(mob.getX(), mob.getY(), user.getX(), user.getY());
                final double controllerDistance = Util.distance(mob.getX(), mob.getY(), mob.getController().getX(), mob.getController().getY());
                if (userDistance < controllerDistance - 20) {
                    field.getUserPool().setController(mob, user);
                }
            }
        }
    }

    @Handler(InHeader.MobHitByMob)
    public static void handleMobHitByMob(User user, InPacket inPacket) {
        // CMob::Update
        final int attackerMobId = inPacket.decodeInt(); // dwMobID
        inPacket.decodeInt(); // dwCharacterID
        final int targetMobId = inPacket.decodeInt(); // MobID

        // Resolve mobs
        final Field field = user.getField();
        final Optional<Mob> attackerMobResult = field.getMobPool().getById(attackerMobId);
        final Optional<Mob> targetMobResult = field.getMobPool().getById(targetMobId);
        if (attackerMobResult.isEmpty() || targetMobResult.isEmpty()) {
            log.error("Received MobHitByMob for invalid objects : {}, {}", attackerMobId, targetMobId);
            return;
        }
        final Mob attackerMob = attackerMobResult.get();
        final Mob targetMob = targetMobResult.get();
        if (!targetMob.isDamagedByMob()) {
            log.error("Received MobHitByMob for illegal mob template ID : {}", targetMob.getTemplateId());
            return;
        }

        // Apply damage
        try (var lockedMob = targetMob.acquire()) {
            final int damage = calcMobDamage(attackerMob.getTemplate(), targetMob.getTemplate());
            targetMob.setHp(targetMob.getHp() - damage);
            field.broadcastPacket(MobPacket.mobDamaged(targetMob, damage));
            if (targetMob.getHp() > 0) {
                targetMob.resetDropItemPeriod();
                return;
            }
            // Process mob death
            field.getMobPool().removeMob(targetMob, MobLeaveType.ETC);
            switch (targetMob.getTemplateId()) {
                case HenesysPQ.MOON_BUNNY -> {
                    field.broadcastPacket(BroadcastPacket.noticeWithoutPrefix("The Moon Bunny went home because he was sick."));
                }
                case EvanQuest.SAFE_GUARD -> {
                    final Optional<QuestRecord> questRecordResult = user.getQuestManager().getQuestRecord(22583); // Releasing the Free Spirits
                    if (questRecordResult.isPresent()) {
                        final QuestRecord qr = questRecordResult.get();
                        qr.setValue("001");
                        user.write(MessagePacket.questRecord(qr));
                        user.validateStat();
                    }
                }
            }
        }
    }

    @Handler(InHeader.MobAttackMob)
    public static void handleMobAttackMob(User user, InPacket inPacket) {
        // CMob::SetDamagedByMob
        final int attackerMobId = inPacket.decodeInt();
        inPacket.decodeInt(); // CWvsContext->dwCharacterId
        final int attackedMobId = inPacket.decodeInt();
        inPacket.decodeByte(); // vx
        final int damage = inPacket.decodeInt();
        inPacket.decodeByte(); // vy < 0
        inPacket.decodeShort(); // (rcBody.right + rcBody.left) / 2
        inPacket.decodeShort(); // (rcBody.top + rcBody.bottom) / 2

        final Field field = user.getField();
        final Optional<Mob> mobResult = field.getMobPool().getById(attackedMobId);
        if (mobResult.isEmpty()) {
            log.error("Received MobAttackMob for invalid attacked mob ID : {}", attackedMobId);
            return;
        }
        try (var lockedMob = mobResult.get().acquire()) {
            final Mob mob = lockedMob.get();
            mob.damage(user, damage, 0);
            mob.getField().broadcastPacket(MobPacket.mobDamaged(mob, damage), user);
        }
    }

    @Handler(InHeader.MobTimeBombEnd)
    public static void handleMobTimeBombEnd(User user, InPacket inPacket) {
        // CMob::UpdateTimeBomb
        final int objectId = inPacket.decodeInt(); // dwMobID

        final Field field = user.getField();
        final Optional<Mob> mobResult = field.getMobPool().getById(objectId);
        if (mobResult.isEmpty()) {
            log.error("Received MobTimeBombEnd for invalid object with ID : {}", objectId);
            return;
        }
        final Mob mob = mobResult.get();
        if (mob.isBoss()) {
            inPacket.decodeInt(); // (rcBody.right + rcBody.left) / 2)
            inPacket.decodeInt(); // (rcBody.bottom + rcBody.top) / 2)
        }
        final int x = inPacket.decodeInt(); // user x
        final int y = inPacket.decodeInt(); // user y

        try (var lockedMob = mob.acquire()) {
            if (!mob.getMobStat().hasOption(MobTemporaryStat.TimeBomb)) {
                log.error("Received MobTimeBombEnd for mob ID : {} without TimeBomb stat", mob.getId());
                return;
            }
            mob.resetTemporaryStat(Set.of(MobTemporaryStat.TimeBomb));
            // Damage user if within range
            if (mob.getRelativeRect(SkillConstants.MONSTER_BOMB_RANGE).isInsideRect(x, y)) {
                try (var locked = user.acquire()) {
                    final int damage = (int) Math.min(CalcDamage.calcDamageMax(user), user.getHp() - 100);
                    user.addHp(-damage);
                    user.write(UserLocal.timeBombAttack(Thief.MONSTER_BOMB, mob.getX(), mob.getY(), 120, damage));
                }
            } else {
                user.write(UserLocal.timeBombAttack(Thief.MONSTER_BOMB, mob.getX(), mob.getY(), 0, 0));
            }
        }
    }

    private static void handleMobAttack(Mob mob, MobAttackInfo mai) {
        final int action = mai.actionAndDir >> 1;
        mai.isAttack = action >= MobActionType.ATTACK1.getValue() && action <= MobActionType.ATTACKF.getValue();
        mai.isSkill = action >= MobActionType.SKILL1.getValue() && action <= MobActionType.SKILLF.getValue();
        if (mai.isAttack) {
            final int attackIndex = action - MobActionType.ATTACK1.getValue();
            final Optional<MobAttack> mobAttackResult = mob.getAttack(attackIndex);
            if (mobAttackResult.isEmpty()) {
                log.error("{} : Could not resolve mob attack for index : {}", mob, attackIndex);
                return;
            }
            final MobAttack mobAttack = mobAttackResult.get();
            log.debug("{} : Using mob attack index {}", mob, attackIndex);
            mob.setMp(Math.max(mob.getMp() - mobAttack.getConMp(), 0));
            mob.setAttackCounter(Util.getRandom(
                    GameConstants.MOB_ATTACK_COOLTIME_MIN,
                    mob.isBoss() ? GameConstants.MOB_ATTACK_COOLTIME_MAX_BOSS : GameConstants.MOB_ATTACK_COOLTIME_MAX
            ));
        } else if (mai.isSkill) {
            mai.skillId = mai.targetInfo & 0xFF;
            mai.slv = (mai.targetInfo >> 8) & 0xFF;
            mai.option = (mai.targetInfo >> 16) & 0xFFFF;

            final Optional<MobSkill> mobSkillResult = mob.getSkill(mai.skillId);
            if (mobSkillResult.isEmpty()) {
                log.error("{} : Could not resolve mob skill with ID {}", mob, mai.skillId);
                return;
            }
            final MobSkill mobSkill = mobSkillResult.get();

            if (mai.skillId != mobSkill.getSkillId() || mai.slv != mobSkill.getSkillLevel()) {
                log.error("{} : Mismatching skill ID or level for mob skill ({}, {})", mob, mai.skillId, mai.slv);
                return;
            }

            final Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillInfoById(mai.skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("{} : Could not resolve skill info for mob skill : {}", mob, mai.skillId);
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            if (mob.canUseSkill(mobSkill)) {
                log.debug("{} : Using mob skill ({}, {})", mob, mai.skillId, mai.slv);
                final Instant now = Instant.now();
                mob.setMp(Math.max(mob.getMp() - si.getValue(SkillStat.mpCon, mai.slv), 0));
                mob.setNextSkillUse(now.plus(GameConstants.MOB_SKILL_COOLTIME, ChronoUnit.SECONDS));
                mob.setSkillOnCooltime(mobSkill, now.plus(si.getValue(SkillStat.interval, mai.slv), ChronoUnit.SECONDS));
                if (!applyMobSkill(mob, mobSkill, si)) {
                    log.error("{} : Could not apply mob skill effect for skill {}", mob, mobSkill.getSkillType().name());
                    return;
                }
                mob.setAttackCounter(Util.getRandom(
                        GameConstants.MOB_ATTACK_COOLTIME_MIN,
                        mob.isBoss() ? GameConstants.MOB_ATTACK_COOLTIME_MAX_BOSS : GameConstants.MOB_ATTACK_COOLTIME_MAX
                ));
            } else {
                log.error("{} : Mob skill ({}, {}) not available", mob, mai.skillId, mai.slv);
                mai.skillId = 0;
                mai.slv = 0;
                mai.option = 0;
            }
        }
    }

    private static boolean applyMobSkill(Mob mob, MobSkill mobSkill, SkillInfo si) {
        final Field field = mob.getField();
        final MobSkillType skillType = mobSkill.getSkillType();
        final int skillId = mobSkill.getSkillId();
        final int slv = mobSkill.getSkillLevel();
        final int prop = si.getValue(SkillStat.prop, slv);

        // Apply mob temporary stat
        final MobTemporaryStat mts = skillType.getMobTemporaryStat();
        if (mts != null) {
            final List<Mob> targetMobs = new ArrayList<>();
            if (si.getRect(slv) != null) {
                targetMobs.addAll(field.getMobPool().getInsideRect(mob.getRelativeRect(si.getRect(slv))));
            }
            targetMobs.add(mob);
            for (Mob targetMob : targetMobs) {
                if (prop > 0 && !Util.succeedProp(prop)) {
                    continue;
                }
                if (mob == targetMob) {
                    mob.setTemporaryStat(mts, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv)), 0);
                } else {
                    try (var lockedTarget = targetMob.acquire()) {
                        lockedTarget.get().setTemporaryStat(mts, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv)), 0);
                    }
                }
            }
            return true;
        }

        // Apply character temporary stat
        final CharacterTemporaryStat cts = skillType.getCharacterTemporaryStat();
        if (cts != null) {
            final List<User> targetUsers = new ArrayList<>();
            if (si.getRect(slv) != null) {
                targetUsers.addAll(field.getUserPool().getInsideRect(mob.getRelativeRect(si.getRect(slv))));
            }
            for (User targetUser : targetUsers) {
                if (prop > 0 && !Util.succeedProp(prop)) {
                    continue;
                }
                try (var locked = targetUser.acquire()) {
                    if ((cts != CharacterTemporaryStat.Stun && cts != CharacterTemporaryStat.Attract) && targetUser.getSecondaryStat().hasOption(CharacterTemporaryStat.Holyshield)) {
                        continue;
                    }
                    if (targetUser.getSecondaryStat().hasOption(CharacterTemporaryStat.DefenseState)) {
                        final DefenseStateStat defenseStateStat = DefenseStateStat.getByValue(targetUser.getSecondaryStat().getOption(CharacterTemporaryStat.DefenseState_Stat).nOption);
                        if (defenseStateStat != null && defenseStateStat.getStat() == cts &&
                                Util.succeedProp(targetUser.getSecondaryStat().getOption(CharacterTemporaryStat.DefenseState).nOption)) {
                            continue;
                        }
                    }
                    targetUser.setTemporaryStat(cts, TemporaryStatOption.ofMobSkill(Math.max(si.getValue(SkillStat.x, slv), 1), skillId, slv, si.getDuration(slv)));
                }
            }
            return true;
        }

        // Special handling
        switch (skillType) {
            case HEAL_M -> {
                final List<Mob> targetMobs = new ArrayList<>();
                if (si.getRect(slv) != null) {
                    targetMobs.addAll(field.getMobPool().getInsideRect(mob.getRelativeRect(si.getRect(slv))));
                }
                targetMobs.add(mob);
                final int x = si.getValue(SkillStat.x, slv);
                final int y = si.getValue(SkillStat.y, slv);
                for (Mob targetMob : targetMobs) {
                    final int healAmount = x + Util.getRandom(y);
                    if (mob == targetMob) {
                        mob.heal(healAmount);
                    } else {
                        try (var lockedTarget = targetMob.acquire()) {
                            lockedTarget.get().heal(healAmount);
                        }
                    }
                }
            }
            case DISPEL -> {
                // Note : slv = 12 : global dispel, but it is not used by any mobs
                final List<User> targetUsers = new ArrayList<>();
                if (si.getRect(slv) != null) {
                    targetUsers.addAll(field.getUserPool().getInsideRect(mob.getRelativeRect(si.getRect(slv))));
                }
                for (User targetUser : targetUsers) {
                    if (prop > 0 && !Util.succeedProp(prop)) {
                        continue;
                    }
                    try (var locked = targetUser.acquire()) {
                        locked.get().resetTemporaryStat((stat, option) -> option.rOption / 1000000 > 0); // SecondaryStat::ResetByUserSkill
                    }
                }
            }
            case AREA_FIRE, AREA_POISON -> {
                field.getAffectedAreaPool().addAffectedArea(AffectedArea.mobSkill(mob, si, slv, 0));
            }
            case PCOUNTER -> {
                mob.setTemporaryStat(Map.of(
                        MobTemporaryStat.PImmune, MobStatOption.ofMobSkill(1, skillId, slv, si.getDuration(slv)),
                        MobTemporaryStat.PCounter, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv))
                ), 0);
            }
            case MCOUNTER -> {
                mob.setTemporaryStat(Map.of(
                        MobTemporaryStat.MImmune, MobStatOption.ofMobSkill(1, skillId, slv, si.getDuration(slv)),
                        MobTemporaryStat.MCounter, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv))
                ), 0);
            }
            case PMCOUNTER -> {
                mob.setTemporaryStat(Map.of(
                        MobTemporaryStat.PImmune, MobStatOption.ofMobSkill(1, skillId, slv, si.getDuration(slv)),
                        MobTemporaryStat.MImmune, MobStatOption.ofMobSkill(1, skillId, slv, si.getDuration(slv)),
                        MobTemporaryStat.PCounter, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv)),
                        MobTemporaryStat.MCounter, MobStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv))
                ), 0);
            }
            case SUMMON -> {
                final Optional<SummonInfo> summonInfoResult = SkillProvider.getMobSummonInfoByLevel(mobSkill.getSkillLevel());
                if (summonInfoResult.isEmpty()) {
                    log.error("{} | Could not resolve summon info for skill ({}, {})", mob, mobSkill.getSkillId(), mobSkill.getSkillLevel());
                    return true;
                }
                final SummonInfo summonInfo = summonInfoResult.get();
                final Rect rect = mob.getRelativeRect(si.getRect(slv) != null ? si.getRect(slv) : Rect.of(-150, -100, 100, 150)); // default rect from BMS
                final List<Foothold> footholds = field.getMapInfo().getFootholds().stream()
                        .filter((fh) -> !fh.isWall() && fh.isIntersect(rect))
                        .toList();
                if (footholds.isEmpty()) {
                    log.error("{} : Could not find any footholds for summon skill ({}, {})", mob, mobSkill.getSkillId(), mobSkill.getSkillLevel());
                    return true;
                }
                for (int summonId : summonInfo.getSummons()) {
                    if (field.getMobPool().getCount() >= 50) {
                        break;
                    }
                    // Resolve summon position
                    final Foothold fh = Util.getRandomFromCollection(footholds).orElseThrow();
                    final int x = Util.getRandom(
                            Math.max(rect.getLeft(), fh.getX1()),
                            Math.min(rect.getRight(), fh.getX2())
                    );
                    final int y = fh.getYFromX(x);
                    // Create summon
                    final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(summonId);
                    if (mobTemplateResult.isEmpty()) {
                        log.error("{} : Could not resolve summon ID : {}", mob, summonId);
                        continue;
                    }
                    final Mob summon = new Mob(
                            mobTemplateResult.get(),
                            null,
                            x,
                            y,
                            fh.getSn()
                    );
                    summon.setSummonType(si.getValue(SkillStat.summonEffect, slv));
                    field.getMobPool().addMob(summon);
                }
            }
            default -> {
                log.error("Unhandled mob skill type {}", skillType);
                return false;
            }
        }
        return true;
    }

    private static int calcMobDamage(MobTemplate attackerTemplate, MobTemplate targetTemplate) {
        // `anonymous namespace'::calc_mob_base_damamge
        final int pad = attackerTemplate.getPad();
        final double baseDamage = CalcDamage.getRand(Integer.toUnsignedLong(Util.getRandom().nextInt()), pad, pad * 0.85);
        return (int) Math.max(1, baseDamage * ((100.0 - targetTemplate.getPdr()) / 100.0));
    }
}
