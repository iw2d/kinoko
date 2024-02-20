package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class QuestMoneyAct implements QuestAct {
    private final int money;

    public QuestMoneyAct(int money) {
        this.money = money;
    }

    @Override
    public boolean canAct(Locked<User> locked) {
        final User user = locked.get();
        final long newMoney = ((long) user.getInventoryManager().getMoney()) + money;
        return newMoney <= Integer.MAX_VALUE && newMoney >= 0;
    }

    @Override
    public boolean doAct(Locked<User> locked) {
        final User user = locked.get();
        if (!user.getInventoryManager().addMoney(money)) {
            return false;
        }
        user.write(WvsContext.message(Message.incMoney(money)));
        return true;
    }
}
