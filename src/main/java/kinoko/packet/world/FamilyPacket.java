package kinoko.packet.world;

import kinoko.server.Server;
import kinoko.server.family.FamilyResultType;
import kinoko.server.family.FamilyTree;
import kinoko.server.header.OutHeader;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;

import java.util.Optional;

public final class FamilyPacket {
    // CWvsContext::OnFamilyResult -------------------------------------------------------------------------------------
    public static OutPacket registerJuniorSuccess(User leader, User junior) {
        final OutPacket outPacket = FamilyPacket.of(FamilyResultType.RegisterJunior_Success, 0);
        outPacket.encodeInt(junior.getCharacterId());
        outPacket.encodeString(junior.getCharacterName());
        outPacket.encodeInt(junior.getLevel());
        outPacket.encodeInt(junior.getJob());
        return outPacket;
    }

    /**
     * CWvsContext::OnFamilyJoinRequest
     * Builds a Family Join Request / Invite packet.
     *
     * This packet prompts the client with a yes/no dialog asking
     * the target player to accept a family invitation.
     *
     * Packet Structure (v95):
     *   byte   OutHeader.FamilyResult (we reuse FamilyResult for simplicity)
     *   int    FamilyResultType (use a dedicated type like RegisterJunior_Invite)
     *   int    leaderCharacterId
     *   int    leaderJob
     *   String leaderName
     *   String requesterName
     *
     * @param senior       The user sending the invite (senior)
     * @param targetUser    The user being invited (junior)
     * @return The encoded packet to send to the client
     */
    public static OutPacket createFamilyInvite(User senior, User targetUser) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyJoinRequest);

        outPacket.encodeInt(senior.getCharacterId());
        outPacket.encodeInt(senior.getLevel());
        outPacket.encodeInt(senior.getJob());
        outPacket.encodeString(senior.getCharacterName());

        return outPacket;
    }

    /**
     * Creates a packet to inform the original inviter of the family join result.
     * This packet is sent to the senior character after the junior has responded.
     *
     * @param responderName The name of the character who responded (the junior).
     * @param accepted      True if the invitation was accepted, false if it was declined.
     * @return The encoded packet to send to the inviter's client.
     */
    public static OutPacket createFamilyJoinRequestResult(String responderName, boolean accepted) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyJoinRequestResult);
        outPacket.encodeByte(accepted ? 1 : 0);
        outPacket.encodeString(responderName);
        return outPacket;
    }

    /**
     * Creates a packet indicating that a family join request has been accepted.
     * This packet is sent only to the senior member who approved the request.
     * It contains the senior member's name for client-side display or logging.
     *
     * @param seniorName the name of the senior family member who accepted the request
     * @return an OutPacket representing the FamilyJoinAccepted response
     */
    public static OutPacket createFamilyJoinAccepted(String seniorName) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyJoinAccepted);
        outPacket.encodeString(seniorName);
        return outPacket;
    }

    public static OutPacket unregisterJunior(int juniorId) {
        final OutPacket outPacket = FamilyPacket.of(FamilyResultType.UnregisterJunior, 0);
        outPacket.encodeInt(juniorId);
        return outPacket;
    }

    public static OutPacket summonJunior(User leader, User junior) {
        final OutPacket outPacket = FamilyPacket.of(FamilyResultType.SummonJunior, 0);
        outPacket.encodeInt(junior.getCharacterId());
        outPacket.encodeString(junior.getCharacterName());
        return outPacket;
    }

    public static OutPacket entitlementError(String message) {
        final OutPacket outPacket = FamilyPacket.of(FamilyResultType.EntitlementError, 0);
        outPacket.encodeString(message != null ? message : "");
        return outPacket;
    }

    public static OutPacket userFamilyInfo(User user) {
        FamilyMember familyInfo = user.getFamilyInfo();
        if (familyInfo == null){
            familyInfo = FamilyMember.EMPTY;
        }
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyInfoResult);
        familyInfo.encode(outPacket);
        return outPacket;
    }

    public static OutPacket userFamilyChart(User user) {
        Optional<FamilyTree> optionalTree = Server.getCentralServerNode().getFamilyTree(user.getCharacterId());
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyChartResult);

        if (optionalTree.isPresent()) {
            FamilyTree familyTree = optionalTree.get();
            // encode the pedigree for this user
            familyTree.encodeChart(outPacket, user.getCharacterId());
        }
        else {
            return null;
        }
        return outPacket;
    }

    /**
     * Builds a Family Result packet.
     *
     * This packet is used for family-related error and informational messages
     * that may require an additional Mesos value, such as separation fees or
     * cost-related failures.
     *
     * The resultType determines the message shown to the client. These map to
     * the FamilyResultType enum values.
     *
     * Packet Structure (v95):
     *   byte   OutHeader.FamilyResult
     *   int    resultType  (from FamilyResultType)
     *   int    mesos       (used for fee-related messages; 0 if not applicable)
     *
     * @param resultType The family result type to display
     * @param mesos      Mesos amount required or related to the message (0 if not applicable)
     * @return The encoded FamilyResult packet
     */
    public static OutPacket of(FamilyResultType resultType, int mesos) {
        final OutPacket outPacket = OutPacket.of(OutHeader.FamilyResult);
        outPacket.encodeInt(resultType.getValue());
        outPacket.encodeInt(mesos);
        return outPacket;
    }
}
