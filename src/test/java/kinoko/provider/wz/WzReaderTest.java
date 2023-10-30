package kinoko.provider.wz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class WzReaderTest {

    private void readWzFile(String fileName, byte[] iv, int version) {
        File file = Path.of("src", "test", "resources", "wz", fileName).toFile();
        try (WzReader reader = WzReader.build(file, new WzReaderConfig(iv, version))) {
            Assertions.assertDoesNotThrow(() -> reader.readPackage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTamingMob() {
        readWzFile("TamingMob_GMS_75.wz", WzConstants.WZ_GMS_IV, 75);
        readWzFile("TamingMob_GMS_87.wz", WzConstants.WZ_GMS_IV, 87);
        readWzFile("TamingMob_GMS_95.wz", WzConstants.WZ_GMS_IV, 95);
        readWzFile("TamingMob_GMS_146.wz", WzConstants.WZ_EMPTY_IV, 146);
        readWzFile("TamingMob_GMS_176.wz", WzConstants.WZ_EMPTY_IV, 176);
//        readWzFile("TamingMob_GMS_230.wz", GameConstants.WZ_EMPTY_IV, 230);

        readWzFile("TamingMob_SEA_135.wz", WzConstants.WZ_EMPTY_IV, 135);
        readWzFile("TamingMob_SEA_160.wz", WzConstants.WZ_EMPTY_IV, 160);
//        readWzFile("TamingMob_SEA_211.wz", GameConstants.WZ_EMPTY_IV, 211);
//        readWzFile("TamingMob_SEA_212.wz", GameConstants.WZ_EMPTY_IV, 212);
    }
}
