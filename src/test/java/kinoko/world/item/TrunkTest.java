package kinoko.world.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TrunkTest {
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
}
