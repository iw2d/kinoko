package kinoko.world.item;

import lombok.Data;

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
}
