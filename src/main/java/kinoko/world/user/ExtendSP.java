package kinoko.world.user;

import kinoko.server.packet.OutPacket;

import java.util.ArrayList;
import java.util.List;

public final class ExtendSP {
    private final List<Integer> spList;

    public ExtendSP(List<Integer> spList) {
        this.spList = spList;
    }

    public void encode(short job, OutPacket outPacket) {
        if (job / 1000 == 3 || job / 100 == 22 || job == 2001) {
            outPacket.encodeByte(spList.size());
            for (int jobLevel = 0; jobLevel < spList.size(); jobLevel++) {
                outPacket.encodeByte(jobLevel);
                outPacket.encodeByte(spList.get(jobLevel));
            }
        } else {
            if (spList == null || spList.isEmpty()) {
                outPacket.encodeShort(0);
            } else {
                outPacket.encodeShort(spList.get(0));
            }
        }
    }

    public List<Integer> getSpList() {
        return spList;
    }

    public static ExtendSP getDefault() {
        final List<Integer> spList = new ArrayList<>();
        spList.add(0);
        return from(spList);
    }

    public static ExtendSP from(List<Integer> spList) {
        return new ExtendSP(spList);
    }
}
