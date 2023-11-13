package kinoko.server.netty;

import kinoko.server.ServerConstants;

public final class LoginServer extends NettyServer {
    @Override
    public int getPort() {
        return ServerConstants.LOGIN_PORT;
    }
}
