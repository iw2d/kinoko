package kinoko.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class CRand32Test {
    @Test
    public void testRandom() {
        final CRand32 random = new CRand32();
        random.setSeed(-2049111696, 403646731, -802051995);
        Assertions.assertEquals(Integer.toUnsignedLong(11510719), random.random());

        random.setSeed(1793952185, -1464183224, -2059282854);
        Assertions.assertEquals(Integer.toUnsignedLong(-7662062), random.random());

        random.setSeed(-660890757, -1952095099, -1397364244);
        Assertions.assertEquals(Integer.toUnsignedLong(-801579400), random.random());
    }
}
