package kinoko.provider.quest.act;

import kinoko.util.Locked;
import kinoko.world.user.User;
import kinoko.world.user.stat.CharacterStat;

public final class QuestPopAct implements QuestAct {
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
        final CharacterStat cs = locked.get().getCharacterStat();
        cs.setPop((short) (cs.getPop() + pop));
        return true;
    }
}
