package kinoko.server.alliance;

import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.List;

public final class AllianceRequest implements Encodable {
    private final AllianceRequestType requestType;
    private int allianceId;
    private int guildId;
    private String guildName;
    
    private int inviterId;
    private int oldMasterId;
    private int newMasterId;
    private List<String> gradeNames;
    private int targetId;
    private boolean gradeUp;
    private String notice;

    public AllianceRequest(AllianceRequestType requestType) {
        this.requestType = requestType;
    }

    public AllianceRequestType getRequestType() {
        return requestType;
    }

    public int getAllianceId() {
        return allianceId;
    }

    public int getGuildId() {
        return guildId;
    }
    
    public String getGuildName() {
        return guildName;
    }

    public int getInviterId() {
        return inviterId;
    }

    public int getOldMasterId() {
        return oldMasterId;
    }

    public int getNewMasterId() {
        return newMasterId;
    }

    public int getTargetId() {
        return targetId;
    }

    public boolean isGradeUp() {
        return gradeUp;
    }

    public String getNotice() {
        return notice;
    }

    @Override
    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(requestType.getValue());
        switch (requestType) {
            case Load -> {
                outPacket.encodeInt(allianceId);
            }
            case Withdraw -> {
            }
            case Invite -> {
                outPacket.encodeString(guildName);
            }
            case Join -> {
                outPacket.encodeInt(inviterId);
                outPacket.encodeString(guildName);
            }
            case Kick -> {
                outPacket.encodeInt(guildId);
            }
            case ChangeMaster -> {
                outPacket.encodeInt(oldMasterId);
                outPacket.encodeInt(newMasterId);
            }
            case SetGradeName -> {
                for (int i = 0; i < GameConstants.UNION_GRADE_MAX; i++) {
                    outPacket.encodeString(gradeNames.get(i));
                }
            }
            case ChangeGrade -> {
                outPacket.encodeInt(targetId);
                outPacket.encodeByte(gradeUp);
            }
            case SetNotice -> {
                outPacket.encodeString(notice);
            }
        }
    }

    public static AllianceRequest decode(InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.getByValue(type));
        switch (request.requestType) {
            case Load -> {
                request.allianceId = inPacket.decodeInt();
            }
            case Withdraw -> {
            }
            case Invite -> {
                request.guildName = inPacket.decodeString();
            }
            case Join -> {
                request.inviterId = inPacket.decodeInt();
                request.guildName = inPacket.decodeString();
            }
            case Kick -> {
                request.guildId = inPacket.decodeInt();
            }
            case ChangeMaster -> {
                request.oldMasterId = inPacket.decodeInt();
                request.newMasterId = inPacket.decodeInt();
            }
            case SetGradeName -> {
                request.gradeNames = new ArrayList<>();
                for (int i = 0; i < GameConstants.UNION_GRADE_MAX; i++) {
                    request.gradeNames.add(inPacket.decodeString());
                }
            }
            case ChangeGrade -> {
                request.targetId = inPacket.decodeInt();
                request.gradeUp = inPacket.decodeBoolean();
            }
            case SetNotice -> {
                request.notice = inPacket.decodeString();
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

    public static AllianceRequest load(int allianceId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Load);
        request.allianceId = allianceId;
        return request;
    }

    public static AllianceRequest withdraw() {
        return new AllianceRequest(AllianceRequestType.Withdraw);
    }

    public static AllianceRequest invite(String guildName) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Invite);
        request.guildName = guildName;
        return request;
    }

    public static AllianceRequest join(int inviterId, String guildName) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Invite);
        request.inviterId = inviterId;
        request.guildName = guildName;
        return request;
    }

    public static AllianceRequest kick(int guildId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Kick);
        request.guildId = guildId;
        return request;
    }

    public static AllianceRequest changeMaster(int oldMasterId, int newMasterId) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Invite);
        request.oldMasterId = oldMasterId;
        request.newMasterId = newMasterId;
        return request;
    }

    public static AllianceRequest setGradeName(List<String> gradeNames) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.Kick);
        request.gradeNames = gradeNames;
        return request;
    }

    public static AllianceRequest changeGrade(int targetId, boolean gradeUp) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.ChangeGrade);
        request.targetId = targetId;
        request.gradeUp = gradeUp;
        return request;
    }

    public static AllianceRequest setNotice(String notice) {
        final AllianceRequest request = new AllianceRequest(AllianceRequestType.SetNotice);
        request.notice = notice;
        return request;
    }
}
