package kinoko.world.field.summoned;

import kinoko.world.skill.AttackInfo;

import java.util.ArrayList;
import java.util.List;

public final class SummonedAttack {
    private final List<AttackInfo> attackInfo = new ArrayList<>();

    public byte actionAndDir;
    public byte mobCount;

    public short userX;
    public short userY;
    public short summonedX;
    public short summonedY;

    public List<AttackInfo> getAttackInfo() {
        return attackInfo;
    }
}
