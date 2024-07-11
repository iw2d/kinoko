package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestInstantiationException;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public final class InventoryManagerTest {
    public static final int RED_POTION = 2000000;
    public static final int ORANGE_POTION = 2000001;
    private static final AtomicLong testItemSnCounter = new AtomicLong(1);

    @Test
    public void testMoney() {
        final InventoryManager im = new InventoryManager();
        Assertions.assertEquals(0, im.getMoney());

        Assertions.assertTrue(im.canAddMoney(500));
        Assertions.assertTrue(im.addMoney(500));
        Assertions.assertEquals(500, im.getMoney());

        Assertions.assertTrue(im.canAddMoney(-200));
        Assertions.assertTrue(im.addMoney(-200));
        Assertions.assertEquals(300, im.getMoney());

        Assertions.assertFalse(im.canAddMoney(Integer.MAX_VALUE));
        Assertions.assertFalse(im.addMoney(Integer.MAX_VALUE));
        Assertions.assertEquals(300, im.getMoney());

        Assertions.assertFalse(im.canAddMoney(-500));
        Assertions.assertFalse(im.addMoney(-500));
        Assertions.assertEquals(300, im.getMoney());
    }

    @Test
    public void testItemCount() {
        final InventoryManager im = new InventoryManager();
        im.setConsumeInventory(new Inventory(5));

        Assertions.assertEquals(0, im.getItemCount(RED_POTION));

        im.getConsumeInventory().putItem(1, createItem(RED_POTION, 5));
        Assertions.assertEquals(5, im.getItemCount(RED_POTION));
        Assertions.assertTrue(im.hasItem(RED_POTION, 5));
        Assertions.assertFalse(im.hasItem(RED_POTION, 30));

        im.getConsumeInventory().putItem(2, createItem(ORANGE_POTION, 10));
        Assertions.assertEquals(5, im.getItemCount(RED_POTION));
        Assertions.assertEquals(10, im.getItemCount(ORANGE_POTION));

        im.getConsumeInventory().putItem(5, createItem(RED_POTION, 25));
        Assertions.assertEquals(30, im.getItemCount(RED_POTION));
    }

    @Test
    public void testRemoveItem() {
        final InventoryManager im = new InventoryManager();
        im.setConsumeInventory(new Inventory(5));
        im.getConsumeInventory().putItem(1, createItem(RED_POTION, 5));

        Assertions.assertEquals(5, im.getItemCount(RED_POTION));
        Assertions.assertTrue(im.removeItem(RED_POTION, 1).isPresent());
        Assertions.assertEquals(4, im.getItemCount(RED_POTION));

        Assertions.assertTrue(im.removeItem(RED_POTION, 4).isPresent());
        Assertions.assertEquals(0, im.getItemCount(RED_POTION));
        Assertions.assertNull(im.getConsumeInventory().getItem(1));
        Assertions.assertFalse(im.removeItem(RED_POTION, 1).isPresent());
        Assertions.assertFalse(im.removeItem(RED_POTION, -1).isPresent());

        im.getConsumeInventory().putItem(1, createItem(RED_POTION, 5));
        Assertions.assertTrue(im.removeItem(1, im.getConsumeInventory().getItem(1)).isPresent());
        Assertions.assertNull(im.getConsumeInventory().getItem(1));
    }

    @Test
    public void testAddItem() {
        final InventoryManager im = new InventoryManager();
        im.setConsumeInventory(new Inventory(3));

        Assertions.assertTrue(im.addItem(createItem(RED_POTION, 5)).isPresent());
        Assertions.assertNotNull(im.getConsumeInventory().getItem(1));
        Assertions.assertEquals(RED_POTION, im.getConsumeInventory().getItem(1).getItemId());
        Assertions.assertEquals(5, im.getConsumeInventory().getItem(1).getQuantity());

        Assertions.assertTrue(im.addItem(createItem(ORANGE_POTION, 5)).isPresent());
        Assertions.assertNotNull(im.getConsumeInventory().getItem(2));
        Assertions.assertEquals(ORANGE_POTION, im.getConsumeInventory().getItem(2).getItemId());
        Assertions.assertEquals(5, im.getConsumeInventory().getItem(2).getQuantity());

        Assertions.assertTrue(im.addItem(createItem(RED_POTION, 100)).isPresent());
        Assertions.assertEquals(100, im.getConsumeInventory().getItem(1).getQuantity());
        Assertions.assertNotNull(im.getConsumeInventory().getItem(3));
        Assertions.assertEquals(5, im.getConsumeInventory().getItem(3).getQuantity());

        Assertions.assertFalse(im.addItem(createItem(RED_POTION, 100)).isPresent());
    }

    @Test
    public void testCanAddItems() {
        final InventoryManager im = new InventoryManager();
        im.setConsumeInventory(new Inventory(5));

        Assertions.assertTrue(im.canAddItem(createItem(RED_POTION, 5)));
        Assertions.assertTrue(im.canAddItems(Set.of(createItem(RED_POTION, 5), createItem(ORANGE_POTION, 5))));
        Assertions.assertFalse(im.canAddItems(Set.of(createItem(RED_POTION, 100), createItem(ORANGE_POTION, 5000))));
        Assertions.assertFalse(im.canAddItems(Set.of(
                createItem(RED_POTION, 100),
                createItem(RED_POTION, 100),
                createItem(RED_POTION, 100),
                createItem(RED_POTION, 100),
                createItem(RED_POTION, 100),
                createItem(RED_POTION, 100)
        )));

        Assertions.assertTrue(im.addItem(createItem(RED_POTION, 50)).isPresent());
        Assertions.assertTrue(im.canAddItems(Set.of(
                createItem(ORANGE_POTION, 100),
                createItem(ORANGE_POTION, 100),
                createItem(ORANGE_POTION, 100),
                createItem(RED_POTION, 50),
                createItem(RED_POTION, 100)
        )));
    }

    @BeforeAll
    public static void setUp() {
        ItemProvider.initialize();
    }

    private static Item createItem(int itemId, int quantity) {
        final Optional<ItemInfo> itemInfoResult = ItemProvider.getItemInfo(itemId);
        if (itemInfoResult.isEmpty()) {
            throw new TestInstantiationException("Failed to create item");
        }
        return itemInfoResult.get().createItem(testItemSnCounter.getAndIncrement(), quantity);
    }
}
