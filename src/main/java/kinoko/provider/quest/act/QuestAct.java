package kinoko.provider.quest.act;

import kinoko.util.Locked;
import kinoko.world.user.User;

public interface QuestAct {
    boolean canAct(Locked<User> locked, int rewardIndex);

    boolean doAct(Locked<User> locked, int rewardIndex);
}
