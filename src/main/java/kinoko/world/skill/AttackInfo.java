package kinoko.world.skill;

import java.util.Arrays;

public final class AttackInfo {
    public int mobId;
    public byte hitAction;
    public byte actionAndDir;
    public byte attackCount; // only used for meso explosion

    public short hitX;
    public short hitY;

    public byte[] critical = new byte[15];
    public int[] damage = new int[15];

    @Override
    public String toString() {
        return "AttackInfo{" +
                "mobId=" + mobId +
                ", hitAction=" + hitAction +
                ", actionAndDir=" + actionAndDir +
                ", attackCount=" + attackCount +
                ", hitX=" + hitX +
                ", hitY=" + hitY +
                ", critical=" + Arrays.toString(critical) +
                ", damage=" + Arrays.toString(damage) +
                '}';
    }
}
