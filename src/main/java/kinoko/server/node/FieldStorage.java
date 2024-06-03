package kinoko.server.node;

import kinoko.provider.MapProvider;
import kinoko.provider.map.MapInfo;
import kinoko.world.field.Field;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldMap = new ConcurrentHashMap<>(); // field id -> field

    public synchronized Optional<Field> getFieldById(int mapId) {
        if (!fieldMap.containsKey(mapId)) {
            final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
            if (mapInfoResult.isEmpty()) {
                return Optional.empty();
            }
            fieldMap.put(mapId, Field.from(this, mapInfoResult.get()));
        }
        return Optional.of(fieldMap.get(mapId));
    }
}
