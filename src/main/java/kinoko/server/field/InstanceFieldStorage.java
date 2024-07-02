package kinoko.server.field;

import kinoko.provider.map.MapInfo;
import kinoko.world.field.Field;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InstanceFieldStorage implements FieldStorage {
    private final ConcurrentHashMap<Integer, Field> fieldMap = new ConcurrentHashMap<>(); // map id -> field
    private final Instance instance;

    public InstanceFieldStorage(Instance instance) {
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }

    @Override
    public Optional<Field> getFieldById(int mapId) {
        return Optional.ofNullable(fieldMap.get(mapId));
    }

    public static InstanceFieldStorage from(Instance instance, List<MapInfo> mapInfos) {
        final InstanceFieldStorage fieldStorage = new InstanceFieldStorage(instance);
        for (MapInfo mapInfo : mapInfos) {
            fieldStorage.fieldMap.put(mapInfo.getMapId(), Field.from(fieldStorage, mapInfo));
        }
        return fieldStorage;
    }
}
