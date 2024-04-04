package kinoko.packet;

import kinoko.server.netty.CentralPacketHeader;
import kinoko.server.node.MigrationInfo;
import kinoko.server.node.RemoteUser;
import kinoko.server.node.TransferInfo;
import kinoko.server.packet.OutPacket;
import kinoko.world.social.party.PartyRequest;
import kinoko.world.social.party.TownPortal;

import java.util.Set;

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

    public static OutPacket userConnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_CONNECT);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userUpdate(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_UPDATE);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userDisconnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_DISCONNECT);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userPacketRequest(String characterName, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_PACKET_REQUEST);
        outPacket.encodeString(characterName);
        final byte[] packetData = remotePacket.getData();
        outPacket.encodeInt(packetData.length);
        outPacket.encodeArray(packetData);
        return outPacket;
    }

    public static OutPacket userPacketReceive(int characterId, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_PACKET_RECEIVE);
        outPacket.encodeInt(characterId);
        final byte[] packetData = remotePacket.getData();
        outPacket.encodeInt(packetData.length);
        outPacket.encodeArray(packetData);
        return outPacket;
    }

    public static OutPacket userPacketBroadcast(Set<Integer> characterIds, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_PACKET_BROADCAST);
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
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_QUERY_REQUEST);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(characterNames.size());
        for (String characterName : characterNames) {
            outPacket.encodeString(characterName);
        }
        return outPacket;
    }

    public static OutPacket userQueryResult(int requestId, Set<RemoteUser> remoteUsers) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.USER_QUERY_RESULT);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(remoteUsers.size());
        for (RemoteUser remoteUser : remoteUsers) {
            remoteUser.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket partyRequest(int characterId, PartyRequest partyRequest) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.PARTY_REQUEST);
        outPacket.encodeInt(characterId);
        partyRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket partyUpdate(int partyId, RemoteUser remoteUser, TownPortal townPortal) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.PARTY_UPDATE);
        outPacket.encodeInt(partyId);
        outPacket.encodeByte(remoteUser != null);
        if (remoteUser != null) {
            remoteUser.encode(outPacket);
        }
        outPacket.encodeByte(townPortal != null);
        if (townPortal != null) {
            townPortal.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket partyResult(int characterId, int partyId) {
        final OutPacket outPacket = OutPacket.of(CentralPacketHeader.PARTY_RESULT);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(partyId);
        return outPacket;
    }
}
