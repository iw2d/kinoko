package kinoko.provider.quest.act;

import kinoko.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(User user) {
        return true;
    }

    @Override
    public void doAct(User user) {

    }
}
