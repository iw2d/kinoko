package kinoko.world.field;

import kinoko.provider.map.MapInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Field {
    private static final Logger log = LogManager.getLogger(Field.class);
    private final MapInfo mapInfo;

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }
}
