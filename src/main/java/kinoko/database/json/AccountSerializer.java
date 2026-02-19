package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.server.cashshop.CashItemInfo;
import kinoko.world.item.Item;
import kinoko.world.user.Account;

import java.util.ArrayList;
import java.util.List;

public final class AccountSerializer implements JsonSerializer<Account> {
    private final ItemSerializer itemSerializer = new ItemSerializer();
    private final CashItemInfoSerializer cashItemInfoSerializer = new CashItemInfoSerializer();

    @Override
    public JSONObject serialize(Account value) {
        throw new IllegalStateException(); // TODO
    }

    @Override
    public Account deserialize(JSONObject object) {
        throw new IllegalStateException(); // TODO
    }

    public JSONArray serializeTrunkItems(List<Item> items) {
        final JSONArray array = new JSONArray();
        for (Item item : items) {
            array.add(itemSerializer.serialize(item));
        }
        return array;
    }

    public List<Item> deserializeTrunkItems(JSONArray array) {
        final List<Item> items = new ArrayList<>();
        for (var item : array) {
            items.add(itemSerializer.deserialize((JSONObject) item));
        }
        return items;
    }

    public JSONArray serializeLockerItems(List<CashItemInfo> items) {
        final JSONArray array = new JSONArray();
        for (CashItemInfo item : items) {
            array.add(cashItemInfoSerializer.serialize(item));
        }
        return array;
    }

    public List<CashItemInfo> deserializeLockerItems(JSONArray array) {
        final List<CashItemInfo> items = new ArrayList<>();
        for (var item : array) {
            items.add(cashItemInfoSerializer.deserialize((JSONObject) item));
        }
        return items;
    }
}
