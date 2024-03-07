package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;

public final class GuidedBullet extends TwoStateTemporaryStat {
    private int mobId;

    public GuidedBullet(int nOption, int rOption, int tOption) {
        super(TwoStateType.NO_EXPIRE, nOption, rOption, tOption);
    }

    public int getMobId() {
        return mobId;
    }

    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    @Override
    public void encode(OutPacket outPacket) {
        super.encode(outPacket);
        outPacket.encodeInt(getMobId()); // dwMobID
    }
}
