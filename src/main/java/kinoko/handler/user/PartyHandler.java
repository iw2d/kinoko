package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.PartyPacket;
import kinoko.server.header.InHeader;
import kinoko.server.packet.InPacket;
import kinoko.server.party.PartyRequest;
import kinoko.server.party.PartyRequestType;
import kinoko.server.party.PartyResultType;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PartyHandler {
    private static final Logger log = LogManager.getLogger(PartyHandler.class);

    @Handler(InHeader.PartyRequest)
    public static void handlePartyRequest(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyRequestType requestType = PartyRequestType.getByValue(type);
        switch (requestType) {
            case CreateNewParty -> {
                // CField::SendCreateNewPartyMsg
                if (user.hasParty()) {
                    user.write(PartyPacket.of(PartyResultType.CreateNewParty_AlreadyJoined));
                    return;
                }
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.createNewParty());
            }
            case WithdrawParty -> {
                // CField::SendWithdrawPartyMsg
                if (!user.hasParty()) {
                    user.write(PartyPacket.of(PartyResultType.WithdrawParty_NotJoined));
                    return;
                }
                inPacket.decodeByte(); // hardcoded 0
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.withdrawParty());
            }
            case JoinParty -> {
                // CWvsContext::OnPartyResult
                if (user.hasParty()) {
                    user.write(PartyPacket.of(PartyResultType.JoinParty_AlreadyJoined));
                    return;
                }
                final int inviterId = inPacket.decodeInt();
                inPacket.decodeByte(); // unknown byte from InviteParty
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
            }
            case InviteParty -> {
                // CField::SendJoinPartyMsg
                if (user.hasParty() && !user.isPartyBoss()) {
                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
                    return;
                }
                final String targetName = inPacket.decodeString();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.invite(targetName));
            }
            case KickParty -> {
                // CField::SendKickPartyMsg
                if (!user.isPartyBoss()) {
                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
                    return;
                }
                final int targetId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.kickParty(targetId));
            }
            case ChangePartyBoss -> {
                // CField::SendChangePartyBossMsg
                if (!user.isPartyBoss()) {
                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
                    return;
                }
                final int targetId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.changePartyBoss(targetId, false));
            }
            case null -> {
                log.error("Unknown party request type : {}", type);
            }
            default -> {
                log.error("Unhandled party request type : {}", requestType);
            }
        }
    }

    @Handler(InHeader.PartyResult)
    public static void handlePartyResult(User user, InPacket inPacket) {
        final int type = inPacket.decodeByte();
        final PartyResultType resultType = PartyResultType.getByValue(type);
        switch (resultType) {
            case InviteParty_Sent, InviteParty_BlockedUser, InviteParty_AlreadyInvited,
                    InviteParty_AlreadyInvitedByInviter, InviteParty_Rejected -> {
                final int inviterId = inPacket.decodeInt();
                final String message = switch (resultType) {
                    // These messages are from the client string pool, but are not used (except for InviteParty_Sent)
                    case InviteParty_Sent, InviteParty_BlockedUser ->
                            String.format("You have invited '%s' to your party.", user.getCharacterName());
                    case InviteParty_AlreadyInvited ->
                            String.format("'%s' is taking care of another invitation.", user.getCharacterName());
                    case InviteParty_AlreadyInvitedByInviter ->
                            String.format("You have already invited '%s' to your party.", user.getCharacterName());
                    case InviteParty_Rejected ->
                            String.format("'%s' has declined the party request.", user.getCharacterName());
                    default -> {
                        throw new IllegalStateException("Unexpected party result type");
                    }
                };
                user.getConnectedServer().submitUserPacketReceive(inviterId, PartyPacket.serverMsg(message));
            }
            case InviteParty_Accepted -> {
                final int inviterId = inPacket.decodeInt();
                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
            }
            case null -> {
                log.error("Unknown party result type : {}", type);
            }
            default -> {
                log.error("Unhandled party result type : {}", resultType);
            }
        }
    }
}
