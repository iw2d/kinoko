package kinoko.world.user;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;

import java.util.List;

public final class ExtendSP implements Encodable {
    private final List<Integer> list;

    public ExtendSP(List<Integer> list) {
        this.list = list;
    }

    public List<Integer> getList() {
        return list;
    }

    public int getTotal() {
        return list.stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(list.size());
        for (int jobLevel = 0; jobLevel < list.size(); jobLevel++) {
            outPacket.encodeByte(jobLevel);
            outPacket.encodeByte(list.get(jobLevel));
        }
    }

    public static ExtendSP from(List<Integer> list) {
        return new ExtendSP(list);
    }
}
