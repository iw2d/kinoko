package kinoko.provider.quest.act;

import kinoko.util.Locked;
import kinoko.world.user.User;

public final class QuestInfoAct implements QuestAct {
    private final int questId;
    private final String info;

    public QuestInfoAct(int questId, String info) {
        this.questId = questId;
        this.info = info;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        locked.get().getQuestManager().setQuestInfoEx(questId, info);
        return true;
    }
}
