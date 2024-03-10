package kinoko.server.client;

import io.netty.channel.socket.SocketChannel;
import kinoko.database.DatabaseManager;
import kinoko.packet.ClientPacket;
import kinoko.server.Server;
import kinoko.server.UserProxy;
import kinoko.server.netty.NettyClient;
import kinoko.server.netty.NettyServer;
import kinoko.world.Account;
import kinoko.world.user.User;

public final class Client extends NettyClient {
    private Account account;
    private User user;
    private byte[] machineId;
    private byte[] clientKey;

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

    public byte[] getClientKey() {
        return clientKey;
    }

    public void setClientKey(byte[] clientKey) {
        this.clientKey = clientKey;
    }

    public void sendPing() {
        write(ClientPacket.aliveReq());
    }

    @Override
    public synchronized void close() {
        super.close();
        getConnectedServer().getClientStorage().removeClient(this);
        if (user != null) {
            Server.getUserStorage().removeUser(UserProxy.from(null, user));
            try (var locked = user.acquire()) {
                user.logout();
                DatabaseManager.characterAccessor().saveCharacter(user.getCharacterData());
                user = null;
            }
        }
        if (account != null) {
            try (var lockedAccount = account.acquire()) {
                DatabaseManager.accountAccessor().saveAccount(account);
                account = null;
            }
        }
    }
}
