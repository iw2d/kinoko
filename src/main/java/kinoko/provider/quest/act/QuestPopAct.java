package kinoko.provider.quest.act;

import kinoko.util.Locked;
import kinoko.world.user.User;

public class QuestPopAct implements QuestAct {
    private final int pop;

    public QuestPopAct(int pop) {
        this.pop = pop;
    }

    @Override
    public boolean canAct(Locked<User> locked) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked) {
        final User user = locked.get();
        user.getCharacterStat().setPop((short) (user.getCharacterStat().getPop() + pop));
        return true;
    }
}
