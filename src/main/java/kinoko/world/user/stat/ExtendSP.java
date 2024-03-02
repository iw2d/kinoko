package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;

import java.util.Map;

public final class ExtendSp implements Encodable {
    public static final int DEFAULT_JOB_LEVEL = 0; // for non-ExtendSp jobs
    private final Map<Integer, Integer> map; // nJobLevel -> nSP

    public ExtendSp(Map<Integer, Integer> map) {
        this.map = map;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public int getNonExtendSp() {
        return map.getOrDefault(DEFAULT_JOB_LEVEL, 0);
    }

    public void addNonExtendSp(int sp) {
        addSp(DEFAULT_JOB_LEVEL, sp);
    }

    public void addSp(int jobLevel, int sp) {
        map.put(jobLevel, map.getOrDefault(jobLevel, 0) + sp);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(map.size());
        for (var entry : map.entrySet()) {
            outPacket.encodeByte(entry.getKey()); // nJobLevel
            outPacket.encodeByte(entry.getValue()); // nSP
        }
    }

    public static ExtendSp from(Map<Integer, Integer> map) {
        return new ExtendSp(map);
    }
}
