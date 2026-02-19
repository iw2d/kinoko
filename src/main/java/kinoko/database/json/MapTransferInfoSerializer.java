package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.world.user.data.MapTransferInfo;

import static kinoko.database.schema.MapTransferInfoSchema.MAP_TRANSFER;
import static kinoko.database.schema.MapTransferInfoSchema.MAP_TRANSFER_EX;

public final class MapTransferInfoSerializer implements JsonSerializer<MapTransferInfo> {
    @Override
    public JSONObject serialize(MapTransferInfo mti) {
        if (mti == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(MAP_TRANSFER, new JSONArray(mti.getMapTransfer()));
        object.put(MAP_TRANSFER_EX, new JSONArray(mti.getMapTransferEx()));
        return object;
    }

    @Override
    public MapTransferInfo deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final MapTransferInfo mti = new MapTransferInfo();
        mti.getMapTransfer().addAll(object.getJSONArray(MAP_TRANSFER).toList(Integer.class));
        mti.getMapTransferEx().addAll(object.getJSONArray(MAP_TRANSFER_EX).toList(Integer.class));
        return mti;
    }
}
