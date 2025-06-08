package kinoko.world.item;

import kinoko.provider.ItemProvider;
import kinoko.provider.item.ItemInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestInstantiationException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public final class TrunkTest {
    public static final int RED_POTION = 2000000;
    public static final int ORANGE_POTION = 2000001;
    public static final int SUBI_THROWING_STARS = 2070000;
    private static final AtomicLong testItemSnCounter = new AtomicLong(1);

    @Test
    public void testAddItem() {
        final Trunk trunk = new Trunk(3);

        trunk.addItem(createItem(RED_POTION, 5));
        Assertions.assertNotNull(trunk.getItems().get(0));
        Assertions.assertEquals(RED_POTION, trunk.getItems().get(0).getItemId());
        Assertions.assertEquals(5, trunk.getItems().get(0).getQuantity());

        trunk.addItem(createItem(ORANGE_POTION, 5));
        Assertions.assertNotNull(trunk.getItems().get(1));
        Assertions.assertEquals(ORANGE_POTION, trunk.getItems().get(1).getItemId());
        Assertions.assertEquals(5, trunk.getItems().get(1).getQuantity());

        trunk.addItem(createItem(RED_POTION, 100));
        Assertions.assertEquals(100, trunk.getItems().get(0).getQuantity());
        Assertions.assertNotNull(trunk.getItems().get(2));
        Assertions.assertEquals(5, trunk.getItems().get(2).getQuantity());
    }

    @Test
    public void testCanAddItems() {
        final Trunk trunk = new Trunk(1);

        Assertions.assertTrue(trunk.canAddItem(createItem(RED_POTION, 5), 5));
        trunk.addItem(createItem(RED_POTION, 50));
        Assertions.assertTrue(trunk.canAddItem(createItem(RED_POTION, 50), 50));
        Assertions.assertTrue(trunk.canAddItem(createItem(RED_POTION, 100), 50));
        Assertions.assertFalse(trunk.canAddItem(createItem(RED_POTION, 100), 100));
    }

    @Test
    public void testRechargeableItems() {
        final Trunk trunk = new Trunk(2);

        Assertions.assertTrue(trunk.canAddItem(createItem(SUBI_THROWING_STARS, 400), 400));
        trunk.addItem(createItem(SUBI_THROWING_STARS, 400));

        Assertions.assertTrue(trunk.canAddItem(createItem(SUBI_THROWING_STARS, 80000), 80000));
        trunk.addItem(createItem(SUBI_THROWING_STARS, 80000));

        Assertions.assertFalse(trunk.canAddItem(createItem(SUBI_THROWING_STARS, 400), 400));
    }

    @Test
    public void testMoney() {
        final Trunk trunk = new Trunk(0);
        Assertions.assertEquals(0, trunk.getMoney());

        Assertions.assertTrue(trunk.canAddMoney(500));
        Assertions.assertTrue(trunk.addMoney(500));
        Assertions.assertEquals(500, trunk.getMoney());

        Assertions.assertTrue(trunk.canAddMoney(-200));
        Assertions.assertTrue(trunk.addMoney(-200));
        Assertions.assertEquals(300, trunk.getMoney());

        Assertions.assertFalse(trunk.canAddMoney(Integer.MAX_VALUE));
        Assertions.assertFalse(trunk.addMoney(Integer.MAX_VALUE));
        Assertions.assertEquals(300, trunk.getMoney());

        Assertions.assertFalse(trunk.canAddMoney(-500));
        Assertions.assertFalse(trunk.addMoney(-500));
        Assertions.assertEquals(300, trunk.getMoney());
    }

    @Test
    public void testRemaining() {
        final Trunk trunk = new Trunk(5);
        Assertions.assertEquals(5, trunk.getSize());
        Assertions.assertEquals(0, trunk.getItems().size());
        Assertions.assertEquals(5, trunk.getRemaining());

        trunk.getItems().add(new Item(ItemType.BUNDLE));
        Assertions.assertEquals(1, trunk.getItems().size());
        Assertions.assertEquals(4, trunk.getRemaining());

        for (int i = 0; i < 5; i++) {
            trunk.getItems().add(new Item(ItemType.BUNDLE));
        }
        Assertions.assertEquals(6, trunk.getItems().size());
        Assertions.assertEquals(0, trunk.getRemaining());
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
