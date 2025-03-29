package kinoko.provider.quest.act;

import kinoko.world.user.User;

public final class QuestInfoAct implements QuestAct {
    private final int questId;
    private final String info;

    public QuestInfoAct(int questId, String info) {
        this.questId = questId;
        this.info = info;
    }

    @Override
    public boolean canAct(User user, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
        user.getQuestManager().setQuestInfoEx(questId, info);
        return true;
    }
}
