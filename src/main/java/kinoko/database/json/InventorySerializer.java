package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.item.Inventory;

import static kinoko.database.schema.InventorySchema.ITEMS;
import static kinoko.database.schema.InventorySchema.SIZE;

public final class InventorySerializer implements JsonSerializer<Inventory> {
    private final ItemSerializer itemSerializer = new ItemSerializer();

    @Override
    public JSONObject serialize(Inventory inventory) {
        if (inventory == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        final JSONObject itemsObject = object.putObject(ITEMS);
        for (var entry : inventory.getItems().entrySet()) {
            itemsObject.put(String.valueOf(entry.getKey()), itemSerializer.serialize(entry.getValue()));
        }
        object.put(SIZE, inventory.getSize());
        return object;
    }

    @Override
    public Inventory deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final Inventory inventory = new Inventory(object.getIntValue(SIZE));
        final JSONObject itemsObject = object.getJSONObject(ITEMS);
        for (var entry : itemsObject.entrySet()) {
            inventory.putItem(Integer.parseInt(entry.getKey()), itemSerializer.deserialize((JSONObject) entry.getValue()));
        }
        return inventory;
    }
}
