package kinoko.world.field;

import kinoko.provider.map.MapInfo;

public final class Field {
    private final MapInfo mapInfo;

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public int getFieldId() {
        return mapInfo.id();
    }
}
