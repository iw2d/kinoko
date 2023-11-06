package kinoko.server.netty;

public final class LoginServer extends NettyServer {
    private final int port;

    public LoginServer(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }
}
