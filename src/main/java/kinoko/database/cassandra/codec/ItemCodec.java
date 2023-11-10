package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.type.ItemUDT;
import kinoko.world.item.EquipData;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;
import kinoko.world.item.PetData;

public final class ItemCodec extends MappingCodec<UdtValue, Item> {
    public ItemCodec(@NonNull TypeCodec<UdtValue> innerCodec, @NonNull GenericType<Item> outerJavaType) {
        super(innerCodec, outerJavaType);
    }

    @NonNull
    @Override
    public UserDefinedType getCqlType() {
        return (UserDefinedType) super.getCqlType();
    }

    @Nullable
    @Override
    protected Item innerToOuter(@Nullable UdtValue value) {
        if (value == null) {
            return null;
        }
        final ItemType itemType = ItemType.getByValue(value.getInt(ItemUDT.ITEM_TYPE));
        if (itemType == null) {
            return null;
        }
        final Item item = new Item(itemType);
        item.setItemSn(value.getLong(ItemUDT.ITEM_SN));
        item.setItemId(value.getInt(ItemUDT.ITEM_ID));
        item.setCash(value.getBoolean(ItemUDT.CASH));
        item.setQuantity(value.getShort(ItemUDT.QUANTITY));
        item.setAttribute(value.getShort(ItemUDT.ATTRIBUTE));
        item.setTitle(value.getString(ItemUDT.TITLE));
        if (item.getItemType() == ItemType.EQUIP) {
            item.setEquipData(value.get(ItemUDT.EQUIP_INFO, EquipData.class));
        } else if (item.getItemType() == ItemType.PET) {
            item.setPetData(value.get(ItemUDT.PET_INFO, PetData.class));
        }
        return item;
    }

    @Nullable
    @Override
    protected UdtValue outerToInner(@Nullable Item item) {
        if (item == null) {
            return null;
        }
        UdtValue value = getCqlType().newValue()
                .setInt(ItemUDT.ITEM_TYPE, item.getItemType().getValue())
                .setLong(ItemUDT.ITEM_SN, item.getItemSn())
                .setInt(ItemUDT.ITEM_ID, item.getItemId())
                .setBoolean(ItemUDT.CASH, item.isCash())
                .setShort(ItemUDT.QUANTITY, item.getQuantity())
                .setShort(ItemUDT.ATTRIBUTE, item.getAttribute())
                .setString(ItemUDT.TITLE, item.getTitle());
        if (item.getItemType() == ItemType.EQUIP) {
            value = value.set(ItemUDT.EQUIP_INFO, item.getEquipData(), EquipData.class);
        } else if (item.getItemType() == ItemType.PET) {
            value = value.set(ItemUDT.PET_INFO, item.getPetData(), PetData.class);
        }
        return value;
    }
}
