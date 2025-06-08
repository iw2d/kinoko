package kinoko.provider.wz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class WzReaderTest {

    private void readWzFile(String fileName, byte[] iv) {
        File file = Path.of("src", "test", "resources", "wz", fileName).toFile();
        WzCrypto.setCipher(WzCrypto.getCipher(iv));
        try (WzPackage source = WzPackage.from(file)) {
            Assertions.assertDoesNotThrow(() -> source.getItem("0001.img/info/speed"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTamingMob() {
//        readWzFile("TamingMob_GMS_75.wz", WzConstants.WZ_GMS_IV);
//        readWzFile("TamingMob_GMS_87.wz", WzConstants.WZ_GMS_IV);
        readWzFile("TamingMob_GMS_95.wz", WzConstants.WZ_GMS_IV);
//        readWzFile("TamingMob_GMS_146.wz", WzConstants.WZ_EMPTY_IV);
//        readWzFile("TamingMob_GMS_176.wz", WzConstants.WZ_EMPTY_IV);
//        readWzFile("TamingMob_GMS_230.wz", GameConstants.WZ_EMPTY_IV);

//        readWzFile("TamingMob_SEA_135.wz", WzConstants.WZ_EMPTY_IV);
//        readWzFile("TamingMob_SEA_160.wz", WzConstants.WZ_EMPTY_IV);
//        readWzFile("TamingMob_SEA_211.wz", GameConstants.WZ_EMPTY_IV);
//        readWzFile("TamingMob_SEA_212.wz", GameConstants.WZ_EMPTY_IV);
    }
}
