package kinoko.server.cashshop;

import kinoko.provider.EtcProvider;
import kinoko.server.packet.OutPacket;

import java.util.Optional;


public final class CashShop {

    public static void initialize() {
    }

    public static Optional<Commodity> getCommodity(int commodityId) {
        return Optional.ofNullable(EtcProvider.getCommodities().get(commodityId));
    }

    public static void encode(OutPacket outPacket) {
        // CWvsContext::SetSaleInfo
        outPacket.encodeInt(0); // nNotSaleCount, int * 4
        outPacket.encodeShort(0); // short * (int, CS_COMMODITY::DecodeModifiedData)
        outPacket.encodeByte(0); // aaDiscountRate[9][30], byte * (byte, byte, byte)
        // ~CWvsContext::SetSaleInfo

        // this->aBest
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 5; k++) {
                    // CS_BEST struct
                    outPacket.encodeInt(i); // nCategory
                    outPacket.encodeInt(j); // nGender
                    outPacket.encodeInt(0); // nCommoditySN
                }
            }
        }
        outPacket.encodeShort(0); // CCashShop::DecodeStock, short * 8
        outPacket.encodeShort(0); // CCashShop::DecodeLimitGoods, short * 104
        outPacket.encodeShort(0); // CCashShop::DecodeZeroGoods, short * 68
    }
}
