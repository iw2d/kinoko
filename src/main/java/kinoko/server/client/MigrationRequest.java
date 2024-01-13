package kinoko.server.client;

import java.util.Arrays;

public final class MigrationRequest {
    private final int accountId;
    private final int channelId;
    private final int characterId;
    private final byte[] clientKey;
    private final byte[] machineId;
    private final byte[] remoteAddress;

    public MigrationRequest(int accountId, int channelId, int characterId, byte[] clientKey, byte[] machineId, byte[] remoteAddress) {
        this.accountId = accountId;
        this.channelId = channelId;
        this.characterId = characterId;
        this.clientKey = clientKey;
        this.machineId = machineId;
        this.remoteAddress = remoteAddress;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getCharacterId() {
        return characterId;
    }

    public byte[] getClientKey() {
        return clientKey;
    }

    public byte[] getMachineId() {
        return machineId;
    }

    public byte[] getRemoteAddress() {
        return remoteAddress;
    }

    public boolean strictMatch(Client client, int characterId) {
        return this.characterId == characterId &&
                Arrays.equals(this.clientKey, client.getClientKey()) &&
                Arrays.equals(this.machineId, client.getMachineId()) &&
                Arrays.equals(this.remoteAddress, client.getRemoteAddress());
    }
}
