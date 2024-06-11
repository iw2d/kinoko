package kinoko.world.user;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.skill.SkillStat;
import kinoko.util.Tuple;
import kinoko.util.Util;
import kinoko.world.GameConstants;
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
        return (int) ((double) (p3 + p2 + 4 * p1) / 100.0 * ((double) ad * k) + 0.5);
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

    private static int getIncPad(User user) {
        // SecondaryStat::GetIncPAD
        final SkillManager sm = user.getSkillManager();
        final SecondaryStat ss = user.getSecondaryStat();
        final int incPad = ss.getOption(CharacterTemporaryStat.PAD).nOption;
        if (ss.getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            // this->aTemporaryStat[0].p->IsActivated
            return incPad;
        }
        final int ecPad = sm.getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.pad);
        return Math.max(incPad, ecPad);
    }

    private static int getIncEpad(User user) {
        // SecondaryStat::GetIncEPAD
        final SkillManager sm = user.getSkillManager();
        final SecondaryStat ss = user.getSecondaryStat();
        final int incEpad = ss.getOption(CharacterTemporaryStat.EPAD).nOption;
        if (ss.getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            // this->aTemporaryStat[0].p->IsActivated (unnecessary, since Energy Charge does not have any epad stat)
            return incEpad;
        }
        final int ecEpad = sm.getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.epad);
        return Math.max(incEpad, ecEpad);
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
