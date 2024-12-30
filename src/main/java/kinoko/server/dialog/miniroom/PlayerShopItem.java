package kinoko.server.dialog.miniroom;

import kinoko.world.item.Item;

public final class PlayerShopItem {
    private final Item item;
    private final int setCount;
    private final int setSize;
    private final int price;

    public PlayerShopItem(Item item, int setCount, int setSize, int price) {
        this.item = item;
        this.setCount = setCount;
        this.setSize = setSize;
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public int getSetCount() {
        return setCount;
    }

    public int getSetSize() {
        return setSize;
    }

    public int getPrice() {
        return price;
    }
}
