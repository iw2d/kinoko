package kinoko.server.cashshop;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.item.ItemInfoType;
import kinoko.util.TimeUtil;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;
import kinoko.world.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class Commodity {
    private final int commodityId;
    private final int itemId;
    private final int count;
    private final int price;
    private final int period;
    private final int gender;
    private final boolean onSale;

    public Commodity(int commodityId, int itemId, int count, int price, int period, int gender, boolean onSale) {
        this.commodityId = commodityId;
        this.itemId = itemId;
        this.count = count;
        this.price = price;
        this.period = period;
        this.gender = gender;
        this.onSale = onSale;
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

    public boolean isOnSale() {
        return onSale;
    }

    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public Optional<CashItemInfo> createCashItemInfo(User user) {
        return createCashItemInfo(user.getNextItemSn(), user.getAccountId(), user.getCharacterId(), "");
    }

    public Optional<CashItemInfo> createCashItemInfo(long itemSn, int accountId, int characterId, String characterName) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(getItemId());
        if (itemInfoResult.isEmpty()) {
            return Optional.empty();
        }
        final ItemInfo ii = itemInfoResult.get();
        final Item item = ii.createItem(itemSn, getCount());
        if (item.getItemType() == ItemType.PET) {
            final int life = ii.getInfo(ItemInfoType.life);
            if (life > 0) {
                item.setDateExpire(TimeUtil.getCurrentTime().plus(life, ChronoUnit.DAYS));
            }
        } else {
            if (getPeriod() > 0) {
                item.setDateExpire(TimeUtil.getCurrentTime().plus(getPeriod(), ChronoUnit.DAYS));
            }
        }
        final CashItemInfo cashItemInfo = new CashItemInfo(
                item,
                getCommodityId(),
                accountId,
                characterId,
                characterName
        );
        return Optional.of(cashItemInfo);
    }

    public Optional<Gift> createGift(User user, String message) {
        return createGift(user.getNextItemSn(), user.getCharacterId(), user.getCharacterName(), message, 0);
    }

    public Optional<Gift> createGift(long itemSn, int characterId, String characterName, String message, long pairItemSn) {
        final Gift gift = new Gift(
                itemSn,
                getItemId(),
                getCommodityId(),
                characterId,
                characterName,
                message,
                pairItemSn
        );
        return Optional.of(gift);
    }
}
