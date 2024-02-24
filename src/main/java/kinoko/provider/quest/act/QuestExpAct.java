package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.IncExpMessage;
import kinoko.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(User user) {
        return true;
    }

    @Override
    public boolean doAct(User user) {
        user.addExp(exp);
        user.write(WvsContext.message(IncExpMessage.quest(exp)));
        return true;
    }
}
