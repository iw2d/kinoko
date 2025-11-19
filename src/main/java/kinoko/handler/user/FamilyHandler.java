package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.FamilyPacket;
import kinoko.server.Server;
import java.util.concurrent.locks.ReentrantLock;
import kinoko.server.family.FamilyResultType;
import kinoko.server.family.FamilyTree;
import kinoko.server.header.InHeader;
import kinoko.server.node.CentralServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Optional;

public final class FamilyHandler {
    private static final Logger log = LogManager.getLogger(FamilyHandler.class);

    // FAMILY HANDLERS -------------------------------------------------------------------------------------------------
    @Handler(InHeader.FamilyInfoRequest)
    public static void handleFamilyInfoRequest(User user, InPacket inPacket) {
        user.write(FamilyPacket.userFamilyInfo(user));
    }

    @Handler(InHeader.FamilyChartRequest)
    public static void handleFamilyChartRequest(User user, InPacket inPacket) {
        OutPacket outPacket = FamilyPacket.userFamilyChart(user);
        if (outPacket == null){
            return;
        }
        user.write(outPacket);
        System.out.println("Handled Family Chart Request");
    }

    /**
     * Handles the result of a family join request when a target user responds
     * to an invitation from a senior (inviter) to become their junior.
     *
     * This method performs the following steps:
     * 1. Decodes the inviter ID, inviter name, and whether the invite was accepted from the packet.
     * 2. Validates that the inviter exists and is online.
     * 3. Initializes or retrieves FamilyMember objects for both the user and the senior.
     * 4. Checks if the user is already a junior of another member; if so, the request is rejected.
     * 5. Checks if the senior already has the maximum allowed number of juniors (2); if so, the request is rejected.
     * 6. Sets the parent ID of the user to the inviter ID to link the hierarchy.
     * 7. Ensures the senior has a FamilyTree in the central server node; if not, creates one.
     * 8. Moves the user (or their existing subtree) under the senior in the FamilyTree.
     * 9. Updates the central server's lookup for faster FamilyTree access.
     * 10. Sends the appropriate response packets to both the senior and the user.
     *
     * @param user the user responding to the family join request
     * @param inPacket the packet containing the join result data from the client
     */
    @Handler(InHeader.FamilyJoinResult)
    public static void handleFamilyJoinResult(User user, InPacket inPacket) {
        int inviterID = inPacket.decodeInt();
        String inviterName = inPacket.decodeString();
        boolean accepted = inPacket.decodeByte() != 0;

        CentralServerNode centralServerNode = Server.getCentralServerNode();
        Optional<User> inviterUserOpt = centralServerNode.getUserByCharacterName(inviterName);

        if (inviterUserOpt.isEmpty()){
            user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
            return;
        }

        User seniorUser = inviterUserOpt.get();

        FamilyMember userMember;
        if (user.getFamilyInfo().isDefault()){
            userMember = new FamilyMember(
                    user.getCharacterId(),
                    user.getCharacterName(),
                    user.getLevel(),
                    user.getJob(),
                    0,
                    0,
                    0,
                    0,
                    inviterID,
                    Collections.emptyMap()
            );
        }
        else {
            userMember = user.getFamilyInfo();
            if (userMember.getParentId() != null){
                user.write(FamilyPacket.of(FamilyResultType.AlreadyJuniorOfAnother, 0));
                return;
            }
        }

        FamilyMember seniorMember;
        if (seniorUser.getFamilyInfo().isDefault()){
            seniorMember = new FamilyMember(
                    seniorUser.getCharacterId(),
                    seniorUser.getCharacterName(),
                    seniorUser.getLevel(),
                    seniorUser.getJob(),
                    0,
                    0,
                    0,
                    0,
                    null,
                    Collections.emptyMap()
            );
        }
        else {
            seniorMember = seniorUser.getFamilyInfo();
        }

        if (seniorMember.getChildrenCount() >= 2){
            user.write(FamilyPacket.of(FamilyResultType.CannotAddJunior, 0));
            return;
        }

        userMember.setParentId(inviterID);

        seniorUser.setFamilyInfo(seniorMember);
        user.setFamilyInfo(userMember);

        // Trees
        Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
        Optional<FamilyTree> seniorTreeOpt = centralServerNode.getFamilyTree(seniorUser.getCharacterId());


        FamilyTree seniorTree = seniorTreeOpt.orElseGet(() -> {
                    FamilyTree newTree = new FamilyTree(seniorMember);
                    centralServerNode.addFamilyTree(newTree);
                    return newTree;
                });

        // Move the user (or their subtree) under the senior
        if (!seniorTree.hasMember(userMember.getCharacterId())) {
            if (userTreeOpt.isPresent()) {
                // User has their own subtree
                seniorTree.addSubTree(userTreeOpt.get(), inviterID);
            } else {
                // User is a single member
                seniorTree.addMember(userMember, inviterID);
            }
        }

        centralServerNode.updateFamilyTree(seniorTree);  // update lookups

        seniorUser.write(FamilyPacket.createFamilyJoinRequestResult(user.getCharacterName(), accepted));
        user.write(FamilyPacket.createFamilyJoinAccepted(inviterName));
    }

