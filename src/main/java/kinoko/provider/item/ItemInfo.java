package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;

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
        return (int) itemInfos.getOrDefault(infoType, 0);
    }

    public boolean isCash() {
        return getInfo(ItemInfoType.cash) != 0;
    }

    public int getPrice() {
        return getInfo(ItemInfoType.price);
    }

    public int getSlotMax() {
        return getInfo(ItemInfoType.slotMax);
    }

    public boolean isTradeBlock() {
        return getInfo(ItemInfoType.tradBlock) != 0 || getInfo(ItemInfoType.tradeBlock) != 0;
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
