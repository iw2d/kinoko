package kinoko.provider.mob;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;

public record MobInfo(int templateId, int level, int maxHP, int maxMP) {
    public static MobInfo from(int mobId, WzListProperty infoProp) {
        int level = 0;
        int maxHP = 0;
        int maxMP = 0;
        for (var entry : infoProp.getItems().entrySet()) {
            switch (entry.getKey()) {
                case "level" -> {
                    level = WzProvider.getInteger(entry.getValue());
                }
                case "maxHP" -> {
                    maxHP = WzProvider.getInteger(entry.getValue());
                }
                case "maxMP" -> {
                    maxMP = WzProvider.getInteger(entry.getValue());
                }
                default -> {
                    // System.err.printf("Unhandled info %s in mob %d%n", entry.getKey(), mobId);
                }
            }
        }
        return new MobInfo(mobId, level, maxHP, maxMP);
    }
}
