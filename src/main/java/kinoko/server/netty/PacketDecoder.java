package kinoko.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kinoko.server.ServerConstants;
import kinoko.server.node.Client;
import kinoko.server.node.ServerExecutor;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.NioBufferInPacket;
import kinoko.util.crypto.IGCipher;
import kinoko.util.crypto.MapleCrypto;
import kinoko.util.crypto.ShandaCrypto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public final class PacketDecoder extends ByteToMessageDecoder {
    public static final short RECV_VERSION = ServerConstants.GAME_VERSION;
    private static final Logger log = LogManager.getLogger(PacketDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (client == null) {
            return;
        }
        final byte[] iv = client.getRecvIv();
        if (client.getStoredLength() < 0) {
            if (in.readableBytes() < 4) {
                return;
            }
            final byte[] header = new byte[4];
            in.readBytes(header);

            final int version = ((header[0] ^ iv[2]) & 0xFF) | (((header[1] ^ iv[3]) << 8) & 0xFF00);
            if (version != RECV_VERSION) {
                log.warn("Incorrect packet seq, dropping client");
                ServerExecutor.submit(client, client::close);
                return;
            }
            final int length = ((header[0] ^ header[2]) & 0xFF) | (((header[1] ^ header[3]) << 8) & 0xFF00);
            client.setStoredLength(length);
        }
        if (in.readableBytes() >= client.getStoredLength()) {
            final byte[] data = new byte[client.getStoredLength()];
            in.readBytes(data);
            client.setStoredLength(-1);

            MapleCrypto.crypt(data, iv);
            ShandaCrypto.decrypt(data);
            client.setRecvIv(IGCipher.innoHash(iv));

            final InPacket inPacket = new NioBufferInPacket(data);
            out.add(inPacket);
        }
    }
}
