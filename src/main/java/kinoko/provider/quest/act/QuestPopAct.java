package kinoko.provider.quest.act;

import kinoko.packet.world.MessagePacket;
import kinoko.world.user.User;

public final class QuestPopAct implements QuestAct {
    private final int pop;

    public QuestPopAct(int pop) {
        this.pop = pop;
    }

    @Override
    public boolean canAct(User user, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
        user.addPop(pop);
        user.write(MessagePacket.incPop(pop));
        return true;
    }
}
