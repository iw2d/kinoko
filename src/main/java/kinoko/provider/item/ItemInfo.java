package kinoko.provider.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.GameConstants;
import kinoko.world.item.*;
import kinoko.world.job.JobConstants;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class ItemInfo {
    private final int itemId;
    private final Map<ItemInfoType, Object> itemInfos;
    private final Map<ItemSpecType, Object> itemSpecs;

    public ItemInfo(int itemId, Map<ItemInfoType, Object> itemInfos, Map<ItemSpecType, Object> itemSpecs) {
        this.itemId = itemId;
        this.itemInfos = itemInfos;
        this.itemSpecs = itemSpecs;
    }

    public int getItemId() {
        return itemId;
    }

    public Map<ItemInfoType, Object> getItemInfos() {
        return itemInfos;
    }

    public Map<ItemSpecType, Object> getItemSpecs() {
        return itemSpecs;
    }

    public int getInfo(ItemInfoType infoType) {
        return getInfo(infoType, 0);
    }

    public int getInfo(ItemInfoType infoType, int defaultValue) {
        return WzProvider.getInteger(itemInfos.get(infoType), defaultValue);
    }

    public int getSpec(ItemSpecType specType) {
        return getSpec(specType, 0);
    }

    public int getSpec(ItemSpecType specType, int defaultValue) {
        return WzProvider.getInteger(itemSpecs.get(specType), defaultValue);
    }

    public boolean isCash() {
        return getInfo(ItemInfoType.cash) != 0;
    }

    public boolean isQuest() {
        return getInfo(ItemInfoType.quest) != 0;
    }

    public int getPrice() {
        return getInfo(ItemInfoType.price);
    }

    public int getSlotMax() {
        if (getItemInfos().containsKey(ItemInfoType.slotMax)) {
            return getInfo(ItemInfoType.slotMax);
        }
        return ItemType.getByItemId(itemId) == ItemType.BUNDLE ? GameConstants.DEFAULT_ITEM_SLOT_MAX : 1;
    }

    public boolean isTradeBlock() {
        return getInfo(ItemInfoType.tradeBlock) != 0;
    }

    public boolean isAbleToEquip(int gender, int level, int job, int subJob, int totalStr, int totalDex, int totalInt, int totalLuk, int pop, int durability, int weaponId, int petTemplateId) {
        // Check durability
        if (getInfo(ItemInfoType.durability) > 0 && durability == 0) {
            return false;
        }
        // Sub dagger (katara) can only be equipped by dual blades, and while equipped with a dagger
        final WeaponType wt = WeaponType.getByItemId(getItemId());
        if (wt == WeaponType.SUB_DAGGER) {
            if (WeaponType.getByItemId(weaponId) != WeaponType.DAGGER || !JobConstants.isDualJob(job) && !JobConstants.isAdminJob(job)) {
                return false;
            }
        }
        // Check pet equip
        if (ItemConstants.isCorrectBodyPart(getItemId(), BodyPart.PET_EQUIP_1, gender)) {
            // CItemInfo::EQUIPITEM::IsItemSuitedForPet
            if (!ItemConstants.isPetEquipItem(getItemId()) || !ItemConstants.isPet(petTemplateId) ||
                    !ItemProvider.isPetEquipSuitable(getItemId(), petTemplateId)) {
                return false;
            }
        }
        // Check other requirements
        final int jobCategory = JobConstants.getJobCategory(job);
        final int jobFlag = jobCategory != 0 ? (1 << (jobCategory - 1)) : 0;
        return JobConstants.isAdminJob(job) ||
                JobConstants.isManagerJob(job) ||
                ItemConstants.isMatchedItemIdGender(getItemId(), gender) &&
                        level >= getInfo(ItemInfoType.reqLEVEL) &&
                        totalStr >= getInfo(ItemInfoType.reqSTR) &&
                        totalDex >= getInfo(ItemInfoType.reqDEX) &&
                        totalInt >= getInfo(ItemInfoType.reqINT) &&
                        totalLuk >= getInfo(ItemInfoType.reqLUK) &&
                        (getInfo(ItemInfoType.reqPOP) == 0 || pop >= getInfo(ItemInfoType.reqPOP)) &&
                        (getInfo(ItemInfoType.reqJob) == 0 ||
                                getInfo(ItemInfoType.reqJob) == -1 && jobFlag == 0 ||
                                getInfo(ItemInfoType.reqJob) > 0 && (getInfo(ItemInfoType.reqJob) & jobFlag) != 0
                        );
    }

    public Item createItem(long itemSn) {
        return createItem(itemSn, 1);
    }

    public Item createItem(long itemSn, int quantity) {
        final ItemType type = ItemType.getByItemId(itemId);
        final Item item = new Item(type);
        item.setItemSn(itemSn);
        item.setItemId(itemId);
        item.setCash(isCash());
        item.setQuantity((short) quantity);
        if (type == ItemType.EQUIP) {
            item.setEquipData(EquipData.from(this));
        } else if (type == ItemType.PET) {
            item.setPetData(PetData.from(this));
        }
        return item;
    }

    @Override
    public String toString() {
        return "ItemInfo[" +
                "itemId=" + itemId + ", " +
                "info=" + itemInfos + ", " +
                "spec=" + itemSpecs + ']';
    }

    public static ItemInfo from(int itemId, WzListProperty itemProp) throws ProviderError {
        final Map<ItemInfoType, Object> info = new EnumMap<>(ItemInfoType.class);
        final Map<ItemSpecType, Object> spec = new EnumMap<>(ItemSpecType.class);

        for (var entry : itemProp.getItems().entrySet()) {
            switch (entry.getKey()) {
                case "info" -> {
                    if (!(entry.getValue() instanceof WzListProperty infoProp)) {
                        throw new ProviderError("Failed to resolve item info property");
                    }
                    for (var infoEntry : infoProp.getItems().entrySet()) {
                        if (ItemInfoType.isIgnored(infoEntry.getKey())) {
                            continue;
                        }
                        final ItemInfoType type = ItemInfoType.fromName(infoEntry.getKey());
                        info.put(type, infoEntry.getValue());
                    }
                }
                case "spec" -> {
                    if (!(entry.getValue() instanceof WzListProperty specProp)) {
                        throw new ProviderError("Failed to resolve item spec property");
                    }
                    for (var specEntry : specProp.getItems().entrySet()) {
                        if (ItemSpecType.isIgnored(specEntry.getKey())) {
                            continue;
                        }
                        final ItemSpecType type = ItemSpecType.fromName(specEntry.getKey());
                        spec.put(type, specEntry.getValue());
                    }
                }
                default -> {
                    // System.out.printf("Unhandled property %s in item %d%n", entry.getKey(), itemId);
                }
            }
        }
        return new ItemInfo(itemId, Collections.unmodifiableMap(info), Collections.unmodifiableMap(spec));
    }

}
