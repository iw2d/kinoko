package kinoko.provider.quest.act;

import kinoko.packet.world.MessagePacket;
import kinoko.util.Locked;
import kinoko.world.quest.QuestRecord;
import kinoko.world.user.User;

public class QuestInfoAct implements QuestAct {
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
        final User user = locked.get();
        final QuestRecord qr = user.getQuestManager().setQuestInfoEx(questId, info);
        user.write(MessagePacket.questRecord(qr));
        return true;
    }
}
