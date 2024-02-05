package kinoko.packet.user;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.life.MovePath;

public final class UserRemotePacket {
    public static OutPacket userMove(int characterId, MovePath movePath) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_CHAT);
        outPacket.encodeInt(characterId);
        movePath.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userEmotion(int characterId, int emotion, int duration, boolean isByItemOption) {
        final OutPacket outPacket = OutPacket.of(OutHeader.USER_EMOTION);
        outPacket.encodeInt(characterId);
        outPacket.encodeInt(emotion);
        outPacket.encodeInt(duration);
        outPacket.encodeByte(isByItemOption); // bEmotionByItemOption
        return outPacket;
    }
}
