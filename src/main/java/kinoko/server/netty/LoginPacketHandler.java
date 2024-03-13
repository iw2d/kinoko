package kinoko.server.netty;

import kinoko.handler.ClientHandler;
import kinoko.handler.stage.LoginHandler;
import kinoko.server.header.InHeader;

import java.lang.reflect.Method;
import java.util.Map;

public class LoginPacketHandler extends PacketHandler {
    private static final Map<InHeader, Method> loginPacketHandlerMap = loadHandlers(
            ClientHandler.class,
            LoginHandler.class
    );

    public LoginPacketHandler() {
        super(loginPacketHandlerMap);
    }
}
