package kinoko.provider.quest.act;

import kinoko.world.user.User;

public interface QuestAct {
    boolean canAct(User user);

    void doAct(User user);
}
