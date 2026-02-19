package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.item.PetData;

import static kinoko.database.schema.PetDataSchema.*;

public final class PetDataSerializer implements JsonSerializer<PetData> {
    @Override
    public JSONObject serialize(PetData value) {
        if (value == null) {
            return null;
        }
        final JSONObject object = new JSONObject();
        object.put(PET_NAME, value.getPetName());
        object.put(LEVEL, value.getLevel());
        object.put(FULLNESS, value.getFullness());
        object.put(TAMENESS, value.getTameness());
        object.put(PET_SKILL, value.getPetSkill());
        object.put(PET_ATTRIBUTE, value.getPetAttribute());
        object.put(REMAIN_LIFE, value.getRemainLife());
        return object;
    }

    @Override
    public PetData deserialize(JSONObject object) {
        if (object == null) {
            return null;
        }
        final PetData data = new PetData();
        data.setPetName(object.getString(PET_NAME));
        data.setLevel(object.getByteValue(LEVEL));
        data.setFullness(object.getByteValue(FULLNESS));
        data.setPetSkill(object.getShortValue(PET_SKILL));
        data.setPetAttribute(object.getShortValue(PET_ATTRIBUTE));
        data.setRemainLife(object.getIntValue(REMAIN_LIFE));
        return data;
    }
}
