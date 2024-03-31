package kinoko.server.netty;

import kinoko.handler.ClientHandler;
import kinoko.handler.field.FieldHandler;
import kinoko.handler.field.MobHandler;
import kinoko.handler.field.NpcHandler;
import kinoko.handler.stage.CashShopHandler;
import kinoko.handler.stage.MigrationHandler;
import kinoko.handler.user.*;
import kinoko.server.header.InHeader;

import java.lang.reflect.Method;
import java.util.Map;

public final class ChannelPacketHandler extends PacketHandler {
    private static final Map<InHeader, Method> channelPacketHandlerMap = loadHandlers(
            ClientHandler.class,
            CashShopHandler.class,
            MigrationHandler.class,
            // Field
            FieldHandler.class,
            MobHandler.class,
            NpcHandler.class,
            // User
            UserHandler.class,
            AttackHandler.class,
            SkillHandler.class,
            ItemHandler.class,
            PetHandler.class,
            SummonedHandler.class
    );

    public ChannelPacketHandler() {
        super(channelPacketHandlerMap);
    }
}
