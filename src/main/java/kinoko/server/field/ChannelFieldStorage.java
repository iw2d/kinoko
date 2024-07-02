package kinoko.server.field;

import kinoko.provider.MapProvider;
import kinoko.provider.map.MapInfo;
import kinoko.world.field.Field;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ChannelFieldStorage implements FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldMap = new ConcurrentHashMap<>(); // map id -> field

    @Override
    public synchronized Optional<Field> getFieldById(int mapId) {
        final Field field = fieldMap.get(mapId);
        if (field != null) {
            return Optional.of(field);
        }
        final Optional<Field> fieldResult = createField(this, mapId);
        fieldResult.ifPresent(value -> fieldMap.put(mapId, value));
        return fieldResult;
    }

    @Override
    public void clear() {
        final var iter = fieldMap.values().iterator();
        while (iter.hasNext()) {
            final Field field = iter.next();
            field.getFieldEventFuture().cancel(true);
            iter.remove();
        }
    }

    private static Optional<Field> createField(FieldStorage fieldStorage, int mapId) {
        final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
        return mapInfoResult.map(mapInfo -> Field.from(fieldStorage, mapInfo));
    }
}
