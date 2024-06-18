package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.SkillProvider;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.BitFlag;
import kinoko.util.Locked;
import kinoko.world.field.Field;
import kinoko.world.item.*;
import kinoko.world.job.explorer.Magician;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Skill;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class SkillHandler {
    private static final Logger log = LogManager.getLogger(SkillHandler.class);

    @Handler(InHeader.UserSkillUseRequest)
    public static void handleUserSkillUseRequest(User user, InPacket inPacket) {
        inPacket.decodeInt(); // update_time
        final Skill skill = new Skill();
        skill.skillId = inPacket.decodeInt(); // nSkillID
        skill.slv = inPacket.decodeByte(); // nSLV

        if (skill.skillId == Mechanic.ROCK_N_SHOCK) {
            // CUserLocal::DoActiveSkill_Summon
            skill.rockAndShockCount = inPacket.decodeByte();
            if (skill.rockAndShockCount == 2) {
                skill.rockAndShock1 = inPacket.decodeInt();
                skill.rockAndShock2 = inPacket.decodeInt();
            }
        }
        if (skill.skillId == Citizen.CAPTURE) {
            // CUserLocal::DoActiveSkill_MobCapture
            skill.captureTargetMobId = inPacket.decodeInt();
        }
        if (skill.skillId == Citizen.CALL_OF_THE_HUNTER) {
            // CUserLocal::DoActiveSkill_SummonMonster
            skill.randomCapturedMobId = inPacket.decodeInt();
        }
        if (SkillConstants.isEncodePositionSkill(skill.skillId)) {
            skill.positionX = inPacket.decodeShort(); // GetPos()->x
            skill.positionY = inPacket.decodeShort(); // GetPos()->y
            if (SkillConstants.isSummonSkill(skill.skillId)) {
                // CUserLocal::DoActiveSkill_Summon
                skill.summonLeft = inPacket.decodeBoolean();
            }
        }
        if (skill.skillId == Thief.SHADOW_STARS) {
            // CUserLocal::SendSkillUseRequest
            skill.spiritJavelinItemId = inPacket.decodeInt(); // nSpiritJavelinItemID
        }
        if (SkillConstants.isPartySkill(skill.skillId) && inPacket.getRemaining() > 2) {
            // CUserLocal::SendSkillUseRequest
            skill.affectedMemberBitMap = inPacket.decodeByte();
            if (skill.skillId == Magician.DISPEL) {
                inPacket.decodeShort(); // tDelay
            }
        }
        if (inPacket.getRemaining() > 2) {
            // CUserLocal::SendSkillUseRequest
            final int mobCount = inPacket.decodeByte(); // nMobCount
            skill.mobIds = new int[mobCount];
            for (int i = 0; i < mobCount; i++) {
                skill.mobIds[i] = inPacket.decodeInt();
                if (skill.skillId == Thief.CHAINS_OF_HELL) {
                    // CUserLocal::TryDoingMonsterMagnet
                    inPacket.decodeByte(); // anMobMove[k] == 3 || anMobMove[k] == 4
                }
            }
        }
        if (skill.skillId == Thief.CHAINS_OF_HELL || skill.skillId == Citizen.CALL_OF_THE_HUNTER || SkillConstants.isSummonSkill(skill.skillId)) {
            // CUserLocal::TryDoingMonsterMagnet || CUserLocal::DoActiveSkill_SummonMonster || CUserLocal::DoActiveSkill_Summon
            skill.left = inPacket.decodeBoolean(); // nMoveAction & 1
        }
        // ignore tDelay

        try (var locked = user.acquire()) {
            if (skill.skillId == Magician.MYSTIC_DOOR) {
                if (user.getTownPortal() != null && user.getTownPortal().getWaitTime().isAfter(Instant.now())) {
                    user.write(MessagePacket.system("Please wait 5 seconds before casting Mystic Door again."));
                    user.dispose();
                    return;
                }
            }
            handleSkill(locked, skill);
        }
    }

    @Handler(InHeader.UserSkillCancelRequest)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        if (SkillConstants.isKeydownSkill(skillId)) {
            return;
        }
        try (var locked = user.acquire()) {
            // Remove stat matching skill ID
            final SecondaryStat ss = locked.get().getSecondaryStat();
            final Set<CharacterTemporaryStat> resetStats = ss.resetTemporaryStat((cts, option) -> option.rOption == skillId);
            final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
            if (!flag.isEmpty()) {
                user.write(WvsContext.temporaryStatReset(flag));
                user.getField().broadcastPacket(UserRemote.temporaryStatReset(user, flag), user);
            }
            // Additional handling for CTS
            if (resetStats.contains(CharacterTemporaryStat.Beholder)) {
                user.removeSummoned(Warrior.BEHOLDER);
            }
            if (resetStats.contains(CharacterTemporaryStat.Aura)) {
                user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
                BattleMage.cancelPartyAura(user, skillId);
            }
        }
    }

    @Handler(InHeader.UserSkillPrepareRequest)
    public static void handleUserSkillPrepareRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        final short actionAndDir = inPacket.decodeShort(); // nOneTimeAction & 0x7FFF | (nMoveAction << 15)
        final byte attackSpeed = inPacket.decodeByte(); // attack_speed_degree
        if (skillId == WildHunter.JAGUAR_OSHI) {
            inPacket.decodeInt(); // dwSwallowMobID
        }
        user.getField().broadcastPacket(UserRemote.skillPrepare(user, skillId, slv, actionAndDir, attackSpeed), user);
    }

    @Handler(InHeader.UserMovingShootAttackPrepare)
    public static void handleMovingShootAttackPrepare(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final short actionAndDir = inPacket.decodeShort(); // (nMoveAction & 1) << 15 | random_shoot_attack_action & 0x7FFF
        final byte attackSpeed = inPacket.decodeByte(); // nActionSpeed
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            log.error("Received UserMovingShootAttackPrepare for skill {}, but skill level is 0", skillId);
            return;
        }
        user.getField().broadcastPacket(UserRemote.movingShootAttackPrepare(user, skillId, slv, actionAndDir, attackSpeed), user);
    }

    @Handler(InHeader.UserCalcDamageStatSetRequest)
    public static void handleUserCalcDamageStatSetRequest(User user, InPacket inPacket) {
        try (var locked = user.acquire()) {
            user.updatePassiveSkillData();
            user.validateStat();

            // Handle effects
            Warrior.handleBerserkEffect(user);
            Evan.handleDragonFuryEffect(user);
        }
    }

    @Handler(InHeader.UserThrowGrenade)
    public static void handleUserThrowGrenade(User user, InPacket inPacket) {
        final Skill skill = new Skill();
        skill.positionX = inPacket.decodeInt();
        skill.positionY = inPacket.decodeInt();
        inPacket.decodeInt();
        skill.keyDown = inPacket.decodeInt();
        skill.skillId = inPacket.decodeInt();
        skill.slv = inPacket.decodeInt();

        try (var locked = user.acquire()) {
            if (skill.skillId != Thief.MONSTER_BOMB) {
                handleSkill(locked, skill);
            }
            user.getField().broadcastPacket(UserRemote.throwGrenade(user, skill), user);
        }
    }

    @Handler(InHeader.UserClientTimerEndRequest)
    public static void handleUserClientTimerEndRequest(User user, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final int[] skillIds = new int[size];
        for (int i = 0; i < size; i++) {
            skillIds[i] = inPacket.decodeInt();
            inPacket.decodeInt();
        }
        try (var locked = user.acquire()) {
            for (int skillId : skillIds) {
                user.resetTemporaryStat(skillId);
            }
        }
    }

    private static void handleSkill(Locked<User> locked, Skill skill) {
        final User user = locked.get();

        // Resolve skill info
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skill.skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for skill ID : {}", skill.skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();

        // Check skill cooltime and cost
        if (user.getSkillManager().hasSkillCooltime(skill.skillId)) {
            log.error("Tried to use skill {} that is still on cooltime", skill.skillId);
            return;
        }
        final int hpCon = si.getHpCon(user, skill.slv, 0);
        if (user.getHp() <= hpCon) {
            log.error("Tried to use skill {} without enough hp, current : {}, required : {}", skill.skillId, user.getHp(), hpCon);
            return;
        }
        final int mpCon = si.getMpCon(user, skill.slv);
        if (user.getMp() < mpCon) {
            log.error("Tried to use skill {} without enough mp, current : {}, required : {}", skill.skillId, user.getMp(), mpCon);
            return;
        }
        final int comboCon = SkillConstants.getRequiredComboCount(skill.skillId);
        if (comboCon > 0) {
            if (user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboAbilityBuff).nOption < comboCon) {
                log.error("Tried to use skill {} without required combo count : {}", skill.skillId, comboCon);
            }
            user.resetTemporaryStat(Set.of(CharacterTemporaryStat.ComboAbilityBuff));
        }
        // Item / Bullet consume are mutually exclusive
        final int itemCon = si.getValue(SkillStat.itemCon, skill.slv);
        if (itemCon > 0) {
            final int itemConNo = si.getValue(SkillStat.itemConNo, skill.slv); // should always be > 0
            final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(itemCon, itemConNo);
            if (removeResult.isEmpty()) {
                log.error("Tried to use skill {} without required item", itemCon);
                return;
            }
            user.write(WvsContext.inventoryOperation(removeResult.get(), true));
        }
        final int bulletCon = si.getBulletCon(skill.slv);
        if (bulletCon > 0) {
            // Resolve bullet item
            final Item weaponItem = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
            if (weaponItem == null) {
                log.error("Tried to use skill {} without a weapon", skill.skillId);
                return;
            }
            final Optional<Map.Entry<Integer, Item>> bulletEntryResult = user.getInventoryManager().getConsumeInventory().getItems().entrySet().stream()
                    .filter((entry) -> ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), entry.getValue().getItemId()) && entry.getValue().getQuantity() >= bulletCon)
                    .findFirst();
            if (bulletEntryResult.isEmpty()) {
                log.error("Tried to use skill {} without enough bullets", skill.skillId);
                return;
            }
            final int position = bulletEntryResult.get().getKey();
            final Item bulletItem = bulletEntryResult.get().getValue();
            // Consume bullets
            bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCon));
            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, position, bulletItem.getQuantity()), true));
        }
        // Consume hp/mp
        user.addHp(-hpCon);
        user.addMp(-mpCon);
        // Set cooltime
        final int cooltime = si.getValue(SkillStat.cooltime, skill.slv);
        if (skill.skillId != Pirate.BATTLESHIP && cooltime > 0) {
            user.setSkillCooltime(skill.skillId, cooltime);
        }

        // Skill-specific handling
        SkillProcessor.processSkill(locked, skill);
        user.write(WvsContext.skillUseResult());

        // Skill effects and party handling
        final Field field = user.getField();
        field.broadcastPacket(UserRemote.effect(user, Effect.skillUse(skill.skillId, skill.slv, user.getLevel())), user);
        skill.forEachAffectedMember(user, field, (member) -> {
            try (var lockedMember = member.acquire()) {
                SkillProcessor.processSkill(lockedMember, skill);
                member.write(UserLocal.effect(Effect.skillAffected(skill.skillId, skill.slv)));
                field.broadcastPacket(UserRemote.effect(member, Effect.skillAffected(skill.skillId, skill.slv)), member);
            }
        });
    }
}
