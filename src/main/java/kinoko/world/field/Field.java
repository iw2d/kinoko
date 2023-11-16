package kinoko.world.field;

import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.MapInfo;
import kinoko.world.life.Life;
import kinoko.world.user.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Field {
    private final MapInfo mapInfo;
    private final AtomicInteger lifeIdCounter = new AtomicInteger(1);
    private final Map<Integer, Life> lifes = new ConcurrentHashMap<>(); // lifeId -> Life
    private final Map<Integer, User> users = new ConcurrentHashMap<>(); // characterId -> User

    public Field(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
        // Initialize Life
        for (LifeInfo lifeInfo : mapInfo.life()) {
            final Optional<Life> lifeResult = Life.fromLifeInfo(lifeInfo);
            if (lifeResult.isEmpty()) {
                continue;
            }
            addLife(lifeResult.get());
        }
    }

    public int getFieldId() {
        return mapInfo.id();
    }

    public int getNewLifeId() {
        return lifeIdCounter.getAndIncrement();
    }

    public void addLife(Life life) {
        life.setLifeId(getNewLifeId());
        lifes.put(life.getLifeId(), life);
        for (User user : users.values()) {
            user.getClient().write(life.enterFieldPacket());
        }
    }

    public void removeLife(int lifeId) {
        final Life removed = lifes.remove(lifeId);
        if (removed == null) {
            return;
        }
        for (User user : users.values()) {
            user.getClient().write(removed.leaveFieldPacket());
        }
    }

    public void addUser(User user) {
        users.put(user.getCharacterId(), user);
        for (Life life : lifes.values()) {
            user.getClient().write(life.enterFieldPacket());
        }
    }

    public void removeUser(int characterId) {
        final User removed = users.remove(characterId);
        if (removed == null) {
            return;
        }
        for (User user : users.values()) {
            user.getClient().write(removed.leaveFieldPacket());
        }
    }
}
