package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.server.guild.GuildBoardComment;
import kinoko.server.guild.GuildBoardEntry;

import java.util.concurrent.atomic.AtomicInteger;

import static kinoko.database.schema.GuildBoardEntrySchema.*;

public final class GuildBoardEntrySerializer implements JsonSerializer<GuildBoardEntry> {
    private final GuildBoardCommentSerializer guildBoardCommentSerializer = new GuildBoardCommentSerializer();

    @Override
    public JSONObject serialize(GuildBoardEntry entry) {
        if (entry == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(ENTRY_ID, entry.getEntryId());
        object.put(CHARACTER_ID, entry.getCharacterId());
        object.put(TITLE, entry.getTitle());
        object.put(TEXT, entry.getText());
        if (entry.getDate() != null) {
            object.put(DATE, entry.getDate().toEpochMilli());
        }
        object.put(EMOTICON, entry.getEmoticon());
        object.put(COMMENT_SN_COUNTER, entry.getCommentSnCounter().get());

        final JSONArray commentsArray = object.putArray(COMMENTS);
        for (GuildBoardComment comment : entry.getComments()) {
            commentsArray.add(guildBoardCommentSerializer.serialize(comment));
        }
        return object;
    }

    @Override
    public GuildBoardEntry deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        final GuildBoardEntry entry = new GuildBoardEntry(
                object.getIntValue(ENTRY_ID),
                object.getIntValue(CHARACTER_ID),
                object.getString(TITLE),
                object.getString(TEXT),
                object.getInstant(DATE),
                object.getIntValue(EMOTICON)
        );
        for (var comment : object.getJSONArray(COMMENTS)) {
            entry.addComment(guildBoardCommentSerializer.deserialize((JSONObject) comment));
        }
        entry.setCommentSnCounter(new AtomicInteger(object.getIntValue(COMMENT_SN_COUNTER)));
        return entry;
    }
}
