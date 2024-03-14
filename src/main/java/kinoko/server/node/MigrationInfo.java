package kinoko.server.node;

import kinoko.server.ServerConfig;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public final class MigrationInfo implements Encodable {
    private final int channelId;
    private final int accountId;
    private final int characterId;
    private final byte[] machineId;
    private final byte[] clientKey;
    private final Instant expireTime;

    public MigrationInfo(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey, Instant expireTime) {
        assert machineId.length == 16 && clientKey.length == 8;
        this.channelId = channelId;
        this.accountId = accountId;
        this.characterId = characterId;
        this.machineId = machineId;
        this.clientKey = clientKey;
        this.expireTime = expireTime;
    }

    public MigrationInfo(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        this(channelId, accountId, characterId, machineId, clientKey, Instant.now().plus(ServerConfig.CENTRAL_REQUEST_TTL, ChronoUnit.SECONDS));
    }

    public int getChannelId() {
        return channelId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public byte[] getMachineId() {
        return machineId;
    }

    public byte[] getClientKey() {
        return clientKey;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expireTime);
    }

    public boolean verify(int channelId, int accountId, int characterId, byte[] machineId, byte[] clientKey) {
        return this.channelId == channelId && this.accountId == accountId && this.characterId == characterId &&
                Arrays.equals(this.machineId, machineId) && Arrays.equals(this.clientKey, clientKey);
    }

    @Override
    public String toString() {
        return "MigrationInfo{" +
                "channelId=" + channelId +
                ", accountId=" + accountId +
                ", characterId=" + characterId +
                ", machineId=" + Arrays.toString(machineId) +
                ", clientKey=" + Arrays.toString(clientKey) +
                ", expireTime=" + expireTime +
                '}';
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(channelId);
        outPacket.encodeInt(accountId);
        outPacket.encodeInt(characterId);
        outPacket.encodeArray(machineId);
        outPacket.encodeArray(clientKey);
        outPacket.encodeLong(expireTime.toEpochMilli());
    }

    public static MigrationInfo decode(InPacket inPacket) {
        final int channelId = inPacket.decodeInt();
        final int accountId = inPacket.decodeInt();
        final int characterId = inPacket.decodeInt();
        final byte[] machineId = inPacket.decodeArray(16);
        final byte[] clientKey = inPacket.decodeArray(8);
        final long expireTime = inPacket.decodeLong();
        return new MigrationInfo(channelId, accountId, characterId, machineId, clientKey, Instant.ofEpochMilli(expireTime));
    }
}
