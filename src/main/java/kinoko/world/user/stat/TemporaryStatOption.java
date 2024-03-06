package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;

public class TemporaryStatOption implements Encodable {
    public static final TemporaryStatOption EMPTY = new TemporaryStatOption();
    public int nOption;
    public int rOption;
    public int tOption;

    public Instant startTime = Instant.now();
    public DiceInfo diceInfo = DiceInfo.DEFAULT;
    public ScheduledFuture<?> statFuture;

    public Instant getExpireTime() {
        return startTime.plus(tOption, ChronoUnit.MILLIS);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeShort(nOption);
        outPacket.encodeInt(rOption);
        outPacket.encodeInt(tOption);
    }
}
