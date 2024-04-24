package kinoko.packet;

import kinoko.server.header.CentralHeader;
import kinoko.server.node.MigrationInfo;
import kinoko.server.node.RemoteUser;
import kinoko.server.node.TransferInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.social.party.PartyRequest;

import java.util.Set;

public final class CentralPacket {
    public static OutPacket initializeRequest() {
        return OutPacket.of(CentralHeader.INITIALIZE_REQUEST);
    }

    public static OutPacket initializeResult(int channelId, byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(CentralHeader.INITIALIZE_RESULT);
        outPacket.encodeInt(channelId);
        outPacket.encodeArray(channelHost);
        outPacket.encodeInt(channelPort);
        return outPacket;
    }

    public static OutPacket shutdownRequest() {
        return OutPacket.of(CentralHeader.SHUTDOWN_REQUEST);
    }

    public static OutPacket shutdownResult(int channelId, boolean success) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.SHUTDOWN_RESULT);
        outPacket.encodeInt(channelId);
        outPacket.encodeByte(success);
        return outPacket;
    }

    public static OutPacket migrateRequest(int requestId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MIGRATE_REQUEST);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeArray(machineId);
        outPacket.encodeArray(clientKey);
        return outPacket;
    }

    public static OutPacket migrateResult(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MIGRATE_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(migrationInfo != null);
        if (migrationInfo != null) {
            migrationInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket transferRequest(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.TRANSFER_REQUEST);
        outPacket.encodeInt(requestId);
        migrationInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket transferResult(int requestId, TransferInfo transferInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.TRANSFER_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(transferInfo != null);
        if (transferInfo != null) {
            transferInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket userConnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_CONNECT);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userUpdate(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_UPDATE);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userDisconnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_DISCONNECT);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userPacketRequest(String characterName, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_PACKET_REQUEST);
        outPacket.encodeString(characterName);
        final byte[] packetData = remotePacket.getData();
        outPacket.encodeInt(packetData.length);
        outPacket.encodeArray(packetData);
        return outPacket;
    }

    public static OutPacket userPacketReceive(int characterId, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_PACKET_RECEIVE);
        outPacket.encodeInt(characterId);
        final byte[] packetData = remotePacket.getData();
        outPacket.encodeInt(packetData.length);
        outPacket.encodeArray(packetData);
        return outPacket;
    }

    public static OutPacket userPacketBroadcast(Set<Integer> characterIds, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_PACKET_BROADCAST);
        outPacket.encodeInt(characterIds.size());
        for (int characterId : characterIds) {
            outPacket.encodeInt(characterId);
        }
        final byte[] packetData = remotePacket.getData();
        outPacket.encodeInt(packetData.length);
        outPacket.encodeArray(packetData);
        return outPacket;
    }

    public static OutPacket userQueryRequest(int requestId, Set<String> characterNames) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_QUERY_REQUEST);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(characterNames.size());
        for (String characterName : characterNames) {
            outPacket.encodeString(characterName);
        }
        return outPacket;
    }

    public static OutPacket userQueryResult(int requestId, Set<RemoteUser> remoteUsers) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.USER_QUERY_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(remoteUsers.size());
        for (RemoteUser remoteUser : remoteUsers) {
            remoteUser.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket partyRequest(int characterId, PartyRequest partyRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.PARTY_REQUEST);
        outPacket.encodeInt(characterId);
        partyRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket partyResult(int characterId, int partyId, int partyMemberIndex) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.PARTY_RESULT);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(partyId);
        outPacket.encodeInt(partyMemberIndex);
        return outPacket;
    }
}
