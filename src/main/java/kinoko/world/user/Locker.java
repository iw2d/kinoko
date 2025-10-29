package kinoko.world.user;

import kinoko.database.DatabaseManager;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.GameConstants;
import kinoko.world.item.Item;

import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.List;

public final class Locker {
    private final List<CashItemInfo> cashItems = new ArrayList<>();

    public List<CashItemInfo> getCashItems() {
        return cashItems;
    }

    public void addCashItem(CashItemInfo cashItemInfo) {
        cashItems.add(cashItemInfo);
    }

    public int getRemaining() {
        return Math.max(GameConstants.LOCKER_SLOT_MAX - cashItems.size(), 0);
    }
}
