package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.IncExpMessage;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    public QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(Locked<User> locked) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked) {
        final User user = locked.get();
        user.addExp(exp);
        user.write(WvsContext.message(IncExpMessage.quest(exp)));
        return true;
    }
}
