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
    private String guildNotice;
    private List<String> gradeNames;
    private int memberMax;
    private short markBg;
    private byte markBgColor;
    private short mark;
    private byte markColor;

    private int inviterId;
    private int targetId;
    private String targetName;
    private GuildRank guildRank;

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

    public String getGuildNotice() {
        return guildNotice;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public int getMemberMax() {
        return memberMax;
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

    public int getInviterId() {
        return inviterId;
    }

    public int getTargetId() {
        return targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public GuildRank getGuildRank() {
        return guildRank;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case LoadGuild, RemoveGuild, WithdrawGuild -> {
                outPacket.encodeInt(guildId);
            }
            case CreateNewGuild -> {
                outPacket.encodeInt(guildId);
                outPacket.encodeString(guildName);
            }
            case InviteGuild -> {
                outPacket.encodeString(targetName);
            }
            case JoinGuild -> {
                outPacket.encodeInt(inviterId);
            }
            case KickGuild -> {
                outPacket.encodeInt(targetId);
                outPacket.encodeString(targetName);
            }
            case IncMaxMemberNum -> {
                outPacket.encodeInt(memberMax);
            }
            case SetGradeName -> {
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    outPacket.encodeString(gradeNames.get(i));
                }
            }
            case SetMemberGrade -> {
                outPacket.encodeInt(targetId);
                outPacket.encodeByte(guildRank.getValue());
            }
            case SetMark -> {
                outPacket.encodeShort(markBg);
                outPacket.encodeByte(markBgColor);
                outPacket.encodeShort(mark);
                outPacket.encodeByte(markColor);
            }
            case SetNotice -> {
                outPacket.encodeString(guildNotice);
            }
        }
    }

    public static GuildRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final GuildRequest request = new GuildRequest(GuildRequestType.getByValue(type));
        switch (request.requestType) {
            case LoadGuild, RemoveGuild, WithdrawGuild -> {
                request.guildId = inPacket.decodeInt();
            }
            case CreateNewGuild -> {
                request.guildId = inPacket.decodeInt();
                request.guildName = inPacket.decodeString();
            }
            case InviteGuild -> {
                request.targetName = inPacket.decodeString();
            }
            case JoinGuild -> {
                request.inviterId = inPacket.decodeInt();
            }
            case KickGuild -> {
                request.targetId = inPacket.decodeInt();
                request.targetName = inPacket.decodeString();
            }
            case IncMaxMemberNum -> {
                request.memberMax = inPacket.decodeInt();
            }
            case SetGradeName -> {
                request.gradeNames = new ArrayList<>();
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    request.gradeNames.add(inPacket.decodeString());
                }
            }
            case SetMemberGrade -> {
                request.targetId = inPacket.decodeInt();
                request.guildRank = GuildRank.getByValue(inPacket.decodeByte());
            }
            case SetMark -> {
                request.markBg = inPacket.decodeShort();
                request.markBgColor = inPacket.decodeByte();
                request.mark = inPacket.decodeShort();
                request.markColor = inPacket.decodeByte();
            }
            case SetNotice -> {
                request.guildNotice = inPacket.decodeString();
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

    public static GuildRequest inviteGuild(String targetName) {
        final GuildRequest request = new GuildRequest(GuildRequestType.InviteGuild);
        request.targetName = targetName;
        return request;
    }

    public static GuildRequest joinGuild(int inviterId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.JoinGuild);
        request.inviterId = inviterId;
        return request;
    }

    public static GuildRequest withdrawGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.WithdrawGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest kickGuild(int targetId, String targetName) {
        final GuildRequest request = new GuildRequest(GuildRequestType.KickGuild);
        request.targetId = targetId;
        request.targetName = targetName;
        return request;
    }

    public static GuildRequest removeGuild(int guildId) {
        final GuildRequest request = new GuildRequest(GuildRequestType.RemoveGuild);
        request.guildId = guildId;
        return request;
    }

    public static GuildRequest incMaxMemberNum(int memberMax) {
        final GuildRequest request = new GuildRequest(GuildRequestType.IncMaxMemberNum);
        request.memberMax = memberMax;
        return request;
    }

    public static GuildRequest setGradeName(List<String> gradeNames) {
        final GuildRequest request = new GuildRequest(GuildRequestType.SetGradeName);
        request.gradeNames = gradeNames;
        return request;
    }

    public static GuildRequest setMemberGrade(int targetId, GuildRank guildRank) {
        final GuildRequest request = new GuildRequest(GuildRequestType.SetMemberGrade);
        request.targetId = targetId;
        request.guildRank = guildRank;
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
        request.guildNotice = notice;
        return request;
    }
}
