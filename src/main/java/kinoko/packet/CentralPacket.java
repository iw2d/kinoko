package kinoko.packet;

import kinoko.server.netty.CentralPacketHeader;
import kinoko.server.node.MigrationInfo;
import kinoko.server.node.TransferInfo;
import kinoko.server.node.UserProxy;
import kinoko.server.packet.OutPacket;
import kinoko.server.whisper.WhisperFlag;

public final class CentralPacket {
    public static OutPacket initializeRequest() {
        return OutPacket.of(CentralPacketHeader.INITIALIZE_REQUEST);
    }

    public static OutPacket initializeResult(int channelId, byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.INITIALIZE_RESULT);
        outPacket.encodeInt(channelId);
        outPacket.encodeArray(channelHost);
        outPacket.encodeInt(channelPort);
        return outPacket;
    }

    public static OutPacket shutdownRequest() {
        return OutPacket.of(CentralPacketHeader.SHUTDOWN_REQUEST);
    }

    public static OutPacket shutdownResult(int channelId, boolean success) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.SHUTDOWN_RESULT);
        outPacket.encodeInt(channelId);
        outPacket.encodeByte(success);
        return outPacket;
    }

    public static OutPacket migrationRequest(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.MIGRATION_REQUEST);
        outPacket.encodeInt(requestId);
        migrationInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket migrationResult(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.MIGRATION_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(migrationInfo != null);
        if (migrationInfo != null) {
            migrationInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket transferRequest(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.TRANSFER_REQUEST);
        outPacket.encodeInt(requestId);
        migrationInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket transferResult(int requestId, TransferInfo transferInfo) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.TRANSFER_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(transferInfo != null);
        if (transferInfo != null) {
            transferInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket userConnect(UserProxy userProxy) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_CONNECT);
        userProxy.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userUpdate(UserProxy userProxy) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_UPDATE);
        userProxy.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userDisconnect(UserProxy userProxy) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_DISCONNECT);
        userProxy.encode(outPacket);
        return outPacket;
    }

    public static OutPacket whisperRequest(WhisperFlag flag, int requestId, String sourceCharacterName, String targetCharacterName, String message) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.WHISPER_REQUEST);
        outPacket.encodeInt(requestId);
        outPacket.encodeString(sourceCharacterName);
        outPacket.encodeString(targetCharacterName);
        outPacket.encodeByte(flag.getValue());
        if (flag == WhisperFlag.WHISPER) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

    public static OutPacket whisperResult(int requestId, UserProxy userProxy) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.WHISPER_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(userProxy != null);
        if (userProxy != null) {
            userProxy.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket whisperReceive(WhisperFlag flag, int targetCharacterId, int sourceChannelId, String sourceCharacterName, String message) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.WHISPER_RECEIVE);
        outPacket.encodeInt(targetCharacterId);
        outPacket.encodeInt(sourceChannelId);
        outPacket.encodeString(sourceCharacterName);
        outPacket.encodeByte(flag.getValue());
        if (flag == WhisperFlag.WHISPER) {
            outPacket.encodeString(message);
        }
        return outPacket;
    }

}
