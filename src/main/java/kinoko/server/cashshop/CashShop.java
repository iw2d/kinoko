package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;


public final class CashShop {

    public static void initialize() {
    }

    public static void encode(OutPacket outPacket) {
        // CWvsContext::SetSaleInfo
        outPacket.encodeInt(0); // nNotSaleCount, int * 4
        outPacket.encodeShort(0); // short * (int, CS_COMMODITY::DecodeModifiedData)
        outPacket.encodeByte(0); // aaDiscountRate[9][30], byte * (byte, byte, byte)
        // ~CWvsContext::SetSaleInfo

        outPacket.encodeArray(new byte[1080]); // this->aBest (9 * 2 * 5 * CS_BEST (12))
        outPacket.encodeShort(0); // CCashShop::DecodeStock, short * 8
        outPacket.encodeShort(0); // CCashShop::DecodeLimitGoods, short * 104
        outPacket.encodeShort(0); // CCashShop::DecodeZeroGoods, short * 68
    }
}
