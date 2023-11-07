package kinoko.world.job;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import lombok.Data;

import java.util.List;

@Data
public final class WildHunterInfo implements Encodable {
    private int ridingType;
    private List<Integer> capturedMobs;

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
