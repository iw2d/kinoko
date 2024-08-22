package kinoko.world.user.stat;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.mob.DamagedAttribute;
import kinoko.provider.skill.ElementAttribute;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.header.OutHeader;
import kinoko.util.Locked;
import kinoko.util.Rand32;
import kinoko.world.GameConstants;
import kinoko.world.field.mob.Mob;
import kinoko.world.field.mob.MobStat;
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
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.Mechanic;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.*;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public final class CalcDamage {
    private static final Logger log = LogManager.getLogger(CalcDamage.class);
    private final Rand32 rndGenForCharacter = new Rand32();
    private boolean nextAttackCritical;

    public void setSeed(int s1, int s2, int s3) {
        rndGenForCharacter.setSeed(
                s1 | 0x100000,
                s2 | 0x1000,
                s3 | 0x10
        );
    }

    public boolean isNextAttackCritical() {
        return nextAttackCritical;
    }

    public void setNextAttackCritical(boolean nextAttackCritical) {
        this.nextAttackCritical = nextAttackCritical;
    }

    public long[] getNextAttackRandom() {
        final long[] random = new long[7];
        for (int i = 0; i < 7; i++) {
            random[i] = Integer.toUnsignedLong(rndGenForCharacter.random());
        }
        return random;
    }


    // PHYSICAL DAMAGE -------------------------------------------------------------------------------------------------

    public static void calcPDamage(Locked<User> locked, Locked<Mob> lockedMob, Attack attack, AttackInfo ai) {
        // CalcDamage::PDamage
        final User user = locked.get();
        final SecondaryStat ss = user.getSecondaryStat();
        final Mob mob = lockedMob.get();
        final MobStat ms = mob.getMobStat();
        final int skillId = attack.skillId;
        final int noviceSkill = Math.max(skillId - (JobConstants.getNoviceSkillRootFromJob(user.getJob()) * 1000), 0);
        final int criticalRate = getCriticalRate(user, attack);
        final int damagePerMob = skillId == Thief.MESO_EXPLOSION ? ai.attackCount : attack.getDamagePerMob();
        // Process attack info
        int counter = 0;
        for (int i = 0; i < damagePerMob; i++) {
            if (noviceSkill == 1009 || noviceSkill == 1020) {
                if (noviceSkill == 1009 && mob.isBoss()) {
                    assertDamage((int) (mob.getMaxHp() * 0.3), ai.damage[i]);
                } else {
                    assertDamage(mob.getMaxHp(), ai.damage[i]);
                }
                continue;
            }
            if (!JobConstants.isAdminJob(user.getJob()) && ms.hasOption(MobTemporaryStat.Disable)) {
                assertDamage(0, ai.damage[i]);
                continue;
            }
            if (!ms.hasOption(MobTemporaryStat.Freeze) || ms.getOption(MobTemporaryStat.Freeze).rOption != Aran.COMBO_TEMPEST) {
                if (ms.hasOption(MobTemporaryStat.PImmune) && (ai.random[counter++ % 7] % 100) > ss.getOption(CharacterTemporaryStat.RespectPImmune).nOption) {
                    assertDamage(1, ai.damage[i]);
                    continue;
                }
                if (skillId == Aran.COMBO_TEMPEST) {
                    if (!mob.isBoss()) {
                        continue;
                    }
                }
                if (skillId == Bowman.SNIPE) { // 33121003 does not exist
                    if (!mob.isBoss()) {
                        final int fixDamage = (int) (999_999.0 - getRand(ai.random[counter++ % 7], 10000.0, 0.0));
                        assertDamage(fixDamage, ai.damage[i]);
                    } else {
                        assertDamage(500_000, ai.damage[i]);
                    }
                    continue;
                }
                if (skillId == Thief.NINJA_STORM || skillId == Aran.ROLLING_SPIN) {
                    final double rand = getRand(ai.random[counter % 7], 100.0, 0.0);
                    final int prop = user.getSkillStatValue(skillId, SkillStat.prop);
                    if (prop <= rand) {
                        assertDamage(0, ai.damage[i]);
                        continue;
                    }
                }
                counter++;
                if ((ms.hasOption(MobTemporaryStat.Freeze) && skillId == Bowman.STRAFE_MM && attack.getHeaderType() == OutHeader.UserShootAttack && i == 0 && !mob.isBoss()) ||
                        (skillId == Thief.OWL_SPIRIT && i == 0 && !mob.isBoss())) {
                    final double rand = getRand(ai.random[counter++ % 7], 0.0, 100.0);
                    final int prop = user.getSkillStatValue(skillId, SkillStat.prop);
                    if (rand < prop) {
                        // assertDamage(mob.getMaxHp(), ai.damage[i]);
                        continue;
                    }
                }
                if (attack.getHeaderType() != OutHeader.UserBodyAttack && skillId != 0 && ss.hasOption(CharacterTemporaryStat.Seal)) {
                    continue;
                }
                final int mobEva = Math.clamp(mob.getTemplate().getEva() + mob.getMobStat().getOption(MobTemporaryStat.EVA).nOption, 0, 9999);
                final int accR = calcAccR(user, mobEva, mob.getLevel());
                if (!JobConstants.isAdminJob(user.getJob())) {
                    final double rand = getRand(ai.random[counter++ % 7], 100.0, 0.0);
                    if (accR < rand) {
                        assertDamage(0, ai.damage[i]);
                        continue;
                    }
                }
                if (skillId != 0) {
                    if (skillId == Warrior.HEAVENS_HAMMER) {
                        continue;
                    }
                    final int fixDamage = user.getSkillStatValue(skillId, SkillStat.fixdamage);
                    if (noviceSkill == 1066 || noviceSkill == 1067 || fixDamage != 0) {
                        assertDamage(fixDamage, ai.damage[i]);
                        continue;
                    }
                }
                if (ss.hasOption(CharacterTemporaryStat.Darkness)) {
                    final double rand = getRand(ai.random[counter++ % 7], 100.0, 0.0);
                    if (rand > 20.0) {
                        assertDamage(0, ai.damage[i]);
                        counter += (int) getRand(ai.random[counter % 7], 0.0, 5.0) + 1;
                        continue;
                    }
                }
                if (ms.hasOption(MobTemporaryStat.Freeze) && ms.getOption(MobTemporaryStat.Freeze).rOption == Aran.COMBO_TEMPEST) {
                    assertDamage(mob.getMaxHp(), mob.getMaxHp());
                    continue;
                }
                if (skillId == Warrior.RUSH_HERO ||
                        skillId == Warrior.RUSH_PALADIN ||
                        skillId == Warrior.RUSH_DRK ||
                        skillId == Thief.TORNADO_SPIN_ATTACK ||
                        skillId == Warrior.BLAST ||
                        skillId == Thief.FLYING_ASSAULTER ||
                        skillId == Thief.SLASH_STORM ||
                        skillId == Thief.BLOODY_STORM) {
                    counter++;
                }
                // Adjust Random Damage
                counter++;
                // Check Critical
                if (attack.skillId != Thief.ASSASSINATE || attack.getAction() != ActionType.ASSASSINATIONS.getValue()) {
                    if (user.getCalcDamage().isNextAttackCritical() || (criticalRate > 0 &&
                            criticalRate > getRand(ai.random[counter++ % 7], 0.0, 100.0))) {
                        ai.critical[i] = 1;
                        // Adjust Critical Damage
                        counter++;
                    }
                }
                if (attack.isShadowPartner() && ss.hasOption(CharacterTemporaryStat.ShadowPartner) &&
                        ss.getOption(CharacterTemporaryStat.ShadowPartner).rOption != Thief.MIRROR_IMAGE) {
                    if (skillId != Thief.TAUNT_NL && skillId != Thief.TAUNT_SHAD && i >= damagePerMob / 2) {
                        ai.critical[i] = ai.critical[i - damagePerMob / 2];
                    }
                }
                if (mob.isBoss()) {
                    counter++; // cd->boss.nProb
                }
                if (!ms.hasOption(MobTemporaryStat.HardSkin) || ai.critical[i] != 0) {
                    if (skillId == Thief.SHADOW_MESO) {
                        counter++; // nMoneyCon
                        counter++; // nProp
                    }
                    // Ignore cd->aSkill prop
                } else {
                    assertDamage(0, ai.damage[i]);
                }
            }
        }
        user.getCalcDamage().setNextAttackCritical(false);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public static void assertDamage(int expected, int actual) {
        if (expected != actual) {
            log.warn("Mismatching damage : expected = {}, actual = {}", expected, actual);
        }
    }

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

    public static int calcAccR(User user, int mobEva, int mobLevel) {
        // `anonymous namespace'::calc_accr
        final int ar = user.getPassiveSkillData().getAllAr();
        final int a = (int) Math.sqrt(getAcc(user));
        final int b = (int) Math.sqrt(mobEva);
        int result = a - b + 100 + ar * (a - b + 100) / 100;
        if (result >= 100) {
            result = 100;
        }
        if (mobLevel > user.getLevel()) {
            final int c = 5 * (mobLevel - user.getLevel());
            result = Math.max(result - c, 0);
        }
        return result;
    }

    private static int getCriticalRate(User user, Attack attack) {
        int criticalRate = user.getSkillStatValue(getCriticalSkillId(user, attack), SkillStat.prop) + 5;
        final int swallowCritical = user.getSecondaryStat().getOption(CharacterTemporaryStat.SwallowCritical).nOption;
        final int sharpEyes = user.getSecondaryStat().getOption(CharacterTemporaryStat.SharpEyes).nOption;
        final int thornsEffect = user.getSecondaryStat().getOption(CharacterTemporaryStat.ThornsEffect).nOption;
        criticalRate = criticalRate + Math.max(sharpEyes >> 8, thornsEffect >> 8) + swallowCritical;
        // ignore cd->critical.nProb
        final int comboCount = user.getSecondaryStat().getOption(CharacterTemporaryStat.ComboAbilityBuff).nOption;
        if (comboCount > 0) {
            final int comboCriticalSkillId = user.getJob() != 2000 ? Aran.COMBO_CRITICAL : 20000018;
            final int stacks = Math.min(comboCount / 10, user.getSkillStatValue(comboCriticalSkillId, SkillStat.x));
            criticalRate += stacks * user.getSkillStatValue(comboCriticalSkillId, SkillStat.y);
        }
        criticalRate += user.getSecondaryStat().getItemCriR();
        criticalRate += user.getPassiveSkillData().getAllCr();
        if (SkillConstants.WILD_HUNTER_JAGUARS.contains(user.getSecondaryStat().getRidingVehicle())) {
            criticalRate += user.getSkillStatValue(WildHunter.JAGUAR_RIDER, SkillStat.w);
        }
        if (JobConstants.isEvanJob(user.getJob())) {
            criticalRate += user.getSkillStatValue(Evan.CRITICAL_MAGIC, SkillStat.prop);
        }
        return criticalRate;
    }

    private static int getCriticalSkillId(User user, Attack attack) {
        // get_critical_skill_level
        if (attack.getAction() == ActionType.ASSASSINATIONS.getValue()) {
            return Thief.ASSASSINATE;
        }
        if (JobConstants.isResistanceJob(user.getJob())) {
            return Citizen.DEADLY_CRITS;
        }
        final Item weapon = user.getInventoryManager().getEquipped().getItem(BodyPart.WEAPON.getValue());
        if (weapon == null) {
            return 0;
        }
        switch (WeaponType.getByItemId(weapon.getItemId())) {
            case BOW, CROSSBOW -> {
                if (attack.getHeaderType() != OutHeader.UserShootAttack) {
                    return 0;
                }
                if (JobConstants.isCygnusJob(user.getJob())) {
                    return WindArcher.CRITICAL_SHOT;
                } else {
                    return Bowman.CRITICAL_SHOT;
                }
            }
            case THROWINGGLOVE -> {
                if (attack.getHeaderType() != OutHeader.UserShootAttack) {
                    return 0;
                }
                if (JobConstants.isCygnusJob(user.getJob())) {
                    return NightWalker.CRITICAL_THROW;
                } else {
                    return Thief.CRITICAL_THROW;
                }
            }
            case KNUCKLE -> {
                return Pirate.CRITICAL_PUNCH;
            }
        }
        return 0;
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

    public static double getRand(long rand, double f0, double f1) {
        // get_rand
        assert rand >= 0;
        if (f0 == f1) {
            return f0;
        } else if (f0 < f1) {
            return f0 + (double) (rand % 10_000_000) * (f1 - f0) / 9_999_999.0;
        } else {
            return f1 + (double) (rand % 10_000_000) * (f0 - f1) / 9_999_999.0;
        }
    }


    // SECONDARY STAT METHODS ------------------------------------------------------------------------------------------

    public static int getPad(User user) {
        // SecondaryStat::GetPAD
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
            final int comboSkillId = SkillConstants.getComboAbilitySkill(user.getJob()); // tutorial skill?
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
        final int dragonFury = Evan.isDragonFury(user) ? user.getSkillStatValue(Evan.DRAGON_FURY, SkillStat.damage) : 0;
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
        final ElementAttribute elemAttr = SkillConstants.getElementByWeaponChargeSkill(skillId);
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
        final ElementAttribute elemAttr = SkillConstants.getElementByWeaponChargeSkill(skillId);
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
