package kinoko.server.alliance;

import kinoko.server.guild.GuildRank;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;

public final class AllianceRequest implements Encodable {
    private final AllianceRequestType requestType;
    private int allianceId;
    private String allianceName;
    private String allianceNotice;
    private List<String> gradeNames;
    private int memberMax;

    private int inviterId;
    private int targetId;
    private String targetName;
    private GuildRank allianceRank;

    public AllianceRequest(AllianceRequestType requestType) {
        this.requestType = requestType;
    }

    public AllianceRequestType getRequestType() {
        return requestType;
    }

    public int getAllianceId() {
        return allianceId;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public String getAllianceNotice() {
        return allianceNotice;
    }

    public List<String> getGradeNames() {
        return gradeNames;
    }

    public int getMemberMax() {
        return memberMax;
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

    public GuildRank getAllianceRank() {
        return allianceRank;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case Create -> {
                outPacket.encodeInt(allianceId);
                outPacket.encodeString(allianceName);
            }
            case Invite -> {
                outPacket.encodeString(targetName);
            }
            case Join -> {
                outPacket.encodeInt(inviterId);
            }
            case Kick -> {
                outPacket.encodeInt(targetId);
                outPacket.encodeString(targetName);
            }
            case UpdateMemberCountMax -> {
                outPacket.encodeInt(memberMax);
            }
            case SetGradeName -> {
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    outPacket.encodeString(gradeNames.get(i));
                }
            }
            case ChangeGrade -> {
                outPacket.encodeInt(targetId);
                outPacket.encodeByte(allianceRank.getValue());
            }
            case SetNotice -> {
                outPacket.encodeString(allianceNotice);
            }
        }
    }

    public static AllianceRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.getByValue(type));
        switch (request.requestType) {
            case Load, Destroy, Withdraw -> {
                request.allianceId = inPacket.decodeInt();
            }
            case Create -> {
                request.allianceId = inPacket.decodeInt();
                request.allianceName = inPacket.decodeString();
            }
            case Invite -> {
                request.targetName = inPacket.decodeString();
            }
            case Join -> {
                request.inviterId = inPacket.decodeInt();
            }
            case Kick -> {
                request.targetId = inPacket.decodeInt();
                request.targetName = inPacket.decodeString();
            }
            case UpdateMemberCountMax -> {
                request.memberMax = inPacket.decodeInt();
            }
            case SetGradeName -> {
                request.gradeNames = new ArrayList<>();
                for (int i = 0; i < GameConstants.GUILD_GRADE_MAX; i++) {
                    request.gradeNames.add(inPacket.decodeString());
                }
            }
            case ChangeGrade -> {
                request.targetId = inPacket.decodeInt();
                request.allianceRank = GuildRank.getByValue(inPacket.decodeByte());
            }
            case SetNotice -> {
                request.allianceNotice = inPacket.decodeString();
            }
            case null -> {
                throw new IllegalStateException(String.format("Unknown alliance request type %d", type));
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled alliance request type %d", type));
            }
        }
        return request;
    }

    public static AllianceRequest loadAlliance(int allianceId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Load);
        request.allianceId = allianceId;
        return request;
    }

    public static AllianceRequest createNewAlliance(String allianceName) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Create);
        request.allianceName = allianceName;
        return request;
    }

    public static AllianceRequest inviteAlliance(String targetName) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Invite);
        request.targetName = targetName;
        return request;
    }

    public static AllianceRequest joinAlliance(int inviterId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Join);
        request.inviterId = inviterId;
        return request;
    }

    public static AllianceRequest withdrawAlliance(int allianceId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Withdraw);
        request.allianceId = allianceId;
        return request;
    }

    public static AllianceRequest kickAlliance(int targetId, String targetName) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Kick);
        request.targetId = targetId;
        request.targetName = targetName;
        return request;
    }

    public static AllianceRequest removeAlliance(int allianceId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Destroy);
        request.allianceId = allianceId;
        return request;
    }

    public static AllianceRequest incMaxMemberNum(int memberMax) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.UpdateMemberCountMax);
        request.memberMax = memberMax;
        return request;
    }

    public static AllianceRequest setGradeName(List<String> gradeNames) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.SetGradeName);
        request.gradeNames = gradeNames;
        return request;
    }

    public static AllianceRequest setMemberGrade(int targetId, GuildRank allianceRank) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.ChangeGrade);
        request.targetId = targetId;
        request.allianceRank = allianceRank;
        return request;
    }

    public static AllianceRequest setNotice(String notice) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.SetNotice);
        request.allianceNotice = notice;
        return request;
    }
}
