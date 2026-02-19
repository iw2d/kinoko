package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.quest.QuestRecord;
import kinoko.world.quest.QuestState;

import java.time.Instant;

import static kinoko.database.schema.QuestRecordSchema.*;

public final class QuestRecordSerializer implements JsonSerializer<QuestRecord> {
    @Override
    public JSONObject serialize(QuestRecord qr) {
        if (qr == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(QUEST_ID, qr.getQuestId());
        object.put(QUEST_STATE, qr.getState().getValue());
        object.put(QUEST_VALUE, qr.getValue());
        if (qr.getCompletedTime() != null) {
            object.put(COMPLETED_TIME, qr.getCompletedTime().toEpochMilli());
        }
        return object;
    }

    @Override
    public QuestRecord deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final int questId = object.getIntValue(QUEST_ID);
        final QuestRecord qr = new QuestRecord(questId);
        qr.setState(QuestState.getByValue(object.getIntValue(QUEST_STATE)));
        qr.setValue(object.getString(QUEST_VALUE));
        if (object.containsKey(COMPLETED_TIME)) {
            final long epoch = object.getLongValue(COMPLETED_TIME);
            qr.setCompletedTime(Instant.ofEpochMilli(epoch));
        }
        return qr;
    }
}
