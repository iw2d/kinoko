package kinoko.provider.mob;

import kinoko.provider.wz.property.WzListProperty;

public record MobInfo(int id) {
    public static MobInfo from(int mobId, WzListProperty infoProp) {
        // TODO
        return new MobInfo(mobId);
    }
}
