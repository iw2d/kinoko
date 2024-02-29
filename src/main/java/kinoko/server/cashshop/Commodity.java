package kinoko.server.cashshop;

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
}
