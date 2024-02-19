package kinoko.world.user;

import kinoko.server.packet.OutPacket;

import java.util.List;

public final class ExtendSP {
    private final List<Integer> spList;

    public ExtendSP(List<Integer> spList) {
        this.spList = spList;
    }

    public List<Integer> getSpList() {
        return spList;
    }

    public void encode(short job, OutPacket outPacket) {
        if (job / 1000 == 3 || job / 100 == 22 || job == 2001) {
            outPacket.encodeByte(spList.size());
            for (int jobLevel = 0; jobLevel < spList.size(); jobLevel++) {
                outPacket.encodeByte(jobLevel);
                outPacket.encodeByte(spList.get(jobLevel));
            }
        } else {
            int totalSp = 0;
            for (int sp : spList) {
                totalSp += sp;
            }
            outPacket.encodeShort(totalSp);
        }
    }

    public static ExtendSP from(List<Integer> spList) {
        return new ExtendSP(spList);
    }
}
