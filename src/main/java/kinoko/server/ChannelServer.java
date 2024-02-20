package kinoko.server;

import kinoko.handler.ClientHandler;
import kinoko.handler.field.DropHandler;
import kinoko.handler.field.FieldHandler;
import kinoko.handler.field.LifeHandler;
import kinoko.handler.script.ScriptHandler;
import kinoko.handler.stage.MigrationHandler;
import kinoko.handler.user.*;
import kinoko.provider.MapProvider;
import kinoko.provider.map.MapInfo;
import kinoko.server.header.InHeader;
import kinoko.server.netty.NettyServer;
import kinoko.server.netty.PacketHandler;
import kinoko.world.field.Field;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ChannelServer extends NettyServer {
    private static final Map<InHeader, Method> handlerMap = PacketHandler.loadHandlers(
            ClientHandler.class,
            MigrationHandler.class,
            FieldHandler.class,
            LifeHandler.class,
            DropHandler.class,
            UserHandler.class,
            AttackHandler.class,
            SkillHandler.class,
            ItemHandler.class,
            InventoryHandler.class,
            ScriptHandler.class
    );
    private final int worldId;
    private final int channelId;
    private final int port;
    private final Map<Integer, Field> fields = new ConcurrentHashMap<>();

    public ChannelServer(int worldId, int channelId, int port) {
        this.worldId = worldId;
        this.channelId = channelId;
        this.port = port;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getChannelId() {
        return channelId;
    }

    public byte[] getAddress() {
        return ServerConstants.SERVER_ADDRESS;
    }

    public String getName() {
        return String.format("%s - %d", ServerConfig.WORLD_NAME, channelId + 1);
    }

    public synchronized Optional<Field> getFieldById(int mapId) {
        if (!fields.containsKey(mapId)) {
            final Optional<MapInfo> mapInfoResult = MapProvider.getMapInfo(mapId);
            if (mapInfoResult.isEmpty()) {
                return Optional.empty();
            }
            fields.put(mapId, Field.from(mapInfoResult.get()));
        }
        return Optional.of(fields.get(mapId));
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Method getHandler(InHeader header) {
        return handlerMap.get(header);
    }
}
