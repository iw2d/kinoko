package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.server.cashshop.CashItemInfo;

import static kinoko.database.schema.CashItemInfoSchema.*;

public final class CashItemInfoSerializer implements JsonSerializer<CashItemInfo> {
    private final ItemSerializer itemSerializer = new ItemSerializer();

    @Override
    public JSONObject serialize(CashItemInfo value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(ITEM, itemSerializer.serialize(value.getItem()));
        object.put(COMMODITY_ID, value.getCommodityId());
        object.put(ACCOUNT_ID, value.getAccountId());
        object.put(CHARACTER_ID, value.getCharacterId());
        object.put(CHARACTER_NAME, value.getCharacterName());
        return object;
    }

    @Override
    public CashItemInfo deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return new CashItemInfo(
                itemSerializer.deserialize(object.getJSONObject(ITEM)),
                object.getIntValue(COMMODITY_ID),
                object.getIntValue(ACCOUNT_ID),
                object.getIntValue(CHARACTER_ID),
                object.getString(CHARACTER_NAME)
        );
    }
}
