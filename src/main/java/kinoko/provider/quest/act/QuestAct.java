package kinoko.provider.quest.act;

import kinoko.util.Locked;
import kinoko.world.user.User;

public interface QuestAct {
    boolean canAct(Locked<User> locked);

    boolean doAct(Locked<User> locked);
}
