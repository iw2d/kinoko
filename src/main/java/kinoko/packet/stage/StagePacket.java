package kinoko.packet.stage;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.User;

import java.time.Instant;

public final class StagePacket {

    public static OutPacket setField(User user, int channelId, boolean isMigrate, boolean isRevive) {
        OutPacket outPacket = OutPacket.of(OutHeader.SET_FIELD);
        outPacket.encodeShort(0); // CClientOptMan::DecodeOpt
        outPacket.encodeInt(channelId); // nChannelID
        outPacket.encodeInt(0); // dwOldDriverID
        outPacket.encodeByte(true); // bFieldKey
        outPacket.encodeByte(isMigrate); // bCharacterData
        outPacket.encodeShort(0); // nNotifierCheck

        if (isMigrate) {
            user.getCalcDamage().encode(outPacket); // m_CalcDamage
            user.getCharacterData().encode(outPacket); // CharacterData::Decode

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
