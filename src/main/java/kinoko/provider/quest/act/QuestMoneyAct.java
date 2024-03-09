package kinoko.provider.quest.act;

import kinoko.packet.world.WvsContext;
import kinoko.packet.world.message.Message;
import kinoko.world.item.InventoryManager;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

public final class QuestMoneyAct implements QuestAct {
    private final int money;

    public QuestMoneyAct(int money) {
        this.money = money;
    }

    @Override
    public boolean canAct(User user) {
        final long newMoney = ((long) user.getInventoryManager().getMoney()) + money;
        return newMoney <= Integer.MAX_VALUE && newMoney >= 0;
    }

    @Override
    public boolean doAct(User user) {
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(money)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
        user.write(WvsContext.message(Message.incMoney(money)));
        return true;
    }
}
