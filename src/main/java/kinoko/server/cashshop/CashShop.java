package kinoko.server.cashshop;

import kinoko.provider.EtcProvider;
import kinoko.provider.ProviderError;
import kinoko.server.ServerConfig;
import kinoko.server.packet.OutPacket;
import kinoko.util.Tuple;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public final class CashShop {
    public static final Path CASH_DATA = Path.of(ServerConfig.DATA_DIRECTORY, "cash");
    public static final int ADD_4_SLOTS_PRICE = 4000;
    public static final int ADD_STORAGE_SLOTS = 9110000;
    public static final int ADD_EQUIP_SLOTS = 9111000;
    public static final int ADD_USE_SLOTS = 9112000;
    public static final int ADD_SETUP_SLOTS = 9113000;
    public static final int ADD_ETC_SLOTS = 9114000;
    public static final int ADD_CHAR_SLOTS = 5430000;
    public static final int EQUIP_SLOT_EXT_30_DAYS = 5550000;
    public static final int EQUIP_SLOT_EXT_7_days = 5550001;

    private static Map<Integer, Commodity> commodities;
    private static Map<Integer, ModifiedCommodity> modifiedCommodities;

    public static void initialize() {
        modifiedCommodities = loadModifiedCommodities();
        final Map<Integer, Commodity> commodityMap = new HashMap<>();
        for (var entry : EtcProvider.getCommodities().entrySet()) {
            final int commodityId = entry.getKey();
            final Commodity commodity = entry.getValue();
            if (modifiedCommodities.containsKey(commodityId)) {
                commodityMap.put(commodityId, modifiedCommodities.get(commodityId).toCommodity());
            } else {
                commodityMap.put(commodityId, commodity);
            }
        }
        commodities = Collections.unmodifiableMap(commodityMap);
    }

    public static Optional<Commodity> getCommodity(int commodityId) {
        return Optional.ofNullable(commodities.get(commodityId));
    }

    public static Optional<Tuple<Commodity, List<Commodity>>> getCashPackage(int packageId) {
        // Resolve package commodity
        final Optional<Commodity> packageCommodityResult = getCommodity(packageId);
        if (packageCommodityResult.isEmpty()) {
            return Optional.empty();
        }
        final Commodity packageCommodity = packageCommodityResult.get();
        // Resolve package contents
        final List<Integer> packageContentIds = EtcProvider.getCashPackages().get(packageCommodity.getItemId());
        if (packageContentIds == null) {
            return Optional.empty();
        }
        final List<Commodity> packageContents = new ArrayList<>();
        for (int commodityId : packageContentIds) {
            final Optional<Commodity> commodityResult = getCommodity(commodityId);
            if (commodityResult.isEmpty()) {
                return Optional.empty();
            }
            packageContents.add(commodityResult.get());
        }
        return Optional.of(Tuple.of(packageCommodity, packageContents));
    }

    public static void encode(OutPacket outPacket) {
        // CWvsContext::SetSaleInfo
        outPacket.encodeInt(0); // nNotSaleCount, int * 4
        outPacket.encodeShort(modifiedCommodities.size());
        for (ModifiedCommodity modifiedCommodity : modifiedCommodities.values()) {
            outPacket.encodeInt(modifiedCommodity.getCommodityId()); // nSN
            modifiedCommodity.encode(outPacket); // CS_COMMODITY::DecodeModifiedData
        }
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

    private static Map<Integer, ModifiedCommodity> loadModifiedCommodities() {
        final Map<Integer, Commodity> commodityMap = EtcProvider.getCommodities();
        final Map<Integer, ModifiedCommodity> modifiedCommodityMap = new HashMap<>();
        final Load yamlLoader = new Load(LoadSettings.builder().build());
        try (final InputStream is = Files.newInputStream(Path.of(CASH_DATA.toString(), "modified.yaml"))) {
            if (!(yamlLoader.loadFromInputStream(is) instanceof Map<?, ?> modifiedData)) {
                throw new ProviderError("Could not resolve modified commodity data");
            }
            for (var entry : modifiedData.entrySet()) {
                final int commodityId = ((Number) entry.getKey()).intValue();
                if (!(entry.getValue() instanceof Map<?, ?> commodityData)) {
                    throw new ProviderError("Could not resolve modified commodity ID : %d", commodityId);
                }
                final Commodity commodity = commodityMap.get(commodityId);
                if (commodity == null) {
                    throw new ProviderError("Could not resolve commodity ID : %d", commodityId);
                }
                final Set<CommodityFlag> flags = new HashSet<>();
                int itemId = commodity.getItemId();
                int count = commodity.getCount();
                int price = commodity.getPrice();
                int period = commodity.getPeriod();
                int gender = commodity.getGender();
                boolean onSale = commodity.isOnSale();
                if (commodityData.get("itemId") instanceof Number itemIdValue) {
                    flags.add(CommodityFlag.ITEMID);
                    itemId = itemIdValue.intValue();
                }
                if (commodityData.get("count") instanceof Number countValue) {
                    flags.add(CommodityFlag.COUNT);
                    count = countValue.intValue();
                }
                if (commodityData.get("price") instanceof Number priceValue) {
                    flags.add(CommodityFlag.PRICE);
                    price = priceValue.intValue();
                }
                if (commodityData.get("period") instanceof Number periodValue) {
                    flags.add(CommodityFlag.PERIOD);
                    period = periodValue.intValue();
                }
                if (commodityData.get("gender") instanceof Number genderValue) {
                    flags.add(CommodityFlag.COMMODITYGENDER);
                    gender = genderValue.intValue();
                }
                if (commodityData.get("onSale") instanceof Boolean onSaleValue) {
                    flags.add(CommodityFlag.ONSALE);
                    onSale = onSaleValue;
                }
                modifiedCommodityMap.put(commodityId, new ModifiedCommodity(
                        commodityId,
                        Collections.unmodifiableSet(flags),
                        itemId,
                        count,
                        price,
                        period,
                        gender,
                        onSale,
                        List.of() // TODO modified packages
                ));
            }
        } catch (IOException e) {
            throw new ProviderError("Exception caught while loading CashShop Data", e);
        }
        return Collections.unmodifiableMap(modifiedCommodityMap);
    }
}
