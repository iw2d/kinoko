package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.List;

public final class WildHunterInfo implements Encodable {
    private int ridingType;
    private List<Integer> capturedMobs;

    public int getRidingType() {
        return ridingType;
    }

    public void setRidingType(int ridingType) {
        this.ridingType = ridingType;
    }

    public List<Integer> getCapturedMobs() {
        return capturedMobs;
    }

    public void setCapturedMobs(List<Integer> capturedMobs) {
        this.capturedMobs = capturedMobs;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getRidingType() * 10); // nRidingType = byte / 10, nIdx = byte % 10
        for (int i = 0; i < 5; i++) {
            if (i < capturedMobs.size()) {
                outPacket.encodeInt(capturedMobs.get(i));
            } else {
                outPacket.encodeInt(0);
            }
        }
    }
}
