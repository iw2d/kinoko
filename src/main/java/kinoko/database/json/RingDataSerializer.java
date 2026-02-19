package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.item.RingData;

import static kinoko.database.schema.RingDataSchema.*;

public final class RingDataSerializer implements JsonSerializer<RingData> {
    @Override
    public JSONObject serialize(RingData value) {
        if (value == null) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put(PAIR_CHARACTER_ID, value.getPairCharacterId());
        object.put(PAIR_CHARACTER_NAME, value.getPairCharacterName());
        object.put(PAIR_ITEM_SN, value.getPairItemSn());
        return object;
    }

    @Override
    public RingData deserialize(JSONObject object) {
        if (object == null) {
            return null;
        }
        final RingData data = new RingData();
        data.setPairCharacterId(object.getIntValue(PAIR_CHARACTER_ID));
        data.setPairCharacterName(object.getString(PAIR_CHARACTER_NAME));
        data.setPairItemSn(object.getLongValue(PAIR_ITEM_SN));
        return data;
    }
}
