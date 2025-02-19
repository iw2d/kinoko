package kinoko.server.dialog.miniroom;

import kinoko.world.item.Item;

public final class PlayerShopItem {
    private final Item item;
    private final int price;
    private final int setSize;


    public PlayerShopItem(Item item, int price, int setSize) {
        this.item = item;
        this.price = price;
        this.setSize = setSize;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public int getSetSize() {
        return setSize;
    }

    public int getSetCount() {
        return item.getQuantity() / setSize;
    }
}
