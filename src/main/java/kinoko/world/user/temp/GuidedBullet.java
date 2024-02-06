package kinoko.world.user.temp;

import kinoko.server.packet.OutPacket;

public final class GuidedBullet extends TwoStateTemporaryStat {
    private int mobId;

    public GuidedBullet() {
        super(TwoStateType.NO_EXPIRE);
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
