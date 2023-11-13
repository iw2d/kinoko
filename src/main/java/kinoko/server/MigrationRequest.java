package kinoko.server;

import kinoko.world.ChannelServer;

import java.util.Arrays;

public record MigrationRequest(int accountId, int channelId, int characterId, byte[] machineId, byte[] remoteAddress) {

    public boolean looseMatch(MigrationRequest mr) {
        return this.accountId == mr.accountId || this.characterId == mr.characterId ||
                Arrays.equals(this.machineId, mr.machineId) || Arrays.equals(this.remoteAddress, mr.remoteAddress);
    }

    public boolean strictMatch(Client client, int characterId) {
        return this.characterId == characterId && Arrays.equals(this.machineId, client.getMachineId()) &&
                Arrays.equals(this.remoteAddress, client.getRemoteAddress());
    }
}
