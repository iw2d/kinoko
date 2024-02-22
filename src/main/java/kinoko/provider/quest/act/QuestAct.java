package kinoko.provider.quest.act;

import kinoko.world.user.User;

public interface QuestAct {
    boolean canAct(User locked);

    boolean doAct(User locked);
}
