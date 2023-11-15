package kinoko.server;

import kinoko.handler.ClientHandler;
import kinoko.handler.stage.LoginHandler;
import kinoko.server.header.InHeader;
import kinoko.server.netty.NettyServer;

import java.lang.reflect.Method;
import java.util.Map;

public final class LoginServer extends NettyServer {
    private static final Map<InHeader, Method> handlerMap = loadHandlers(ClientHandler.class, LoginHandler.class);

    @Override
    public int getPort() {
        return ServerConstants.LOGIN_PORT;
    }

    @Override
    public Method getHandler(InHeader header) {
        return handlerMap.get(header);
    }
}
