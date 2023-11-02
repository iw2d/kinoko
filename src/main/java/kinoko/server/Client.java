package kinoko.server;

import io.netty.channel.Channel;
import kinoko.packet.stage.LoginPacket;
import kinoko.server.netty.NettyClient;

public final class Client extends NettyClient {
    public Client(Channel channel) {
        super(channel);
    }

    public void sendPing() {
        write(LoginPacket.aliveReq());
    }
}