    /**
     * Handles a request from a user to register another user as their junior in the family system.
     *
     * This method performs several validations before sending an invite:
     * 1. Checks that the target user exists and is online.
     * 2. Ensures both users are at least level 10.
     * 3. Validates that the level difference between the inviter and target is no more than 50.
     * 4. Confirms that the target user is not already a junior of another user.
     * 5. Ensures that the inviter and target are not already in the same family.
     *
     * If all checks pass, the target user receives a family invite packet from the inviter.
     * Otherwise, an appropriate FamilyResultType error is sent to the inviter.
     *
     * @param user the user sending the junior registration request
     * @param inPacket the packet containing the target username
     */
    @Handler(InHeader.FamilyRegisterJunior)
    public static void handleFamilyRegisterJunior(User user, InPacket inPacket) {
        String targetUsername = inPacket.decodeString();

        CentralServerNode centralServerNode = Server.getCentralServerNode();
        Optional<User> targetUserOpt = centralServerNode.getUserByCharacterName(targetUsername);

        if (targetUserOpt.isEmpty()){
            user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
            return;
        }

        User targetUser = targetUserOpt.get();

        // Both users must be at least level 10
        if (user.getLevel() < 10 || targetUser.getLevel() < 10) {
            user.write(FamilyPacket.of(FamilyResultType.JuniorMustBeOverLevel10, 0));
            return;
        }

        // Level gap check (must be within 50 levels)
        int levelGap = Math.abs(user.getLevel() - targetUser.getLevel());
        if (levelGap > 50) {
            user.write(FamilyPacket.of(FamilyResultType.LevelGapTooHigh, 0));
            return;
        }

        if (targetUser.getFamilyInfo().getParentId() != null){
            user.write(FamilyPacket.of(FamilyResultType.AlreadyJuniorOfAnother, 0));
            return;
        }

        // make sure both are not in the same family
        Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
        Optional<FamilyTree> targetTreeOpt = centralServerNode.getFamilyTree(targetUser.getCharacterId());
        if (userTreeOpt.isPresent() && targetTreeOpt.isPresent() && userTreeOpt.get() == targetTreeOpt.get()) {
            user.write(FamilyPacket.of(FamilyResultType.SameFamily, 0));
            return;
        }

        targetUser.write(FamilyPacket.createFamilyInvite(user, targetUser));
    }


    @Handler(InHeader.FamilySetPrecept)
    public static void handleFamilySetPrecept(User user, InPacket inPacket) {
        System.out.println("Handled FamilySetPrecept");
    }


    @Handler(InHeader.FamilySummonResult)
    public static void handleFamilySummonResult(User user, InPacket inPacket) {
        System.out.println("Handled FamilySummonResult");
    }

    @Handler(InHeader.FamilyUnregisterJunior)
    public static void handleFamilyUnregisterJunior(User user, InPacket inPacket) {
        int juniorID = inPacket.decodeInt();

        CentralServerNode centralServerNode = Server.getCentralServerNode();

        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();

        try {

            Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
            Optional<FamilyTree> juniorTreeOpt = centralServerNode.getFamilyTree(juniorID);

            if (userTreeOpt.isPresent() && juniorTreeOpt.isPresent()) {
                FamilyTree userTree = userTreeOpt.get();
                FamilyTree juniorTree = juniorTreeOpt.get();
                FamilyMember juniorMember = juniorTree.getMember(juniorID);
                if (juniorMember.getParentId() != user.getCharacterId()) {
                    user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
                    return;
                }

                // unregister junior and extract subtree
                FamilyTree juniorSubTree = userTree.extractAndRemoveSubTree(juniorID);
                centralServerNode.addFamilyTree(juniorSubTree);

                // todo: test with bigger trees
                // todo: write to the target user if they're online.
                user.write(FamilyPacket.unregisterJunior(juniorID));

            } else {  // something went wrong
                user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
                return;
            }
        }
        finally {
            lock.unlock();
        }

    }

