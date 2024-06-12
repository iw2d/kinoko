package kinoko.world.user;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.mob.DamagedAttribute;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;
import kinoko.world.item.BodyPart;
import kinoko.world.item.Item;
import kinoko.world.item.ItemConstants;
import kinoko.world.item.WeaponType;
import kinoko.world.job.JobConstants;
import kinoko.world.job.cygnus.*;
import kinoko.world.job.explorer.*;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.BattleMage;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.PassiveSkillData;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.stat.BasicStat;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public final class CalcDamage {
    private static final Random damageRandom = new SecureRandom();

    public static int getRandomDamage(User user) {
        final Tuple<Double, Double> damageRange = CalcDamage.calcDamageRange(user);
        final double randomDamage = damageRandom.nextDouble(damageRange.getLeft(), damageRange.getRight());
        return (int) Math.min(randomDamage, GameConstants.DAMAGE_MAX);
    }

    public static Tuple<Double, Double> calcDamageRange(User user) {
        final double damageMax = calcDamageMax(user);
        final double damageMin = getTotalMastery(user) * damageMax + 0.5;
        return new Tuple<>(damageMin, damageMax);
    }


    // MAGIC DAMAGE ----------------------------------------------------------------------------------------------------

    public static int calcMagicDamage(User user, SkillInfo si, int slv, Mob mob) {
        // CalcDamage::MDamage
        final PassiveSkillData psd = user.getPassiveSkillData();
        final int psdCr = psd.getCr() + psd.getAdditionPsd().stream().mapToInt((a) -> a.cr).sum();
        final int psdCdMin = psd.getCdMin() + psd.getAdditionPsd().stream().mapToInt((a) -> a.cdMin).sum();
        final int psdMdamR = psd.getMdamR() + psd.getAdditionPsd().stream().mapToInt((a) -> a.mdamR).sum();
        final int psdImpR = psd.getImpR() + psd.getAdditionPsd().stream().mapToInt((a) -> a.impR).sum();
        final int psdDipR = psd.getDipR() + psd.getAdditionPsd().stream().mapToInt((a) -> a.dipR).sum();

        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);

        final int acc = getAcc(user);
        final int mad = getMad(user);
        int mastery = getWeaponMastery(user, weaponType);
        if (mastery == 0) {
            mastery = si.getValue(SkillStat.mastery, slv);
        }
        final double k = getMasteryConstByWT(weaponType);
        final int amp = user.getSkillManager().getSkillStatValue(SkillConstants.getAmplificationSkill(user.getJob()), SkillStat.y);

        int cr = 5;
        int cd = 0;
        if (JobConstants.isEvanJob(user.getJob())) {
            cr = user.getSkillManager().getSkillStatValue(Evan.CRITICAL_MAGIC, SkillStat.prop) + 5;
            cd = user.getSkillManager().getSkillStatValue(Evan.CRITICAL_MAGIC, SkillStat.damage);
        }
        final int sharpEyes = user.getSecondaryStat().getOption(CharacterTemporaryStat.SharpEyes).nOption;
        final int thornsEffect = user.getSecondaryStat().getOption(CharacterTemporaryStat.ThornsEffect).nOption;
        cr = cr + Math.max(sharpEyes >> 8, thornsEffect >> 8) + psdCr; // ignore cd->critical.nProb
        cd = cd + Math.max(sharpEyes & 0xFF, thornsEffect & 0xFF); // ignore cd->critical.nDamage

        double damage = calcDamageByWT(weaponType, user.getBasicStat(), 0, mad);
        // damage = adjustRandomDamage(damage, rand, k, mastery);
        damage = (damage + psdMdamR * damage / 100.0) * amp / 100.0;
        damage = getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());

        // Process ms->nMDR, ms->nMDR_ v.s. nPsdIMPR + nIgnoreTargetDEF
        // Process ms->nMGuardUp_

        final int skillDamage = si.getValue(SkillStat.damage, slv);
        if (skillDamage > 0) {
            damage = skillDamage / 100.0 * damage;
        }

        // Process critical damage
        cd = Math.max(cd + psdCdMin + 20, 50);
        // damage = get_rand(rand, cd / 100.0, 50.0) * damage + damage;
        // Ignore - weakness skills (9000 - 9002), cd->aMobCategoryDamage, cd->boss.nDamage
        // Process tKeyDown
        // Process nDojangBerserk, nWeakness, nAR01Mad, paralyze damage decrease (2121006)
        // Ignore cd->aSkill

        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Infinity)) {
            final int infinityDamR = user.getSecondaryStat().getOption(CharacterTemporaryStat.Infinity).nOption - 1;
            damage = damage + infinityDamR * damage / 100.0;
        }
        final int damR = psdDipR + user.getSecondaryStat().getOption(CharacterTemporaryStat.DamR).nOption;
        damage = damage + damR * damage / 100.0;

        return (int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX);
    }

    public static double getDamageAdjustedByElemAttr(User user, double damage, SkillInfo si, int slv, Map<ElementAttribute, DamagedAttribute> damagedElemAttr) {
        // get_damage_adjusted_by_elemAttr
        final double adjustByBuff = 1.0 - user.getSecondaryStat().getOption(CharacterTemporaryStat.ElementalReset).nOption / 100.0;
        final double boost = 0.0; // only available through item info.addition.elemBoost, ignore
        final int skillId = si.getSkillId();
        if (skillId == Bowman.INFERNO || skillId == Bowman.BLIZZARD) {
            return getDamageAdjustedByElemAttr(damage, damagedElemAttr.getOrDefault(si.getElemAttr(), DamagedAttribute.NONE), si.getValue(SkillStat.x, slv) / 100.0, boost);
        } else if (skillId == Magician.ELEMENT_COMPOSITION_FP) {
            final double half = damage * 0.5; // only poison attr gets boost
            return getDamageAdjustedByElemAttr(half, damagedElemAttr.getOrDefault(ElementAttribute.FIRE, DamagedAttribute.NONE), 1.0, 0.0) +
                    getDamageAdjustedByElemAttr(half, damagedElemAttr.getOrDefault(ElementAttribute.POISON, DamagedAttribute.NONE), 1.0, boost);
        } else if (skillId == Magician.ELEMENT_COMPOSITION_IL) {
            final double half = damage * 0.5; // only light attr gets boost
            return getDamageAdjustedByElemAttr(half, damagedElemAttr.getOrDefault(ElementAttribute.ICE, DamagedAttribute.NONE), 1.0, 0.0) +
                    getDamageAdjustedByElemAttr(half, damagedElemAttr.getOrDefault(ElementAttribute.LIGHT, DamagedAttribute.NONE), 1.0, boost);
        }
        return getDamageAdjustedByElemAttr(damage, damagedElemAttr.getOrDefault(si.getElemAttr(), DamagedAttribute.NONE), adjustByBuff, boost);
    }

    private static double getDamageAdjustedByElemAttr(double damage, DamagedAttribute damagedAttr, double adjust, double boost) {
        // get_damage_adjusted_by_elemAttr
        switch (damagedAttr) {
            case DAMAGE0 -> {
                return (1.0 - adjust) * damage;
            }
            case DAMAGE50 -> {
                return (1.0 - (adjust * 0.5 + boost)) * damage;
            }
            case DAMAGE150 -> {
                final double result = (adjust * 0.5 + boost + 1.0) * damage;
                if (damage >= result) {
                    return damage;
                }
                return Math.min(result, GameConstants.DAMAGE_MAX);
            }
            default -> {
                return damage;
            }
        }
    }


    // COMMON DAMAGE ---------------------------------------------------------------------------------------------------

    public static double calcDamageMax(User user) {
        final Item weaponItem = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType;
        if (weaponItem == null) {
            weaponType = JobConstants.canUseBareHand(user.getJob()) ? WeaponType.BAREHAND : WeaponType.NONE;
        } else {
            weaponType = WeaponType.getByItemId(weaponItem.getItemId());
        }
        return calcDamageByWT(weaponType, user.getBasicStat(), getPad(user), getMad(user));
    }

    private static double calcDamageByWT(WeaponType wt, BasicStat bs, int pad, int mad) {
        // CalcDamage::CalcDamageByWT
        final int jobId = bs.getJob();
        if (JobConstants.isBeginnerJob(jobId)) {
            return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.2);
        } else if (JobConstants.getJobCategory(jobId) == 2) { // is_mage_job
            return calcBaseDamage(bs.getInt(), bs.getLuk(), 0, mad, 1.0);
        }
        switch (wt) {
            case OH_SWORD, OH_AXE, OH_MACE -> {
                return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.2);
            }
            case DAGGER -> {
                return calcBaseDamage(bs.getLuk(), bs.getDex(), bs.getStr(), pad, 1.3);
            }
            case BAREHAND -> {
                return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.43);
            }
            case TH_SWORD, TH_AXE, TH_MACE -> {
                return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.32);
            }
            case SPEAR, POLEARM -> {
                return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.49);
            }
            case BOW -> {
                return calcBaseDamage(bs.getDex(), bs.getStr(), 0, pad, 1.2);
            }
            case CROSSBOW -> {
                return calcBaseDamage(bs.getDex(), bs.getStr(), 0, pad, 1.35);
            }
            case THROWINGGLOVE -> {
                return calcBaseDamage(bs.getLuk(), bs.getDex(), 0, pad, 1.75);
            }
            case KNUCKLE -> {
                return calcBaseDamage(bs.getStr(), bs.getDex(), 0, pad, 1.7);
            }
            case GUN -> {
                return calcBaseDamage(bs.getDex(), bs.getStr(), 0, pad, 1.5);
            }
            default -> {
                return 0.0;
            }
        }
    }

    private static int calcBaseDamage(int p1, int p2, int p3, int ad, double k) {
        // `anonymous namespace'::calc_base_damage
        return (int) ((double) (p3 + p2 + 4 * p1) / 100.0 * ((double) ad * k) + 0.5);
    }

    private static double adjustRandomDamage(double damage, int rand, double k, int mastery) {
        // `anonymous namespace'::adjust_ramdom_damage
        final double totalMastery = Math.max(mastery / 100.0 + k, GameConstants.MASTERY_MAX);
        return getRand(rand, damage, totalMastery * damage + 0.5);
    }

    private static double getRand(int rand, double f0, double f1) {
        if (f0 == f1) {
            return f0;
        } else if (f0 < f1) {
            return f0 + (rand % 10_000_000) * (f1 - f0) / 9_999_999.0;
        } else {
            return f1 + (rand % 10_000_000) * (f0 - f1) / 9_999_999.0;
        }
    }


    // SECONDARY STAT METHODS ------------------------------------------------------------------------------------------

    public static int getPad(User user) {
        // SecondaryStat::GetPAD
        final SkillManager sm = user.getSkillManager();
        final SecondaryStat ss = user.getSecondaryStat();
        final PassiveSkillData psd = user.getPassiveSkillData();
        // nPAD + incPAD + incEPAD + nPsdPADX + nBlessingArmorIncPAD
        int pad = ss.getPad() + getIncPad(user) + getIncEpad(user) + psd.getPadX() + ss.getOption(CharacterTemporaryStat.BlessingArmorIncPAD).nOption;
        // CItemInfo::GetBulletPAD
        final int bulletItemId = getBulletItemId(user);
        if (bulletItemId != 0 && !JobConstants.isMechanicJob(user.getJob())) {
            pad += ItemProvider.getItemInfo(bulletItemId).map((ii) -> ii.getInfo(ItemInfoType.incPAD)).orElse(0);
        }
        // nComboAbilityBuff
        final int comboAbilityBuff = ss.getOption(CharacterTemporaryStat.ComboAbilityBuff).nOption;
        if (comboAbilityBuff != 0) {
            final int comboSkillId = user.getJob() != 200 ? Aran.COMBO_ABILITY : 20000017; // tutorial skill?
            final int maxStacks = sm.getSkillStatValue(comboSkillId, SkillStat.x);
            final int stacks = Math.max(comboAbilityBuff / 10, maxStacks);
            pad += stacks * sm.getSkillStatValue(comboSkillId, SkillStat.y);
        }
        // Apply padR
        final int statPadR = ss.getOption(CharacterTemporaryStat.MaxLevelBuff).nOption +
                ss.getOption(CharacterTemporaryStat.DarkAura).nOption +
                ss.getOption(CharacterTemporaryStat.MorewildDamageUp).nOption +
                ss.getOption(CharacterTemporaryStat.SwallowAttackDamage).nOption;
        final int totalPadR = statPadR + psd.getPadR() + ss.getItemPadR();
        if (totalPadR > 0) {
            pad += pad * totalPadR / 100;
        }
        return Math.clamp(pad, 0, GameConstants.PAD_MAX);
    }

    public static int getMad(User user) {
        // SecondaryStat::GetMAD
        final SecondaryStat ss = user.getSecondaryStat();
        final PassiveSkillData psd = user.getPassiveSkillData();
        // nMAD + incMAD + nPsdMADX
        int mad = ss.getMad() + ss.getOption(CharacterTemporaryStat.MAD).nOption + psd.getMadX();
        // Apply madR
        final int dragonFury = 0; // TODO dragonFury
        final int statMadR = ss.getOption(CharacterTemporaryStat.MaxLevelBuff).nOption +
                ss.getOption(CharacterTemporaryStat.DarkAura).nOption +
                ss.getOption(CharacterTemporaryStat.SwallowAttackDamage).nOption +
                dragonFury;
        final int totalMadR = statMadR + psd.getMadR() + ss.getItemMadR();
        if (totalMadR > 0) {
            mad += mad * totalMadR / 100;
        }
        return Math.clamp(mad, 0, GameConstants.MAD_MAX);
    }

    public static int getAcc(User user) {
        // SecondaryStat::GetAcc
        final BasicStat bs = user.getBasicStat();
        final SecondaryStat ss = user.getSecondaryStat();
        final PassiveSkillData psd = user.getPassiveSkillData();
        final int baseAcc = (int) (bs.getLuk() + bs.getDex() * 1.2);
        // nBaseACC + nACC + nIncACC
        int acc = baseAcc + ss.getAcc() + getIncAcc(user);
        // Apply accR
        final int totalAccR = psd.getAccR() + ss.getItemAccR();
        if (totalAccR > 0) {
            acc += acc * totalAccR / 100;
        }
        return Math.clamp(acc, 0, GameConstants.ACC_MAX);
    }

    private static int getIncPad(User user) {
        // SecondaryStat::GetIncPAD
        final int incPad = user.getSecondaryStat().getOption(CharacterTemporaryStat.PAD).nOption;
        if (user.getSecondaryStat().getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            // this->aTemporaryStat[0].p->IsActivated
            return incPad;
        }
        final int ecPad = user.getSkillManager().getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.pad);
        return Math.max(incPad, ecPad);
    }

    private static int getIncEpad(User user) {
        // SecondaryStat::GetIncEPAD
        final int incEpad = user.getSecondaryStat().getOption(CharacterTemporaryStat.EPAD).nOption;
        if (user.getSecondaryStat().getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            // this->aTemporaryStat[0].p->IsActivated (unnecessary, since Energy Charge does not have any epad stat)
            return incEpad;
        }
        final int ecEpad = user.getSkillManager().getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.epad);
        return Math.max(incEpad, ecEpad);
    }

    private static int getIncAcc(User user) {
        // SecondaryStat::GetIncACC
        final int incAcc = user.getSecondaryStat().getOption(CharacterTemporaryStat.ACC).nOption;
        if (user.getSecondaryStat().getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            return incAcc;
        }
        final int ecAcc = user.getSkillManager().getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.acc);
        return Math.max(incAcc, ecAcc);
    }

    private static int getBulletItemId(User user) {
        final Item weaponItem = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        if (weaponItem == null) {
            return 0;
        }
        final Optional<Item> bulletItemResult = user.getInventoryManager().getConsumeInventory().getItems().values().stream()
                .filter((item) -> ItemConstants.isCorrectBulletItem(weaponItem.getItemId(), item.getItemId()) && item.getQuantity() >= 1)
                .findFirst();
        return bulletItemResult.map(Item::getItemId).orElse(0);
    }


    // MASTERY METHODS -------------------------------------------------------------------------------------------------

    public static double getTotalMastery(User user) {
        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        final double weaponMastery = getWeaponMastery(user, weaponType) / 100.0;
        return Math.min(weaponMastery, GameConstants.MASTERY_MAX) + getMasteryConstByWT(weaponType);
    }

    public static int getWeaponMastery(User user, WeaponType weaponType) {
        // get_weapon_mastery
        switch (weaponType) {
            case OH_SWORD, TH_SWORD -> {
                return getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_HERO, Warrior.WEAPON_MASTERY_PALADIN, DawnWarrior.SWORD_MASTERY);
            }
            case OH_AXE, TH_AXE -> {
                return getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_HERO);
            }
            case OH_MACE, TH_MACE -> {
                return getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_PALADIN);
            }
            case DAGGER -> {
                final Item shield = user.getInventoryManager().getEquipped().getItem(BodyPart.SHIELD.getValue());
                if (shield != null && WeaponType.getByItemId(shield.getItemId()) == WeaponType.SUB_DAGGER) {
                    return getMasteryFromSkill(user, Thief.KATARA_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Thief.DAGGER_MASTERY);
                }
            }
            case WAND, STAFF -> {
                // get_magic_mastery
                if (JobConstants.isEvanJob(user.getJob())) {
                    return getMasteryFromSkill(user, Evan.MAGIC_MASTERY, Evan.SPELL_MASTERY);
                } else if (JobConstants.isBattleMageJob(user.getJob())) {
                    return getMasteryFromSkill(user, BattleMage.STAFF_MASTERY);
                } else if (JobConstants.isBlazeWizardJob(user.getJob())) {
                    return getMasteryFromSkill(user, BlazeWizard.SPELL_MASTERY);
                } else if (JobConstants.isFirePoisonJob(user.getJob())) {
                    return getMasteryFromSkill(user, Magician.SPELL_MASTERY_FP);
                } else if (JobConstants.isIceLightningJob(user.getJob())) {
                    return getMasteryFromSkill(user, Magician.SPELL_MASTERY_IL);
                } else if (JobConstants.isBishopJob(user.getJob())) {
                    return getMasteryFromSkill(user, Magician.SPELL_MASTERY_BISH);
                }
            }
            case SPEAR -> {
                return getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_DRK) + user.getSecondaryStat().getOption(CharacterTemporaryStat.Beholder).nOption;
            }
            case POLEARM -> {
                if (JobConstants.isAranJob(user.getJob())) {
                    return getMasteryFromSkill(user, Aran.HIGH_MASTERY, Aran.POLEARM_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_DRK) + user.getSecondaryStat().getOption(CharacterTemporaryStat.Beholder).nOption;
                }
            }
            case BOW -> {
                if (JobConstants.isCygnusJob(user.getJob())) {
                    return getMasteryFromSkill(user, WindArcher.BOW_EXPERT, WindArcher.BOW_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Bowman.BOW_EXPERT, Bowman.BOW_MASTERY);
                }
            }
            case CROSSBOW -> {
                if (JobConstants.isWildHunterJob(user.getJob())) {
                    return getMasteryFromSkill(user, WildHunter.CROSSBOW_EXPERT, WildHunter.CROSSBOW_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Bowman.MARKSMAN_BOOST, Bowman.CROSSBOW_MASTERY);
                }
            }
            case THROWINGGLOVE -> {
                if (JobConstants.isCygnusJob(user.getJob())) {
                    return getMasteryFromSkill(user, NightWalker.CLAW_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Thief.CLAW_MASTERY);
                }
            }
            case KNUCKLE -> {
                if (JobConstants.isCygnusJob(user.getJob())) {
                    return getMasteryFromSkill(user, ThunderBreaker.KNUCKLE_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Pirate.KNUCKLE_MASTERY);
                }
            }
            case GUN -> {
                if (JobConstants.isMechanicJob(user.getJob())) {
                    return getMasteryFromSkill(user, Mechanic.EXTREME_MECH, Mechanic.MECHANIC_MASTERY);
                } else {
                    return getMasteryFromSkill(user, Pirate.GUN_MASTERY);
                }
            }
        }
        return 0;
    }

    private static int getMasteryFromSkill(User user, int... skillIds) {
        for (int skillId : skillIds) {
            final int mastery = user.getSkillManager().getSkillStatValue(skillId, SkillStat.mastery);
            if (mastery > 0) {
                return mastery;
            }
        }
        return 0;
    }

    private static double getMasteryConstByWT(WeaponType wt) {
        switch (wt) {
            case WAND, STAFF -> {
                return 0.25;
            }
            case BOW, CROSSBOW, THROWINGGLOVE, GUN -> {
                return 0.15;
            }
            default -> {
                return 0.2;
            }
        }
    }
}
