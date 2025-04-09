package kinoko.provider.quest.act;

import kinoko.packet.world.MessagePacket;
import kinoko.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(User user, int rewardIndex) {
        return true;
    }

    @Override
    public boolean doAct(User user, int rewardIndex) {
        user.addExp(exp);
        user.write(MessagePacket.incExp(exp, 0, true, true));
        return true;
    }
}
