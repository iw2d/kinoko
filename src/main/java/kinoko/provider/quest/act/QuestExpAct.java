package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.util.Locked;
import kinoko.world.user.Stat;
import kinoko.world.user.User;

import java.util.Map;
import java.util.Optional;

public final class QuestExpAct implements QuestAct {
    private final int exp;

    QuestExpAct(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean canAct(Locked<User> locked) {
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked) {
        final User user = locked.get();
        final Optional<Map<Stat, Object>> addExpResult = user.getCharacterStat().addExp(exp);
        if (addExpResult.isEmpty()) {
            return false;
        }
        user.write(WvsContext.message(Message.incExp(exp, true, true)));
        return true;
    }
}
