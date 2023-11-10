package kinoko.provider.item;

import kinoko.provider.ProviderError;
import kinoko.provider.wz.property.WzListProperty;

import java.util.*;

public record ItemInfo(int itemId, Map<ItemInfoType, Object> info, Map<ItemSpecType, Object> spec) {

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
                        final ItemInfoType type = ItemInfoType.fromName(infoEntry.getKey());
                        if (type == null || type == ItemInfoType.LEVEL) {
                            // System.out.printf("Unhandled info %s in item %d%n", entry.getKey(), itemId);
                            continue;
                        }
                        info.put(type, infoEntry.getValue());
                    }
                }
                case "spec" -> {
                    if (!(entry.getValue() instanceof WzListProperty specProp)) {
                        throw new ProviderError("Failed to resolve item spec property");
                    }
                    for (var specEntry : specProp.getItems().entrySet()) {
                        final ItemSpecType type = ItemSpecType.fromName(specEntry.getKey());
                        if (type == null || type == ItemSpecType.MORPH_RANDOM || type == ItemSpecType.CON || type == ItemSpecType.MOB) {
                            // System.out.printf("Unhandled spec %s in item %d%n", entry.getKey(), itemId);
                            continue;
                        }
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
