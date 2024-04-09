package kinoko.server.cashshop;

import kinoko.provider.EtcProvider;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public final class CashShop {
    public static final int ADD_4_SLOTS_PRICE = 4000;
    public static final int ADD_STORAGE_SLOTS = 9110000;
    public static final int ADD_EQUIP_SLOTS = 9111000;
    public static final int ADD_USE_SLOTS = 9112000;
    public static final int ADD_SETUP_SLOTS = 9113000;
    public static final int ADD_ETC_SLOTS = 9114000;
    public static final int ADD_CHAR_SLOTS = 5430000;
    public static final int EQUIP_SLOT_EXT_30_DAYS = 5550000;
    public static final int EQUIP_SLOT_EXT_7_days = 5550001;

    public static Optional<Commodity> getCommodity(int commodityId) {
        return Optional.ofNullable(EtcProvider.getCommodities().get(commodityId));
    }

    public static Optional<Tuple<Commodity, Set<Commodity>>> getCashPackage(int packageId) {
        // Resolve package commodity
        final Optional<Commodity> packageCommodityResult = getCommodity(packageId);
        if (packageCommodityResult.isEmpty()) {
            return Optional.empty();
        }
        final Commodity packageCommodity = packageCommodityResult.get();
        // Resolve package contents
        final Set<Integer> packageContentIds = EtcProvider.getCashPackages().get(packageCommodity.getItemId());
        if (packageContentIds == null) {
            return Optional.empty();
        }
        final Set<Commodity> packageContents = new HashSet<>();
        for (int commodityId : packageContentIds) {
            final Optional<Commodity> commodityResult = getCommodity(commodityId);
            if (commodityResult.isEmpty()) {
                return Optional.empty();
            }
            packageContents.add(commodityResult.get());
        }
        return Optional.of(new Tuple<>(packageCommodity, packageContents));
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
