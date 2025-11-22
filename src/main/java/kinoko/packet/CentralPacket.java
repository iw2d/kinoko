package kinoko.packet;

import kinoko.server.alliance.AllianceRequest;
import kinoko.server.guild.GuildBoardRequest;
import kinoko.server.guild.GuildRequest;
import kinoko.server.header.CentralHeader;
import kinoko.server.messenger.MessengerRequest;
import kinoko.server.migration.MigrationInfo;
import kinoko.server.migration.TransferInfo;
import kinoko.server.node.RemoteServerNode;
import kinoko.server.packet.OutPacket;
import kinoko.server.party.PartyRequest;
import kinoko.server.user.RemoteUser;
import kinoko.world.user.GuildInfo;
import kinoko.world.user.PartyInfo;

import java.util.List;

/**
 * See {@link CentralHeader} for more info.
 */
public final class CentralPacket {
    public static OutPacket initializeRequest() {
        return OutPacket.of(CentralHeader.InitializeRequest);
    }

    public static OutPacket initializeResult(int channelId, byte[] channelHost, int channelPort) {
        assert channelHost.length == 4;
        final OutPacket outPacket = OutPacket.of(CentralHeader.InitializeResult);
        outPacket.encodeInt(channelId);
        outPacket.encodeArray(channelHost);
        outPacket.encodeInt(channelPort);
        return outPacket;
    }

    public static OutPacket initializeComplete(List<RemoteServerNode> serverNodes) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.InitializeComplete);
        outPacket.encodeInt(serverNodes.size());
        for (RemoteServerNode serverNode : serverNodes) {
            outPacket.encodeInt(serverNode.getChannelId());
            outPacket.encodeInt(serverNode.getUserCount());
        }
        return outPacket;
    }

    public static OutPacket shutdownRequest() {
        return OutPacket.of(CentralHeader.ShutdownRequest);
    }

    public static OutPacket shutdownResult(int channelId, boolean success) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.ShutdownResult);
        outPacket.encodeInt(channelId);
        outPacket.encodeByte(success);
        return outPacket;
    }

    public static OutPacket onlineRequest(int requestId, int accountId) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.OnlineRequest);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(accountId);
        return outPacket;
    }

    public static OutPacket onlineResult(int requestId, boolean online) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.OnlineResult);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(online);
        return outPacket;
    }

    public static OutPacket migrateRequest(int requestId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MigrateRequest);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeArray(machineId);
        outPacket.encodeArray(clientKey);
        return outPacket;
    }

    public static OutPacket migrateResult(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MigrateResult);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(migrationInfo != null);
        if (migrationInfo != null) {
            migrationInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket transferRequest(int requestId, MigrationInfo migrationInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.TransferRequest);
        outPacket.encodeInt(requestId);
        migrationInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket transferResult(int requestId, TransferInfo transferInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.TransferResult);
        outPacket.encodeInt(requestId);
        outPacket.encodeByte(transferInfo != null);
        if (transferInfo != null) {
            transferInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket userConnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserConnect);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userUpdate(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserUpdate);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userDisconnect(RemoteUser remoteUser) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserDisconnect);
        remoteUser.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userPacketRequest(String characterName, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserPacketRequest);
        outPacket.encodeString(characterName);
        outPacket.encodeRemotePacket(remotePacket);
        return outPacket;
    }

    public static OutPacket userPacketReceive(int characterId, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserPacketReceive);
        outPacket.encodeInt(characterId);
        outPacket.encodeRemotePacket(remotePacket);
        return outPacket;
    }

    public static OutPacket userPacketBroadcast(List<Integer> characterIds, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserPacketBroadcast);
        outPacket.encodeInt(characterIds.size());
        for (int characterId : characterIds) {
            outPacket.encodeInt(characterId);
        }
        outPacket.encodeRemotePacket(remotePacket);
        return outPacket;
    }

    public static OutPacket userQueryRequest(int requestId, List<String> characterNames) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserQueryRequest);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(characterNames.size());
        for (String characterName : characterNames) {
            outPacket.encodeString(characterName);
        }
        return outPacket;
    }

    public static OutPacket userQueryRequestAll(int requestId) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserQueryRequest);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(-1);
        return outPacket;
    }

    public static OutPacket userQueryResult(int requestId, List<RemoteUser> remoteUsers) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.UserQueryResult);
        outPacket.encodeInt(requestId);
        outPacket.encodeInt(remoteUsers.size());
        for (RemoteUser remoteUser : remoteUsers) {
            remoteUser.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket worldSpeakerRequest(int characterId, boolean avatar, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.WorldSpeakerRequest);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(avatar);
        outPacket.encodeRemotePacket(remotePacket);
        return outPacket;
    }

    public static OutPacket serverPacketBroadcast(OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.ServerPacketBroadcast);
        outPacket.encodeRemotePacket(remotePacket);
        return outPacket;
    }

    public static OutPacket messengerRequest(int characterId, MessengerRequest messengerRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MessengerRequest);
        outPacket.encodeInt(characterId);
        messengerRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket messengerResult(int characterId, int messengerId) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.MessengerResult);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(messengerId);
        return outPacket;
    }

    public static OutPacket partyRequest(int characterId, PartyRequest partyRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.PartyRequest);
        outPacket.encodeInt(characterId);
        partyRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket partyResult(int characterId, PartyInfo partyInfo, OutPacket remotePacket) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.PartyResult);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(partyInfo != null);
        if (partyInfo != null) {
            partyInfo.encode(outPacket);
        }
        outPacket.encodeByte(remotePacket != null);
        if (remotePacket != null) {
            outPacket.encodeRemotePacket(remotePacket);
        }
        return outPacket;
    }

    public static OutPacket guildRequest(int characterId, GuildRequest guildRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.GuildRequest);
        outPacket.encodeInt(characterId);
        guildRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket guildResult(int characterId, GuildInfo guildInfo) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.GuildResult);
        outPacket.encodeInt(characterId);
        outPacket.encodeByte(guildInfo != null);
        if (guildInfo != null) {
            guildInfo.encode(outPacket);
        }
        return outPacket;
    }

    public static OutPacket allianceRequest(int characterId, AllianceRequest allianceRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.AllianceRequest);
        outPacket.encodeInt(characterId);
        allianceRequest.encode(outPacket);
        return outPacket;
    }

    public static OutPacket boardRequest(int characterId, GuildBoardRequest boardRequest) {
        final OutPacket outPacket = OutPacket.of(CentralHeader.BoardRequest);
        outPacket.encodeInt(characterId);
        boardRequest.encode(outPacket);
        return outPacket;
    }
}
