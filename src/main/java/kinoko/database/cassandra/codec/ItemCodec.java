package kinoko.database.cassandra.codec;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import kinoko.database.cassandra.model.EquipInfoModel;
import kinoko.database.cassandra.model.PetInfoModel;
import kinoko.world.item.EquipInfo;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;
import kinoko.world.item.PetInfo;

import static kinoko.database.cassandra.model.ItemModel.*;

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
        final ItemType itemType = ItemType.getByValue(value.getInt(ITEM_TYPE.getName()));
        if (itemType == null) {
            return null;
        }
        final Item item = new Item(itemType);
        item.setItemSn(value.getLong(ITEM_SN.getName()));
        item.setItemId(value.getInt(ITEM_ID.getName()));
        item.setCash(value.getBoolean(CASH.getName()));
        item.setQuantity(value.getShort(QUANTITY.getName()));
        item.setAttribute(value.getShort(ATTRIBUTE.getName()));
        item.setTitle(value.getString(TITLE.getName()));
        if (item.getItemType() == ItemType.EQUIP) {
            item.setEquipInfo(value.get(EQUIP_INFO.getName(), EquipInfo.class));
        } else if (item.getItemType() == ItemType.PET) {
            item.setPetInfo(value.get(PET_INFO.getName(), PetInfo.class));
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
                .setInt(ITEM_TYPE.getName(), item.getItemType().getValue())
                .setLong(ITEM_SN.getName(), item.getItemSn())
                .setInt(ITEM_ID.getName(), item.getItemId())
                .setBoolean(CASH.getName(), item.isCash())
                .setShort(QUANTITY.getName(), item.getQuantity())
                .setShort(ATTRIBUTE.getName(), item.getAttribute())
                .setString(TITLE.getName(), item.getTitle());
        if (item.getItemType() == ItemType.EQUIP) {
            value = value.set(EQUIP_INFO.getName(), item.getEquipInfo(), EquipInfo.class);
        } else if (item.getItemType() == ItemType.PET) {
            value = value.set(PET_INFO.getName(), item.getPetInfo(), PetInfo.class);
        }
        return value;
    }

    public static void createUserDefinedType(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createType(keyspace, TYPE_NAME).ifNotExists()
                        .withField(ITEM_TYPE.getName(), DataTypes.INT)
                        .withField(ITEM_SN.getName(), DataTypes.BIGINT)
                        .withField(ITEM_ID.getName(), DataTypes.INT)
                        .withField(CASH.getName(), DataTypes.BOOLEAN)
                        .withField(QUANTITY.getName(), DataTypes.SMALLINT)
                        .withField(ATTRIBUTE.getName(), DataTypes.SMALLINT)
                        .withField(TITLE.getName(), DataTypes.TEXT)
                        .withField(EQUIP_INFO.getName(), SchemaBuilder.udt(EquipInfoModel.TYPE_NAME, true))
                        .withField(PET_INFO.getName(), SchemaBuilder.udt(PetInfoModel.TYPE_NAME, true))
                        .build()
        );
    }
}
