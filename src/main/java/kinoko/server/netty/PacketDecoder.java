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
    private byte[] iv;
    private int length = -1;


    public PacketDecoder(byte[] iv) {
        this.iv = iv;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        final Client client = (Client) ctx.channel().attr(NettyClient.CLIENT_KEY).get();
        if (client == null) {
            return;
        }
        if (this.length < 0) {
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
            this.length = ((header[0] ^ header[2]) & 0xFF) | (((header[1] ^ header[3]) << 8) & 0xFF00);
        }
        if (in.readableBytes() >= length) {
            final byte[] data = new byte[length];
            in.readBytes(data);
            length = -1;

            MapleCrypto.crypt(data, iv);
            ShandaCrypto.decrypt(data);
            this.iv = IGCipher.innoHash(iv);

            final InPacket inPacket = new NioBufferInPacket(data);
            out.add(inPacket);
        }
    }
}
