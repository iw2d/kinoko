package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.item.Item;
import kinoko.world.item.ItemType;

import static kinoko.database.schema.ItemSchema.*;

public final class ItemSerializer implements JsonSerializer<Item> {
    private final EquipDataSerializer equipDataSerializer = new EquipDataSerializer();
    private final PetDataSerializer petDataSerializer = new PetDataSerializer();
    private final RingDataSerializer ringDataSerializer = new RingDataSerializer();

    @Override
    public JSONObject serialize(Item value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(ITEM_SN, value.getItemSn());
        object.put(ITEM_ID, value.getItemId());
        object.put(ITEM_TYPE, value.getItemType().getValue());
        object.put(CASH, value.isCash());
        object.put(QUANTITY, value.getQuantity());
        object.put(ATTRIBUTE, value.getAttribute());
        object.put(TITLE, value.getTitle());
        if (value.getDateExpire() != null) {
            object.put(DATE_EXPIRE, value.getDateExpire().toEpochMilli());
        }
        object.put(EQUIP_DATA, equipDataSerializer.serialize(value.getEquipData()));
        object.put(PET_DATA, petDataSerializer.serialize(value.getPetData()));
        object.put(RING_DATA, ringDataSerializer.serialize(value.getRingData()));
        return object;
    }

    @Override
    public Item deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final ItemType itemType = ItemType.getByValue(object.getIntValue(ITEM_TYPE));
        if (itemType == null) {
            throw new IllegalStateException(String.format("Unexpected item type : %d", object.getIntValue(ITEM_TYPE)));
        }
        final Item item = new Item(itemType);
        item.setItemSn(object.getLongValue(ITEM_SN));
        item.setItemId(object.getIntValue(ITEM_ID));
        item.setCash(object.getBooleanValue(CASH));
        item.setQuantity(object.getShortValue(QUANTITY));
        item.setAttribute(object.getShortValue(ATTRIBUTE));
        item.setTitle(object.getString(TITLE));
        item.setDateExpire(object.getInstant(DATE_EXPIRE));
        item.setEquipData(equipDataSerializer.deserialize(object.getJSONObject(EQUIP_DATA)));
        item.setPetData(petDataSerializer.deserialize(object.getJSONObject(PET_DATA)));
        item.setRingData(ringDataSerializer.deserialize(object.getJSONObject(RING_DATA)));
        return item;
    }
}
