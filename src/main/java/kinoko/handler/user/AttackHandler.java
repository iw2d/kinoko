package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.field.MobPacket;
import kinoko.packet.user.UserLocal;
import kinoko.packet.user.UserRemote;
import kinoko.packet.world.WvsContext;
import kinoko.provider.ItemProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.skill.MorphInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import kinoko.world.GameConstants;
import kinoko.world.field.Field;
import kinoko.world.field.affectedarea.AffectedArea;
import kinoko.world.field.drop.Drop;
import kinoko.world.field.drop.DropEnterType;
import kinoko.world.field.drop.DropLeaveType;
import kinoko.world.field.drop.DropOwnType;
import kinoko.world.field.mob.BurnedInfo;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
import kinoko.world.field.summoned.Summoned;
import kinoko.world.field.summoned.SummonedAssistType;
import kinoko.world.field.summoned.SummonedMoveAbility;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.job.cygnus.BlazeWizard;
import kinoko.world.job.cygnus.DawnWarrior;
import kinoko.world.job.cygnus.NightWalker;
import kinoko.world.job.cygnus.ThunderBreaker;
import kinoko.world.job.explorer.Magician;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.explorer.Warrior;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.Attack;
import kinoko.world.skill.AttackInfo;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillProcessor;
import kinoko.world.user.User;
import kinoko.world.user.effect.Effect;
import kinoko.world.user.stat.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class AttackHandler {
    private static final Logger log = LogManager.getLogger(AttackHandler.class);

    @Handler(InHeader.UserMeleeAttack)
    public static void handlerUserMeleeAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMeleeAttack, CUserLocal::TryDoingNormalAttack
        final Attack attack = new Attack(OutHeader.UserMeleeAttack);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
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

        attack.crc = inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
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
        if (attack.skillId == NightWalker.POISON_BOMB) {
            attack.grenadeX = inPacket.decodeShort(); // pGrenade->GetPos()->x
            attack.grenadeY = inPacket.decodeShort(); // pGrenade->GetPos()->y
        }
        if (attack.skillId == Thief.MESO_EXPLOSION) {
            // CUserLocal::DoActiveSkill_MesoExplosion
            final int size = inPacket.decodeByte();
            attack.drops = new int[size];
            for (int i = 0; i < size; i++) {
                attack.drops[i] = inPacket.decodeInt(); // aDrop
                inPacket.decodeByte();
            }
            attack.dropExplodeDelay = inPacket.decodeShort();
        }

        handleAttack(user, attack);
    }

    @Handler(InHeader.UserShootAttack)
    public static void handlerUserShootAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingShootAttack
        final Attack attack = new Attack(OutHeader.UserShootAttack);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
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

        attack.crc = inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
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
        if (JobConstants.isWildHunterJob(user.getJob())) {
            inPacket.decodeShort(); // ptBodyRelMove.y
        }
        attack.ballStartX = inPacket.decodeShort(); // pt0.x
        attack.ballStartY = inPacket.decodeShort(); // pt0.y
        if (attack.skillId == ThunderBreaker.SPARK) {
            inPacket.decodeInt(); // tReserveSpark
        }

        handleAttack(user, attack);
    }

    @Handler(InHeader.UserMagicAttack)
    public static void handlerUserMagicAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingMagicAttack
        final Attack attack = new Attack(OutHeader.UserMagicAttack);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
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

        attack.crc = inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
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

        handleAttack(user, attack);
    }

    @Handler(InHeader.UserBodyAttack)
    public static void handlerUserBodyAttack(User user, InPacket inPacket) {
        // CUserLocal::TryDoingBodyAttack
        final Attack attack = new Attack(OutHeader.UserBodyAttack);
        final byte fieldKey = inPacket.decodeByte(); // bFieldKey
        if (user.getFieldKey() != fieldKey) {
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

        attack.crc = inPacket.decodeInt(); // SKILLLEVELDATA::GetCrC
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

        handleAttack(user, attack);
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
            if (attack.skillId == Thief.MESO_EXPLOSION) {
                ai.attackCount = inPacket.decodeByte();
                for (int j = 0; j < ai.attackCount; j++) {
                    ai.damage[j] = inPacket.decodeInt();
                }
            } else {
                ai.delay = Math.min(inPacket.decodeShort(), 1000); // tDelay
                for (int j = 0; j < attack.getDamagePerMob(); j++) {
                    ai.damage[j] = inPacket.decodeInt();
                }
            }
            inPacket.decodeInt(); // CMob::GetCrc
            attack.getAttackInfo().add(ai);
        }
    }

    private static void handleAttack(User user, Attack attack) {
        final Field field = user.getField();
        // Assign attack random
        for (AttackInfo ai : attack.getAttackInfo()) {
            ai.random = user.getCalcDamage().getNextAttackRandom();
        }

        // Set skill level
        if (attack.skillId != 0) {
            attack.slv = user.getSkillLevel(attack.skillId);
            switch (attack.skillId) {
                case Aran.FULL_SWING_DOUBLE_SWING, Aran.FULL_SWING_TRIPLE_SWING -> {
                    attack.slv = user.getSkillLevel(Aran.FULL_SWING);
                }
                case Aran.OVER_SWING_DOUBLE_SWING, Aran.OVER_SWING_TRIPLE_SWING -> {
                    attack.slv = user.getSkillLevel(Aran.OVER_SWING);
                }
                case Mechanic.ENHANCED_FLAME_LAUNCHER -> {
                    attack.slv = user.getSkillLevel(Mechanic.FLAME_LAUNCHER);
                }
                case Mechanic.ENHANCED_GATLING_GUN -> {
                    attack.slv = user.getSkillLevel(Mechanic.GATLING_GUN);
                }
                case 35111008, 35111009, 35111010 -> {
                    attack.slv = user.getSkillLevel(Mechanic.SATELLITE);
                }
                case 32001007, 32001008, 32001009, 32001010, 32001011 -> {
                    attack.slv = user.getSkillLevel(BattleMage.THE_FINISHER);
                }
            }
            if (attack.slv == 0) {
                log.error("Tried to attack with skill {} not learned by user", attack.skillId);
                return;
            }
            // Check seal
            if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Seal)) {
                log.error("Tried to attack with skill {} while sealed", attack.skillId);
                return;
            }
            // Resolve skill info and check CRC
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(attack.skillId);
            if (skillInfoResult.isEmpty()) {
                log.error("Could not to resolve skill info for attack skill ID : {}", attack.skillId);
                return;
            }
            final SkillInfo si = skillInfoResult.get();
            if (si.getLevelDataCrc(attack.slv) != attack.crc) {
                log.warn("Received mismatching CRC for skill ID : {}", attack.skillId);
            }
        }

        // Check morph
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Morph)) {
            final int morphId = user.getSecondaryStat().getOption(CharacterTemporaryStat.Morph).nOption;
            final Optional<MorphInfo> morphInfoResult = SkillProvider.getMorphInfoById(morphId);
            if (morphInfoResult.isEmpty()) {
                log.error("Could not resolve morph info for morph ID : {}", morphId);
                return;
            }
            final MorphInfo morphInfo = morphInfoResult.get();
            if (!morphInfo.isSuperman() && !morphInfo.isAttackable()) {
                log.error("Tried to attack with skill {} while morphed as morph ID : {}", attack.skillId, morphId);
                return;
            }
        }

        // Resolve mastery
        final Item weaponItem = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        if (weaponItem != null) {
            final int mastery = CalcDamage.getWeaponMastery(user, WeaponType.getByItemId(weaponItem.getItemId()));
            attack.mastery = (byte) Math.clamp(mastery, 0, 90); // changes attack afterimages
        }

        // Resolve bullet ID
        if (attack.bulletPosition != 0 && !attack.isSoulArrow() && !attack.isSpiritJavelin()) {
            final Item bulletItem = user.getInventoryManager().getConsumeInventory().getItem(attack.bulletPosition);
            if (weaponItem == null || bulletItem == null || !ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), bulletItem.getItemId())) {
                log.error("Tried to attack with incorrect bullet {} using weapon {}", bulletItem != null ? bulletItem.getItemId() : 0, weaponItem != null ? weaponItem.getItemId() : 0);
                return;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(bulletItem.getItemId());
            if (itemInfoResult.isEmpty() || itemInfoResult.get().getReqLevel() > user.getLevel()) {
                log.error("Tried to attack with bullet {} without meeting level requirements", bulletItem.getItemId());
                return;
            }
            attack.bulletItemId = bulletItem.getItemId();
            // Consume bullet for basic attack
            if (attack.skillId == 0) {
                final int bulletCount = attack.isShadowPartner() ? 2 : 1;
                if (bulletItem.getQuantity() < bulletCount) {
                    log.error("Tried to attack without enough bullets in position {}", attack.bulletPosition);
                    return;
                }
                bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCount));
                user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, attack.bulletPosition, bulletItem.getQuantity()), false));
            }
        }

        // Resolve swallow template ID
        if (attack.skillId == WildHunter.JAGUAR_OSHI_ATTACK) {
            if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.Swallow_Template)) {
                log.error("Tried to attack with Jaguar-oshi without Swallow_Template CTS set");
                return;
            }
            attack.swallowMobTemplateId = user.getSecondaryStat().getOption(CharacterTemporaryStat.Swallow_Template).nOption;
            user.resetTemporaryStat(Set.of(CharacterTemporaryStat.Swallow_Mob, CharacterTemporaryStat.Swallow_Template));
        }

        // Process skill
        if (attack.skillId != 0 && !SkillConstants.isNoConsumeAttack(attack.skillId)) {
            // Check skill cooltime and cost
            final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
            if (user.getSkillManager().hasSkillCooltime(attack.skillId)) {
                log.error("Tried to use skill {} that is still on cooltime", attack.skillId);
                return;
            }
            final int hpCon = si.getHpCon(user, attack.slv, attack.keyDown);
            if (user.getHp() <= hpCon) {
                log.error("Tried to use skill {} without enough hp, current : {}, required : {}", attack.skillId, user.getHp(), hpCon);
                return;
            }
            final int mpCon = si.getMpCon(user, attack.slv);
            if (user.getMp() < mpCon) {
                log.error("Tried to use skill {} without enough mp, current : {}, required : {}", attack.skillId, user.getMp(), mpCon);
                return;
            }
            final int comboCon = SkillConstants.getRequiredComboCount(attack.skillId);
            if (comboCon > 0) {
                if (user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboAbilityBuff).nOption < comboCon) {
                    log.error("Tried to use skill {} without required combo count : {}", attack.skillId, comboCon);
                }
                user.resetTemporaryStat(Set.of(CharacterTemporaryStat.ComboAbilityBuff));
            }
            // Item / Bullet consume are mutually exclusive
            final int itemCon = si.getValue(SkillStat.itemCon, attack.slv);
            if (itemCon > 0) {
                final int itemConNo = si.getValue(SkillStat.itemConNo, attack.slv); // should always be > 0
                final Optional<List<InventoryOperation>> removeResult = user.getInventoryManager().removeItem(itemCon, itemConNo);
                if (removeResult.isEmpty()) {
                    log.error("Tried to use skill {} without required item", itemCon);
                    return;
                }
                user.write(WvsContext.inventoryOperation(removeResult.get(), false));
            }
            final int bulletCon = si.getBulletCon(attack.slv);
            if (bulletCon > 0) {
                final int exJablinProp = user.getSkillStatValue(Thief.EXPERT_THROWING_STAR_HANDLING, SkillStat.prop);
                final boolean exJablin = exJablinProp != 0 && Util.succeedProp(exJablinProp);
                if (exJablin) {
                    user.write(UserLocal.requestExJablin());
                }
                if (attack.bulletPosition != 0 && !attack.isSoulArrow() && !attack.isSpiritJavelin()) {
                    final int bulletCount = bulletCon * (attack.isShadowPartner() ? 2 : 1);
                    final Item bulletItem = user.getInventoryManager().getConsumeInventory().getItem(attack.bulletPosition);
                    if (bulletItem == null || bulletItem.getQuantity() < bulletCount) {
                        log.error("Tried to use skill {} without enough bullets", attack.skillId);
                        return;
                    }
                    if (exJablin) {
                        // Recharge 1 throwing star if possible
                        final int slotMax = ItemProvider.getItemInfo(bulletItem.getItemId()).map(ItemInfo::getSlotMax).orElse(0);
                        if (bulletItem.getQuantity() < slotMax) {
                            bulletItem.setQuantity((short) (bulletItem.getQuantity() + 1));
                            user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, attack.bulletPosition, bulletItem.getQuantity()), false));
                        }
                        user.write(UserLocal.requestExJablin());
                    } else {
                        // Consume bullets
                        bulletItem.setQuantity((short) (bulletItem.getQuantity() - bulletCount));
                        user.write(WvsContext.inventoryOperation(InventoryOperation.itemNumber(InventoryType.CONSUME, attack.bulletPosition, bulletItem.getQuantity()), false));
                    }
                }
            }
            // Consume hp/mp
            user.addHp(-hpCon);
            user.addMp(-mpCon);
            // Set cooltime
            final int cooltime = si.getValue(SkillStat.cooltime, attack.slv);
            if (cooltime > 0) {
                user.setSkillCooltime(attack.skillId, cooltime);
            }
        }

        // Process attack
        int hpGain = 0;
        int mpGain = 0;
        for (AttackInfo ai : attack.getAttackInfo()) {
            final Optional<Mob> mobResult = field.getMobPool().getById(ai.mobId);
            if (mobResult.isEmpty()) {
                continue;
            }
            final Mob mob = mobResult.get();
            // Verify damage
            if (attack.isMagicAttack()) {
                CalcDamage.calcMDamage(user, mob, attack, ai);
            } else {
                CalcDamage.calcPDamage(user, mob, attack, ai);
            }
            // Skill specific handling
            if (attack.skillId != 0) {
                SkillProcessor.processAttack(user, mob, attack, ai.delay);
            }
            // Process damage
            int totalDamage = Arrays.stream(ai.damage).sum();
            int mpDamage = 0;
            // Handle skills
            handlePickpocket(user, attack, mob);
            handleOwlSpirit(user, attack, mob.getMaxHp() == totalDamage);
            if (attack.skillId == Aran.COMBO_TEMPEST) {
                // client sends normal damage for bosses, normal mobs are set to 1 hp
                if (!mob.isBoss()) {
                    totalDamage = mob.getHp() - 1;
                }
            } else if (attack.skillId == Warrior.HEAVENS_HAMMER) {
                // client sends 1 damage, calculate damage = Math.min(damage, mob.getHp() - 1)
                totalDamage = calculateHeavensHammer(user, mob);
            } else if (attack.skillId == Thief.DRAIN || attack.skillId == NightWalker.VAMPIRE) {
                // cannot absorb more than half of your max hp or more than the enemy's max hp
                final int absorbAmount = totalDamage * user.getSkillStatValue(attack.skillId, SkillStat.x) / 100;
                hpGain += Math.min(Math.min(absorbAmount, user.getMaxHp() / 2), mob.getMaxHp());
            } else if (attack.skillId == WildHunter.SWIPE) {
                // cannot absorb more than 15% of your max hp or more than the enemy's max hp
                final int absorbAmount = totalDamage * user.getSkillStatValue(attack.skillId, SkillStat.x) / 100;
                hpGain += Math.min(Math.min(absorbAmount, user.getMaxHp() * 15 / 100), mob.getMaxHp());
            } else if (attack.skillId == Pirate.ENERGY_DRAIN || attack.skillId == ThunderBreaker.ENERGY_DRAIN) {
                hpGain += totalDamage * user.getSkillStatValue(attack.skillId, SkillStat.x) / 100;
            } else if (attack.skillId != 0) {
                mpDamage = calculateMpEater(user, mob);
            }
            if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.ComboDrain)) {
                final int absorbAmount = totalDamage * user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboDrain).nOption / 100;
                hpGain += Math.min(absorbAmount, user.getMaxHp() / 10);
            }
            // Process damage
            mob.damage(user, totalDamage, attack.skillId == Thief.MESO_EXPLOSION ? attack.dropExplodeDelay : ai.delay);
            mob.setMp(mob.getMp() - mpDamage);
            mpGain += mpDamage;
            // Process on-hit effects
            if (mob.getHp() > 0) {
                handleHamString(user, mob, ai.delay);
                handleBlind(user, mob, ai.delay);
                handleVenom(user, mob, ai.delay);
                handleWeaponCharge(user, mob, ai.delay);
                handleEvanSlow(user, mob, ai.delay);
                handleMortalBlow(user, mob, ai.delay);
            }
            // Process on-kill effects
            if (mob.getHp() <= 0) {
                handleRevive(user, mob);
            }
        }

        // Broadcast packet
        field.broadcastPacket(UserRemote.attack(user, attack), user);

        // Process hp/mp gains
        if (hpGain > 0) {
            user.addHp(hpGain);
        }
        if (mpGain > 0) {
            user.addMp(mpGain);
            // Show MP eater effect
            final int skillId = SkillConstants.getMpEaterSkill(user.getJob());
            final int slv = user.getSkillLevel(skillId);
            user.write(UserLocal.effect(Effect.skillUse(skillId, slv, user.getLevel())));
            field.broadcastPacket(UserRemote.effect(user, Effect.skillUse(skillId, slv, user.getLevel())), user);
        }
        if (attack.exJablin != 0) {
            user.getCalcDamage().setNextAttackCritical(true);
        }

        // Skill effects after attack
        handleAffectedArea(user, attack);
        handleMesoExplosion(user, attack);
        handleFinalCut(user, attack);
        if (attack.getMobCount() > 0) {
            handleComboAbility(user, attack);
            handleComboAttack(user, attack);
            handleDarkSight(user);
            handleEnergyCharge(user);
            handleWindWalk(user);
        }
    }


    // -----------------------------------------------------------------------------------------------------------------

    private static void handlePickpocket(User user, Attack attack, Mob mob) {
        if (attack.skillId == Thief.MESO_EXPLOSION || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.PickPocket)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.PickPocket);
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(option.rOption);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for pick pocket skill ID : {}", option.rOption);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final int money = option.nOption;
        final int prop = si.getValue(SkillStat.prop, option.nOption) +
                user.getSkillStatValue(Thief.MESO_MASTERY, SkillStat.u);
        // Trigger per hit, spread and delay drops
        final List<Drop> drops = new ArrayList<>();
        for (int i = 0; i < attack.getDamagePerMob(); i++) {
            if (money > 0 && Util.succeedProp(prop)) {
                drops.add(Drop.money(DropOwnType.USEROWN, mob, money, user.getCharacterId()));
            }
        }
        if (!drops.isEmpty()) {
            user.getField().getDropPool().addDrops(drops, DropEnterType.CREATE, mob.getX(), mob.getY() - GameConstants.DROP_HEIGHT, 0, 120);
        }
    }

    private static void handleOwlSpirit(User user, Attack attack, boolean instantKill) {
        // Decrement attack counter
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.SuddenDeath)) {
            final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.SuddenDeath_Count);
            final int newCount = option.nOption - 1;
            if (newCount > 0) {
                user.setTemporaryStat(CharacterTemporaryStat.SuddenDeath_Count, option.update(newCount));
            } else {
                user.resetTemporaryStat(Set.of(CharacterTemporaryStat.SuddenDeath, CharacterTemporaryStat.SuddenDeath_Count));
            }
        }
        // Give buff on instant kill with owl spirit
        if (!instantKill || attack.skillId != Thief.OWL_SPIRIT) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(attack.skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for owl spirit skill ID : {}", attack.skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        user.setTemporaryStat(Map.of(
                CharacterTemporaryStat.SuddenDeath, TemporaryStatOption.of(si.getValue(SkillStat.y, attack.slv), attack.skillId, 0),
                CharacterTemporaryStat.SuddenDeath_Count, TemporaryStatOption.of(si.getValue(SkillStat.x, attack.slv), attack.skillId, 0)
        ));
    }

    private static int calculateHeavensHammer(User user, Mob mob) {
        // Resolve skill info
        final int skillId = Warrior.HEAVENS_HAMMER;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for mp eater skill ID : {}", skillId);
            return 0;
        }
        final SkillInfo si = skillInfoResult.get();
        final int slv = user.getSkillLevel(skillId);
        // Calculate damage
        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        final int mastery = CalcDamage.getWeaponMastery(user, weaponType);
        final double k = CalcDamage.getMasteryConstByWT(weaponType);
        final double damageMax = CalcDamage.calcDamageByWT(weaponType, user.getBasicStat(), CalcDamage.getPad(user), CalcDamage.getMad(user));
        double damage = CalcDamage.adjustRandomDamage(damageMax, Util.getRandom().nextInt(), k, mastery);
        damage = damage + user.getPassiveSkillData().getPdamR() * damage / 100.0;
        damage = CalcDamage.getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());
        damage = CalcDamage.getDamageAdjustedByChargedElemAttr(user, damage, mob.getDamagedElemAttr());
        damage += CalcDamage.getDamageAdjustedByAssistChargedElemAttr(user, damage, mob.getDamagedElemAttr());
        final int skillDamage = si.getValue(SkillStat.damage, slv);
        if (skillDamage > 0) {
            damage = skillDamage / 100.0 * damage;
        }
        final int damR = user.getPassiveSkillData().getDipR() + user.getSecondaryStat().getOption(CharacterTemporaryStat.DamR).nOption;
        damage = damage + damR * damage / 100.0;
        return Math.min((int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX), mob.getHp() - 1);
    }

    private static int calculateMpEater(User user, Mob mob) {
        final int skillId = SkillConstants.getMpEaterSkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return 0;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for mp eater skill ID : {}", skillId);
            return 0;
        }
        final SkillInfo si = skillInfoResult.get();
        if (!Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            return 0;
        }
        final int delta = mob.getMaxMp() * si.getValue(SkillStat.x, slv) / 100;
        return Math.clamp(delta, 0, mob.getMp());
    }

    private static void handleHamString(User user, Mob mob, int delay) {
        if (mob.isBoss() || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.HamString)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.HamString);
        final int skillId = option.rOption;
        final int slv = option.nOption;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for hamstring skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            mob.setTemporaryStat(MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getValue(SkillStat.y, slv) * 1000), delay);
        }
    }

    private static void handleBlind(User user, Mob mob, int delay) {
        if (mob.isBoss() || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.Blind)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.Blind);
        final int skillId = option.rOption;
        final int slv = option.nOption;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for blind skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            mob.setTemporaryStat(MobTemporaryStat.Blind, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getValue(SkillStat.y, slv) * 1000), delay);
        }
    }

    private static void handleVenom(User user, Mob mob, int delay) {
        final int skillId = SkillConstants.getVenomSkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for venom skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            mob.setBurnedInfo(BurnedInfo.from(user, si, slv, mob), delay);
        }
    }

    private static void handleWeaponCharge(User user, Mob mob, int delay) {
        if (mob.isBoss() || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.WeaponCharge)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.WeaponCharge);
        final int skillId = option.rOption;
        if (skillId == Warrior.ICE_CHARGE) {
            final int duration = user.getSkillStatValue(skillId, SkillStat.y);
            if (duration > 0) {
                mob.setTemporaryStat(MobTemporaryStat.Freeze, MobStatOption.of(1, skillId, duration * 1000), delay);
            }
        } else if (skillId == Aran.SNOW_CHARGE) {
            final int duration = user.getSkillStatValue(skillId, SkillStat.y);
            if (duration > 0) {
                mob.setTemporaryStat(MobTemporaryStat.Speed, MobStatOption.of(user.getSkillStatValue(skillId, SkillStat.x), skillId, duration * 1000), delay);
            }
        }
    }

    private static void handleEvanSlow(User user, Mob mob, int delay) {
        if (mob.isBoss() || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.EvanSlow)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.EvanSlow);
        final int skillId = option.rOption;
        final int slv = option.nOption;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for evan slow skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            mob.setTemporaryStat(MobTemporaryStat.Speed, MobStatOption.of(si.getValue(SkillStat.x, slv), skillId, si.getValue(SkillStat.y, slv) * 1000), delay);
        }
    }

    private static void handleMortalBlow(User user, Mob mob, int delay) {
        if (mob.isBoss()) {
            return;
        }
        final int skillId = SkillConstants.getMortalBlowSkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for mortal blow skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (!Util.succeedProp(si.getValue(SkillStat.y, slv))) {
            return;
        }
        final double percentage = (double) mob.getHp() / mob.getMaxHp();
        if (percentage * 100 < si.getValue(SkillStat.x, slv)) {
            user.getField().broadcastPacket(MobPacket.mobSpecialEffectBySkill(mob, skillId, user.getCharacterId(), delay));
            mob.damage(user, mob.getHp(), delay);
        }
    }

    private static void handleRevive(User user, Mob mob) {
        if (mob.isBoss() || !user.getSecondaryStat().hasOption(CharacterTemporaryStat.Revive)) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.Revive);
        final int skillId = option.rOption; // 32111006 - Summon Reaper Buff
        final int slv = option.nOption;
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for revive skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        if (Util.succeedProp(si.getValue(SkillStat.prop, slv))) {
            final Summoned summoned = Summoned.from(skillId, slv, SummonedMoveAbility.WALK_RANDOM, SummonedAssistType.ATTACK, Instant.now().plus(si.getValue(SkillStat.x, slv), ChronoUnit.SECONDS));
            summoned.setPosition(user.getField(), mob.getX(), mob.getY(), mob.isLeft());
            user.addSummoned(summoned);
        }
    }


    // -----------------------------------------------------------------------------------------------------------------

    private static void handleAffectedArea(User user, Attack attack) {
        switch (attack.skillId) {
            case Magician.POISON_MIST, BlazeWizard.FLAME_GEAR, NightWalker.POISON_BOMB -> {
                final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
                final AffectedArea affectedArea = AffectedArea.userSkill(user, si, attack.slv, 0, attack.userX, attack.userY);
                user.getField().getAffectedAreaPool().addAffectedArea(affectedArea);
            }
        }
    }

    private static void handleMesoExplosion(User user, Attack attack) {
        if (attack.skillId != Thief.MESO_EXPLOSION) {
            return;
        }
        int index = 0;
        final Field field = user.getField();
        for (int dropId : attack.drops) {
            final Optional<Drop> dropResult = field.getDropPool().getById(dropId);
            if (dropResult.isEmpty() || !dropResult.get().isMoney()) {
                log.error("Received invalid drop ID {} for meso explosion skill", dropId);
                continue;
            }
            final int delay = Math.min(attack.dropExplodeDelay + 100 * (index++ % 5), 1000);
            field.getDropPool().removeDrop(dropResult.get(), DropLeaveType.EXPLODE, 0, 0, delay);
        }
    }

    private static void handleFinalCut(User user, Attack attack) {
        if (attack.skillId != Thief.FINAL_CUT) {
            return;
        }
        final SkillInfo si = SkillProvider.getSkillInfoById(attack.skillId).orElseThrow();
        final int finalCut = si.getValue(SkillStat.y, attack.slv) * attack.keyDown / SkillConstants.getMaxGaugeTime(attack.skillId);
        user.setTemporaryStat(CharacterTemporaryStat.FinalCut, TemporaryStatOption.of(finalCut, attack.skillId, si.getDuration(attack.slv)));
    }

    private static void handleComboAbility(User user, Attack attack) {
        final int skillId = SkillConstants.getComboAbilitySkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboAbilityBuff);
        final int newCombo = option.nOption + attack.getMobCount();
        user.setTemporaryStat(CharacterTemporaryStat.ComboAbilityBuff, TemporaryStatOption.of(newCombo, skillId, 0));
        user.write(UserLocal.incCombo(newCombo));
    }

    private static void handleComboAttack(User user, Attack attack) {
        final TemporaryStatOption option = user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboCounter);
        if (option.nOption == 0) {
            return;
        }
        switch (attack.skillId) {
            case Warrior.PANIC, Warrior.COMA, DawnWarrior.PANIC, DawnWarrior.COMA -> {
                Warrior.resetComboCounter(user);
                return;
            }
        }
        final int comboAttackId = SkillConstants.getComboAttackSkill(user.getJob());
        final int advancedComboId = SkillConstants.getAdvancedComboSkill(user.getJob());
        final int maxCombo = 1 + Math.max(
                user.getSkillStatValue(comboAttackId, SkillStat.x),
                user.getSkillStatValue(advancedComboId, SkillStat.x)
        );
        if (option.nOption < maxCombo) {
            final int doubleProp = user.getSkillStatValue(advancedComboId, SkillStat.prop);
            final int newCombo = Math.min(option.nOption + (Util.succeedProp(doubleProp) ? 2 : 1), maxCombo);
            user.setTemporaryStat(CharacterTemporaryStat.ComboCounter, option.update(newCombo));
        }
    }

    private static void handleDarkSight(User user) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.DarkSight)) {
            return;
        }
        if (!Util.succeedProp(user.getSkillStatValue(Thief.ADVANCED_DARK_SIGHT, SkillStat.prop))) {
            user.resetTemporaryStat(user.getSecondaryStat().getOption(CharacterTemporaryStat.DarkSight).rOption);
        }
    }

    private static void handleEnergyCharge(User user) {
        final int skillId = SkillConstants.getEnergyChargeSkill(user.getJob());
        final int slv = user.getSkillLevel(skillId);
        if (slv == 0) {
            return;
        }
        final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
        if (skillInfoResult.isEmpty()) {
            log.error("Could not resolve skill info for energy charge skill ID : {}", skillId);
            return;
        }
        final SkillInfo si = skillInfoResult.get();
        final SecondaryStat ss = user.getSecondaryStat();
        final int energyCharge = ss.getOption(CharacterTemporaryStat.EnergyCharged).nOption;
        if (energyCharge < SkillConstants.ENERGY_CHARGE_MAX) {
            final TwoStateTemporaryStat option = TemporaryStatOption.ofTwoState(
                    CharacterTemporaryStat.EnergyCharged,
                    Math.min(energyCharge + si.getValue(SkillStat.x, slv), SkillConstants.ENERGY_CHARGE_MAX),
                    skillId,
                    si.getDuration(slv)
            );
            user.setTemporaryStat(CharacterTemporaryStat.EnergyCharged, option);
        }
    }

    private static void handleWindWalk(User user) {
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.WindWalk)) {
            return;
        }
        user.resetTemporaryStat(user.getSecondaryStat().getOption(CharacterTemporaryStat.WindWalk).rOption);
    }
}