    @Handler({InHeader.FamilyUnregisterParent})
    public static void handleFamilyUnregisterParent(User user, InPacket inPacket) {
        System.out.println("Handled FamilyUnregisterParent");
    }

    @Handler({InHeader.FamilyUsePrivilege})
    public static void handleFamilyUsePrivilege(User user, InPacket inPacket) {
        System.out.println("Handled FamilyUsePrivilege");
    }

//
//    @Handler(InHeader.PartyRequest)
//    public static void handlePartyRequest(User user, InPacket inPacket) {
//        final int type = inPacket.decodeByte();
//        final PartyRequestType requestType = PartyRequestType.getByValue(type);
//        switch (requestType) {
//            case CreateNewParty -> {
//                // CField::SendCreateNewPartyMsg
//                if (user.hasParty()) {
//                    user.write(PartyPacket.of(PartyResultType.CreateNewParty_AlreadyJoined));
//                    return;
//                }
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.createNewParty());
//            }
//            case WithdrawParty -> {
//                // CField::SendWithdrawPartyMsg
//                if (!user.hasParty()) {
//                    user.write(PartyPacket.of(PartyResultType.WithdrawParty_NotJoined));
//                    return;
//                }
//                inPacket.decodeByte(); // hardcoded 0
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.withdrawParty());
//            }
//            case JoinParty -> {
//                // CWvsContext::OnPartyResult
//                if (user.hasParty()) {
//                    user.write(PartyPacket.of(PartyResultType.JoinParty_AlreadyJoined));
//                    return;
//                }
//                final int inviterId = inPacket.decodeInt();
//                inPacket.decodeByte(); // unknown byte from InviteParty
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
//            }
//            case InviteParty -> {
//                // CField::SendJoinPartyMsg
//                if (user.hasParty() && !user.isPartyBoss()) {
//                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
//                    return;
//                }
//                final String targetName = inPacket.decodeString();
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.invite(targetName));
//            }
//            case KickParty -> {
//                // CField::SendKickPartyMsg
//                if (!user.isPartyBoss()) {
//                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
//                    return;
//                }
//                final int targetId = inPacket.decodeInt();
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.kickParty(targetId));
//            }
//            case ChangePartyBoss -> {
//                // CField::SendChangePartyBossMsg
//                if (!user.isPartyBoss()) {
//                    user.write(PartyPacket.serverMsg("You are not the leader of the party."));
//                    return;
//                }
//                final int targetId = inPacket.decodeInt();
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.changePartyBoss(targetId, false));
//            }
//            case null -> {
//                log.error("Unknown party request type : {}", type);
//            }
//            default -> {
//                log.error("Unhandled party request type : {}", requestType);
//            }
//        }
//    }
//
//    @Handler(InHeader.PartyResult)
//    public static void handlePartyResult(User user, InPacket inPacket) {
//        final int type = inPacket.decodeByte();
//        final PartyResultType resultType = PartyResultType.getByValue(type);
//        switch (resultType) {
//            case InviteParty_Sent, InviteParty_BlockedUser, InviteParty_AlreadyInvited,
//                    InviteParty_AlreadyInvitedByInviter, InviteParty_Rejected -> {
//                final int inviterId = inPacket.decodeInt();
//                final String message = switch (resultType) {
//                    // These messages are from the client string pool, but are not used (except for InviteParty_Sent)
//                    case InviteParty_Sent, InviteParty_BlockedUser ->
//                            String.format("You have invited '%s' to your party.", user.getCharacterName());
//                    case InviteParty_AlreadyInvited ->
//                            String.format("'%s' is taking care of another invitation.", user.getCharacterName());
//                    case InviteParty_AlreadyInvitedByInviter ->
//                            String.format("You have already invited '%s' to your party.", user.getCharacterName());
//                    case InviteParty_Rejected ->
//                            String.format("'%s' has declined the party request.", user.getCharacterName());
//                    default -> {
//                        throw new IllegalStateException("Unexpected party result type");
//                    }
//                };
//                user.getConnectedServer().submitUserPacketReceive(inviterId, PartyPacket.serverMsg(message));
//            }
//            case InviteParty_Accepted -> {
//                final int inviterId = inPacket.decodeInt();
//                user.getConnectedServer().submitPartyRequest(user, PartyRequest.joinParty(inviterId));
//            }
//            case null -> {
//                log.error("Unknown party result type : {}", type);
//            }
//            default -> {
//                log.error("Unhandled party result type : {}", resultType);
//            }
//        }
//    }
}
