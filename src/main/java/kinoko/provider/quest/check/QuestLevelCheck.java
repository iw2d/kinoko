package kinoko.provider.quest.check;

import kinoko.util.Locked;
import kinoko.world.user.User;

public final class QuestLevelCheck implements QuestCheck {
    private final int level;
    private final boolean isMinimum;

    public QuestLevelCheck(int level, boolean isMinimum) {
        this.level = level;
        this.isMinimum = isMinimum;
    }

    @Override
    public boolean check(Locked<User> locked) {
        final User user = locked.get();
        if (isMinimum) {
            return user.getCharacterStat().getLevel() >= level;
        } else {
            return user.getCharacterStat().getLevel() <= level;
        }
    }
}
