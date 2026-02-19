package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.skill.SkillRecord;

import static kinoko.database.schema.SkillRecordSchema.*;

public final class SkillRecordSerializer implements JsonSerializer<SkillRecord> {
    @Override
    public JSONObject serialize(SkillRecord sr) {
        if (sr == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(SKILL_ID, sr.getSkillId());
        object.put(SKILL_LEVEL, sr.getSkillLevel());
        object.put(MASTER_LEVEL, sr.getMasterLevel());
        return object;
    }

    @Override
    public SkillRecord deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final int skillId = object.getIntValue(SKILL_ID);
        final SkillRecord sr = new SkillRecord(skillId);
        sr.setSkillLevel(object.getIntValue(SKILL_LEVEL));
        sr.setMasterLevel(object.getIntValue(MASTER_LEVEL));
        return sr;
    }
}
