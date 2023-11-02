package kinoko.server;

import kinoko.handler.Dispatch;
import kinoko.server.crypto.MapleCrypto;
import kinoko.server.netty.LoginServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Server {
    private static final Logger log = LogManager.getRootLogger();

    public static void main(String[] args) {
        MapleCrypto.initialize();
        Dispatch.registerHandlers();
        new Thread(new LoginServer()).start();
    }
}
