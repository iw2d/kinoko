package kinoko.world.field;

import kinoko.provider.NpcProvider;
import kinoko.provider.map.LifeInfo;
import kinoko.provider.map.MapInfo;
import kinoko.provider.npc.NpcInfo;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.life.Life;
import kinoko.world.life.Npc;
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
    }

    public int getFieldId() {
        return mapInfo.id();
    }

    public int getNewLifeId() {
        return lifeIdCounter.getAndIncrement();
    }

    public Optional<Life> getLifeById(int lifeId) {
        if (!lifes.containsKey(lifeId)) {
            return Optional.empty();
        }
        return Optional.of(lifes.get(lifeId));
    }

    public void addLife(Life life) {
        life.setLifeId(getNewLifeId());
        lifes.put(life.getLifeId(), life);
        broadcastPacket(life.enterFieldPacket());
        // Handle controller
        if (!(life instanceof ControlledObject controlledLife)) {
            return;
        }
        final Optional<User> controllerResult = Util.getRandomFromCollection(users.values());
        if (controllerResult.isEmpty()) {
            return;
        }
        final User controller = controllerResult.get();
        controlledLife.setController(controller);
        controller.getClient().write(controlledLife.changeControllerPacket(true));
        broadcastPacket(controlledLife.changeControllerPacket(false), controller);
    }

    public void removeLife(int lifeId) {
        final Life removed = lifes.remove(lifeId);
        if (removed == null) {
            return;
        }
        broadcastPacket(removed.leaveFieldPacket());
    }

    public void addUser(User user) {
        user.setField(this);
        users.put(user.getCharacterId(), user);
        // Set controller
        for (Life life : lifes.values()) {
            user.getClient().write(life.enterFieldPacket());
            if (!(life instanceof ControlledObject controlledLife)) {
                continue;
            }
            if (controlledLife.getController() == null) {
                controlledLife.setController(user);
                user.getClient().write(controlledLife.changeControllerPacket(true));
            } else {
                user.getClient().write(controlledLife.changeControllerPacket(false));
            }
        }
    }

    public void removeUser(int characterId) {
        final User removed = users.remove(characterId);
        if (removed == null) {
            return;
        }
        broadcastPacket(removed.leaveFieldPacket());
        // Handle controller change
        final Optional<User> nextControllerResult = Util.getRandomFromCollection(users.values());
        for (Life life : lifes.values()) {
            if (!(life instanceof ControlledObject controlledLife)) {
                continue;
            }
            if (controlledLife.getController() != removed) {
                continue;
            }
            if (nextControllerResult.isEmpty()) {
                controlledLife.setController(null);
                continue;
            }
            final User nextController = nextControllerResult.get();
            controlledLife.setController(nextController);
            nextController.getClient().write(controlledLife.changeControllerPacket(true));
            broadcastPacket(controlledLife.changeControllerPacket(false), nextController);
        }
    }

    public void broadcastPacket(OutPacket outPacket) {
        broadcastPacket(outPacket, null);
    }

    public void broadcastPacket(OutPacket outPacket, User except) {
        for (User user : users.values()) {
            if (except != null && user.getCharacterId() == except.getCharacterId()) {
                continue;
            }
            user.getClient().write(outPacket);
        }
    }

    public static Field from(MapInfo mapInfo) {
        final Field field = new Field(mapInfo);
        for (LifeInfo lifeInfo : mapInfo.life()) {
            switch (lifeInfo.type()) {
                case NPC -> {
                    final Optional<NpcInfo> npcInfoResult = NpcProvider.getNpcInfo(lifeInfo.id());
                    final Npc npc = new Npc(field, lifeInfo, npcInfoResult.orElse(null));
                    field.addLife(npc);
                }
                case MOB -> {
                    // TODO
                }
            }
        }
        return field;
    }
}
