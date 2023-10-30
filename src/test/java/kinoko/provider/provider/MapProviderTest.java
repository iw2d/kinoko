package kinoko.provider.provider;

import kinoko.provider.MapProvider;
import kinoko.provider.wz.WzConstants;
import kinoko.provider.map.MapInfo;
import kinoko.provider.wz.WzReader;
import kinoko.provider.wz.WzReaderConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public class MapProviderTest {

    @Test
    @Disabled
    public void readMapWz() {
        try (WzReader reader = WzReader.build("Map.wz", new WzReaderConfig(WzConstants.WZ_GMS_IV, 101))) {
            long start = System.currentTimeMillis();
            Map<Integer, MapInfo> mapInfos = MapProvider.resolveMapInfos(reader.readPackage());
            System.out.printf("%d ms%n", System.currentTimeMillis() - start);
            System.out.println(mapInfos.get(100000000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
