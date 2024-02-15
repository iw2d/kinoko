package kinoko.packet.stage;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.user.User;

import java.time.Instant;

public final class StagePacket {

    public static OutPacket setField(User user, int channelId, boolean isMigrate, boolean isRevive) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SET_FIELD);
        outPacket.encodeShort(0); // CClientOptMan::DecodeOpt
        outPacket.encodeInt(channelId); // nChannelID
        outPacket.encodeInt(0); // dwOldDriverID
        outPacket.encodeByte(true); // bFieldKey
        outPacket.encodeByte(isMigrate); // bCharacterData
        outPacket.encodeShort(0); // nNotifierCheck

        if (isMigrate) {
            // m_CalcDamage
            final int s1 = Util.getRandom().nextInt();
            final int s2 = Util.getRandom().nextInt();
            final int s3 = Util.getRandom().nextInt();
            outPacket.encodeInt(s1);
            outPacket.encodeInt(s2);
            outPacket.encodeInt(s3);

            // CharacterData::Decode
            user.getCharacterData().encode(outPacket);

            // CWvsContext::OnSetLogoutGiftConfig
            outPacket.encodeInt(0); // bPredictQuit
            outPacket.encodeArray(new byte[4 * 3]); // anLogoutGiftCommoditySN
        } else {
            outPacket.encodeByte(isRevive);
            outPacket.encodeInt(user.getCharacterData().getCharacterStat().getPosMap());
            outPacket.encodeByte(user.getCharacterData().getCharacterStat().getPortal());
            outPacket.encodeInt(user.getCharacterData().getCharacterStat().getHp());
            outPacket.encodeByte(false); // bChaseEnable -> int, int
        }

        outPacket.encodeFT(Instant.now()); // ftServer
        return outPacket;
    }
}
