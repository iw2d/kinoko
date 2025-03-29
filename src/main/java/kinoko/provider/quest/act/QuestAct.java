package kinoko.provider.quest.act;

import kinoko.world.user.User;

public interface QuestAct {
    boolean canAct(User user, int rewardIndex);

    boolean doAct(User user, int rewardIndex);
}
