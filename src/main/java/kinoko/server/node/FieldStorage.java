package kinoko.server.node;

import kinoko.provider.MapProvider;
import kinoko.provider.map.MapInfo;
import kinoko.world.field.Field;
import kinoko.world.social.party.TownPortal;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldStorage = new ConcurrentHashMap<>(); // field id -> field
    private final ConcurrentHashMap<Integer, TownPortal> townPortals = new ConcurrentHashMap<>(); // character id -> town portal

    public synchronized Optional<Field> getFieldById(int mapId) {
        if (!fieldStorage.containsKey(mapId)) {
            final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
            if (mapInfoResult.isEmpty()) {
                return Optional.empty();
            }
            fieldStorage.put(mapId, Field.from(this, mapInfoResult.get()));
        }
        return Optional.of(fieldStorage.get(mapId));
    }
}
