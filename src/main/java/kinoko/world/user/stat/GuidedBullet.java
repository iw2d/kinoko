package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;

public final class GuidedBullet extends TwoStateTemporaryStat {
    private final int mobId;

    public GuidedBullet(int nOption, int rOption, int tOption, int mobId) {
        super(TwoStateType.NO_EXPIRE, nOption, rOption, tOption);
        this.mobId = mobId;
    }

    public int getMobId() {
        return mobId;
    }

    @Override
    public void encode(OutPacket outPacket) {
        super.encode(outPacket);
        outPacket.encodeInt(getMobId()); // dwMobID
    }
}
