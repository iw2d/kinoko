package kinoko.server.cashshop;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.world.item.Item;
import kinoko.world.user.User;

import java.util.Optional;

public final class Commodity {
    private final int commodityId;
    private final int itemId;
    private final int count;
    private final int price;
    private final int period;
    private final int gender;

    public Commodity(int commodityId, int itemId, int count, int price, int period, int gender) {
        this.commodityId = commodityId;
        this.itemId = itemId;
        this.count = count;
        this.price = price;
        this.period = period;
        this.gender = gender;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public int getPeriod() {
        return period;
    }

    public int getGender() {
        return gender;
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public Optional<CashItemInfo> createCashItemInfo(User user) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(getItemId());
        if (itemInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), getCount());
        final CashItemInfo cashItemInfo = new CashItemInfo(
                item,
                getCommodityId(),
                user.getAccountId(),
                user.getCharacterId(),
                ""
        );
        return Optional.of(cashItemInfo);
    }

    public Optional<Gift> createGift(User user, String message) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(getItemId());
        if (itemInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final Item item = itemInfoResult.get().createItem(user.getNextItemSn(), getCount());
        final Gift gift = new Gift(
                item,
                user.getCharacterName(),
                message
        );
        return Optional.of(gift);
    }
}
