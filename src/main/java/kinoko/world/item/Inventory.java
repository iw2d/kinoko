package kinoko.world.item;

import java.util.SortedMap;
import java.util.TreeMap;

public final class Inventory {
    private final SortedMap<Integer, Item> items = new TreeMap<>();
    private int size;

    public Inventory(int size) {
        this.size = size;
    }

    public SortedMap<Integer, Item> getItems() {
        return items;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getRemaining() {
        return Math.max(size - items.size(), 0);
    }

    public Item getItem(int position) {
        return items.get(Math.abs(position));
    }

    public void putItem(int position, Item item) {
        if (item != null) {
            items.put(Math.abs(position), item);
        } else {
            items.remove(Math.abs(position));
        }
    }

    public void addItem(int position, Item item) {
        putItem(position, item);
    }

    public Item removeItem(int position) {
        return items.remove(Math.abs(position));
    }

    public boolean removeItem(int position, Item item) {
        return items.remove(Math.abs(position), item);
    }
}
