package kinoko.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class Rand32Test {
    @Test
    public void testRandom() {
        final Rand32 random = new Rand32();
        random.setSeed(0, 0, 0);
        Assertions.assertEquals(0, random.random());
        Assertions.assertEquals(0, random.getS1());
        Assertions.assertEquals(0, random.getS2());
        Assertions.assertEquals(0, random.getS3());

        random.setSeed(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);
        Assertions.assertEquals(0xFFE01F80, random.random());
        Assertions.assertEquals(0xFFFFE000, random.getS1());
        Assertions.assertEquals(0xFFFFFF80, random.getS2());
        Assertions.assertEquals(0xFFE00000, random.getS3());

        random.setSeed(0xCCCCCCCC, 0xCCCCCCCC, 0xCCCCCCCC);
        Assertions.assertEquals(0x99955300, random.random());
        Assertions.assertEquals(0xCCCCCAAA, random.getS1());
        Assertions.assertEquals(0xCCCCCCFF, random.getS2());
        Assertions.assertEquals(0x99955555, random.getS3());

        Assertions.assertEquals(0xAACCC2B3, random.random());
        Assertions.assertEquals(0xCCAAAAB3, random.getS1());
        Assertions.assertEquals(0xCCCCCFFF, random.getS2());
        Assertions.assertEquals(0xAAAAA7FF, random.getS3());

        Assertions.assertEquals(0x29983333, random.random());
        Assertions.assertEquals(0xAAAB333F, random.getS1());
        Assertions.assertEquals(0xCCCCFFFF, random.getS2());
        Assertions.assertEquals(0x4FFFFFF3, random.getS3());
    }
}
