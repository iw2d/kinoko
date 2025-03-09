package kinoko.world.field.mob;

import kinoko.util.Tuple;

import java.util.List;

public final class MobAttackInfo {
    public byte actionMask;
    public byte actionAndDir;
    public int targetInfo;
    public List<Tuple<Integer, Integer>> multiTargetForBall;
    public List<Integer> randTimeForAreaAttack;

    public boolean isAttack;
    public boolean isSkill;
}
