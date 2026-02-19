package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.server.guild.GuildBoardComment;

import static kinoko.database.schema.GuildBoardCommentSchema.*;

public final class GuildBoardCommentSerializer implements JsonSerializer<GuildBoardComment> {
    @Override
    public JSONObject serialize(GuildBoardComment comment) {
        if (comment == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(COMMENT_SN, comment.getCommentSn());
        object.put(CHARACTER_ID, comment.getCharacterId());
        object.put(TEXT, comment.getText());
        if (comment.getDate() != null) {
            object.put(DATE, comment.getDate().toEpochMilli());
        }
        return object;
    }

    @Override
    public GuildBoardComment deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return new GuildBoardComment(
                object.getIntValue(COMMENT_SN),
                object.getIntValue(CHARACTER_ID),
                object.getString(TEXT),
                object.getInstant(DATE)
        );
    }
}
