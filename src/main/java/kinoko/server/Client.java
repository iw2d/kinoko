package kinoko.server;

import io.netty.channel.socket.SocketChannel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.netty.NettyClient;
import kinoko.server.netty.NettyServer;
import kinoko.world.Account;
import kinoko.world.user.User;

public final class Client extends NettyClient {
    private Account account;
    private User user;
    private byte[] machineId;

    public Client(NettyServer nettyServer, SocketChannel nettyChannel) {
        super(nettyServer, nettyChannel);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
