package kinoko.world.item;

import lombok.Data;

import java.util.SortedMap;

@Data
public final class Inventory {
    private SortedMap<Integer, Item> items;
    private int size;
}
