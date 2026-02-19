package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;

public interface JsonSerializer<T> {
    JSONObject serialize(T value);

    T deserialize(JSONObject object);
}
