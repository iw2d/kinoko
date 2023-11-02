package kinoko.handler.stage;

import kinoko.handler.Handler;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.Client;
import kinoko.server.InHeader;
import kinoko.server.InPacket;
import kinoko.server.OutHeader;
import kinoko.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoginHandler {
    private static final Logger log = LogManager.getLogger(Handler.class);

    @Handler(InHeader.CHECK_PASSWORD)
    public static void handleCheckPassword(Client c, InPacket inPacket) {
        final String password = inPacket.decodeString();
        final String username = inPacket.decodeString();
        final byte[] machineId = inPacket.decodeArray(16);
        final int gameRoomClient = inPacket.decodeInt();
        final int gameStartMode = inPacket.decodeByte();
        final int worldId = inPacket.decodeByte();
        final int channelId = inPacket.decodeByte();
        final byte[] address = inPacket.decodeArray(4);
        c.write(LoginPacket.checkPasswordResult());
    }

    @Handler({ InHeader.WORLD_INFORMATION, InHeader.VIEW_WORLD_SELECT })
    public static void handleViewWorldSelect(Client c, InPacket inPacket) {
        c.write(LoginPacket.worldInformation());
        c.write(LoginPacket.worldInformationEnd());
    }

    @Handler(InHeader.CHECK_USER_LIMIT)
    public static void handlerCheckUserLimit(Client c, InPacket inPacket) {
        final int worldId = inPacket.decodeShort();
        c.write(LoginPacket.checkUserLimitResult());
    }

    @Handler(InHeader.SELECT_WORLD)
    public static void handleSelectWorld(Client c, InPacket inPacket) {
        final byte gameStartMode = inPacket.decodeByte();
        if (gameStartMode == 2) {
            final int worldId = inPacket.decodeByte();
            final int channelId = inPacket.decodeByte();
            inPacket.decodeInt(); // unk
            c.write(LoginPacket.selectWorldResult());
        }
    }

    @Handler(InHeader.CHECK_DUPLICATE_ID)
    public static void handleCheckDuplicateId(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        c.write(LoginPacket.checkDuplicatedIdResult(name));
    }

    @Handler(InHeader.NEW_CHAR)
    public static void handleNewChar(Client c, InPacket inPacket) {
        final String name = inPacket.decodeString();
        final int selectedRace = inPacket.decodeInt();
        final int selectedSubJob = inPacket.decodeShort();
        for (int i = 0; i < 8; i++) {
            inPacket.decodeInt(); // AL
        }
        final int gender = inPacket.decodeByte();
        c.write(LoginPacket.createNewCharacterResult());
    }

    @Handler(InHeader.ALIVE_ACK)
    public static void handleAliveAck(Client c, InPacket inPacket) {
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

    @Handler(InHeader.UPDATE_CLIENT_ENVIRONMENT)
    public static void handleUpdateClientEnvironment(Client c, InPacket inPacket) {
    }
}
