package kinoko.world.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class MobStatOption implements Encodable {
    public static final MobStatOption EMPTY = new MobStatOption();
    public int nOption;
    public int rOption;
    public int tOption;

    public Instant startTime = Instant.now();

    public Instant getExpireTime() {
        return startTime.plus(tOption, ChronoUnit.MILLIS);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(nOption);
        outPacket.encodeInt(rOption);
        outPacket.encodeShort(tOption);
    }
}
