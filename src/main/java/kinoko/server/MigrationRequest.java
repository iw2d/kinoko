package kinoko.server;

import kinoko.server.netty.ChannelServer;
import kinoko.world.Channel;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Arrays;

public record MigrationRequest(int accountId, int channelId, int characterId, byte[] machineId, byte[] remoteAddress) {

    public boolean looseMatch(MigrationRequest mr) {
        return this.accountId == mr.accountId || this.characterId == mr.characterId ||
                Arrays.equals(this.machineId, mr.machineId) || Arrays.equals(this.remoteAddress, mr.remoteAddress);
    }

    public boolean strictMatch(Client c, int characterId) {
        if (this.characterId != characterId || !Arrays.equals(this.machineId, c.getMachineId()) ||
                !Arrays.equals(this.remoteAddress, c.getRemoteAddress())) {
            return false;
        }
        // Check correct channel, this could be removed if migrations are refactored to ChannelServer
        if (!(c.getConnectedServer() instanceof final ChannelServer connectedServer)) {
            return false;
        }
        return this.channelId == connectedServer.getChannel().getChannelId();
    }
}
