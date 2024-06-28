package kinoko.server.node;

import kinoko.provider.MapProvider;
import kinoko.provider.map.MapInfo;
import kinoko.world.field.Field;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldMap = new ConcurrentHashMap<>(); // field id -> field
    private final ConcurrentHashMap<Integer, Field> instanceMap = new ConcurrentHashMap<>(); // field id -> field

    public synchronized Optional<Field> getFieldById(int mapId) {
        final Field field = fieldMap.get(mapId);
        if (field != null) {
            return Optional.of(field);
        }
        final Optional<Field> fieldResult = createField(mapId);
        fieldResult.ifPresent(value -> fieldMap.put(mapId, value));
        return fieldResult;
    }

    public synchronized Optional<Field> getFieldInstanceById(int mapId) {
        final Field existingField = instanceMap.get(mapId);
        if (existingField != null) {
            // User inside, cannot create instance
            if (existingField.hasUser()) {
                return Optional.empty();
            }
            // Dispose existing field
            existingField.getFieldEventFuture().cancel(true);
        }
        // Create new instance
        final Optional<Field> fieldResult = createField(mapId);
        fieldResult.ifPresent(field -> instanceMap.put(mapId, field));
        return fieldResult;
    }

    private Optional<Field> createField(int mapId) {
        final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
        return mapInfoResult.map(mapInfo -> Field.from(this, mapInfo));
    }
}
