package kinoko.provider.skill;

import java.util.List;

public final class SummonInfo {
    private final List<Integer> summons;

    public SummonInfo(List<Integer> summons) {
        this.summons = summons;
    }

    public List<Integer> getSummons() {
        return summons;
    }
}
