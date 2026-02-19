package kinoko.database.json;

import com.alibaba.fastjson2.JSONObject;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRank;

import static kinoko.database.schema.GuildMemberSchema.*;

public final class GuildMemberSerializer implements JsonSerializer<GuildMember> {
    @Override
    public JSONObject serialize(GuildMember member) {
        if (member == null) {
            throw new NullPointerException();
        }
        final JSONObject object = new JSONObject();
        object.put(CHARACTER_ID, member.getCharacterId());
        object.put(CHARACTER_NAME, member.getCharacterName());
        object.put(JOB, member.getJob());
        object.put(LEVEL, member.getLevel());
        object.put(GUILD_RANK, member.getGuildRank().getValue());
        object.put(ALLIANCE_RANK, member.getAllianceRank().getValue());
        return object;
    }

    @Override
    public GuildMember deserialize(JSONObject object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return new GuildMember(
                object.getIntValue(CHARACTER_ID),
                object.getString(CHARACTER_NAME),
                object.getIntValue(JOB),
                object.getIntValue(LEVEL),
                false,
                GuildRank.getByValue(object.getIntValue(GUILD_RANK)),
                GuildRank.getByValue(object.getIntValue(ALLIANCE_RANK))
        );
    }
}
