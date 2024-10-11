package kinoko.world.user.stat;

import kinoko.provider.EtcProvider;
import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.provider.item.ItemOptionLevelData;
import kinoko.provider.item.SetItemInfo;
import kinoko.util.TimeUtil;
import kinoko.world.item.*;
import kinoko.world.user.Pet;
import kinoko.world.user.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for resolving valid equipped items for a User. This is only instantiated for computing and checking the
 * stat requirements of equipped items.
 */
public final class EquipStat {
    private int incStr = 0;
    private int incDex = 0;
    private int incInt = 0;
    private int incLuk = 0;
    private int incStrR = 0;
    private int incDexR = 0;
    private int incIntR = 0;
    private int incLukR = 0;

    public EquipStat() {
    }

    public EquipStat(EquipStat stat) {
        this.incStr = stat.incStr;
        this.incDex = stat.incDex;
        this.incInt = stat.incInt;
        this.incLuk = stat.incLuk;
        this.incStrR = stat.incStrR;
        this.incDexR = stat.incDexR;
        this.incIntR = stat.incIntR;
        this.incLukR = stat.incLukR;
    }

    private int getStr(int baseStr) {
        final int totalStr = baseStr + incStr;
        return totalStr + (totalStr * incStrR / 100);
    }

    private int getDex(int baseDex) {
        final int totalDex = baseDex + incDex;
        return totalDex + (totalDex * incDexR / 100);
    }

    private int getInt(int baseInt) {
        final int totalInt = baseInt + incInt;
        return totalInt + (totalInt * incIntR / 100);
    }

    private int getLuk(int baseLuk) {
        final int totalLuk = baseLuk + incLuk;
        return totalLuk + (totalLuk * incLukR / 100);
    }

    private void applyItemOption(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
        if (itemOptionResult.isEmpty()) {
            return;
        }
        for (var entry : itemOptionResult.get().getStats().entrySet()) {
            switch (entry.getKey()) {
                case incSTR -> this.incStr += entry.getValue();
                case incDEX -> this.incDex += entry.getValue();
                case incINT -> this.incInt += entry.getValue();
                case incLUK -> this.incLuk += entry.getValue();
            }
        }
    }

    private void applyItemOptionR(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
        if (itemOptionResult.isEmpty()) {
            return;
        }
        for (var entry : itemOptionResult.get().getStats().entrySet()) {
            switch (entry.getKey()) {
                case incSTRr -> this.incStrR += entry.getValue();
                case incDEXr -> this.incDexR += entry.getValue();
                case incINTr -> this.incIntR += entry.getValue();
                case incLUKr -> this.incLukR += entry.getValue();
            }
        }
    }

    private void clearItemOption(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
        if (itemOptionResult.isEmpty()) {
            return;
        }
        for (var entry : itemOptionResult.get().getStats().entrySet()) {
            switch (entry.getKey()) {
                case incSTR -> this.incStr -= entry.getValue();
                case incDEX -> this.incDex -= entry.getValue();
                case incINT -> this.incInt -= entry.getValue();
                case incLUK -> this.incLuk -= entry.getValue();
            }
        }
    }

    private void clearItemOptionR(int itemOptionId, int optionLevel) {
        final Optional<ItemOptionLevelData> itemOptionResult = ItemProvider.getItemOptionInfo(itemOptionId, optionLevel);
        if (itemOptionResult.isEmpty()) {
            return;
        }
        for (var entry : itemOptionResult.get().getStats().entrySet()) {
            switch (entry.getKey()) {
                case incSTRr -> this.incStrR -= entry.getValue();
                case incDEXr -> this.incDexR -= entry.getValue();
                case incINTr -> this.incIntR -= entry.getValue();
                case incLUKr -> this.incLukR -= entry.getValue();
            }
        }
    }

