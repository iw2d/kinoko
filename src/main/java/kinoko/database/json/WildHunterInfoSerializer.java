package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.world.user.data.WildHunterInfo;

import static kinoko.database.schema.WildHunterInfoSchema.CAPTURED_MOBS;
import static kinoko.database.schema.WildHunterInfoSchema.RIDING_TYPE;

public final class WildHunterInfoSerializer implements JsonSerializer<WildHunterInfo> {

    @Override
    public JSONObject serialize(WildHunterInfo whi) {
        if (whi == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(RIDING_TYPE, whi.getRidingType());
        object.put(CAPTURED_MOBS, new JSONArray(whi.getCapturedMobs()));
        return object;
    }

    @Override
    public WildHunterInfo deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final WildHunterInfo whi = new WildHunterInfo();
        whi.setRidingType(object.getIntValue(RIDING_TYPE));
        whi.getCapturedMobs().addAll(object.getJSONArray(CAPTURED_MOBS).toList(Integer.class));
        return whi;
    }
}
