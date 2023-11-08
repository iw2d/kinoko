package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.ArrayList;
import java.util.List;

public final class ExtendSP implements Encodable {
    private final CharacterStat cs;
    private final List<Integer> spList;

    public ExtendSP(CharacterStat cs, List<Integer> spList) {
        this.cs = cs;
        this.spList = spList;
    }

    public Integer[] toArray() {
        final Integer[] array = new Integer[spList.size()];
        for (int i = 0; i < spList.size(); i++) {
            array[i] = spList.get(i);
        }
        return array;
    }

    @Override
    public void encode(OutPacket outPacket) {
        final short job = cs.getJob();
        if (job / 1000 == 3 || job / 100 == 22 || job == 2001) {
            outPacket.encodeByte(spList.size());
            for (int jobLevel = 0; jobLevel < spList.size(); jobLevel++) {
                outPacket.encodeShort(jobLevel);
                outPacket.encodeShort(spList.get(jobLevel));
            }
        } else {
            if (spList.isEmpty()) {
                outPacket.encodeShort(0);
            } else {
                outPacket.encodeShort(spList.get(0));
            }
        }
    }

    public static ExtendSP getDefault(CharacterStat cs) {
        final List<Integer> spList = new ArrayList<>();
        spList.add(0);
        return from(cs, spList);
    }

    public static ExtendSP from(CharacterStat cs, List<Integer> spList) {
        return new ExtendSP(cs, spList);
    }
}
