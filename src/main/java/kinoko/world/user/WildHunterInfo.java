package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.skill.SkillConstants;

import java.util.ArrayList;
import java.util.List;

public final class WildHunterInfo implements Encodable {
    private final List<Integer> capturedMobs = new ArrayList<>();
    private int ridingType;

    public List<Integer> getCapturedMobs() {
        return capturedMobs;
    }

    public int getRidingType() {
        return ridingType;
    }

    public void setRidingType(int ridingType) {
        this.ridingType = ridingType;
    }

    public int getRidingItem() {
        return SkillConstants.WILD_HUNTER_JAGUARS.get(Math.clamp(ridingType - 1, 0, SkillConstants.WILD_HUNTER_JAGUARS.size()));
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
