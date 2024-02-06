package kinoko.world.user.temp;

import kinoko.server.packet.OutPacket;

public class TwoStateTemporaryStat extends Option {
    private final TwoStateType twoStateType;

    public TwoStateTemporaryStat(TwoStateType twoStateType) {
        this.twoStateType = twoStateType;
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
