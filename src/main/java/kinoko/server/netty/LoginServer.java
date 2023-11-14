package kinoko.server.netty;

import kinoko.handler.stage.LoginHandler;
import kinoko.server.ServerConstants;
import kinoko.server.header.InHeader;

import java.lang.reflect.Method;
import java.util.Map;

public final class LoginServer extends NettyServer {
    private static final Map<InHeader, Method> handlerMap = loadHandlers(LoginHandler.class);
    @Override
    public int getPort() {
        return ServerConstants.LOGIN_PORT;
    }

    @Override
    public Method getHandler(InHeader header) {
        return handlerMap.get(header);
    }
}
