package kinoko.server.netty;

import kinoko.handler.ClientHandler;
import kinoko.handler.field.FieldHandler;
import kinoko.handler.field.MobHandler;
import kinoko.handler.field.NpcHandler;
import kinoko.handler.stage.CashShopHandler;
import kinoko.handler.stage.MigrationHandler;
import kinoko.handler.user.*;
import kinoko.handler.user.item.CashItemHandler;
import kinoko.handler.user.item.ItemHandler;
import kinoko.handler.user.item.UpgradeItemHandler;
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
            PartyHandler.class,
            GuildHandler.class,
            PetHandler.class,
            SummonedHandler.class,
            AttackHandler.class,
            SkillHandler.class,
            HitHandler.class,
            ItemHandler.class,
            CashItemHandler.class,
            UpgradeItemHandler.class
    );

    public ChannelPacketHandler() {
        super(channelPacketHandlerMap);
    }
}
