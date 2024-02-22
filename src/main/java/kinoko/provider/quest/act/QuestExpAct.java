package kinoko.provider.quest.act;

import kinoko.packet.user.UserLocal;
import kinoko.packet.user.effect.Effect;
import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

import java.util.Map;

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
        final Map<Stat, Object> addExpResult = user.getCharacterStat().addExp(exp);
        if (addExpResult.containsKey(Stat.LEVEL)) {
            user.write(UserLocal.effect(Effect.levelUp()));
        }
        user.write(WvsContext.statChanged(addExpResult));
        user.write(WvsContext.message(Message.incExp(exp, true, true)));
        return true;
    }
}
