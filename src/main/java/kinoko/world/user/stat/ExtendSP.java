package kinoko.world.user.stat;

import kinoko.server.packet.OutPacket;
import kinoko.world.Encodable;
import kinoko.world.job.JobConstants;

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

    public void addSp(short jobId, int incSp) {
        final int jobLevel = JobConstants.getJobLevel(jobId);
        while (list.size() < jobLevel) {
            list.add(0);
        }
        final int i = jobLevel - 1;
        list.set(i, list.get(i) + incSp);
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(list.size());
        for (int i = 0; i < list.size(); i++) {
            outPacket.encodeByte(i + 1); // nJobLevel
            outPacket.encodeByte(list.get(i)); // nSP
        }
    }

    public static ExtendSP from(List<Integer> list) {
        return new ExtendSP(list);
    }
}
