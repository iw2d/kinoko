package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;

import java.time.Instant;

public class TwoStateTemporaryStat extends TemporaryStatOption {
    private final TwoStateType twoStateType;

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption) {
        super(nOption, rOption, tOption);
        this.twoStateType = twoStateType;
    }

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption, Instant expireTime) {
        super(nOption, rOption, tOption, expireTime);
        this.twoStateType = twoStateType;
    }


    public final TwoStateType getType() {
        return twoStateType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // TemporaryStatBase<long>::DecodeForClient
        outPacket.encodeInt(nOption); // value
        outPacket.encodeInt(rOption); // reason
        // tLastUpdated = `anonymous namespace'::DecodeTime
        outPacket.encodeByte(twoStateType != TwoStateType.NO_EXPIRE); // isDynamicTermSet
        outPacket.encodeInt(tOption);

        if (twoStateType == TwoStateType.EXPIRE_BASED_ON_CURRENT_TIME) {
            // tCurrentTime = `anonymous namespace'::DecodeTime
            outPacket.encodeByte(true);
            outPacket.encodeInt(tOption);
        }

        if (twoStateType != TwoStateType.NO_EXPIRE) {
            outPacket.encodeShort(tOption / 1000); // usExpireTerm
        }
    }
}
