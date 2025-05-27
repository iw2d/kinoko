package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.serialize.WzProperty;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class ItemOptionLevelData {
    private final Map<ItemOptionStat, Integer> stats;

    public ItemOptionLevelData(Map<ItemOptionStat, Integer> stats) {
        this.stats = stats;
    }

    public Map<ItemOptionStat, Integer> getStats() {
        return stats;
    }

    public static Map<Integer, ItemOptionLevelData> resolveLevelData(WzProperty levelList) throws ProviderError {
        final Map<Integer, ItemOptionLevelData> levelData = new HashMap<>();
        for (var levelEntry : levelList.getItems().entrySet()) {
            final int level = Integer.parseInt(levelEntry.getKey());
            if (!(levelEntry.getValue() instanceof WzProperty levelProp)) {
                throw new ProviderError("Failed to resolve item option level prop");
            }
            levelData.put(level, ItemOptionLevelData.from(levelProp));
        }
        return Collections.unmodifiableMap(levelData);
    }

    public static ItemOptionLevelData from(WzProperty levelProp) throws ProviderError {
        final Map<ItemOptionStat, Integer> stats = new EnumMap<>(ItemOptionStat.class);
        for (var propEntry : levelProp.getItems().entrySet()) {
            if (ItemOptionStat.isIgnored(propEntry.getKey())) {
                continue;
            }
            final ItemOptionStat stat = ItemOptionStat.fromName(propEntry.getKey());
            if (stat == null) {
                throw new ProviderError("Unknown item option stat : %s", propEntry.getKey());
            }
            stats.put(stat, WzProvider.getInteger(propEntry.getValue()));
        }
        return new ItemOptionLevelData(Collections.unmodifiableMap(stats));
    }
}
