package kinoko.database.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;
import kinoko.server.guild.GuildMember;

import java.util.ArrayList;
import java.util.List;

public final class GuildSerializer implements JsonSerializer<Guild> {
    private final GuildMemberSerializer guildMemberSerializer = new GuildMemberSerializer();
    private final GuildBoardEntrySerializer guildBoardEntrySerializer = new GuildBoardEntrySerializer();

    @Override
    public JSONObject serialize(Guild value) {
        throw new IllegalStateException(); // TODO
    }

    @Override
    public Guild deserialize(JSONObject object) {
        throw new IllegalStateException(); // TODO
    }

    public JSONArray serializeGradeNames(List<String> gradeNames) {
        return new JSONArray(gradeNames);
    }

    public List<String> deserializeGradeNames(JSONArray array) {
        final List<String> gradeNames = new ArrayList<>();
        for (var name : array) {
            gradeNames.add((String) name);
        }
        return gradeNames;
    }

    public JSONArray serializeGuildMembers(List<GuildMember> members) {
        final JSONArray array = new JSONArray();
        for (GuildMember member : members) {
            array.add(guildMemberSerializer.serialize(member));
        }
        return array;
    }

    public List<GuildMember> deserializeGuildMembers(JSONArray array) {
        final List<GuildMember> members = new ArrayList<>();
        for (var member : array) {
            members.add(guildMemberSerializer.deserialize((JSONObject) member));
        }
        return members;
    }

    public JSONArray serializeBoardEntryList(List<GuildBoardEntry> entries) {
        final JSONArray array = new JSONArray();
        for (GuildBoardEntry entry : entries) {
            array.add(guildBoardEntrySerializer.serialize(entry));
        }
        return array;
    }

    public List<GuildBoardEntry> deserializeBoardEntryList(JSONArray array) {
        final List<GuildBoardEntry> entries = new ArrayList<>();
        for (var entry : array) {
            entries.add(guildBoardEntrySerializer.deserialize((JSONObject) entry));
        }
        return entries;
    }

    public JSONObject serializeBoardEntryNotice(GuildBoardEntry entry) {
        return entry != null ? guildBoardEntrySerializer.serialize(entry) : null;
    }

    public GuildBoardEntry deserializeBoardEntryNotice(JSONObject object) {
        return object != null ? guildBoardEntrySerializer.deserialize(object) : null;
    }
}
