package kinoko.provider.quest.act;

import kinoko.packet.user.QuestPacket;
import kinoko.packet.world.MessagePacket;
import kinoko.packet.world.WvsContext;
import kinoko.util.Locked;
import kinoko.world.item.InventoryManager;
import kinoko.world.user.User;
import kinoko.world.user.stat.Stat;

public final class QuestMoneyAct implements QuestAct {
    private final int money;

    public QuestMoneyAct(int money) {
        this.money = money;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final long newMoney = ((long) user.getInventoryManager().getMoney()) + money;
        if (newMoney > Integer.MAX_VALUE || newMoney < 0) {
            user.write(QuestPacket.failedMeso());
            return false;
        }
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        final User user = locked.get();
        final InventoryManager im = user.getInventoryManager();
        if (!im.addMoney(money)) {
            return false;
        }
        user.write(WvsContext.statChanged(Stat.MONEY, im.getMoney(), false));
        user.write(MessagePacket.incMoney(money));
        return true;
    }
}
