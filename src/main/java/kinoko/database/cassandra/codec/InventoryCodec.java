package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.InventoryUDT;
import kinoko.world.item.Inventory;
import kinoko.world.item.Item;

import java.util.Map;

public final class InventoryCodec extends MappingCodec<UdtValue, Inventory> {
    public InventoryCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<Inventory> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected Inventory innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final int size = value.getInt(InventoryUDT.SIZE);
        final Inventory inventory = new Inventory(size);

        final Map<Integer, Item> items = value.getMap(InventoryUDT.ITEMS, Integer.class, Item.class);
        items.forEach((bagIndex, item) -> {
            inventory.getItems().put(bagIndex, item);
        });

        return inventory;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return getCqlType().newValue()
                .setMap(InventoryUDT.ITEMS, inventory.getItems(), Integer.class, Item.class)
                .setInt(InventoryUDT.SIZE, inventory.getSize());
    }
}