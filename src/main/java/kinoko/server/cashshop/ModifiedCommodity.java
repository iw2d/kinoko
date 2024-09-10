package kinoko.server.cashshop;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.List;
import java.util.Set;

public final class ModifiedCommodity implements Encodable {
    private final int commodityId;
    private final Set<CommodityFlag> flags;
    private final int itemId;
    private final int count;
    private final int price;
    private final int period;
    private final int gender;
    private final boolean onSale;
    private final List<Integer> packageList;

    public ModifiedCommodity(int commodityId, Set<CommodityFlag> flags, int itemId, int count, int price, int period, int gender, boolean onSale, List<Integer> packageList) {
        this.commodityId = commodityId;
        this.flags = flags;
        this.itemId = itemId;
        this.count = count;
        this.price = price;
        this.period = period;
        this.gender = gender;
        this.onSale = onSale;
        this.packageList = packageList;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public Set<CommodityFlag> getFlags() {
        return flags;
    }

    public Commodity toCommodity() {
        return new Commodity(
                commodityId,
                itemId,
                count,
                price,
                period,
                gender,
                onSale
        );
    }

    @Override
    public void encode(OutPacket outPacket) {
        // CS_COMMODITY::DecodeModifiedData
        outPacket.encodeInt(CommodityFlag.from(flags));
        if (flags.contains(CommodityFlag.ITEMID)) {
            outPacket.encodeInt(itemId); // nItemId
        }
        if (flags.contains(CommodityFlag.COUNT)) {
            outPacket.encodeShort(count); // nCount
        }
        if (flags.contains(CommodityFlag.PRIORITY)) {
            outPacket.encodeByte(0); // nPriority
        }
        if (flags.contains(CommodityFlag.PRICE)) {
            outPacket.encodeInt(price); // nPrice
        }
        if (flags.contains(CommodityFlag.BONUS)) {
            outPacket.encodeByte(false); // bBonus
        }
        if (flags.contains(CommodityFlag.PERIOD)) {
            outPacket.encodeShort(period); // nPeriod
        }
        if (flags.contains(CommodityFlag.REQPOP)) {
            outPacket.encodeShort(0); // nReqPOP
        }
        if (flags.contains(CommodityFlag.REQLEV)) {
            outPacket.encodeShort(0); // nReqLEV
        }
        if (flags.contains(CommodityFlag.MAPLEPOINT)) {
            outPacket.encodeInt(0); // nMaplePoint
        }
        if (flags.contains(CommodityFlag.MESO)) {
            outPacket.encodeInt(0); // nMeso
        }
        if (flags.contains(CommodityFlag.FORPREMIUMUSER)) {
            outPacket.encodeByte(false); // bForPremiumUser
        }
        if (flags.contains(CommodityFlag.COMMODITYGENDER)) {
            outPacket.encodeByte(gender); // nCommodityGender
        }
        if (flags.contains(CommodityFlag.ONSALE)) {
            outPacket.encodeByte(onSale); // bOnSale
        }
        if (flags.contains(CommodityFlag.CLASS)) {
            outPacket.encodeByte(0); // nClass
        }
        if (flags.contains(CommodityFlag.LIMIT)) {
            outPacket.encodeByte(0); // nLimit
        }
        if (flags.contains(CommodityFlag.PBCASH)) {
            outPacket.encodeShort(0); // nPbCash
        }
        if (flags.contains(CommodityFlag.PBPOINT)) {
            outPacket.encodeShort(0); // nPbPoint
        }
        if (flags.contains(CommodityFlag.PBGIFT)) {
            outPacket.encodeShort(0); // nPbGift
        }
        if (flags.contains(CommodityFlag.PACKAGESN)) {
            // aPackageSN
            outPacket.encodeByte(packageList.size());
            for (int packageSn : packageList) {
                outPacket.encodeInt(packageSn);
            }
        }
    }
}
