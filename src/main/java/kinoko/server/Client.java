package kinoko.server;

import io.netty.channel.Channel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.netty.NettyClient;
import kinoko.world.Account;

public final class Client extends NettyClient {
    private Account account;
    private byte[] machineId;

    public Client(Channel channel) {
        super(channel);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public byte[] getMachineId() {
        return machineId;
    }

    public void setMachineId(byte[] machineId) {
        this.machineId = machineId;
    }

    public void sendPing() {
        write(LoginPacket.aliveReq());
    }
}
