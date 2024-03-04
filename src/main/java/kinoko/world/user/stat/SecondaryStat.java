package kinoko.world.user.stat;

import kinoko.provider.ItemProvider;
import kinoko.provider.SkillProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.skill.SkillInfo;
import kinoko.provider.skill.SkillStat;
import kinoko.server.packet.OutPacket;
import kinoko.util.BitFlag;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;
import kinoko.world.job.cygnus.NightWalker;
import kinoko.world.job.cygnus.Noblesse;
import kinoko.world.job.cygnus.ThunderBreaker;
import kinoko.world.job.explorer.Beginner;
import kinoko.world.job.explorer.Pirate;
import kinoko.world.job.explorer.Thief;
import kinoko.world.job.legend.Aran;
import kinoko.world.job.legend.Evan;
import kinoko.world.job.resistance.Citizen;
import kinoko.world.job.resistance.WildHunter;
import kinoko.world.skill.SkillConstants;
import kinoko.world.skill.SkillManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class SecondaryStat {
    private final Map<CharacterTemporaryStat, Option> stats = new EnumMap<>(CharacterTemporaryStat.class);
    private final BitFlag<CharacterTemporaryStat> setStatFlag = new BitFlag<>(CharacterTemporaryStat.FLAG_SIZE);
    private final BitFlag<CharacterTemporaryStat> resetStatFlag = new BitFlag<>(CharacterTemporaryStat.FLAG_SIZE);
    private final DiceInfo diceInfo = new DiceInfo();

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

    private int padR;
    private int pddR;
    private int madR;
    private int mddR;
    private int accR;
    private int evaR;

    public void setFrom(BasicStat bs, InventoryManager im, SkillManager sm) {
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

        // Bare hands for pirates
        final Item weapon = im.getEquipped().getItem(BodyPart.WEAPON.getValue());
        if (weapon == null && JobConstants.getJobCategory(bs.getJob()) == 5) {
            if (bs.getLevel() > 30) {
                this.pad = 31;
            } else {
                this.pad = (int) (bs.getLevel() * 0.7 + 10.0);
            }
        }

        // Equip stats
        // TODO: set items
        for (var entry : im.getEquipped().getItems().entrySet()) {
            // Resolve item and item info
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.EQUIP) {
                continue;
            }
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
            applySocketOption(ed.getSocket1(), optionLevel);
            applySocketOption(ed.getSocket2(), optionLevel);
            applyItemOption(ed.getOption1(), optionLevel);
            applyItemOption(ed.getOption2(), optionLevel);
            applyItemOption(ed.getOption3(), optionLevel);
            applyItemOptionR(ed.getOption1(), optionLevel);
            applyItemOptionR(ed.getOption2(), optionLevel);
            applyItemOptionR(ed.getOption3(), optionLevel);
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

        // Weapon mastery
        final int weaponId = weapon != null ? weapon.getItemId() : 0;

    }

    private void applySocketOption(int socketOptionId, int optionLevel) {
        // TODO
    }

    private void applySocketOptionR(int socketOptionId, int optionLevel) {
        // TODO
    }

    private void applyItemOption(int itemOptionId, int optionLevel) {
        // TODO
    }

    private void applyItemOptionR(int itemOptionId, int optionLevel) {
        // TODO
    }


    public int getRidingVehicle() {
        return getOption(CharacterTemporaryStat.RideVehicle).rOption;
    }












    public Option getOption(CharacterTemporaryStat cts) {
        return stats.getOrDefault(cts, new Option());
    }

    public DiceInfo getDiceInfo() {
        return diceInfo;
    }

    public int getJaguarRidingMaxHpR() {
        return 0; // TODO
    }

    public void encodeForLocal(OutPacket outPacket, boolean complete) {
        final BitFlag<CharacterTemporaryStat> statFlag = complete ? BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE) : setStatFlag;
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.LOCAL_ENCODE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                getOption(cts).encode(outPacket);
            }
        }

        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseAtt).nOption);
        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseState).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.SWALLOW_BUFF_STAT) {
            if (statFlag.hasFlag(cts)) {
                outPacket.encodeByte(getOption(cts).tOption); // tSwallowBuffTime
                break;
            }
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.Dice)) {
            diceInfo.encode(outPacket); // aDiceInfo
        }

        if (statFlag.hasFlag(CharacterTemporaryStat.BlessingArmor)) {
            outPacket.encodeInt(stats.get(CharacterTemporaryStat.BlessingArmor).nOption); // nBlessingArmorIncPAD
        }

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                getOption(cts).encode(outPacket);
            }
        }
    }

    public void encodeForRemote(OutPacket outPacket, boolean complete) {
        final BitFlag<CharacterTemporaryStat> statFlag = complete ? BitFlag.from(stats.keySet(), CharacterTemporaryStat.FLAG_SIZE) : setStatFlag;
        statFlag.encode(outPacket);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.REMOTE_ENCODE_ORDER) {
            if (statFlag.hasFlag(cts)) {
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
                    case WeaponCharge, Stun, Darkness, Seal, Weakness, ShadowPartner, Attract, BanMap, DojangShield,
                            ReverseInput, RepeatEffect, StopPortion, StopMotion, Fear, Frozen, SuddenDeath, FinalCut,
                            Mechanic, DarkAura, BlueAura, YellowAura -> {
                        outPacket.encodeInt(getOption(cts).rOption);
                    }
                    case Poison -> {
                        outPacket.encodeShort(getOption(cts).nOption); // overwritten with 1
                        outPacket.encodeInt(getOption(cts).rOption);
                    }
                }
            }
        }

        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseAtt).nOption);
        outPacket.encodeByte(getOption(CharacterTemporaryStat.DefenseState).nOption);

        for (CharacterTemporaryStat cts : CharacterTemporaryStat.TWO_STATE_ORDER) {
            if (statFlag.hasFlag(cts)) {
                getOption(cts).encode(outPacket);
            }
        }
    }

    public void encodeReset(OutPacket outPacket) {
        resetStatFlag.encode(outPacket);
    }
}
