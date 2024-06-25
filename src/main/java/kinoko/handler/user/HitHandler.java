package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.user.SummonedPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.MobProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.mob.MobAttack;
import kinoko.provider.mob.MobSkillType;
import kinoko.provider.mob.MobTemplate;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Locked;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobActionType;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedActionType;
import kinoko.world.item.BodyPart;
import kinoko.world.item.InventoryManager;
import kinoko.world.item.Item;
import kinoko.world.item.WeaponType;
import kinoko.world.job.explorer.Bowman;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Aran;
import kinoko.world.skill.*;
import kinoko.world.user.User;
import kinoko.world.user.stat.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public final class HitHandler {
    private static final Logger log = LogManager.getLogger(HitHandler.class);

    @Handler(InHeader.UserHit)
    public static void handleUserHit(User user, InPacket inPacket) {
        // CUserLocal::SetDamaged, CUserLocal::Update
        final HitInfo hitInfo = new HitInfo();
        inPacket.decodeInt(); // get_update_time()
        hitInfo.attackIndex = inPacket.decodeByte(); // nAttackIdx
        if (hitInfo.attackIndex > -2) {
            hitInfo.magicElemAttr = inPacket.decodeByte(); // nMagicElemAttr
            hitInfo.damage = inPacket.decodeInt(); // nDamage
            hitInfo.templateId = inPacket.decodeInt(); // dwTemplateID
            hitInfo.mobId = inPacket.decodeInt(); // MobID
            hitInfo.dir = inPacket.decodeByte(); // nDir
            hitInfo.reflect = inPacket.decodeByte(); // nX = 0
            hitInfo.guard = inPacket.decodeByte(); // bGuard
            hitInfo.knockback = inPacket.decodeByte(); // (bKnockback != 0) + 1
            if (hitInfo.knockback > 1 || hitInfo.reflect != 0) {
                hitInfo.powerGuard = inPacket.decodeByte(); // nX != 0 && nPowerGuard != 0
                hitInfo.reflectMobId = inPacket.decodeInt(); // reflectMobID
                hitInfo.reflectMobAction = inPacket.decodeByte(); // hitAction
                hitInfo.reflectMobX = inPacket.decodeShort(); // ptHit.x
                hitInfo.reflectMobY = inPacket.decodeShort(); // ptHit.y
                inPacket.decodeShort(); // this->GetPos()->x
                inPacket.decodeShort(); // this->GetPos()->y
            }
            hitInfo.stance = inPacket.decodeByte(); // bStance | (nSkillID_Stance == 33101006 ? 2 : 0)
        } else if (hitInfo.attackIndex == AttackIndex.Counter.getValue() || hitInfo.attackIndex == AttackIndex.Obstacle.getValue()) {
            inPacket.decodeByte(); // 0
            hitInfo.damage = inPacket.decodeInt(); // nDamage
            hitInfo.obstacleData = inPacket.decodeShort(); // dwObstacleData
            inPacket.decodeByte(); // 0
        } else if (hitInfo.attackIndex == AttackIndex.Stat.getValue()) {
            hitInfo.magicElemAttr = inPacket.decodeByte(); // nElemAttr
            hitInfo.damage = inPacket.decodeInt(); // nDamage
            hitInfo.diseaseData = inPacket.decodeShort(); // dwDiseaseData = (nSkillID << 8) | nSLV
            hitInfo.diseaseType = inPacket.decodeByte(); // 1 : Poison, 2 : AffectedArea, 3 : Shadow of Darkness
        } else {
            log.error("Unknown attack index received : {}", hitInfo.attackIndex);
            return;
        }

        try (var locked = user.acquire()) {
            if (hitInfo.attackIndex > 0) {
                handleMobAttack(locked, hitInfo);
            }
            handleHit(locked, hitInfo);
        }
    }

    private static void handleMobAttack(Locked<User> locked, HitInfo hitInfo) {
        // Resolve mob attack and attack index
        final Optional<MobTemplate> mobTemplateResult = MobProvider.getMobTemplate(hitInfo.templateId);
        if (mobTemplateResult.isEmpty()) {
            log.error("Could not resolve mob template ID : {}", hitInfo.templateId);
            return;
        }
        final Optional<MobAttack> mobAttackResult = mobTemplateResult.get().getAttack(hitInfo.attackIndex);
        if (mobAttackResult.isEmpty()) {
            return;
        }
        final MobAttack mobAttack = mobAttackResult.get();

        // Resolve mob skill, check if it applies a CTS
        final int skillId = mobAttack.getSkillId();
        final MobSkillType skillType = MobSkillType.getByValue(skillId);
        if (skillType == null) {
            log.error("Could not resolve mob skill type for mob skill ID : {}", skillId);
            return;
        }
        final CharacterTemporaryStat cts = skillType.getCharacterTemporaryStat();
        if (cts == null) {
            return;
        }

        // Apply mob skill
        final User user = locked.get();
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Holyshield)) {
            return;
        }
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.DefenseState)) {
            final DefenseStateStat defenseStateStat = DefenseStateStat.getByValue(user.getSecondaryStat().getOption(CharacterTemporaryStat.DefenseState_Stat).nOption);
            if (defenseStateStat != null && defenseStateStat.getStat() == cts &&
                    Util.succeedProp(user.getSecondaryStat().getOption(CharacterTemporaryStat.DefenseState).nOption)) {
                return;
            }
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getMobSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for mob skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final int slv = mobAttack.getSkillLevel();
        user.setTemporaryStat(cts, TemporaryStatOption.ofMobSkill(si.getValue(SkillStat.x, slv), skillId, slv, si.getDuration(slv)));
    }

    private static void handleHit(Locked<User> locked, HitInfo hitInfo) {
        final User user = locked.get();
        final int damage = hitInfo.damage;

        // Compute damage reductions
        final int powerGuardReduce = handleReflect(user, hitInfo);
        final int mesoGuardReduce = handleMesoGuard(user, hitInfo);

        final int achillesReduce = getAchillesReduce(user, damage);
        final int comboBarrierReduce = getComboBarrierReduce(user, damage, achillesReduce);

        final int magicGuardReduce = getMagicGuardReduce(user, damage);
        final int magicShieldReduce = getMagicShieldReduce(user, damage);
        final int blueAuraReduce = getBlueAuraReduce(user, damage);

        // Final damage
        hitInfo.finalDamage = damage - powerGuardReduce - mesoGuardReduce - achillesReduce - comboBarrierReduce - magicGuardReduce - magicShieldReduce - blueAuraReduce;
        log.debug("Hit delta : {} = {} - {} - {} - {} - {} - {} - {} - {}", hitInfo.finalDamage, damage, powerGuardReduce, mesoGuardReduce, achillesReduce, comboBarrierReduce, magicGuardReduce, magicShieldReduce, blueAuraReduce);

        // Process hit damage
        if (hitInfo.finalDamage > 0) {
            user.addHp(-hitInfo.finalDamage);
        }
        user.getField().broadcastPacket(UserRemote.hit(user, hitInfo), user);

        // Process on hit effects
        handleGuardian(user, hitInfo);
        handleDivineShield(user, hitInfo);
        handleBeholderCounter(user, hitInfo);
        handleVengeance(user, hitInfo);
        handleDarkFlare(user, hitInfo);
        handlePiratesRevenge(user, hitInfo);
        handleBattleship(user, hitInfo);
    }

    private static int handleReflect(User user, HitInfo hitInfo) {
        // Resolve target
        final Optional<Mob> mobResult = user.getField().getMobPool().getById(hitInfo.reflectMobId);
        if (mobResult.isEmpty()) {
            return 0;
        }
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.PowerGuard)) {
            // Power Guard - reflect nPowerGuard %, subtract reflected amount
            final int percentage = user.getSecondaryStat().getOption(CharacterTemporaryStat.PowerGuard).nOption;
            try (var lockedMob = mobResult.get().acquire()) {
                final Mob mob = lockedMob.get();
                final int damage = Math.min(hitInfo.damage * percentage / 100, mob.getMaxHp() * percentage / 100);
                final int finalDamage = mob.getFixedDamage() > 0 ? mob.getFixedDamage() : damage;
                // Process reflect damage and return amount to subtract from hit damage
                mob.damage(user, finalDamage);
                return finalDamage;
            }
        } else if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.ManaReflection)) {
            // Mana Reflection - reflect si.getValue(SkillStat.x, nManaReflection) %
            final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.ManaReflection);
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(option.rOption);
            if (skillInfoResult.isPresent()) {
                final int percentage = skillInfoResult.get().getValue(SkillStat.x, option.nOption);
                try (var lockedMob = mobResult.get().acquire()) {
                    final Mob mob = lockedMob.get();
                    final int damage = Math.min(hitInfo.damage * percentage / 100, mob.getMaxHp() / 20); // skill description says 20% of max hp, but coded incorrectly in client
                    final int finalDamage = mob.getFixedDamage() > 0 ? mob.getFixedDamage() : damage;
                    mob.damage(user, finalDamage);
                    // no amount is subtracted from hit damage
                }
            } else {
                log.error("Could not resolve skill info for mana reflection skill ID : {}", option.rOption);
            }
        } else {
            log.error("Reflect on mob ID {} without PowerGuard or ManaReflection CTS", hitInfo.reflectMobId);
        }
        return 0;
    }

    private static int handleMesoGuard(User user, HitInfo hitInfo) {
        // CalcDamage::GetMesoGuardReduce
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.MesoGuard)) {
            return 0;
        }
        // Calculate reduction rate
        final int mesoGuardRate = Math.clamp(50 + user.getSkillStatValue(Thief.MESO_MASTERY, SkillStat.v), 50, 100); // hard coded 50
        final int mesoRequiredRate = Math.max(0, user.getSkillStatValue(Thief.MESO_GUARD, SkillStat.x) - user.getSkillStatValue(Thief.MESO_MASTERY, SkillStat.w));
        // Calculate damage reduction
        final int realDamage = Math.clamp(hitInfo.damage, 1, GameConstants.DAMAGE_MAX);
        final int mesoGuardReduce = realDamage * mesoGuardRate / 100;
        final int mesoRequired = mesoGuardReduce * mesoRequiredRate / 100;
        // Check if character has enough money and deduct
        final InventoryManager im = user.getInventoryManager();
        final int mesoRemaining = im.getMoney();
        if (mesoRemaining > mesoRequired) {
            if (!im.addMoney(-mesoRequired)) {
                throw new IllegalStateException("Could not deduct money for meso guard");
            }
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
            return mesoGuardReduce;
        } else {
            if (!im.addMoney(-mesoRemaining)) {
                throw new IllegalStateException("Could not deduct money for meso guard");
            }
            user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
            return 100 * mesoRemaining / mesoRequiredRate;
        }
    }

    private static int getAchillesReduce(User user, int damage) {
        // CUserLocal::GetAchillesReduce
        final int skillId = switch (user.getJob()) {
            case 112 -> Warrior.ACHILLES_HERO;
            case 122 -> Warrior.ACHILLES_PALADIN;
            case 132 -> Warrior.ACHILLES_DRK;
            case 2112 -> Aran.HIGH_DEFENSE;
            default -> 0;
        };
        if (skillId == 0) {
            return 0;
        }
        final int x = user.getSkillStatValue(skillId, SkillStat.x);
        return damage * (1000 - x) / 1000;
    }

    private static int getComboBarrierReduce(User user, int damage, int achillesReduce) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.ComboBarrier)) {
            return 0;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboBarrier);
        return (damage - achillesReduce) * (1000 - option.nOption) / 1000;
    }

    private static int getMagicGuardReduce(User user, int damage) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.MagicGuard)) {
            return 0;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.MagicGuard);
        int mpDamage = damage * option.nOption / 100;
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Infinity)) {
            return mpDamage;
        } else {
            mpDamage = Math.min(mpDamage, user.getMp());
            user.addMp(-mpDamage);
            return mpDamage;
        }
    }

    private static int getMagicShieldReduce(User user, int damage) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.MagicShield)) {
            return 0;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.MagicShield);
        return damage * option.nOption / 100;
    }

    private static int getBlueAuraReduce(User user, int damage) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.BlueAura)) {
            return 0;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.BlueAura);
        final int skillId = option.rOption;
        final int slv = option.nOption;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for blue aura cts reason : {}", skillId);
            return 0;
        }
        final SkillInfo si = skillInfoResult.get();
        final int x = si.getValue(SkillStat.x, slv); // TODO distribute damage
        return damage * x / 100;
    }

    private static void handleGuardian(User user, HitInfo hitInfo) {
        if (hitInfo.knockback <= 1) {
            return;
        }
        final int stunDuration = user.getSkillStatValue(Warrior.GUARDIAN, SkillStat.time);
        if (stunDuration == 0) {
            return;
        }
        final Optional<Mob> knockbackMobResult = user.getField().getMobPool().getById(hitInfo.reflectMobId);
        if (knockbackMobResult.isPresent()) {
            // Acquire and stun mob
            try (var lockedMob = knockbackMobResult.get().acquire()) {
                lockedMob.get().setTemporaryStat(MobTemporaryStat.Stun, MobStatOption.of(1, Warrior.GUARDIAN, stunDuration * 1000));
            }
        }
    }

    private static void handleDivineShield(User user, HitInfo hitInfo) {
        if (hitInfo.attackIndex <= AttackIndex.Counter.getValue()) {
            return;
        }
        // Resolve skill
        final int skillId = Warrior.DIVINE_SHIELD;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for divine shield skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final int slv = user.getSkillLevel(skillId);
        // Check current stack count
        final SecondaryStat ss = user.getSecondaryStat();
        if (ss.hasOption(CharacterTemporaryStat.BlessingArmor) && ss.hasOption(CharacterTemporaryStat.BlessingArmor)) {
            // Decrement divine shield count
            final TemporaryStatOption option = ss.getOption(CharacterTemporaryStat.BlessingArmor);
            final int newCount = option.nOption - 1;
            if (newCount > 0) {
                user.setTemporaryStat(Map.of(
                        CharacterTemporaryStat.BlessingArmor, option.update(newCount),
                        CharacterTemporaryStat.BlessingArmorIncPAD, ss.getOption(CharacterTemporaryStat.BlessingArmorIncPAD) // required for TemporaryStatSet
                ));
            } else {
                user.resetTemporaryStat(skillId);
                user.setSkillCooltime(skillId, si.getValue(SkillStat.cooltime, slv));
            }
        } else {
            // Try giving divine shield buff
            if (slv == 0 || user.getSkillManager().hasSkillCooltime(skillId) || !Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
                return;
            }
            user.setTemporaryStat(Map.of(
                    CharacterTemporaryStat.BlessingArmor, TemporaryStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getDuration(slv)),
                    CharacterTemporaryStat.BlessingArmorIncPAD, TemporaryStatOption.of(si.getValue(SkillStat.epad, slv), skillId, si.getDuration(slv))
            ));
        }
    }

    private static void handleBeholderCounter(User user, HitInfo hitInfo) {
        if (hitInfo.attackIndex <= AttackIndex.Counter.getValue()) {
            return;
        }
        // Resolve summoned
        final Optional<Summoned> summonedResult = user.getSummonedBySkillId(Warrior.BEHOLDER);
        if (summonedResult.isEmpty()) {
            return;
        }
        final Summoned summoned = summonedResult.get();
        // Resolve skill info
        final int skillId = Warrior.HEX_OF_THE_BEHOLDER_COUNTER;
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve hex of the beholder counter skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        // Resolve target mob
        final Optional<Mob> mobResult = user.getField().getMobPool().getById(hitInfo.mobId);
        if (mobResult.isEmpty()) {
            return;
        }
        try (var lockedMob = mobResult.get().acquire()) {
            final Mob mob = lockedMob.get();
            if (mob.getHp() <= 0) {
                return;
            }
            // Calculate damage
            final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
            final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
            final int mastery = CalcDamage.getWeaponMastery(user, weaponType);
            final double k = CalcDamage.getMasteryConstByWT(weaponType);
            final double damageMax = CalcDamage.calcDamageByWT(weaponType, user.getBasicStat(), CalcDamage.getPad(user), CalcDamage.getMad(user));
            double damage = CalcDamage.adjustRandomDamage(damageMax, Util.getRandom().nextInt(), k, mastery);
            damage = damage + user.getPassiveSkillData().getAllPdamR() * damage / 100.0;
            damage = CalcDamage.getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());
            final int skillDamage = si.getValue(SkillStat.damage, slv);
            if (skillDamage > 0) {
                damage = skillDamage / 100.0 * damage;
            }
            final int damR = user.getPassiveSkillData().getAllDipR() + user.getSecondaryStat().getOption(CharacterTemporaryStat.DamR).nOption;
            damage = damage + damR * damage / 100.0;
            // Create attack
            final Attack attack = new Attack(OutHeader.SummonedAttack);
            attack.actionAndDir = (byte) ((summoned.getMoveAction() & 1) << 7 | SummonedActionType.ATTACK1.getValue() & 0x7F);
            final AttackInfo attackInfo = new AttackInfo();
            attackInfo.mobId = mob.getId();
            attackInfo.hitAction = MobActionType.HIT1.getValue();
            attackInfo.damage[0] = (int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX);
            attack.getAttackInfo().add(attackInfo);
            // Broadcast packet and process damage
            user.getField().broadcastPacket(SummonedPacket.summonedAttack(user, summoned, attack));
            mob.damage(user, attackInfo.damage[0]);
        }
    }

    private static void handleVengeance(User user, HitInfo hitInfo) {
        if (hitInfo.attackIndex <= AttackIndex.Counter.getValue()) {
            return;
        }
        final int prop = user.getSkillStatValue(Bowman.VENGEANCE, SkillStat.prop);
        if (prop != 0 && Util.succeedProp(prop)) {
            user.write(UserLocal.requestVengeance());
        }
    }

    private static void handleDarkFlare(User user, HitInfo hitInfo) {
        if (hitInfo.attackIndex <= AttackIndex.Counter.getValue() || hitInfo.damage <= 0) {
            return;
        }
        user.getField().getUserPool().forEachPartySummoned(user, (member, summoned) -> {
            if (summoned.getSkillId() != Thief.DARK_FLARE_NL && summoned.getSkillId() != Thief.DARK_FLARE_SHAD) {
                return;
            }
            if (!summoned.getRect().isInsideRect(user.getX(), user.getY())) {
                return;
            }
            // Resolve skill info
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(summoned.getSkillId());
            if (skillInfoResult.isEmpty()) {
                log.error("Could not resolve skill info for dark flare skill ID : {}", summoned.getSkillId());
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            final double damage = si.getValue(SkillStat.x, summoned.getSkillLevel()) / 100.0 * hitInfo.damage;
            // Resolve target mob
            final Optional<Mob> mobResult = user.getField().getMobPool().getById(hitInfo.mobId);
            if (mobResult.isEmpty()) {
                return;
            }
            try (var lockedMob = mobResult.get().acquire()) {
                final Mob mob = lockedMob.get();
                if (mob.getHp() <= 0) {
                    return;
                }
                // Create attack
                final Attack attack = new Attack(OutHeader.SummonedAttack);
                attack.actionAndDir = (byte) ((summoned.getMoveAction() & 1) << 7 | SummonedActionType.ATTACK1.getValue() & 0x7F);
                final AttackInfo attackInfo = new AttackInfo();
                attackInfo.mobId = mob.getId();
                attackInfo.hitAction = MobActionType.HIT1.getValue();
                attackInfo.damage[0] = Math.min((int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX), mob.getMaxHp() / 2);
                attack.getAttackInfo().add(attackInfo);
                // Broadcast packet and process damage
                user.getField().broadcastPacket(SummonedPacket.summonedAttack(member, summoned, attack));
                mob.damage(user, attackInfo.damage[0]);
            }
        });
    }

    private static void handlePiratesRevenge(User user, HitInfo hitInfo) {
        if (hitInfo.attackIndex <= AttackIndex.Counter.getValue()) {
            return;
        }
        final int skillId = SkillConstants.getPiratesRevengeSkill(user.getJob());
        if (user.getSkillManager().hasSkillCooltime(skillId)) {
            return;
        }
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for pirates revenge skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            user.setTemporaryStat(CharacterTemporaryStat.DamR, TemporaryStatOption.of(si.getValue(SkillStat.damR, slv), skillId, si.getDuration(slv)));
            user.setSkillCooltime(skillId, si.getValue(SkillStat.x, slv)); // 50 seconds
        }
    }

    private static void handleBattleship(User user, HitInfo hitInfo) {
        if (hitInfo.finalDamage <= 0 || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.RideVehicle)) {
            return;
        }
        final TwoStateTemporaryStat option = (TwoStateTemporaryStat) user.getSecondaryStat().getOption(CharacterTemporaryStat.RideVehicle);
        if (option.nOption != SkillConstants.BATTLESHIP_VEHICLE) {
            return;
        }
        final int newDurability = Pirate.getBattleshipDurability(user) - hitInfo.finalDamage;
        if (newDurability > 0) {
            Pirate.setBattleshipDurability(user, newDurability);
        } else {
            user.getSkillManager().getSkillCooltimes().remove(SkillConstants.BATTLESHIP_DURABILITY);
            user.resetTemporaryStat(option.rOption);
            user.setSkillCooltime(option.rOption, user.getSkillStatValue(option.rOption, SkillStat.cooltime));
        }
    }
}
