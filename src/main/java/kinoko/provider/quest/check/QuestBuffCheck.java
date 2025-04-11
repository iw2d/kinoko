package kinoko.provider.quest.check;

import kinoko.world.user.User;
import kinoko.world.user.stat.SecondaryStat;

public final class QuestBuffCheck implements QuestCheck {
    private final int buffItemId;
    private final boolean isExcept;

    public QuestBuffCheck(int buffItemId, boolean isExcept) {
        this.buffItemId = buffItemId;
        this.isExcept = isExcept;
    }

    @Override
    public boolean check(User user) {
        return isSetted(user.getSecondaryStat(), -buffItemId) ^ isExcept;
    }

    private static boolean isSetted(SecondaryStat secondaryStat, int rOption) {
        return secondaryStat.getTemporaryStats().values().stream()
                .anyMatch(option -> option.rOption == rOption);
    }
}
