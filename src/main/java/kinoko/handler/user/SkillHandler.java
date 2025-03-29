package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.skill.MorphInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.BitFlag;
import kinoko.world.field.Field;
import kinoko.world.field.mob.Mob;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.job.explorer.Magician;
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
import kinoko.world.user.stat.TemporaryStatOption;
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
            // CUserLocal::SendSkillUseRequest, CUserLocal::DoActiveSkill_StatChangeAdmin
            final int targetCount = Byte.toUnsignedInt(inPacket.decodeByte());
            skill.targetIds = new int[targetCount];
            for (int i = 0; i < targetCount; i++) {
                skill.targetIds[i] = inPacket.decodeInt();
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

        // Check skill root
        final int skillRoot = SkillConstants.getSkillRoot(skill.skillId);
        if (!JobConstants.isBeginnerJob(skillRoot) && !JobConstants.isCorrectJobForSkillRoot(user.getJob(), skillRoot)) {
            log.error("Tried to use skill {} as incorrect job : {}", skill.skillId, user.getJob());
            user.dispose();
            return;
        }
        // Check seal
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Seal)) {
            log.error("Tried to use skill {} while sealed", skill.skillId);
            user.dispose();
            return;
        }
        // Check morph
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Morph)) {
            final int morphId = user.getSecondaryStat().getOption(CharacterTemporaryStat.Morph).nOption;
            final Optional<MorphInfo> morphInfoResult = SkillProvider.getMorphInfoById(morphId);
            if (morphInfoResult.isEmpty()) {
                log.error("Could not resolve morph info for morph ID : {}", morphId);
                user.dispose();
                return;
            }
            final MorphInfo morphInfo = morphInfoResult.get();
            if (!morphInfo.isSuperman() && !morphInfo.isAttackable()) {
                log.error("Tried to use skill {} while morphed as morph ID : {}", skill.skillId, morphId);
                user.dispose();
                return;
            }
        }
        // Mystic Door cooltime to avoid crashes
        if (skill.skillId == Magician.MYSTIC_DOOR) {
            if (user.getTownPortal() != null && user.getTownPortal().getWaitTime().isAfter(Instant.now())) {
                user.write(MessagePacket.system("Please wait 5 seconds before casting Mystic Door again."));
                user.dispose();
                return;
            }
        }
        handleSkill(user, skill);
    }

    @Handler(InHeader.UserSkillCancelRequest)
    public static void handleUserSkillCancelRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        if (SkillConstants.isKeydownSkill(skillId)) {
            return;
        }
        // Remove stat matching skill ID
        final SecondaryStat ss = user.getSecondaryStat();
        final Set<CharacterTemporaryStat> resetStats = ss.resetTemporaryStat((cts, option) -> option.rOption == skillId);
        final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
        if (!flag.isEmpty()) {
            user.write(WvsContext.temporaryStatReset(flag));
            user.getField().broadcastPacket(UserRemote.temporaryStatReset(user, flag), user);
        }
        // Additional handling for CTS
        if (resetStats.contains(CharacterTemporaryStat.Beholder)) {
            user.removeSummoned((summoned) -> summoned.getSkillId() == Warrior.BEHOLDER);
        }
        if (resetStats.contains(CharacterTemporaryStat.Aura)) {
            user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
            BattleMage.cancelPartyAura(user, skillId);
        }
        if (resetStats.contains(CharacterTemporaryStat.SuperBody)) {
            user.resetTemporaryStat(CharacterTemporaryStat.AURA_STAT);
        }
    }

    @Handler(InHeader.UserSkillPrepareRequest)
    public static void handleUserSkillPrepareRequest(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt(); // nSkillID
        final int slv = inPacket.decodeByte(); // nSLV
        final short actionAndDir = inPacket.decodeShort(); // nOneTimeAction & 0x7FFF | (nMoveAction << 15)
        final byte attackSpeed = inPacket.decodeByte(); // attack_speed_degree

        if (skillId == WildHunter.JAGUAR_OSHI) {
            final int mobId = inPacket.decodeInt(); // dwSwallowMobID
            final Optional<Mob> mobResult = user.getField().getMobPool().getById(mobId);
            if (mobResult.isEmpty()) {
                log.error("Could not resolve swallow mob ID : {}", mobId);
                return;
            }
            final Mob mob = mobResult.get();
            // Should implement all client-side checks in CUserLocal::FindSwallowMob
            if (mob.isBoss() || mob.getLevel() > user.getLevel() + 5 || SkillConstants.isNotSwallowableMob(mob.getTemplateId())) {
                log.error("Tried to swallow non-swallowable mob ID : {}, template ID : {}", mobId, mob.getTemplateId());
                return;
            }
            mob.setSwallowCharacterId(user.getCharacterId());
            user.setTemporaryStat(Map.of(
                    CharacterTemporaryStat.Swallow_Mob, TemporaryStatOption.of(mobId, skillId, 0),
                    CharacterTemporaryStat.Swallow_Template, TemporaryStatOption.of(mob.getTemplateId(), skillId, 0)
            ));
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

    @Handler(InHeader.UserEffectLocal)
    public static void handleUserEffectLocal(User user, InPacket inPacket) {
        final int skillId = inPacket.decodeInt();
        final int slv = inPacket.decodeByte();
        final boolean sendLocal = inPacket.decodeBoolean();

        final Effect effect = Effect.skillUse(skillId, slv, user.getLevel());
        if (sendLocal) {
            user.write(UserLocal.effect(effect));

            // Not a real skill ID, but client sends this when trying to cancel Mech: Siege Mode (35111004), Mech: Missile Tank (35121005), and Mech: Siege Mode 2 (35121013)
            if (skillId == 35110004 || skillId == 35120005 || skillId == 35120013) {
                if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Mechanic)) {
                    Mechanic.handleMech(user, skillId == 35120013 ? Mechanic.MECH_MISSILE_TANK : Mechanic.MECH_PROTOTYPE);
                }
            }
        }
        user.getField().broadcastPacket(UserRemote.effect(user, effect), user);
    }

    @Handler(InHeader.UserCalcDamageStatSetRequest)
    public static void handleUserCalcDamageStatSetRequest(User user, InPacket inPacket) {
        user.updatePassiveSkillData();
        user.validateStat();

        // Handle effects
        Warrior.handleBerserkEffect(user);
        Evan.handleDragonFuryEffect(user);
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

        if (skill.skillId != Thief.MONSTER_BOMB) {
            handleSkill(user, skill);
        }
        user.getField().broadcastPacket(UserRemote.throwGrenade(user, skill), user);
    }

    @Handler(InHeader.UserClientTimerEndRequest)
    public static void handleUserClientTimerEndRequest(User user, InPacket inPacket) {
        final int size = inPacket.decodeInt();
        final int[] skillIds = new int[size];
        for (int i = 0; i < size; i++) {
            skillIds[i] = inPacket.decodeInt();
            inPacket.decodeInt();
        }
        for (int skillId : skillIds) {
            user.resetTemporaryStat(skillId);
        }
    }

    private static void handleSkill(User user, Skill skill) {
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
            user.write(WvsContext.inventoryOperation(removeResult.get(), false));
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
                    .filter((entry) -> {
                        final Item bulletItem = entry.getValue();
                        if (!ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), bulletItem.getItemId())) {
                            return false;
                        }
                        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(bulletItem.getItemId());
                        if (itemInfoResult.isEmpty() || itemInfoResult.get().getReqLevel() > user.getLevel()) {
                            return false;
                        }
                        return bulletItem.getQuantity() >= bulletCon;
                    })
                    .findFirst();
            if (bulletEntryResult.isEmpty()) {
                log.error("Tried to use skill {} without enough bullets", skill.skillId);
                return;
            }
            final int position = bulletEntryResult.get().getKey();
            final Item bulletItem = bulletEntryResult.get().getValue();
            // Consume bullets
            bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCon));
            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, position, bulletItem.getQuantity()), false));
        }

        // Consume hp/mp
        user.addHp(-hpCon);
        user.addMp(-mpCon);

        // Set cooltime
        final int cooltime = si.getValue(SkillStat.cooltime, skill.slv);
        if (!SkillConstants.isNoCooltimeSkill(skill.skillId) && cooltime > 0) {
            user.setSkillCooltime(skill.skillId, cooltime);
        }

        // Skill-specific handling
        SkillProcessor.processSkill(user, skill);
        user.write(WvsContext.skillUseResult());

        // Skill effects and party handling
        final Field field = user.getField();
        field.broadcastPacket(UserRemote.effect(user, Effect.skillUse(skill, user.getLevel())), user);
        skill.forEachAffectedMember(user, field, (member) -> {
            SkillProcessor.processSkill(member, skill);
            member.write(UserLocal.effect(Effect.skillAffected(skill.skillId, skill.slv)));
            field.broadcastPacket(UserRemote.effect(member, Effect.skillAffected(skill.skillId, skill.slv)), member);
        });
    }
}
