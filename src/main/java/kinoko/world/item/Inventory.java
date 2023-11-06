package kinoko.world.item;

import java.util.SortedMap;

public final class Inventory {
    private SortedMap<Integer, Item> items;
    private int size;

    public int getSize() {
        return size;
    }
}
