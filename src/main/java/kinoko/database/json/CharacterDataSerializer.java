package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.world.quest.QuestRecord;
import kinoko.world.skill.SkillRecord;
import kinoko.world.user.CharacterData;
import kinoko.world.user.data.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CharacterDataSerializer implements JsonSerializer<CharacterData> {

    private final SkillRecordSerializer skillRecordSerializer = new SkillRecordSerializer();
    private final QuestRecordSerializer questRecordSerializer = new QuestRecordSerializer();
    private final ConfigSerializer configSerializer = new ConfigSerializer();
    private final MiniGameRecordSerializer miniGameRecordSerializer = new MiniGameRecordSerializer();
    private final MapTransferInfoSerializer mapTransferInfoSerializer = new MapTransferInfoSerializer();
    private final WildHunterInfoSerializer wildHunterInfoSerializer = new WildHunterInfoSerializer();

    @Override
    public JSONObject serialize(CharacterData value) {
        throw new IllegalStateException(); // TODO
    }

    @Override
    public CharacterData deserialize(JSONObject object) {
        throw new IllegalStateException(); // TODO
    }

    public JSONObject serializeSkillCooltimes(Map<Integer, Instant> skillCooltimes) {
        final JSONObject object = new JSONObject();
        for (var entry : skillCooltimes.entrySet()) {
            object.put(String.valueOf(entry.getKey()), entry.getValue().toEpochMilli());
        }
        return object;
    }

    public Map<Integer, Instant> deserializeSkillCooltimes(JSONObject object) {
        final Map<Integer, Instant> skillCooltimes = new HashMap<>();
        for (var entry : object.entrySet()) {
            skillCooltimes.put(Integer.parseInt(entry.getKey()), Instant.ofEpochMilli((Long) entry.getValue()));
        }
        return skillCooltimes;
    }

    public JSONArray serializeSkillRecords(List<SkillRecord> skillRecords) {
        final JSONArray array = new JSONArray();
        for (var record : skillRecords) {
            array.add(skillRecordSerializer.serialize(record));
        }
        return array;
    }

    public List<SkillRecord> deserializeSkillRecords(JSONArray array) {
        final List<SkillRecord> skillRecords = new ArrayList<>();
        for (var object : array) {
            skillRecords.add(skillRecordSerializer.deserialize((JSONObject) object));
        }
        return skillRecords;
    }

    public JSONArray serializeQuestRecords(List<QuestRecord> questRecords) {
        final JSONArray array = new JSONArray();
        for (var record : questRecords) {
            array.add(questRecordSerializer.serialize(record));
        }
        return array;
    }

    public List<QuestRecord> deserializeQuestRecords(JSONArray array) {
        final List<QuestRecord> questRecords = new ArrayList<>();
        for (var object : array) {
            questRecords.add(questRecordSerializer.deserialize((JSONObject) object));
        }
        return questRecords;
    }

    public JSONObject serializeConfigManager(ConfigManager cm) {
        return configSerializer.serialize(cm);
    }

    public ConfigManager deserializeConfigManager(JSONObject object) {
        return configSerializer.deserialize(object);
    }

    public JSONObject serializePopularityRecord(PopularityRecord popularityRecord) {
        final JSONObject object = new JSONObject();
        for (var entry : popularityRecord.getRecords().entrySet()) {
            object.put(String.valueOf(entry.getKey()), entry.getValue().toEpochMilli());
        }
        return object;
    }

    public PopularityRecord deserializePopularityRecord(JSONObject object) {
        final PopularityRecord popularityRecord = new PopularityRecord();
        for (var entry : object.entrySet()) {
            popularityRecord.addRecord(Integer.parseInt(entry.getKey()), Instant.ofEpochMilli((Long) entry.getValue()));
        }
        return popularityRecord;
    }

    public JSONObject serializeMiniGameRecord(MiniGameRecord mgr) {
        return miniGameRecordSerializer.serialize(mgr);
    }

    public MiniGameRecord deserializeMiniGameRecord(JSONObject object) {
        return miniGameRecordSerializer.deserialize(object);
    }

    public JSONObject serializeMapTransferInfo(MapTransferInfo mti) {
        return mapTransferInfoSerializer.serialize(mti);
    }

    public MapTransferInfo deserializeMapTransferInfo(JSONObject object) {
        return mapTransferInfoSerializer.deserialize(object);
    }

    public JSONObject serializeWildHunterInfo(WildHunterInfo whi) {
        return wildHunterInfoSerializer.serialize(whi);
    }

    public WildHunterInfo deserializeWildHunterInfo(JSONObject object) {
        return wildHunterInfoSerializer.deserialize(object);
    }
}
