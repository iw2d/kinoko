package kinoko.server.guild;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;

public final class GuildRequest implements Encodable {
    private final GuildRequestType requestType;
    private int guildId;
    private String guildName;
    private List<String> gradeNames;
    private short markBg;
    private byte markBgColor;
    private short mark;
    private byte markColor;
    private String notice;

    public GuildRequest(GuildRequestType requestType) {
        this.requestType = requestType;
    }

    public GuildRequestType getRequestType() {
        return requestType;
    }

    public int getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public short getMarkBg() {
        return markBg;
    }

    public byte getMarkBgColor() {
        return markBgColor;
    }

    public short getMark() {
        return mark;
    }

    public byte getMarkColor() {
        return markColor;
    }

    public String getNewNotice() {
        return notice;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadGuild, RemoveGuild -> {
                outPacket.encodeInt(guildId);
            }
            case CreateNewGuild -> {
                outPacket.encodeInt(guildId);
                outPacket.encodeString(guildName);
            }
            case SetGradeName -> {
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    outPacket.encodeString(gradeNames.get(i));
                }
            }
            case SetMark -> {
                outPacket.encodeShort(markBg);
                outPacket.encodeByte(markBgColor);
                outPacket.encodeShort(mark);
                outPacket.encodeByte(markColor);
            }
            case SetNotice -> {
                outPacket.encodeString(notice);
            }
        }
    }

    public static GuildRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildRequest request = new GuildRequest(GuildRequestType.getByValue(type));
        switch (request.requestType) {
            case LoadGuild, RemoveGuild -> {
                request.guildId = inPacket.decodeInt();
            }
            case CreateNewGuild -> {
                request.guildId = inPacket.decodeInt();
                request.guildName = inPacket.decodeString();
            }
            case SetGradeName -> {
                request.gradeNames = new ArrayList<>();
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    request.gradeNames.add(inPacket.decodeString());
                }
            }
            case SetMark -> {
                request.markBg = inPacket.decodeShort();
                request.markBgColor = inPacket.decodeByte();
                request.mark = inPacket.decodeShort();
                request.markColor = inPacket.decodeByte();
            }
            case SetNotice -> {
                request.notice = inPacket.decodeString();
            }
        }
        return request;
    }

    public static GuildRequest loadGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.LoadGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest createNewGuild(int guildId, String guildName) {
        final GuildRequest request = new GuildRequest(GuildRequestType.CreateNewGuild);
        request.guildId = guildId;
        request.guildName = guildName;
        return request;
    }

    public static GuildRequest removeGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.RemoveGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest setGradeName(List<String> gradeNames) {
        final GuildRequest request = new GuildRequest(GuildRequestType.SetGradeName);
        request.gradeNames = gradeNames;
        return request;
    }

    public static GuildRequest setMark(short markBg, byte markBgColor, short mark, byte markColor) {
        final GuildRequest request = new GuildRequest(GuildRequestType.SetMark);
        request.markBg = markBg;
        request.markBgColor = markBgColor;
        request.mark = mark;
        request.markColor = markColor;
        return request;
    }

    public static GuildRequest setNotice(String notice) {
        final GuildRequest request = new GuildRequest(GuildRequestType.SetNotice);
        request.notice = notice;
        return request;
    }
}
