package kinoko.handler;

import kinoko.server.client.Client;
import kinoko.server.header.InHeader;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.InPacket;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.ALIVE_ACK)
    public static void handleAliveAck(Client c, InPacket inPacket) {
    }

    @Handler(InHeader.EXCEPTION_LOG)
    public static void handleExceptionLog(Client c, InPacket inPacket) {
        final String data = inPacket.decodeString();
        log.error("Exception log : {}", data);
    }

    @Handler(InHeader.CLIENT_ERROR)
    public static void handleClientError(Client c, InPacket inPacket) {
        final short callType = inPacket.decodeShort();
        final int errorType = inPacket.decodeInt();
        final int bufferSize = inPacket.decodeShort();
        inPacket.decodeInt(); // unk

        final short op = inPacket.decodeShort();
        log.error("[Error {}] {}({}) | {}", errorType, OutHeader.getByValue(op), Util.opToString(op), inPacket);
        c.close();
    }
}
