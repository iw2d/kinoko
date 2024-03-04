package kinoko.world.user.stat;

import kinoko.provider.EtcProvider;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemOptionLevelData;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.skill.PassiveSkillData;

import java.util.Optional;

public final class BasicStat {
    private int gender;
    private int level;
    private int job;
    private int str;
    private int dex;
    private int int_;
    private int luk;
    private int pop;
    private int maxHp;
    private int maxMp;

    public int getGender() {
        return gender;
    }

    public int getLevel() {
        return level;
    }

    public int getJob() {
        return job;
    }

    public int getStr() {
        return str;
    }

    public int getDex() {
        return dex;
    }

    public int getInt() {
        return int_;
    }

    public int getLuk() {
        return luk;
    }

    public int getPop() {
        return pop;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setFrom(CharacterStat cs, ForcedStat fs, SecondaryStat ss, InventoryManager im, PassiveSkillData psd) {
        this.gender = cs.getGender();
        this.level = cs.getLevel();
        this.job = cs.getJob();
        this.str = cs.getBaseStr();
        this.dex = cs.getBaseDex();
        this.int_ = cs.getBaseInt();
        this.luk = cs.getBaseLuk();
        this.pop = cs.getPop();
        this.maxHp = cs.getMaxHp();
        this.maxMp = cs.getMaxMp();

        final RateOption option = new RateOption();
        int incMaxHpR = 0;
        int incMaxMpR = 0;

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
            this.str += ed.getIncStr();
            this.int_ += ed.getIncInt();
            this.dex += ed.getIncDex();
            this.luk += ed.getIncLuk();
            this.maxHp += ed.getIncMaxHp();
            this.maxMp += ed.getIncMaxMp();

            incMaxHpR += ed.getIncMaxHpR();
            incMaxMpR += ed.getIncMaxMpR();

            final int optionLevel = (ii.getInfo(ItemInfoType.reqLevel) - 1) / 10; // no sockets in v95
            applyItemOption(ed.getOption1(), optionLevel);
            applyItemOption(ed.getOption2(), optionLevel);
            applyItemOption(ed.getOption3(), optionLevel);
            applyItemOptionR(ed.getOption1(), optionLevel, option);
            applyItemOptionR(ed.getOption2(), optionLevel, option);
            applyItemOptionR(ed.getOption3(), optionLevel, option);
        }

        // BasicStatUp CTS (Maple Warrior)
        final int basicStatUp = ss.getOption(CharacterTemporaryStat.BasicStatUp).nOption;
        this.str += basicStatUp * cs.getBaseStr() / 100;
        this.dex += basicStatUp * cs.getBaseDex() / 100;
        this.int_ += basicStatUp * cs.getBaseInt() / 100;
        this.luk += basicStatUp * cs.getBaseLuk() / 100;

        // Forced Stat
        if (fs.getStr() > 0) {
            this.str = fs.getStr();
        }
        if (fs.getDex() > 0) {
            this.dex = fs.getDex();
        }
        if (fs.getInt() > 0) {
            this.int_ = fs.getInt();
        }
        if (fs.getLuk() > 0) {
            this.luk = fs.getLuk();
        }

        // EMHP/EMMP CTS
        this.maxHp += ss.getOption(CharacterTemporaryStat.EMHP).nOption;
        this.maxMp += ss.getOption(CharacterTemporaryStat.EMMP).nOption;

        this.str += option.strR * this.str / 100;
        this.dex += option.dexR * this.dex / 100;
        this.int_ += option.intR * this.int_ / 100;
        this.luk += option.lukR * this.luk / 100;

        int hpIncRateFromCts = ss.getOption(CharacterTemporaryStat.Conversion).nOption;
        hpIncRateFromCts = Math.max(hpIncRateFromCts, ss.getOption(CharacterTemporaryStat.MaxHP).nOption);
        hpIncRateFromCts = Math.max(hpIncRateFromCts, ss.getOption(CharacterTemporaryStat.MorewildMaxHP).nOption);
        hpIncRateFromCts += ss.getJaguarRidingMaxHpR();
        int mpIncRateFromCts = ss.getOption(CharacterTemporaryStat.SwallowMaxMP).nOption;
        mpIncRateFromCts += ss.getOption(CharacterTemporaryStat.MaxMP).nOption;

        // Fixed point division
        this.maxHp = 0x51EB851F * maxHp * (incMaxHpR + option.incMaxHpR + hpIncRateFromCts + psd.mhpR);
        this.maxMp = 0x51EB851F * maxMp * (incMaxMpR + option.incMaxMpR + mpIncRateFromCts + psd.mmpR);

        // Max hp/mp cap
        this.maxHp = Math.min(this.maxHp, GameConstants.MAX_HP);
        this.maxMp = Math.min(this.maxMp, GameConstants.MAX_MP);
    }

    private void applyItemOption(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = EtcProvider.getItemOptionInfo(itemOptionId, optionLevel);
        // TODO
    }

    private void applyItemOptionR(int itemOptionId, int optionLevel, RateOption option) {
        final Optional<ItemOptionLevelData> itemOptionResult = EtcProvider.getItemOptionInfo(itemOptionId, optionLevel);
        // TODO
    }

    private class RateOption {
        private int strR;
        private int dexR;
        private int intR;
        private int lukR;
        private int incMaxHpR;
        private int incMaxMpR;
    }
}
