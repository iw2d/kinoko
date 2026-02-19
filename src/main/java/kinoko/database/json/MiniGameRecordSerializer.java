package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.world.user.data.MiniGameRecord;

import static kinoko.database.schema.MiniGameRecordSchema.MEMORY_LOSSES;
import static kinoko.database.schema.MiniGameRecordSchema.MEMORY_SCORE;
import static kinoko.database.schema.MiniGameRecordSchema.MEMORY_TIES;
import static kinoko.database.schema.MiniGameRecordSchema.MEMORY_WINS;
import static kinoko.database.schema.MiniGameRecordSchema.OMOK_LOSSES;
import static kinoko.database.schema.MiniGameRecordSchema.OMOK_SCORE;
import static kinoko.database.schema.MiniGameRecordSchema.OMOK_TIES;
import static kinoko.database.schema.MiniGameRecordSchema.OMOK_WINS;

public final class MiniGameRecordSerializer implements JsonSerializer<MiniGameRecord> {
    @Override
    public JSONObject serialize(MiniGameRecord miniGameRecord) {
        if (miniGameRecord == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(OMOK_WINS, miniGameRecord.getOmokGameWins());
        object.put(OMOK_TIES, miniGameRecord.getOmokGameTies());
        object.put(OMOK_LOSSES, miniGameRecord.getOmokGameLosses());
        object.put(OMOK_SCORE, miniGameRecord.getOmokGameScore());
        object.put(MEMORY_WINS, miniGameRecord.getMemoryGameWins());
        object.put(MEMORY_TIES, miniGameRecord.getMemoryGameTies());
        object.put(MEMORY_LOSSES, miniGameRecord.getMemoryGameLosses());
        object.put(MEMORY_SCORE, miniGameRecord.getMemoryGameScore());
        return object;
    }

    @Override
    public MiniGameRecord deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final MiniGameRecord miniGameRecord = new MiniGameRecord();
        miniGameRecord.setOmokGameWins(object.getIntValue(OMOK_WINS));
        miniGameRecord.setOmokGameTies(object.getIntValue(OMOK_TIES));
        miniGameRecord.setOmokGameLosses(object.getIntValue(OMOK_LOSSES));
        miniGameRecord.setOmokGameScore(object.getDoubleValue(OMOK_SCORE));
        miniGameRecord.setMemoryGameWins(object.getIntValue(MEMORY_WINS));
        miniGameRecord.setMemoryGameTies(object.getIntValue(MEMORY_TIES));
        miniGameRecord.setMemoryGameLosses(object.getIntValue(MEMORY_LOSSES));
        miniGameRecord.setMemoryGameScore(object.getDoubleValue(MEMORY_SCORE));
        return miniGameRecord;
    }
}
