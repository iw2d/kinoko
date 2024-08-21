package kinoko.world.skill;

import kinoko.util.Locked;
import kinoko.world.field.mob.Mob;

import java.util.Arrays;

public final class AttackInfo {
    public int mobId;
    public byte hitAction;
    public byte actionAndDir;
    public byte attackCount; // only used for meso explosion

    public short hitX;
    public short hitY;
    public int delay;

    public byte[] critical = new byte[15];
    public int[] damage = new int[15];

    public long[] random;
    public Locked<Mob> lockedMob;

    @Override
    public String toString() {
        return "AttackInfo{" +
                "mobId=" + mobId +
                ", hitAction=" + hitAction +
                ", actionAndDir=" + actionAndDir +
                ", attackCount=" + attackCount +
                ", hitX=" + hitX +
                ", hitY=" + hitY +
                ", delay=" + delay +
                ", critical=" + Arrays.toString(critical) +
                ", damage=" + Arrays.toString(damage) +
                ", random=" + Arrays.toString(random) +
                ", lockedMob=" + lockedMob +
                '}';
    }
}
