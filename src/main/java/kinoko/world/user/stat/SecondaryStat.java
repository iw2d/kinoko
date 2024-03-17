package kinoko.world.user.stat;

import kinoko.provider.EtcProvider;
import kinoko.provider.ItemProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemOptionLevelData;
import kinoko.provider.item.SetItemInfo;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.world.GameConstants;
import kinoko.world.item.BodyPart;
import kinoko.world.item.EquipData;
import kinoko.world.item.Item;
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
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class SecondaryStat {
    private final Map<CharacterTemporaryStat, TemporaryStatOption> temporaryStats = new EnumMap<>(CharacterTemporaryStat.class);
    private int pad;
    private int pdd;
    private int mad;
    private int mdd;
    private int acc;
    private int eva;
    private int itemPadR;
    private int itemPddR;
    private int itemMadR;
    private int itemMddR;
    private int itemAccR;
    private int itemEvaR;
    private int craft;
    private int speed;
    private int jump;

    public Map<CharacterTemporaryStat, TemporaryStatOption> getTemporaryStats() {
        return temporaryStats;
    }

    public int getPad() {
        return pad;
    }

    public int getPdd() {
        return pdd;
    }

    public int getMad() {
        return mad;
    }

    public int getMdd() {
        return mdd;
    }

    public int getAcc() {
        return acc;
    }

    public int getEva() {
        return eva;
    }

    public int getItemPadR() {
        return itemPadR;
    }

    public int getItemPddR() {
        return itemPddR;
    }

    public int getItemMadR() {
        return itemMadR;
    }

    public int getItemMddR() {
        return itemMddR;
    }

    public int getItemAccR() {
        return itemAccR;
    }

    public int getItemEvaR() {
        return itemEvaR;
    }

    public int getCraft() {
        return craft;
    }

    public int getSpeed() {
        return speed;
    }

    public int getJump() {
        return jump;
    }


    // TEMPORARY STAT METHODS ------------------------------------------------------------------------------------------

    public void clear() {
        temporaryStats.clear();
    }

    public TemporaryStatOption getOption(CharacterTemporaryStat cts) {
        return temporaryStats.getOrDefault(cts, TemporaryStatOption.EMPTY);
    }

    public int getRidingVehicle() {
        return getOption(CharacterTemporaryStat.RideVehicle).rOption;
    }

    public DiceInfo getDiceInfo() {
        return getOption(CharacterTemporaryStat.Dice).diceInfo;
    }

    public Set<CharacterTemporaryStat> resetTemporaryStat(int skillId) {
        final Set<CharacterTemporaryStat> resetStats = new HashSet<>();
        final var iter = getTemporaryStats().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<CharacterTemporaryStat, TemporaryStatOption> entry = iter.next();
            final CharacterTemporaryStat cts = entry.getKey();
            final TemporaryStatOption option = entry.getValue();
            // Check skill reason and remove cts
            if (option.rOption != skillId) {
                continue;
            }
            iter.remove();
            resetStats.add(cts);
        }
        return resetStats;
    }

    public Set<CharacterTemporaryStat> expireTemporaryStat(Instant now) {
        final Set<CharacterTemporaryStat> resetStats = new HashSet<>();
        final var iter = getTemporaryStats().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<CharacterTemporaryStat, TemporaryStatOption> entry = iter.next();
            final CharacterTemporaryStat cts = entry.getKey();
            final TemporaryStatOption option = entry.getValue();
            // Check temporary stat expire time and remove cts
            if (now.isBefore(option.getExpireTime())) {
                continue;
            }
            iter.remove();
            resetStats.add(cts);
        }
        return resetStats;
    }


    // VALIDATE STAT METHODS -------------------------------------------------------------------------------------------

    public void setFrom(BasicStat bs, ForcedStat fs, SkillManager sm, Map<Integer, Item> realEquip) {
        this.pad = 0;
        this.pdd = 0;
        this.mad = 0;
        this.mdd = 0;
        this.acc = 0;
        this.eva = 0;
        this.itemPadR = 0;
        this.itemPddR = 0;
        this.itemMadR = 0;
        this.itemMddR = 0;
        this.itemAccR = 0;
        this.itemEvaR = 0;
        this.craft = bs.getDex() + bs.getInt() + bs.getLuk();
        this.speed = 100;
        this.jump = 100;

        // Set items
        for (SetItemInfo setItemInfo : EtcProvider.getSetItemInfos()) {
            final Set<Integer> equippedItems = realEquip.values().stream().map(Item::getItemId).collect(Collectors.toSet());
            equippedItems.retainAll(setItemInfo.getItems());
            for (int itemCount = 0; itemCount <= equippedItems.size(); itemCount++) {
                final Map<ItemInfoType, Integer> effect = setItemInfo.getEffect().get(itemCount);
                if (effect == null) {
                    continue;
                }
                for (var entry : effect.entrySet()) {
                    switch (entry.getKey()) {
                        case incPAD -> this.pad += entry.getValue();
                        case incPDD -> this.pdd += entry.getValue();
                        case incMAD -> this.mad += entry.getValue();
                        case incMDD -> this.mdd += entry.getValue();
                        case incACC -> this.acc += entry.getValue();
                        case incEVA -> this.eva += entry.getValue();
                        case incSpeed -> this.speed += entry.getValue();
                        case incJump -> this.jump += entry.getValue();
                    }
                }
            }
        }

        // Bare hands for pirates
        final Item weapon = realEquip.get(BodyPart.WEAPON.getValue());
        if (weapon == null && JobConstants.getJobCategory(bs.getJob()) == 5) {
            if (bs.getLevel() > 30) {
                this.pad = 31;
            } else {
                this.pad = (int) (bs.getLevel() * 0.7 + 10.0);
            }
        }

        // Equip stats
        final SecondaryStatRateOption option = new SecondaryStatRateOption();
        for (var item : realEquip.values()) {
            // Resolve item and item info
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
            if (itemInfoResult.isEmpty()) {
                continue;
            }
            final ItemInfo ii = itemInfoResult.get();

            // Apply stats
            final EquipData ed = item.getEquipData();
            this.pad += ed.getIncPad();
            this.pdd += ed.getIncPdd();
            this.mad += ed.getIncMad();
            this.mdd += ed.getIncMdd();
            this.acc += ed.getIncAcc();
            this.eva += ed.getIncEva();
            this.craft += ed.getIncCraft();
            this.speed += ed.getIncSpeed();
            this.jump += ed.getIncJump();

            final int optionLevel = (ii.getInfo(ItemInfoType.reqLevel) - 1) / 10;
            this.applyItemOption(ed.getOption1(), optionLevel);
            this.applyItemOption(ed.getOption2(), optionLevel);
            this.applyItemOption(ed.getOption3(), optionLevel);
            option.applyItemOptionR(ed.getOption1(), optionLevel);
            option.applyItemOptionR(ed.getOption2(), optionLevel);
            option.applyItemOptionR(ed.getOption3(), optionLevel);
        }

        // Passive skills
        for (int skillId : SkillConstants.SECONDARY_STAT_SKILLS) {
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                continue;
            }
            final SkillInfo si = skillInfoResult.get();
            final int slv = sm.getSkillLevel(skillId);
            if (slv == 0) {
                continue;
            }
            switch (skillId) {
                case Thief.NIMBLE_BODY, NightWalker.NIMBLE_BODY, Pirate.BULLET_TIME, ThunderBreaker.QUICK_MOTION -> {
                    this.acc += si.getValue(SkillStat.x, slv);
                    this.eva += si.getValue(SkillStat.y, slv);
                }
                case Evan.DRAGON_SOUL -> {
                    this.mad += si.getValue(SkillStat.mad, slv);
                }
                case Beginner.BLESSING_OF_THE_FAIRY, Noblesse.BLESSING_OF_THE_FAIRY, Citizen.BLESSING_OF_THE_FAIRY,
                        Aran.BLESSING_OF_THE_FAIRY, Evan.BLESSING_OF_THE_FAIRY -> {
                    this.pad += si.getValue(SkillStat.x, slv);
                    this.mad += si.getValue(SkillStat.y, slv);
                    this.acc += si.getValue(SkillStat.z, slv);
                    this.eva += si.getValue(SkillStat.z, slv);
                }
            }
        }

        // Jaguar Rider
        if (SkillConstants.WILD_HUNTER_JAGUARS.contains(getRidingVehicle())) {
            final int slv = sm.getSkillLevel(WildHunter.JAGUAR_RIDER);
            if (slv > 0) {
                SkillProvider.getSkillInfoById(WildHunter.JAGUAR_RIDER).ifPresent((si) -> {
                    this.eva += si.getValue(SkillStat.y, slv);
                });
            }
        }

        // get_weapon_mastery
        final WeaponType wt = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        switch (wt) {
            case OH_SWORD, TH_SWORD -> {
                getStatFromSkill(sm, Warrior.WEAPON_MASTERY_HERO, Warrior.WEAPON_MASTERY_PALADIN, DawnWarrior.SWORD_MASTERY);
            }
            case OH_AXE, TH_AXE -> {
                getStatFromSkill(sm, Warrior.WEAPON_MASTERY_HERO);
            }
            case OH_MACE, TH_MACE -> {
                getStatFromSkill(sm, Warrior.WEAPON_MASTERY_PALADIN);
            }
            case DAGGER -> {
                final Item shield = realEquip.get(BodyPart.SHIELD.getValue());
                if (shield != null && WeaponType.getByItemId(shield.getItemId()) == WeaponType.SUB_DAGGER) {
                    getStatFromSkill(sm, Thief.KATARA_MASTERY);
                } else {
                    getStatFromSkill(sm, Thief.DAGGER_MASTERY);
                }
            }
            case SPEAR -> {
                getStatFromSkill(sm, Warrior.WEAPON_MASTERY_DRK);
            }
            case POLEARM -> {
                if (JobConstants.isAranJob(bs.getJob())) {
                    getStatFromSkill(sm, Aran.POLEARM_MASTERY);
                    getStatFromSkill(sm, Aran.HIGH_MASTERY);
                } else {
                    getStatFromSkill(sm, Warrior.WEAPON_BOOSTER_DRK);
                }
            }
            case BOW -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(sm, WindArcher.BOW_MASTERY);
                    getStatFromSkill(sm, WindArcher.BOW_EXPERT);
                } else {
                    getStatFromSkill(sm, Bowman.BOW_MASTERY);
                    getStatFromSkill(sm, Bowman.BOW_EXPERT);
                }
            }
            case CROSSBOW -> {
                if (JobConstants.isWildHunterJob(bs.getJob())) {
                    getStatFromSkill(sm, WildHunter.CROSSBOW_MASTERY);
                    getStatFromSkill(sm, WildHunter.CROSSBOW_EXPERT);
                } else {
                    getStatFromSkill(sm, Bowman.CROSSBOW_MASTERY);
                    getStatFromSkill(sm, Bowman.MARKSMAN_BOOST);
                }
            }
            case THROWINGGLOVE -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(sm, NightWalker.CLAW_MASTERY);
                } else {
                    getStatFromSkill(sm, Thief.CLAW_MASTERY);
                }
            }
            case KNUCKLE -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(sm, ThunderBreaker.KNUCKLE_MASTERY);
                } else {
                    getStatFromSkill(sm, Pirate.KNUCKLE_MASTERY);
                }
            }
            case GUN -> {
                if (JobConstants.isMechanicJob(bs.getJob())) {
                    getStatFromSkill(sm, Mechanic.EXTREME_MECH, Mechanic.MECHANIC_MASTERY);
                } else {
                    getStatFromSkill(sm, Pirate.GUN_MASTERY);
                }
            }
        }

        // get_magic_mastery
        if (JobConstants.isEvanJob(bs.getJob())) {
            getStatFromSkill(sm, Evan.SPELL_MASTERY);
            getStatFromSkill(sm, Evan.MAGIC_MASTERY);
        } else if (JobConstants.isBattleMageJob(bs.getJob())) {
            getStatFromSkill(sm, BattleMage.STAFF_MASTERY);
        } else if (JobConstants.isBlazeWizardJob(bs.getJob())) {
            getStatFromSkill(sm, BlazeWizard.SPELL_MASTERY);
        } else if (JobConstants.isBishopJob(bs.getJob())) {
            getStatFromSkill(sm, Magician.SPELL_MASTERY_BISH);
        } else if (JobConstants.isIceLightningJob(bs.getJob())) {
            getStatFromSkill(sm, Magician.SPELL_MASTERY_IL);
        } else if (JobConstants.isFirePoisonJob(bs.getJob())) {
            getStatFromSkill(sm, Magician.SPELL_MASTERY_FP);
        }

        // get_increase_speed
        if (JobConstants.isBowmasterJob(bs.getJob())) {
            getStatFromSkill(sm, Bowman.THRUST_BM);
        } else if (JobConstants.isMarksmanJob(bs.getJob())) {
            getStatFromSkill(sm, Bowman.THRUST_MM);
        } else if (JobConstants.isWindArcherJob(bs.getJob())) {
            getStatFromSkill(sm, WindArcher.THRUST);
        }
        if (getOption(CharacterTemporaryStat.YellowAura).nOption > 0) {
            final Optional<SkillInfo> yellowAuraResult = SkillProvider.getSkillInfoById(getOption(CharacterTemporaryStat.YellowAura).rOption);
            yellowAuraResult.ifPresent((si) -> this.speed += si.getValue(SkillStat.x, getOption(CharacterTemporaryStat.YellowAura).nOption));
            if (getOption(CharacterTemporaryStat.SuperBody).nOption > 0) {
                final Optional<SkillInfo> bodyBoostResult = SkillProvider.getSkillInfoById(BattleMage.BODY_BOOST_YELLOW_AURA);
                bodyBoostResult.ifPresent((si) -> this.speed += si.getValue(SkillStat.x, getOption(CharacterTemporaryStat.SuperBody).nOption));
            }
        }

        // Forced stat
        if (fs.getPad() > 0) {
            this.pad = fs.getPad();
        }
        if (fs.getPdd() > 0) {
            this.pdd = fs.getPdd();
        }
        if (fs.getMad() > 0) {
            this.mad = fs.getMad();
        }
        if (fs.getMdd() > 0) {
            this.mdd = fs.getMdd();
        }
        if (fs.getAcc() > 0) {
            this.acc = fs.getAcc();
        }
        if (fs.getEva() > 0) {
            this.eva = fs.getEva();
        }
        if (fs.getSpeed() > 0) {
            this.speed = fs.getSpeed();
        }
        if (fs.getJump() > 0) {
            this.jump = fs.getJump();
        }

        this.itemPadR += option.padR;
        this.itemPddR += option.pddR;
        this.itemMadR += option.madR;
        this.itemMddR += option.mddR;
        this.itemAccR += option.accR;
        this.itemEvaR += option.evaR;

        // Clamp values
        this.pad = Math.clamp(this.pad, 0, GameConstants.PAD_MAX);
        this.pdd = Math.clamp(this.pdd, 0, GameConstants.PDD_MAX);
        this.mad = Math.clamp(this.mad, 0, GameConstants.MAD_MAX);
        this.mdd = Math.clamp(this.mdd, 0, GameConstants.MDD_MAX);
        this.acc = Math.clamp(this.acc, 0, GameConstants.ACC_MAX);
        this.eva = Math.clamp(this.eva, 0, GameConstants.EVA_MAX);
        this.speed = Math.clamp(this.speed, GameConstants.SPEED_MIN, fs.getSpeedMax() != 0 ? fs.getSpeedMax() : GameConstants.SPEED_MAX);
        this.jump = Math.clamp(this.jump, GameConstants.JUMP_MIN, GameConstants.JUMP_MAX);
    }

    private void applyItemOption(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
        if (itemOptionResult.isEmpty()) {
            return;
        }
        for (var entry : itemOptionResult.get().getStats().entrySet()) {
            switch (entry.getKey()) {
                case incPAD -> this.pad += entry.getValue();
                case incPDD -> this.pdd += entry.getValue();
                case incMAD -> this.mad += entry.getValue();
                case incMDD -> this.mdd += entry.getValue();
                case incACC -> this.acc += entry.getValue();
                case incEVA -> this.eva += entry.getValue();
                case incSpeed -> this.speed += entry.getValue();
                case incJump -> this.jump += entry.getValue();
            }
        }
    }

    private void getStatFromSkill(SkillManager sm, int... skillIds) {
        for (int skillId : skillIds) {
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                continue;
            }
            final SkillInfo si = skillInfoResult.get();
            final int slv = sm.getSkillLevel(skillId);
            if (slv == 0) {
                continue;
            }
            switch (skillId) {
                case Warrior.WEAPON_MASTERY_HERO, Warrior.WEAPON_MASTERY_PALADIN, DawnWarrior.SWORD_MASTERY,
                        Thief.KATARA_MASTERY, Thief.DAGGER_MASTERY, Warrior.WEAPON_MASTERY_DRK, Aran.POLEARM_MASTERY,
                        WindArcher.BOW_MASTERY, Bowman.BOW_MASTERY, WildHunter.CROSSBOW_MASTERY,
                        Bowman.CROSSBOW_MASTERY, NightWalker.CLAW_MASTERY, Thief.CLAW_MASTERY,
                        ThunderBreaker.KNUCKLE_MASTERY, Pirate.KNUCKLE_MASTERY, Mechanic.EXTREME_MECH,
                        Mechanic.MECHANIC_MASTERY, Pirate.GUN_MASTERY -> {
                    this.acc += si.getValue(SkillStat.x, slv);
                }
                case Aran.HIGH_MASTERY, WindArcher.BOW_EXPERT, Bowman.BOW_EXPERT, WildHunter.CROSSBOW_EXPERT,
                        Bowman.MARKSMAN_BOOST -> {
                    this.pad += si.getValue(SkillStat.x, slv);
                }
                case Evan.SPELL_MASTERY, Evan.MAGIC_MASTERY, BattleMage.STAFF_MASTERY, BlazeWizard.SPELL_MASTERY,
                        Magician.SPELL_MASTERY_BISH, Magician.SPELL_MASTERY_IL, Magician.SPELL_MASTERY_FP -> {
                    this.mad += si.getValue(SkillStat.x, slv);
                }
                case Bowman.THRUST_BM, Bowman.THRUST_MM, WindArcher.THRUST -> {
                    this.speed += si.getValue(SkillStat.speed, slv);
                }
            }
            break;
        }
    }


    // ENCODE METHODS --------------------------------------------------------------------------------------------------

    public static void encodeForLocal(OutPacket outPacket, Map<CharacterTemporaryStat, TemporaryStatOption> stats) {
        final BitFlag<CharacterTemporaryStat> statFlag = BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.LOCAL_ENCODE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }

        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseAtt, TemporaryStatOption.EMPTY).nOption);
        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseState, TemporaryStatOption.EMPTY).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.SWALLOW_BUFF_STAT) {
            if (statFlag.hasFlag(cts)) {
                outPacket.encodeByte(stats.get(cts).tOption); // tSwallowBuffTime
                break;
            }
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.Dice)) {
            stats.get(CharacterTemporaryStat.Dice).encode(outPacket); // aDiceInfo
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(stats.get(CharacterTemporaryStat.BlessingArmor).nOption); // nBlessingArmorIncPAD
        }

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }
    }

    public static void encodeForRemote(OutPacket outPacket, Map<CharacterTemporaryStat, TemporaryStatOption> stats) {
        // SecondaryStat::DecodeForRemote
        final BitFlag<CharacterTemporaryStat> statFlag = BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.REMOTE_ENCODE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                switch (cts) {
                    case Speed, ComboCounter, Cyclone -> {
                        outPacket.encodeByte(stats.get(cts).nOption);
                    }
                    case Morph, Ghost -> {
                        outPacket.encodeShort(stats.get(cts).nOption);
                    }
                    case SpiritJavelin, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, MagicShield -> {
                        outPacket.encodeInt(stats.get(cts).nOption);
                    }
                    case WeaponCharge, Stun, Darkness, Seal, Weakness, ShadowPartner, Attract, BanMap, DojangShield,
                            ReverseInput, RepeatEffect, StopPortion, StopMotion, Fear, Frozen, SuddenDeath, FinalCut,
                            Mechanic, DarkAura, BlueAura, YellowAura -> {
                        outPacket.encodeInt(stats.get(cts).rOption);
                    }
                    case Poison -> {
                        outPacket.encodeShort(stats.get(cts).nOption); // overwritten with 1
                        outPacket.encodeInt(stats.get(cts).rOption);
                    }
                }
            }
        }

        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseAtt, TemporaryStatOption.EMPTY).nOption);
        outPacket.encodeByte(stats.getOrDefault(CharacterTemporaryStat.DefenseState, TemporaryStatOption.EMPTY).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                stats.get(cts).encode(outPacket);
            }
        }
    }

    public static void encodeReset(OutPacket outPacket, Set<CharacterTemporaryStat> resetStats) {
        final BitFlag<CharacterTemporaryStat> statFlag = BitFlag.from(resetStats, CharacterTemporaryStat.FLAG_SIZE);
        statFlag.encode(outPacket);
    }

    private static class SecondaryStatRateOption {
        private int padR;
        private int pddR;
        private int madR;
        private int mddR;
        private int accR;
        private int evaR;

        private void applyItemOptionR(short itemOptionId, int optionLevel) {
            final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
            if (itemOptionResult.isEmpty()) {
                return;
            }
            for (var entry : itemOptionResult.get().getStats().entrySet()) {
                switch (entry.getKey()) {
                    case incPADr -> this.padR += entry.getValue();
                    case incPDDr -> this.pddR += entry.getValue();
                    case incMADr -> this.madR += entry.getValue();
                    case incMDDr -> this.mddR += entry.getValue();
                    case incACCr -> this.accR += entry.getValue();
                    case incEVAr -> this.evaR += entry.getValue();
                }
            }
        }
    }
}
