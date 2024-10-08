package kinoko.provider.quest.act;

import kinoko.packet.user.QuestPacket;
import kinoko.provider.ItemProvider;
import kinoko.util.Locked;
import kinoko.world.user.User;

public final class QuestBuffAct implements QuestAct {
    private final int buffItemId;

    public QuestBuffAct(int buffItemId) {
        this.buffItemId = buffItemId;
    }

    @Override
    public boolean canAct(Locked<User> locked, int rewardIndex) {
        if (ItemProvider.getItemInfo(buffItemId).isEmpty()) {
            locked.get().write(QuestPacket.failedUnknown());
            return false;
        }
        return true;
    }

    @Override
    public boolean doAct(Locked<User> locked, int rewardIndex) {
        locked.get().setConsumeItemEffect(ItemProvider.getItemInfo(buffItemId).orElseThrow());
        return true;
    }
}
