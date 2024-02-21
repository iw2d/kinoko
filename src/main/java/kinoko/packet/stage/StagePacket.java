package kinoko.packet.stage;

import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.util.Util;
import kinoko.world.user.User;

import java.time.Instant;

public final class StagePacket {
    // CStage::OnPacket ------------------------------------------------------------------------------------------------

    public static OutPacket setField(User user, int channelId, boolean isMigrate, boolean isRevive) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SET_FIELD);
        outPacket.encodeShort(0); // CClientOptMan::DecodeOpt
        outPacket.encodeInt(channelId); // nChannelID
        outPacket.encodeInt(0); // dwOldDriverID
        outPacket.encodeByte(user.getField().getFieldKey()); // bFieldKey
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
            outPacket.encodeInt(user.getCharacterStat().getPosMap());
            outPacket.encodeByte(user.getCharacterStat().getPortal());
            outPacket.encodeInt(user.getCharacterStat().getHp());
            outPacket.encodeByte(false); // bChaseEnable -> int, int
        }

        outPacket.encodeFT(Instant.now()); // ftServer
        return outPacket;
    }

    public static OutPacket setCashShop(User user) {
        final OutPacket outPacket = OutPacket.of(OutHeader.SET_CASHSHOP);
        user.getCharacterData().encode(outPacket);

        // CCashShop::LoadData
        outPacket.encodeByte(true); // bCashShopAuthorized
        outPacket.encodeString(user.getAccount().getUsername()); // sNexonClubID

        // CWvsContext::SetSaleInfo
        outPacket.encodeInt(0); // nNotSaleCount, int * 4
        outPacket.encodeShort(0); // short * (int, CS_COMMODITY::DecodeModifiedData)
        outPacket.encodeByte(0); // aaDiscountRate[9][30], byte * (byte, byte, byte)
        // ~CWvsContext::SetSaleInfo

        outPacket.encodeArray(new byte[0x438]); // this->aBest
        outPacket.encodeShort(0); // CCashShop::DecodeStock, short * 8
        outPacket.encodeShort(0); // CCashShop::DecodeLimitGoods, short * 104
        outPacket.encodeShort(0); // CCashShop::DecodeZeroGoods, short * 68
        // ~CCashShop::LoadData

        outPacket.encodeByte(false); // bEventOn
        outPacket.encodeInt(user.getCharacterStat().getLevel()); // nHighestCharacterLevelInThisAccount
        return outPacket;
    }
}
