package kinoko.provider.skill;

import kinoko.util.Rect;

import java.util.List;

public final class SummonInfo {
    private final Rect rect;
    private final List<Integer> summons;

    public SummonInfo(Rect rect, List<Integer> summons) {
        this.rect = rect;
        this.summons = summons;
    }

    public Rect getRect() {
        return rect;
    }

    public List<Integer> getSummons() {
        return summons;
    }
}
