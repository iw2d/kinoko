package kinoko.server.dialog.shop;

public final class ShopItem {
    private final int itemId;
    private final int price;
    private final int quantity;
    private final int maxPerSlot;

    private final int tokenItemId;
    private final int tokenPrice;
    private final double unitPrice;

    public ShopItem(int itemId, int price, int quantity, int maxPerSlot, int tokenItemId, int tokenPrice, double unitPrice) {
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.maxPerSlot = maxPerSlot;
        this.tokenItemId = tokenItemId;
        this.tokenPrice = tokenPrice;
        this.unitPrice = unitPrice;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxPerSlot() {
        return maxPerSlot;
    }

    public int getTokenItemId() {
        return tokenItemId;
    }

    public int getTokenPrice() {
        return tokenPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "itemId=" + itemId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", maxPerSlot=" + maxPerSlot +
                ", tokenItemId=" + tokenItemId +
                ", tokenPrice=" + tokenPrice +
                ", unitPrice=" + unitPrice +
                '}';
    }

    public static ShopItem from(int itemId, int price, int quantity, int maxPerSlot) {
        return new ShopItem(itemId, price, quantity, maxPerSlot, 0, 0, 0);
    }

    public static ShopItem rechargeable(int itemId, int maxPerSlot, double unitPrice) {
        return new ShopItem(itemId, 0, 0, maxPerSlot, 0, 0, unitPrice);
    }
}
