package kinoko.world.field;

import kinoko.provider.map.MapInfo;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Field {
    private final MapInfo mapInfo;
    private final Map<Integer, Life> lifes = new ConcurrentHashMap<>();
    private final Map<Integer, User> users = new ConcurrentHashMap<>();

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public int getFieldId() {
        return mapInfo.id();
    }

    public void addLife(Life life) {

    }

    public void addUser(User user) {

    }
}
