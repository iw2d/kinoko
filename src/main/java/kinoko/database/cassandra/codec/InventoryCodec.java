package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import kinoko.database.cassandra.type.InventoryUDT;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;

import java.util.Map;

public final class InventoryCodec extends MappingCodec<UdtValue, Inventory> {
    public InventoryCodec(TypeCodec<UdtValue> innerCodec, GenericType<Inventory> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Override
    protected Inventory innerToOuter(UdtValue value) {
        if (value == null) {
            return null;
        }
        final int size = value.getInt(InventoryUDT.SIZE);
        final Inventory inventory = new Inventory(size);

        final Map<Integer, Item> items = value.getMap(InventoryUDT.ITEMS, Integer.class, Item.class);
        if (items != null) {
            for (var entry : items.entrySet()) {
                inventory.putItem(entry.getKey(), entry.getValue());
            }
        }

        return inventory;
    }

    @Override
    protected UdtValue outerToInner(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return getCqlType().newValue()
                .setMap(InventoryUDT.ITEMS, inventory.getItems(), Integer.class, Item.class)
                .setInt(InventoryUDT.SIZE, inventory.getSize());
    }
}