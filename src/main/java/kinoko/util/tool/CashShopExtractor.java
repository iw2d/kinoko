package kinoko.util.tool;

import kinoko.provider.EtcProvider;
import kinoko.provider.ItemProvider;
import kinoko.provider.StringProvider;
import kinoko.server.ServerConfig;
import kinoko.server.cashshop.Commodity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CashShopExtractor {
    public static final Path CASH_DATA = Path.of(ServerConfig.DATA_DIRECTORY, "cash");
    private static final List<Integer> disabledItems = List.of(
            5062000, // Miracle Cube
            5200000, // Bronze Sack of Mesos
            5200001, // Silver Sack of Mesos
            5200002, // Gold Sack of Mesos
            5360000, // 1 day 2x Drop Special Coupon
            5360001, // 2x Drop Card Type 1 - Everyday
            5360002, // 2x Drop Card Type 2 - Everyday
            5360003, // 2x Drop Card Type 3 - Everyday
            5360004, // 2x Drop Card Type 4 - Everyday
            5360005, // 2x Drop Card Type 5 - Everyday
            5360006, // 2x Drop Card Type 6 - Everyday
            5360007, // 2x Drop Card Type 7 - Everyday
            5360008, // 2x Drop Card Type 8 - Everyday
            5360042, // 4 hour 2 x Drop special coupon
            5400000, // Character Name Change
            5401000, // Character Transfer
            5490002, // Premium Gold Master Key
            5490003, // Premium Silver Master Key
            5660000, // Quest Deliverer Thomas
            5660001, // Quest Completer Alice
            9102259, // Slash Storm Mastery
            9102260, // Tornado Spin Mastery
            9102261, // Mirror Image Mastery
            9102262, // Flying Assaulter Mastery
            9102263, // Sudden Raid Mastery
            9102264, // Thorns Mastery
            9102289, // Magic Guard Package 1
            9102290, // Magic Guard Package 2
            9102291, // Magic Booster Package
            9102292, // Critical Magic Package
            9102293, // Critical Booster Package 1
            9102294, // Critical Booster Package 2
            9102374 // Special Discount Package 4
    );

    public static void main(String[] args) throws IOException {
        ItemProvider.initialize();
        EtcProvider.initialize();
        StringProvider.initialize();

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(CASH_DATA.toString(), "commodity.yaml"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (var entry : EtcProvider.getCommodities().entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                final int commodityId = entry.getKey();
                final Commodity commodity = entry.getValue();
                bw.write(String.format("# %s\n", StringProvider.getItemName(commodity.getItemId())));
                bw.write(String.format("%d:\n", commodityId));
                bw.write(String.format("  itemId: %d\n", commodity.getItemId()));
                bw.write(String.format("  count: %d\n", commodity.getCount()));
                bw.write(String.format("  price: %d\n", commodity.getPrice()));
                bw.write(String.format("  period: %d\n", commodity.getPeriod()));
                bw.write(String.format("  gender: %d\n", commodity.getGender()));
                bw.write(String.format("  onSale: %b\n", commodity.isOnSale()));

                final List<Integer> packageList = EtcProvider.getCashPackages().get(commodityId);
                if (packageList != null) {
                    bw.write("  package:\n");
                    for (int packageSn : packageList) {
                        bw.write(String.format("    - %d\n", packageSn));
                    }
                }
                bw.write("\n");
            }
        }

        final List<Commodity> modifiedCommodities = new ArrayList<>();
        for (var entry : EtcProvider.getCommodities().entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            final Commodity commodity = entry.getValue();
            if (!commodity.isOnSale()) {
                continue;
            }
            if (!disabledItems.contains(commodity.getItemId())) {
                continue;
            }
            modifiedCommodities.add(commodity);
        }

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(CASH_DATA.toString(), "modified.yaml"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Commodity commodity : modifiedCommodities) {
                bw.write(String.format("# %s\n", StringProvider.getItemName(commodity.getItemId())));
                bw.write(String.format("%d:\n", commodity.getCommodityId()));
                bw.write(String.format("  onSale: %b\n", false));
                bw.write("\n");
            }
        }
    }
}
