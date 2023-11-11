package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.Random;

public final class CalcDamage implements Encodable {
    private final int seed1;
    private final int seed2;
    private final int seed3;

    public CalcDamage(int seed1, int seed2, int seed3) {
        this.seed1 = seed1;
        this.seed2 = seed2;
        this.seed3 = seed3;
    }


    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeInt(seed1);
        outPacket.encodeInt(seed2);
        outPacket.encodeInt(seed3);
    }

    public static CalcDamage using(Random random) {
        return new CalcDamage(random.nextInt(), random.nextInt(), random.nextInt());
    }
}
