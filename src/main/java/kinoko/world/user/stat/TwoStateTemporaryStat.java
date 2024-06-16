package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TwoStateTemporaryStat extends TemporaryStatOption {
    public static final Map<CharacterTemporaryStat, TwoStateTemporaryStat> DEFAULT = CharacterTemporaryStat.TWO_STATE_ORDER.stream()
            .collect(Collectors.toMap(Function.identity(), (cts) -> TwoStateTemporaryStat.ofTwoState(cts, 0, 0, 0)));
    private final TwoStateType twoStateType;

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption) {
        super(nOption, rOption, tOption);
        this.twoStateType = twoStateType;
        assert twoStateType != TwoStateType.NOT_TWO_STATE;
    }

    public TwoStateTemporaryStat(TwoStateType twoStateType, int nOption, int rOption, int tOption, Instant expireTime) {
        super(nOption, rOption, tOption, expireTime);
        assert twoStateType != TwoStateType.NOT_TWO_STATE;
        this.twoStateType = twoStateType;
    }

    public final TwoStateType getType() {
        return twoStateType;
    }

    @Override
    public void encode(OutPacket outPacket) {
        // TemporaryStatBase<long>::DecodeForClient
        outPacket.encodeInt(nOption); // m_value
        outPacket.encodeInt(rOption); // m_reason
        outPacket.encodeByte(false);
        outPacket.encodeInt(0);
        if (twoStateType == TwoStateType.EXPIRE_BASED_ON_CURRENT_TIME) {
            outPacket.encodeByte(false);
            outPacket.encodeInt(0);
        }
        if (twoStateType != TwoStateType.NO_EXPIRE) {
            outPacket.encodeShort(tOption / 1000); // usExpireTerm
        }
    }
}
