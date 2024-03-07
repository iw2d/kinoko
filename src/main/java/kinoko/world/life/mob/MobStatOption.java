package kinoko.world.life.mob;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class MobStatOption implements Encodable {
    public static final MobStatOption EMPTY = MobStatOption.of(0, 0, 0);
    public final int nOption;
    public final int rOption;
    public final int tOption;

    public final Instant expireTime;

    public MobStatOption(int nOption, int rOption, int tOption, Instant expireTime) {
        this.nOption = nOption;
        this.rOption = rOption;
        this.tOption = tOption;
        this.expireTime = expireTime;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(nOption);
        outPacket.encodeInt(rOption);
        outPacket.encodeShort(tOption / 500);
    }

    public static MobStatOption of(int nOption, int rOption, int tOption) {
        return new MobStatOption(nOption, rOption, tOption, Instant.now().plus(tOption, ChronoUnit.MILLIS));
    }
}
