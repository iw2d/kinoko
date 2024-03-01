package kinoko.server.cashshop;

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
}
