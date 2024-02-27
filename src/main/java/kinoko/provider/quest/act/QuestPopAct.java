package kinoko.provider.quest.act;

import kinoko.world.user.User;

public final class QuestPopAct implements QuestAct {
    private final int pop;

    public QuestPopAct(int pop) {
        this.pop = pop;
    }

    @Override
    public boolean canAct(User user) {
        return true;
    }

    @Override
    public boolean doAct(User user) {
        user.getCharacterStat().setPop((short) (user.getCharacterStat().getPop() + pop));
        return true;
    }
}