    public static Map<Integer, Item> getRealEquip(User user) {
        final CharacterStat cs = user.getCharacterStat();
        final int basicStatUp = user.getSecondaryStat().getOption(CharacterTemporaryStat.BasicStatUp).nOption;
        final int baseStr = cs.getBaseStr() + (basicStatUp * cs.getBaseStr() / 100);
        final int baseDex = cs.getBaseDex() + (basicStatUp * cs.getBaseDex() / 100);
        final int baseInt = cs.getBaseInt() + (basicStatUp * cs.getBaseInt() / 100);
        final int baseLuk = cs.getBaseLuk() + (basicStatUp * cs.getBaseLuk() / 100);

        EquipStat stat = new EquipStat();

        // Compute total stat
        final InventoryManager im = user.getInventoryManager();
        final Inventory equipped = im.getEquipped();
        for (var entry : equipped.getItems().entrySet()) {
            final BodyPart bodyPart = BodyPart.getByValue(entry.getKey());
            if (bodyPart == BodyPart.EXT_PENDANT1 && im.getExtSlotExpire().isBefore(TimeUtil.getCurrentTime())) {
                continue;
            }
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.EQUIP) {
                continue;
            }

            final EquipData ed = item.getEquipData();
            stat.incStr += ed.getIncStr();
            stat.incDex += ed.getIncDex();
            stat.incInt += ed.getIncInt();
            stat.incLuk += ed.getIncLuk();

            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
            if (itemInfoResult.isEmpty()) {
                continue;
            }
            final ItemInfo ii = itemInfoResult.get();
            final int optionLevel = ii.getOptionLevel();
            if (ed.isReleased()) {
                stat.applyItemOption(ed.getOption1(), optionLevel);
                stat.applyItemOption(ed.getOption2(), optionLevel);
                stat.applyItemOption(ed.getOption3(), optionLevel);
                stat.applyItemOptionR(ed.getOption1(), optionLevel);
                stat.applyItemOptionR(ed.getOption2(), optionLevel);
                stat.applyItemOptionR(ed.getOption3(), optionLevel);
            }
        }

        // Set items
        for (SetItemInfo setItemInfo : EtcProvider.getSetItemInfos()) {
            final Set<Integer> equippedItems = equipped.getItems().values().stream().map(Item::getItemId).collect(Collectors.toSet());
            equippedItems.retainAll(setItemInfo.getItems());
            for (int itemCount = 0; itemCount <= equippedItems.size(); itemCount++) {
                final Map<ItemInfoType, Integer> effect = setItemInfo.getEffect().get(itemCount);
                if (effect == null) {
                    continue;
                }
                for (var entry : effect.entrySet()) {
                    switch (entry.getKey()) {
                        case incSTR -> stat.incStr += entry.getValue();
                        case incDEX -> stat.incDex += entry.getValue();
                        case incINT -> stat.incInt += entry.getValue();
                        case incLUK -> stat.incLuk += entry.getValue();
                    }
                }
            }
        }

        // Build real equip list
        final Map<Integer, Item> realEquip = new HashMap<>();
        final Item weapon = equipped.getItem(BodyPart.WEAPON.getValue());
        for (var entry : equipped.getItems().entrySet()) {
            // Resolve item info and equip data
            final int position = entry.getKey();
            final Item item = entry.getValue();
            if (item.getItemType() != ItemType.EQUIP) {
                continue;
            }
            final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(item.getItemId());
            if (itemInfoResult.isEmpty()) {
                continue;
            }
            final ItemInfo ii = itemInfoResult.get();
            final EquipData ed = item.getEquipData();

            // Compute stat without this equip
            final EquipStat statWithout = new EquipStat(stat);
            statWithout.incStr -= ed.getIncStr();
            statWithout.incDex -= ed.getIncDex();
            statWithout.incInt -= ed.getIncInt();
            statWithout.incLuk -= ed.getIncLuk();
            final int optionLevel = ii.getOptionLevel();
            statWithout.clearItemOption(ed.getOption1(), optionLevel);
            statWithout.clearItemOption(ed.getOption2(), optionLevel);
            statWithout.clearItemOption(ed.getOption3(), optionLevel);
            statWithout.clearItemOptionR(ed.getOption1(), optionLevel);
            statWithout.clearItemOptionR(ed.getOption2(), optionLevel);
            statWithout.clearItemOptionR(ed.getOption3(), optionLevel);

            // Find applicable pet
            final Pet pet;
            if (position == BodyPart.PETWEAR.getValue()) {
                pet = user.getPet(0);
            } else if (position == BodyPart.PETWEAR2.getValue()) {
                pet = user.getPet(1);
            } else if (position == BodyPart.PETWEAR3.getValue()) {
                pet = user.getPet(2);
            } else {
                pet = null;
            }

            // Check if able to equip
            if (ii.isAbleToEquip(
                    cs.getGender(),
                    cs.getLevel(),
                    cs.getJob(),
                    cs.getSubJob(),
                    statWithout.getStr(baseStr),
                    statWithout.getDex(baseDex),
                    statWithout.getInt(baseInt),
                    statWithout.getLuk(baseLuk),
                    cs.getPop(),
                    ed.getDurability(),
                    weapon != null ? weapon.getItemId() : 0,
                    pet != null ? pet.getTemplateId() : 0
            )) {
                realEquip.put(position, item);
            } else {
                stat = statWithout;
            }
        }
        return Collections.unmodifiableMap(realEquip);
    }
}
