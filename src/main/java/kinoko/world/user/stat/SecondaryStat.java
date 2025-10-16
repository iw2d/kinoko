package kinoko.world.user.stat;

import kinoko.meta.SkillId;
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

import java.util.*;
import java.util.function.BiPredicate;
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
    private int itemCriR; // computed every attack in client
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

    public int getItemCriR() {
        return itemCriR;
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

    public boolean hasOption(CharacterTemporaryStat cts) {
        return getOption(cts).nOption > 0;
    }

    public int getRidingVehicle() {
        return getOption(CharacterTemporaryStat.RideVehicle).nOption;
    }

    public DiceInfo getDiceInfo() {
        return getOption(CharacterTemporaryStat.Dice).diceInfo;
    }

    public Set<CharacterTemporaryStat> resetTemporaryStat(BiPredicate<CharacterTemporaryStat, TemporaryStatOption> predicate) {
        final Set<CharacterTemporaryStat> resetStats = new HashSet<>();
        final var iter = getTemporaryStats().entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry<CharacterTemporaryStat, TemporaryStatOption> entry = iter.next();
            final CharacterTemporaryStat cts = entry.getKey();
            final TemporaryStatOption option = entry.getValue();
            if (predicate.test(cts, option)) {
                iter.remove();
                resetStats.add(cts);
            }
        }
        return resetStats;
    }

    public void encodeForLocal(BitFlag<CharacterTemporaryStat> flag, OutPacket outPacket) {
        // SecondaryStat::DecodeForLocal
        flag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.LOCAL_ENCODE_ORDER) {
            if (flag.hasFlag(cts)) {
                getOption(cts).encode(outPacket);
            }
        }

        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseAtt_Elem).nOption);
        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseState_Stat).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.SWALLOW_BUFF_STAT) {
            if (flag.hasFlag(cts)) {
                outPacket.encodeByte(getOption(cts).tOption / 1000); // tSwallowBuffTime
                break;
            }
        }

        if (flag.hasFlag(CharacterTemporaryStat.Dice)) {
            getOption(CharacterTemporaryStat.Dice).getDiceInfo().encode(outPacket); // aDiceInfo
        }

        if (flag.hasFlag(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(getOption(CharacterTemporaryStat.BlessingArmorIncPAD).nOption); // nBlessingArmorIncPAD
        }

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (flag.hasFlag(cts)) {
                temporaryStats.getOrDefault(cts, TwoStateTemporaryStat.DEFAULT.get(cts)).encode(outPacket);
            }
        }
    }

    public void encodeForRemote(OutPacket outPacket) {
        final BitFlag<CharacterTemporaryStat> flag = BitFlag.from(getTemporaryStats().keySet(), CharacterTemporaryStat.FLAG_SIZE);
        encodeForRemote(flag, outPacket);
    }

    public void encodeForRemote(BitFlag<CharacterTemporaryStat> flag, OutPacket outPacket) {
        // SecondaryStat::DecodeForRemote
        flag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.REMOTE_ENCODE_ORDER) {
            if (flag.hasFlag(cts)) {
                switch (cts) {
                    case Speed, ComboCounter, Cyclone -> {
                        outPacket.encodeByte(getOption(cts).nOption);
                    }
                    case Morph, Ghost -> {
                        outPacket.encodeShort(getOption(cts).nOption);
                    }
                    case SpiritJavelin, RespectPImmune, RespectMImmune, DefenseAtt, DefenseState, MagicShield -> {
                        outPacket.encodeInt(getOption(cts).nOption);
                    }
                    case WeaponCharge, Stun, Darkness, Seal, Weakness, Curse, ShadowPartner, Attract, BanMap, Barrier,
                         DojangShield, ReverseInput, RepeatEffect, StopPortion, StopMotion, Fear, Frozen,
                         SuddenDeath, FinalCut, Mechanic, DarkAura, BlueAura, YellowAura -> {
                        outPacket.encodeInt(getOption(cts).rOption);
                    }
                    case Poison -> {
                        outPacket.encodeShort(getOption(cts).nOption); // overwritten with 1
                        outPacket.encodeInt(getOption(cts).rOption);
                    }
                }
            }
        }

        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseAtt_Elem).nOption);
        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseState_Stat).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (flag.hasFlag(cts)) {
                temporaryStats.getOrDefault(cts, TwoStateTemporaryStat.DEFAULT.get(cts)).encode(outPacket);
            }
        }
    }


    // VALIDATE STAT METHODS -------------------------------------------------------------------------------------------

    public void setFrom(BasicStat bs, ForcedStat fs, SecondaryStat ss, SkillManager sm, Map<Integer, Item> realEquip) {
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
        this.itemCriR = 0;
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

            final int optionLevel = ii.getOptionLevel();
            if (ed.isReleased()) {
                this.applyItemOption(ed.getOption1(), optionLevel);
                this.applyItemOption(ed.getOption2(), optionLevel);
                this.applyItemOption(ed.getOption3(), optionLevel);
                option.applyItemOptionR(ed.getOption1(), optionLevel);
                option.applyItemOptionR(ed.getOption2(), optionLevel);
                option.applyItemOptionR(ed.getOption3(), optionLevel);
            }
        }

        // Passive skills
        for (SkillId skillId : SkillConstants.SECONDARY_STAT_SKILLS) {
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                continue;
            }
            final SkillInfo si = skillInfoResult.get();
            final int slv = SkillManager.getSkillLevel(ss, sm, skillId);
            if (slv == 0) {
                continue;
            }
            switch (skillId) {
                case SkillId.THIEF_NIMBLE_BODY, SkillId.NW1_NIMBLE_BODY, SkillId.PIRATE_BULLET_TIME,
                     SkillId.TB1_QUICK_MOTION -> {
                    this.acc += si.getValue(SkillStat.x, slv);
                    this.eva += si.getValue(SkillStat.y, slv);
                }
                case SkillId.EVAN1_DRAGON_SOUL -> {
                    this.mad += si.getValue(SkillStat.mad, slv);
                }
                case SkillId.BEGINNER_BLESSING_OF_THE_FAIRY, SkillId.NOBLESSE_BLESSING_OF_THE_FAIRY,
                     SkillId.CITIZEN_BLESSING_OF_THE_FAIRY,
                     SkillId.LEGEND_BLESSING_OF_THE_FAIRY, SkillId.EVANBEGINNER_BLESSING_OF_THE_FAIRY -> {
                    this.pad += si.getValue(SkillStat.x, slv);
                    this.mad += si.getValue(SkillStat.y, slv);
                    this.acc += si.getValue(SkillStat.z, slv);
                    this.eva += si.getValue(SkillStat.z, slv);
                }
            }
        }

        // Combo Ability - description says Weapon/Magic ATT, but should be PDD, MDD
        final int comboAbilityBuff = ss.getOption(CharacterTemporaryStat.ComboAbilityBuff).nOption;
        if (comboAbilityBuff != 0) {
            final SkillId comboSkillId = SkillConstants.getComboAbilitySkill(bs.getJob());
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(comboSkillId);
            if (skillInfoResult.isPresent()) {
                final SkillInfo si = skillInfoResult.get();
                final int slv = SkillManager.getSkillLevel(ss, sm, comboSkillId);
                if (slv > 0) {
                    final int stacks = Math.max(comboAbilityBuff / 10, si.getValue(SkillStat.x, slv));
                    pdd += stacks * si.getValue(SkillStat.z, slv);
                    mdd += stacks * si.getValue(SkillStat.z, slv);
                }
            }
        }

        // Jaguar Rider
        if (SkillConstants.WILD_HUNTER_JAGUARS.contains(getRidingVehicle())) {
            final int slv = SkillManager.getSkillLevel(ss, sm, SkillId.WH1_JAGUAR_RIDER);
            if (slv > 0) {
                SkillProvider.getSkillInfoById(SkillId.WH1_JAGUAR_RIDER).ifPresent((si) -> {
                    this.eva += si.getValue(SkillStat.y, slv);
                });
            }
        }

        // get_weapon_mastery
        final WeaponType wt = WeaponType.getByItemId(weapon != null ? weapon.getItemId() : 0);
        switch (wt) {
            case OH_SWORD, TH_SWORD -> {
                getStatFromSkill(ss, sm, SkillId.FIGHTER_WEAPON_MASTERY, SkillId.PAGE_WEAPON_MASTERY, SkillId.DW2_SWORD_MASTERY);
            }
            case OH_AXE, TH_AXE -> {
                getStatFromSkill(ss, sm, SkillId.FIGHTER_WEAPON_MASTERY);
            }
            case OH_MACE, TH_MACE -> {
                getStatFromSkill(ss, sm, SkillId.PAGE_WEAPON_MASTERY);
            }
            case DAGGER -> {
                final Item shield = realEquip.get(BodyPart.SHIELD.getValue());
                if (shield != null && WeaponType.getByItemId(shield.getItemId()) == WeaponType.SUB_DAGGER) {
                    getStatFromSkill(ss, sm, SkillId.DB1_KATARA_MASTERY);
                } else {
                    getStatFromSkill(ss, sm, SkillId.BANDIT_DAGGER_MASTERY);
                }
            }
            case SPEAR -> {
                getStatFromSkill(ss, sm, SkillId.SPEARNMAN_WEAPON_MASTERY);
            }
            case POLEARM -> {
                if (JobConstants.isAranJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.ARAN2_POLEARM_MASTERY);
                    getStatFromSkill(ss, sm, SkillId.ARAN4_HIGH_MASTERY);
                } else {
                    getStatFromSkill(ss, sm, SkillId.SPEARNMAN_WEAPON_MASTERY);
                }
            }
            case BOW -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.WA2_BOW_MASTERY);
                    getStatFromSkill(ss, sm, SkillId.WA3_BOW_EXPERT);
                } else {
                    getStatFromSkill(ss, sm, SkillId.HUNTER_BOW_MASTERY);
                    getStatFromSkill(ss, sm, SkillId.BOWMASTER_BOW_EXPERT);
                }
            }
            case CROSSBOW -> {
                if (JobConstants.isWildHunterJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.WH2_CROSSBOW_MASTERY);
                    getStatFromSkill(ss, sm, SkillId.WH4_CROSSBOW_EXPERT);
                } else {
                    getStatFromSkill(ss, sm, SkillId.CROSSBOWMAN_CROSSBOW_MASTERY);
                    getStatFromSkill(ss, sm, SkillId.MARKSMAN_MARKSMAN_BOOST);
                }
            }
            case THROWINGGLOVE -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.NW2_CLAW_MASTERY);
                } else {
                    getStatFromSkill(ss, sm, SkillId.ASSASSIN_CLAW_MASTERY);
                }
            }
            case KNUCKLE -> {
                if (JobConstants.isCygnusJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.TB2_KNUCKLE_MASTERY);
                } else {
                    getStatFromSkill(ss, sm, SkillId.BRAWLER_KNUCKLE_MASTERY);
                }
            }
            case GUN -> {
                if (JobConstants.isMechanicJob(bs.getJob())) {
                    getStatFromSkill(ss, sm, SkillId.MECH4_EXTREME_MECH, SkillId.MECH2_MECHANIC_MASTERY);
                } else {
                    getStatFromSkill(ss, sm, SkillId.GUNSLINGER_GUN_MASTERY);
                }
            }
        }

        // get_magic_mastery
        if (JobConstants.isEvanJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.EVAN4_SPELL_MASTERY);
            getStatFromSkill(ss, sm, SkillId.EVAN9_MAGIC_MASTERY);
        } else if (JobConstants.isBattleMageJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.STAFF_MASTERY);
        } else if (JobConstants.isBlazeWizardJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.BW2_SPELL_MASTERY);
        } else if (JobConstants.isFirePoisonJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.FP1_SPELL_MASTERY);
        } else if (JobConstants.isIceLightningJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.IL1_SPELL_MASTERY);
        } else if (JobConstants.isBishopJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.CLERIC_SPELL_MASTERY);
        }

        // get_increase_speed
        if (JobConstants.isBowmasterJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.RANGER_THRUST);
        } else if (JobConstants.isMarksmanJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.SNIPER_THRUST);
        } else if (JobConstants.isWindArcherJob(bs.getJob())) {
            getStatFromSkill(ss, sm, SkillId.WA2_THRUST);
        }
        if (hasOption(CharacterTemporaryStat.YellowAura)) {
            final Optional<SkillInfo> yellowAuraResult = SkillProvider.getSkillInfoById(getOption(CharacterTemporaryStat.YellowAura).getSkillId());
            yellowAuraResult.ifPresent((si) -> this.speed += si.getValue(SkillStat.x, getOption(CharacterTemporaryStat.YellowAura).nOption));
            if (hasOption(CharacterTemporaryStat.SuperBody)) {
                //TODO BattleMage.BODY_BOOST_YELLOW_AURA
                final Optional<SkillInfo> bodyBoostResult = SkillProvider.getSkillInfoById(SkillId.BODY_BOOST);
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
        this.itemCriR += option.criR;

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

    private void getStatFromSkill(SecondaryStat ss, SkillManager sm, SkillId... skillIds) {
        for (SkillId skillId : skillIds) {
            final Optional<SkillInfo> skillInfoResult = SkillProvider.getSkillInfoById(skillId);
            if (skillInfoResult.isEmpty()) {
                continue;
            }
            final SkillInfo si = skillInfoResult.get();
            final int slv = SkillManager.getSkillLevel(ss, sm, skillId);
            if (slv == 0) {
                continue;
            }
            //TODO
            /*switch (skillId) {
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
            }*/
            break;
        }
    }


    private static class SecondaryStatRateOption {
        private int padR;
        private int pddR;
        private int madR;
        private int mddR;
        private int accR;
        private int evaR;
        private int criR;

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
                    case incCr -> this.criR += entry.getValue();
                }
            }
        }
    }
}
