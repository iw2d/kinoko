package kinoko.world.user;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.mob.DamagedAttribute;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStatOption;
import kinoko.world.field.mob.MobTemporaryStat;
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
import kinoko.world.skill.ActionType;
import kinoko.world.skill.PassiveSkillData;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;
import kinoko.world.user.stat.BasicStat;
import kinoko.world.user.stat.CharacterTemporaryStat;
import kinoko.world.user.stat.SecondaryStat;

import java.util.Map;
import java.util.Optional;

public final class CalcDamage {

    public static double calcDamageMax(User user) {
        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        return calcDamageByWT(weaponType, user.getBasicStat(), getPad(user), getMad(user));
    }

    public static double calcDamageMin(User user) {
        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        final double k = getMasteryConstByWT(weaponType);
        final int mastery = getWeaponMastery(user, weaponType);
        // Note : for CUIStatDetail::Draw, totalMastery = k + min(mastery, 0.95)
        // whereas in the actual calculation (adjust_ramdom_damage), totalMastery = min(mastery + k, 0.95)
        return (k + Math.min(mastery / 100.0, GameConstants.MASTERY_MAX)) * calcDamageMax(user) + 0.5;
    }


    // PHYSICAL DAMAGE -------------------------------------------------------------------------------------------------

    public static double calcPDamage(User user, double damage, SkillInfo si, int slv, Mob mob, ActionType actionType) {
        // nPsdPDamR
        damage = damage + user.getPassiveSkillData().getAllPdamR() + damage / 100.0;
        // Level disadvantage
        if (mob.getLevel() > user.getLevel()) {
            damage = (100.0 - (mob.getLevel() - user.getLevel())) / 100.0 * damage;
        }
        // Damage adjusted by elem attr
        damage = getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());
        damage = CalcDamage.getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());
        damage = CalcDamage.getDamageAdjustedByChargedElemAttr(user, damage, mob.getDamagedElemAttr());
        damage += CalcDamage.getDamageAdjustedByAssistChargedElemAttr(user, damage, mob.getDamagedElemAttr());
        // ms->nPDR
        final int skillId = si.getSkillId();
        if (skillId != Warrior.SACRIFICE && skillId != Thief.ASSAULTER && skillId != Pirate.DEMOLITION) {
            final MobStatOption option = mob.getMobStat().getOption(MobTemporaryStat.PDR);
            final int mobBasePdr = mob.getTemplate().getPdr();
            final int mobTotalPdr;
            if (mobBasePdr != 0 && option.rOption == Warrior.THREATEN && mob.getTemplateId() / 10000 != 882) {
                mobTotalPdr = (int) ((option.nOption / 100.0 + 1.0) * mobBasePdr);
            } else {
                mobTotalPdr = mobBasePdr + option.nOption;
            }
            final int mobFinalPdr = Math.clamp(mobTotalPdr, 0, 100);
            // nPsdIMPR + nIgnoreTargetDEF
            final int totalIgnoreDef = Math.min(user.getPassiveSkillData().getAllImpR(), 100);
            final double multiplier = totalIgnoreDef * mobFinalPdr / -100 + mobFinalPdr;
            damage = (100.0 - multiplier) / 100.0 * damage;
        }
        // Skill damage
        final SecondaryStat ss = user.getSecondaryStat();
        final int skillDamage = getSkillPDamage(user, si, slv, mob, 0);
        if (skillDamage > 0) {
            damage = skillDamage / 100.0 * damage;
        }
        final int comboDamageParam = getComboDamageParam(user, si, slv);
        if (comboDamageParam > 0) {
            damage = damage * comboDamageParam / 100.0;
        }
        final int enrageDamageRate = ss.getOption(CharacterTemporaryStat.Enrage).nOption / 100;
        if (enrageDamageRate > 0) {
            damage = (enrageDamageRate + 100) / 100.0 * damage;
        }
        // Process critical damage
        if (skillId != Thief.ASSASSINATE || actionType == ActionType.ASSASSINATIONS) {
            // cd = cdFromSkills + nPsdCDMin + 20;
            // cd = Math.min(cd, nTempCriticalAttackParam + 50);
            // multiplier = get_rand(rand, cd, nTempCriticalAttackParam + 50.0) / 100.0;
            // damage = multiplier * damage? + damage; // multiplied damage is before applying comboDamageParam and enrageDamageRate?
        }
        if (skillId != Warrior.SACRIFICE) {
            final int mobPGuardUp = mob.getMobStat().getOption(MobTemporaryStat.PGuardUp).nOption;
            if (mobPGuardUp > 0) {
                damage = mobPGuardUp * damage / 100.0;
            }
        }
        // TODO : Handle shadow partner, assassinate scaling with dark sight
        if (ss.hasOption(CharacterTemporaryStat.WindWalk)) {
            damage = user.getSkillStatValue(WindArcher.WIND_WALK, SkillStat.damage) / 100.0 * damage;
        }
        if (ss.hasOption(CharacterTemporaryStat.DarkSight)) {
            if (user.getSkillLevel(NightWalker.VANISH) > 0) {
                damage = user.getSkillStatValue(NightWalker.VANISH, SkillStat.damage) / 100.0 * damage;
            }
        }
        if (mob.getMobStat().hasOption(MobTemporaryStat.Stun) || mob.getMobStat().hasOption(MobTemporaryStat.Blind)) {
            if (user.getSkillLevel(Warrior.CHANCE_ATTACK) > 0) {
                damage = user.getSkillStatValue(Warrior.CHANCE_ATTACK, SkillStat.damage) / 100.0 * damage;
            }
        }
        // Ignore - weakness skills (9000 - 9002), cd->aMobCategoryDamage, cd->boss.nDamage
        // TODO : Process tKeyDown, guided bullet damage, nDojangBerserk
        if (ss.hasOption(CharacterTemporaryStat.SuddenDeath) && !SkillConstants.isDualAddDamageExceptSkill(skillId)) {
            damage = ss.getOption(CharacterTemporaryStat.SuddenDeath).nOption * damage / 100.0;
        }
        if (ss.hasOption(CharacterTemporaryStat.FinalCut) && !SkillConstants.isDualAddDamageExceptSkill(skillId)) {
            damage = ss.getOption(CharacterTemporaryStat.FinalCut).nOption * damage / 100.0;
        }
        // TODO : nAR01Pad
        if (SkillConstants.isJaguarMeleeAttackSkill(skillId)) {
            if (user.getSkillLevel(WildHunter.JAGUAR_BOOST) > 0) {
                final int jaguarBoost = user.getSkillStatValue(WildHunter.JAGUAR_BOOST, SkillStat.damage) + 100;
                damage = jaguarBoost * damage / 100.0;
            }
        }
        // TODO : handle shadow meso
        // Ignore - cd->aSkill
        // nTotalDAMr + nPsdDIPR + nBossDAMr + nDamR
        int damR = user.getPassiveSkillData().getAllDipR() + ss.getOption(CharacterTemporaryStat.DamR).nOption;
        damage = damage + damR * damage / 100.0;
        return Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX);
    }

    private static int getSkillPDamage(User user, SkillInfo si, int slv, Mob mob, int i) {
        final SkillManager sm = user.getSkillManager();
        final int skillId = si.getSkillId();
        int damage = si.getValue(SkillStat.damage, slv);
        if (skillId == Mechanic.GIANT_ROBOT_SG_88) {
            damage = si.getValue(SkillStat.y, slv);
        }
        if (skillId == Bowman.STRAFE_MM) {
            if (user.getSkillLevel(Bowman.ULTIMATE_STRAFE) > 0) {
                damage = user.getSkillStatValue(Bowman.ULTIMATE_STRAFE, SkillStat.damage);
            }
        } else if (skillId == WildHunter.EXPLODING_ARROWS) {
            if (i == si.getValue(SkillStat.attackCount, slv) - 1) {
                damage = si.getValue(SkillStat.x, slv);
            }
        }
        if (user.getSkillLevel(Pirate.BRAWLING_MASTERY) > 0) {
            switch (skillId) {
                case Pirate.BACKSPIN_BLOW -> {
                    damage += user.getSkillStatValue(Pirate.BRAWLING_MASTERY, SkillStat.x);
                }
                case Pirate.DOUBLE_UPPERCUT -> {
                    damage += user.getSkillStatValue(Pirate.BRAWLING_MASTERY, SkillStat.y);
                }
                case Pirate.CORKSCREW_BLOW -> {
                    damage += user.getSkillStatValue(Pirate.BRAWLING_MASTERY, SkillStat.z);
                }
            }
        }
        if (skillId == Pirate.FLAMETHROWER || skillId == Pirate.ICE_SPLITTER) {
            if (user.getSkillLevel(Pirate.ELEMENTAL_BOOST) > 0) {
                damage += user.getSkillStatValue(Pirate.ELEMENTAL_BOOST, SkillStat.damage);
            }
        }
        if (damage > 0) {
            damage += mob.getMobStat().getOption(MobTemporaryStat.RiseByToss).nOption;
            if (skillId == Warrior.POWER_STRIKE || skillId == Warrior.SLASH_BLAST) {
                final int enhancedBasicsId = SkillConstants.getEnhancedBasicsSkill(user.getJob());
                if (user.getSkillLevel(enhancedBasicsId) > 0) {
                    damage += user.getSkillStatValue(enhancedBasicsId, skillId == Warrior.POWER_STRIKE ? SkillStat.x : SkillStat.y);
                }
            } else if (skillId == Bowman.ARROW_BLOW || skillId == Bowman.DOUBLE_SHOT) {
                final int enhancedBasicsId = SkillConstants.getEnhancedBasicsSkill(user.getJob());
                if (user.getSkillLevel(enhancedBasicsId) > 0) {
                    damage += user.getSkillStatValue(enhancedBasicsId, skillId == Bowman.ARROW_BLOW ? SkillStat.x : SkillStat.y);
                }
            }
        }
        if (skillId == Thief.MESO_EXPLOSION) {
            if (JobConstants.isCorrectJobForSkillRoot(user.getJob(), 422)) {
                if (user.getSkillLevel(Thief.MESO_MASTERY) > 0) {
                    damage += user.getSkillStatValue(Thief.MESO_MASTERY, SkillStat.x);
                }
            }
        }
        if (skillId == Warrior.CHARGED_BLOW) {
            final int advancedChargeDamage = user.getSkillStatValue(Warrior.ADVANCED_CHARGE, SkillStat.damage);
            if (advancedChargeDamage > 0) {
                damage = advancedChargeDamage;
            }
        }
        return damage;
    }

    private static int getComboDamageParam(User user, SkillInfo si, int slv) {
        // get_combo_damage_param
        int comboCounter = user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboCounter).nOption - 1;
        if (comboCounter < 1) {
            return 0;
        }
        final SkillManager sm = user.getSkillManager();
        final int comboAttackId = SkillConstants.getComboAttackSkill(user.getJob());
        final int advancedComboId = SkillConstants.getAdvancedComboSkill(user.getJob());
        if (user.getSkillLevel(comboAttackId) == 0) {
            return 0;
        }
        final int maxCombo;
        int damagePerCombo = user.getSkillStatValue(comboAttackId, SkillStat.damR); // nDIPr
        if (user.getSkillLevel(advancedComboId) > 0) {
            damagePerCombo += user.getSkillStatValue(advancedComboId, SkillStat.damR); // nDIPr
            maxCombo = user.getSkillStatValue(advancedComboId, SkillStat.x);
        } else {
            maxCombo = user.getSkillStatValue(comboAttackId, SkillStat.x);
        }
        comboCounter = Math.min(comboCounter, maxCombo);
        if (si.getSkillId() == Warrior.COMA || si.getSkillId() == Warrior.PANIC || si.getSkillId() == DawnWarrior.PANIC || si.getSkillId() == DawnWarrior.COMA) {
            if (slv == 0) {
                return 0;
            }
            damagePerCombo += si.getValue(SkillStat.y, slv);
        }
        return comboCounter * damagePerCombo + 100;
    }


    // MAGICAL DAMAGE --------------------------------------------------------------------------------------------------

    public static int calcMDamage(User user, SkillInfo si, int slv, Mob mob) {
        // CalcDamage::MDamage
        final int psdCr = user.getPassiveSkillData().getAllCr();
        final int psdCdMin = user.getPassiveSkillData().getAllCdMin();
        final int psdMdamR = user.getPassiveSkillData().getAllMdamR();
        final int psdImpR = user.getPassiveSkillData().getAllImpR();
        final int psdDipR = user.getPassiveSkillData().getAllDipR();

        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        final WeaponType weaponType = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);

        final int acc = getAcc(user);
        final int mad = getMad(user);
        int mastery = getWeaponMastery(user, weaponType);
        if (mastery == 0) {
            mastery = si.getValue(SkillStat.mastery, slv);
        }
        final double k = getMasteryConstByWT(weaponType);
        final int amp = user.getSkillStatValue(SkillConstants.getAmplificationSkill(user.getJob()), SkillStat.y);

        int cr = 5;
        int cd = 0;
        if (JobConstants.isEvanJob(user.getJob())) {
            cr = user.getSkillStatValue(Evan.CRITICAL_MAGIC, SkillStat.prop) + 5;
            cd = user.getSkillStatValue(Evan.CRITICAL_MAGIC, SkillStat.damage);
        }
        final int sharpEyes = user.getSecondaryStat().getOption(CharacterTemporaryStat.SharpEyes).nOption;
        final int thornsEffect = user.getSecondaryStat().getOption(CharacterTemporaryStat.ThornsEffect).nOption;
        cr = cr + Math.max(sharpEyes >> 8, thornsEffect >> 8) + psdCr; // ignore cd->critical.nProb
        cd = cd + Math.max(sharpEyes & 0xFF, thornsEffect & 0xFF); // ignore cd->critical.nDamage

        double damage = calcDamageByWT(weaponType, user.getBasicStat(), 0, mad);
        // damage = adjustRandomDamage(damage, rand, k, mastery);
        damage = (damage + psdMdamR * damage / 100.0) * amp / 100.0;
        damage = getDamageAdjustedByElemAttr(user, damage, si, slv, mob.getDamagedElemAttr());

        // TODO : Process ms->nMDR, ms->nMDR_ v.s. nPsdIMPR + nIgnoreTargetDEF
        // TODO : Process ms->nMGuardUp_

        final int skillDamage = si.getValue(SkillStat.damage, slv);
        if (skillDamage > 0) {
            damage = skillDamage / 100.0 * damage;
        }

        // Process critical damage
        cd = Math.max(cd + psdCdMin + 20, 50);
        // damage = get_rand(rand, cd / 100.0, 50.0) * damage + damage;
        // Ignore - weakness skills (9000 - 9002), cd->aMobCategoryDamage, cd->boss.nDamage
        // TODO : Process tKeyDown, guided bullet damage, nDojangBerserk, nWeakness, nAR01Mad, paralyze damage decrease
        // Ignore cd->aSkill
        // TODO : nBossDAMr?
        if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.Infinity)) {
            final int infinityDamR = user.getSecondaryStat().getOption(CharacterTemporaryStat.Infinity).nOption - 1;
            damage = damage + infinityDamR * damage / 100.0;
        }
        final int damR = psdDipR + user.getSecondaryStat().getOption(CharacterTemporaryStat.DamR).nOption;
        damage = damage + damR * damage / 100.0;

        return (int) Math.clamp(damage, 1.0, GameConstants.DAMAGE_MAX);
    }


    // COMMON ----------------------------------------------------------------------------------------------------------

    public static double calcDamageByWT(WeaponType wt, BasicStat bs, int pad, int mad) {
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

    public static double adjustRandomDamage(double damage, int rand, double k, int mastery) {
        // `anonymous namespace'::adjust_ramdom_damage
        final double totalMastery = Math.min(mastery / 100.0 + k, GameConstants.MASTERY_MAX);
        return getRand(rand, damage, totalMastery * damage + 0.5);
    }

    private static double getRand(int rand, double f0, double f1) {
        // get_rand
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
            final int maxStacks = user.getSkillStatValue(comboSkillId, SkillStat.x);
            final int stacks = Math.max(comboAbilityBuff / 10, maxStacks);
            pad += stacks * user.getSkillStatValue(comboSkillId, SkillStat.y);
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
        final int ecPad = user.getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.pad);
        return Math.max(incPad, ecPad);
    }

    private static int getIncEpad(User user) {
        // SecondaryStat::GetIncEPAD
        final int incEpad = user.getSecondaryStat().getOption(CharacterTemporaryStat.EPAD).nOption;
        if (user.getSecondaryStat().getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            // this->aTemporaryStat[0].p->IsActivated (unnecessary, since Energy Charge does not have any epad stat)
            return incEpad;
        }
        final int ecEpad = user.getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.epad);
        return Math.max(incEpad, ecEpad);
    }

    private static int getIncAcc(User user) {
        // SecondaryStat::GetIncACC
        final int incAcc = user.getSecondaryStat().getOption(CharacterTemporaryStat.ACC).nOption;
        if (user.getSecondaryStat().getOption(CharacterTemporaryStat.EnergyCharged).nOption < 10000) {
            return incAcc;
        }
        final int ecAcc = user.getSkillStatValue(SkillConstants.getEnergyChargeSkill(user.getJob()), SkillStat.acc);
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

    public static int getWeaponMastery(User user, WeaponType weaponType) {
        // get_weapon_mastery
        switch (weaponType) {
            case OH_SWORD, TH_SWORD -> {
                int mastery = getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_HERO);
                if (mastery > 0) {
                    return mastery;
                }
                mastery = getMasteryFromSkill(user, Warrior.WEAPON_MASTERY_PALADIN);
                if (mastery > 0) {
                    if (user.getSecondaryStat().hasOption(CharacterTemporaryStat.WeaponCharge)) {
                        final int masteryFromCharge = getMasteryFromSkill(user, Warrior.ADVANCED_CHARGE);
                        if (masteryFromCharge > 0) {
                            return masteryFromCharge;
                        }
                    }
                    return mastery;
                }
                return getMasteryFromSkill(user, DawnWarrior.SWORD_MASTERY);
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
            final int mastery = user.getSkillStatValue(skillId, SkillStat.mastery);
            if (mastery > 0) {
                return mastery;
            }
        }
        return 0;
    }

    public static double getMasteryConstByWT(WeaponType wt) {
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


    // ELEMENT METHODS -------------------------------------------------------------------------------------------------

    public static double getDamageAdjustedByElemAttr(User user, double damage, SkillInfo si, int slv, Map<ElementAttribute, DamagedAttribute> damagedElemAttr) {
        // get_damage_adjusted_by_elemAttr
        final double adjust = 1.0 - user.getSecondaryStat().getOption(CharacterTemporaryStat.ElementalReset).nOption / 100.0;
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
        return getDamageAdjustedByElemAttr(damage, damagedElemAttr.getOrDefault(si.getElemAttr(), DamagedAttribute.NONE), adjust, boost);
    }

    public static double getDamageAdjustedByChargedElemAttr(User user, double damage, Map<ElementAttribute, DamagedAttribute> damagedElemAttr) {
        // get_damage_adjusted_by_charged_elemAttr
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.WeaponCharge)) {
            return damage;
        }
        final int skillId = user.getSecondaryStat().getOption(CharacterTemporaryStat.WeaponCharge).rOption;
        final ElementAttribute elemAttr = SkillConstants.getElementByChargedSkillId(skillId);
        if (elemAttr == ElementAttribute.PHYSICAL) {
            return damage;
        }
        final double adjust = user.getSkillStatValue(skillId, SkillStat.z) / 100.0;
        final double amp = user.getSkillStatValue(skillId, SkillStat.damage) / 100.0;
        return getDamageAdjustedByElemAttr(amp * damage, damagedElemAttr.getOrDefault(elemAttr, DamagedAttribute.NONE), adjust, 0.0);
    }

    public static double getDamageAdjustedByAssistChargedElemAttr(User user, double damage, Map<ElementAttribute, DamagedAttribute> damagedElemAttr) {
        // get_damage_adjusted_by_assist_charged_elemAttr
        if (!user.getSecondaryStat().hasOption(CharacterTemporaryStat.AssistCharge)) {
            return damage;
        }
        final int skillId = user.getSecondaryStat().getOption(CharacterTemporaryStat.AssistCharge).rOption;
        final ElementAttribute elemAttr = SkillConstants.getElementByChargedSkillId(skillId);
        if (elemAttr == ElementAttribute.PHYSICAL) {
            return damage;
        }
        final double adjust = user.getSkillStatValue(skillId, SkillStat.z) / 100.0;
        final double amp = user.getSkillStatValue(skillId, SkillStat.damage) / 100.0;
        return getDamageAdjustedByElemAttr((amp - 1.0) * damage * 0.5, damagedElemAttr.getOrDefault(elemAttr, DamagedAttribute.NONE), adjust, 0.0);
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
}
