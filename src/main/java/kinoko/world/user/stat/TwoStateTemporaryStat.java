package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;

import java.time.Instant;

public class TwoStateTemporaryStat extends TemporaryStatOption {
    private final TwoStateType twoStateType;
    private final Instant lastUpdated;

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption) {
        super(nOption, rOption, tOption);
        this.twoStateType = twoStateType;
        this.lastUpdated = Instant.now();
    }

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption, Instant expireTime) {
        super(nOption, rOption, tOption, expireTime);
        this.twoStateType = twoStateType;
        this.lastUpdated = Instant.now();
    }

    public final TwoStateType getType() {
        return twoStateType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // TemporaryStatBase<long>::DecodeForClient
        outPacket.encodeInt(nOption); // m_value
        outPacket.encodeInt(rOption); // m_reason
        encodeTime(outPacket, lastUpdated); // tLastUpdated

        if (twoStateType == TwoStateType.EXPIRE_BASED_ON_CURRENT_TIME) {
            // tCurrentTime
            outPacket.encodeByte(false);
            outPacket.encodeInt(0);
        }

        if (twoStateType != TwoStateType.NO_EXPIRE) {
            outPacket.encodeShort((int) (getRemainingMillis() / 1000)); // usExpireTerm
        }
    }

    private void encodeTime(OutPacket outPacket, Instant time) {
        // tLastUpdated = `anonymous namespace'::DecodeTime
        final Instant now = Instant.now();
        if (time.isAfter(now)) {
            outPacket.encodeByte(false);
            outPacket.encodeInt((int) (time.toEpochMilli() - now.toEpochMilli()));
        } else {
            outPacket.encodeByte(true);
            outPacket.encodeInt((int) (now.toEpochMilli() - time.toEpochMilli()));
        }
    }

    public static TwoStateTemporaryStat ofTwoState(CharacterTemporaryStat cts, int nOption, int rOption, int tOption) {
        switch (cts) {
            case RideVehicle -> {
                return new TwoStateTemporaryStat(TwoStateType.NO_EXPIRE, nOption, rOption, tOption);
            }
            case PartyBooster -> {
                return new TwoStateTemporaryStat(TwoStateType.EXPIRE_BASED_ON_CURRENT_TIME, nOption, rOption, tOption);
            }
            case GuidedBullet -> {
                return new GuidedBullet(nOption, rOption, 0, tOption);
            }
            default -> {
                return new TwoStateTemporaryStat(TwoStateType.EXPIRE_BASED_ON_LAST_UPDATED_TIME, nOption, rOption, tOption);
            }
        }
    }
}
