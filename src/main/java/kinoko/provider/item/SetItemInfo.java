package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

import java.util.*;

public final class SetItemInfo {
    private final Set<Integer> items; // itemIds
    private final Map<Integer, Map<ItemInfoType, Integer>> effect; // count -> (incStat -> incValue)

    public SetItemInfo(Set<Integer> items, Map<Integer, Map<ItemInfoType, Integer>> effect) {
        this.items = items;
        this.effect = effect;
    }

    public Set<Integer> getItems() {
        return items;
    }

    public Map<Integer, Map<ItemInfoType, Integer>> getEffect() {
        return effect;
    }

    public static SetItemInfo from(WzListProperty setItemProp) throws ProviderError {
        // Items
        final Set<Integer> items = new HashSet<>();
        if (!(setItemProp.get("ItemID") instanceof WzListProperty itemIdList)) {
            throw new ProviderError("Could not resolve set item id list");
        }
        for (var itemIdEntry : itemIdList.getItems().entrySet()) {
            final int itemId = WzProvider.getInteger(itemIdEntry.getValue());
            items.add(itemId);
        }
        // Effect
        final Map<Integer, Map<ItemInfoType, Integer>> effect = new HashMap<>();
        if (!(setItemProp.get("Effect") instanceof WzListProperty effectList)) {
            throw new ProviderError("Could not resolve set item effect list");
        }
        for (var effectEntry : effectList.getItems().entrySet()) {
            final int count = WzProvider.getInteger(effectEntry.getKey());
            if (!(effectEntry.getValue() instanceof WzListProperty effectProp)) {
                throw new ProviderError("Could not resolve set item effect prop");
            }
            final Map<ItemInfoType, Integer> stats = new EnumMap<>(ItemInfoType.class);
            for (var statEntry : effectProp.getItems().entrySet()) {
                if (ItemInfoType.isIgnored(statEntry.getKey())) {
                    throw new ProviderError("Unhandled set item effect stat : %s", statEntry.getKey());
                }
                final ItemInfoType type = ItemInfoType.fromName(statEntry.getKey());
                if (type == ItemInfoType.setKey) {
                    // Visitor set property
                    continue;
                }
                final int value = WzProvider.getInteger(statEntry.getValue());
                stats.put(type, value);
            }
            effect.put(count, Collections.unmodifiableMap(stats));
        }
        return new SetItemInfo(
                Collections.unmodifiableSet(items),
                Collections.unmodifiableMap(effect)
        );
    }
}
