package kinoko.handler.user;

import kinoko.handler.Handler;
import kinoko.packet.world.FamilyPacket;
import kinoko.server.Server;
import java.util.concurrent.locks.ReentrantLock;

import kinoko.server.family.FamilyEntitlement;
import kinoko.server.family.FamilyResultType;
import kinoko.server.family.FamilyTree;
import kinoko.server.header.InHeader;
import kinoko.server.node.CentralServerNode;
import kinoko.server.packet.InPacket;
import kinoko.server.packet.OutPacket;
import kinoko.util.exceptions.DumbDeveloperFoundException;
import kinoko.util.exceptions.InvalidInputException;
import kinoko.world.GameConstants;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public final class FamilyHandler {
    private static final Logger log = LogManager.getLogger(FamilyHandler.class);

    // FAMILY HANDLERS -------------------------------------------------------------------------------------------------

    /**
     * Handles a client's request for their family information.
     *
     * This method sends the user's FamilyMember information back to the client
     * by encoding it into a FamilyInfoResult packet. No global family lock is required
     * here because the user already holds a snapshot of their FamilyMember.
     *
     * Since this snapshot is local to the user and not shared mutable state,
     * reading it and sending the packet is thread-safe without acquiring any locks.
     *
     * @param user the user requesting their family information
     * @param inPacket the incoming packet containing the request (unused)
     */
    @Handler(InHeader.FamilyInfoRequest)
    public static void handleFamilyInfoRequest(User user, InPacket inPacket) {
        user.write(FamilyPacket.userFamilyInfo(user));
    }

    /**
     * Handles a client's request for their family chart.
     *
     * This method generates a FamilyChart packet for the user by reading shared
     * family data. The global family lock is acquired only while constructing
     * the packet to ensure thread-safe access to shared FamilyTree and FamilyMember
     * objects. Once the packet is created, the lock is released before sending
     * it to the user, since writing the packet does not require access to shared data.
     *
     * If the user is not part of a family or the packet cannot be created,
     * no packet is sent.
     *
     * @param user the user requesting their family chart
     * @param inPacket the incoming packet containing the request (unused)
     */
    @Handler(InHeader.FamilyChartRequest)
    public static void handleFamilyChartRequest(User user, InPacket inPacket) {
        CentralServerNode centralServerNode = Server.getCentralServerNode();
        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();

        OutPacket outPacket;
        try {
            outPacket = FamilyPacket.userFamilyChart(user);
            if (outPacket == null){
                return;
            }
        }
        finally {
            lock.unlock();
        }

        user.write(outPacket);
    }

    /**
     * Handles a family join response when a user accepts or rejects an invitation
     * to become a junior of a senior.
     *
     * This method:
     * - Validates that the inviter exists and both users are eligible.
     * - Locks the global family structure to safely update FamilyMember objects
     *   and FamilyTrees.
     * - Updates parent/child relationships and moves any subtrees as needed.
     * - Prepares response packets inside the lock, then sends them after unlocking.
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

        // Packets to send after unlocking
        OutPacket outToUser;
        OutPacket outToSenior;

        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();
        try {
            FamilyMember userMember;

            if (user.getFamilyInfo().isDefault()) {
                userMember = new FamilyMember(
                        user.getCharacterId(),
                        user.getCharacterName(),
                        user.getLevel(),
                        user.getJob(),
                        0,
                        0,
                        0,
                        0,
                        inviterID
                );
            } else {
                userMember = user.getFamilyInfo();
                if (userMember.getParentId() != null) {
                    user.write(FamilyPacket.of(FamilyResultType.AlreadyJuniorOfAnother, 0));
                    return;
                }
            }

            FamilyMember seniorMember;
            if (seniorUser.getFamilyInfo().isDefault()) {
                seniorMember = new FamilyMember(
                        seniorUser.getCharacterId(),
                        seniorUser.getCharacterName(),
                        seniorUser.getLevel(),
                        seniorUser.getJob(),
                        0,
                        0,
                        0,
                        0,
                        null
                );
            } else {
                seniorMember = seniorUser.getFamilyInfo();
            }

            if (seniorMember.getChildrenCount() >= GameConstants.MAX_FAMILY_CHILDREN_COUNT) {
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
                    seniorTree.addSubTree(userTreeOpt.get(), inviterID);  // has their own subtree
                } else {
                    seniorTree.addMember(userMember, inviterID);  // single member
                }
            }

            centralServerNode.updateFamilyTree(seniorTree);  // update lookups

            // prepare packets
            outToSenior = FamilyPacket.createFamilyJoinRequestResult(user.getCharacterName(), accepted);
            outToUser = FamilyPacket.createFamilyJoinAccepted(inviterName);
        }
        finally {
            lock.unlock();
        }

        // send packets outside of lock
        user.write(outToUser);
        user.write(FamilyPacket.loadFamilyEntitlements(!user.getFamilyInfo().hasFamily()));
        updateFamilyDisplay(user);

        seniorUser.write(outToSenior);
        seniorUser.write(FamilyPacket.loadFamilyEntitlements(!seniorUser.getFamilyInfo().hasFamily()));
        updateFamilyDisplay(seniorUser);  // not necessary, but is smoother if they have the dialog open.
    }

    /**
     * Handles a request to register another user as the sender's junior in the family system.
     *
     * Validates that the target exists, meets level requirements, is not already a junior,
     * and is not in the same family. Uses a global family lock to ensure thread-safe access
     * to the family tree during validation and invite creation. If all checks pass, sends a
     * family invite to the target. Otherwise, sends an appropriate error to the sender.
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
        if (user.getLevel() < GameConstants.MIN_FAMILY_LEVEL || targetUser.getLevel() < GameConstants.MIN_FAMILY_LEVEL) {
            user.write(FamilyPacket.of(FamilyResultType.JuniorMustBeOverLevel10, 0));
            return;
        }

        // Level gap check (must be within x levels)
        int levelGap = Math.abs(user.getLevel() - targetUser.getLevel());
        if (levelGap > GameConstants.MAX_LEVEL_GAP_FOR_FAMILY) {
            user.write(FamilyPacket.of(FamilyResultType.LevelGapTooHigh, 0));
            return;
        }

        if (targetUser.getFamilyInfo().getParentId() != null){
            user.write(FamilyPacket.of(FamilyResultType.AlreadyJuniorOfAnother, 0));
            return;
        }

        OutPacket userPacket = null;
        OutPacket targetPacket = null;

        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();
        try {
            // make sure both are not in the same family
            Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
            Optional<FamilyTree> targetTreeOpt = centralServerNode.getFamilyTree(targetUser.getCharacterId());
            if (userTreeOpt.isPresent() && targetTreeOpt.isPresent() && userTreeOpt.get() == targetTreeOpt.get()) {
                userPacket = FamilyPacket.of(FamilyResultType.SameFamily, 0);  // cannot invite.
            }
            else {
                targetPacket = FamilyPacket.createFamilyInvite(user);  // can invite
            }
        }
        finally {
            lock.unlock();
        }

        // send outside the lock
        if (userPacket != null){
            user.write(userPacket);
            updateFamilyDisplay(user);
        }
        if (targetPacket != null) {
            targetUser.write(targetPacket);
            updateFamilyDisplay(targetUser);  // not necessary, but is smoother if they have the dialog open.
        }
    }


    @Handler(InHeader.FamilySetPrecept)
    public static void handleFamilySetPrecept(User user, InPacket inPacket) {
        String message = inPacket.decodeString();
        String feedbackMessage;

        if (!user.getFamilyInfo().isLeader()) {  // no longer in the same family as when they used the precept.
            user.write(FamilyPacket.of(FamilyResultType.DifferentFamily, 0));
            return;
        }

        CentralServerNode centralServerNode = Server.getCentralServerNode();
        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();
        try {
            Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
            if (userTreeOpt.isPresent()) {
                FamilyTree userTree = userTreeOpt.get();
                userTree.setFamilyMessage(message);
                feedbackMessage = "The family message has been set to: " + message;
            } else {
                feedbackMessage = "Failed to set your family message to: " + message;
            }
        } finally {
            lock.unlock();
        }

        user.systemMessage(feedbackMessage);
        updateFamilyDisplay(user);
    }


    /**
     * Handles a user's response to a Family Summon request.
     *
     * A requester may only have one active summon request at a time. If the
     * responder rejects the request, the requester is refunded the entitlement.
     * If the responder accepts but is offline, the responder is notified and the
     * summon fails. If accepted and both users are online, the responder is
     * teleported to the requester. If the responder never replies, the requester
     * does not receive an entitlement refund.
     */
    @Handler(InHeader.FamilySummonResult)
    public static void handleFamilySummonResult(User user, InPacket inPacket) {
        String requesterName = inPacket.decodeString();
        boolean accepted = inPacket.decodeBoolean();

        User requester = Server.getCentralServerNode()
                .getUserByCharacterName(requesterName)
                .orElse(null);

        // --- Accepted but requester offline ---
        if (accepted && requester == null) {
            user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
            return;
        }

        // --- Rejected ---
        if (!accepted) {
            // Requester online -> refund them
            if (requester != null) {
                requester.getFamilyInfo().rollbackEntitlementUsage(FamilyEntitlement.SUMMON_FAMILY);
                requester.systemMessage(user.getCharacterName() + " has rejected your summon invite.");
            }

            // Notify the rejecting user
            user.systemMessage("You have rejected " + requesterName + "'s summon invitation!");
            return;
        }

        // --- Accepted and requester online ---
        // TODO: Restrict player from teleporting to maps that aren't allowed.
        // Not a popup, "The summons has failed. Your current location or state does not allow a summons."
        // requester.write(FamilyPacket.of(FamilyResultType.SummonFailed, 0));

        user.warpTo(requester);
        user.systemMessage("You have teleported to " + requesterName);
        requester.systemMessage(user.getCharacterName() + " has been teleported to you.");
    }

    /**
     * Handles a request to unregister a junior from the user's family.
     *
     * The global family lock is used while validating the user and junior,
     * and while modifying the family trees to ensure thread-safe updates.
     * Once packets are prepared, the lock is released before sending them
     * to the user and junior, as writing packets does not require locking.
     *
     * If the junior is invalid, not a child of the user, or offline, the
     * user receives an "IncorrectOrOffline" packet.
     *
     * @param user the user requesting to unregister a junior
     * @param inPacket the incoming packet containing the junior ID
     */
    @Handler(InHeader.FamilyUnregisterJunior)
    public static void handleFamilyUnregisterJunior(User user, InPacket inPacket) {
        int juniorID = inPacket.decodeInt();

        CentralServerNode centralServerNode = Server.getCentralServerNode();

        OutPacket userResultPacket;
        Optional<User> juniorUser = Optional.empty();

        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();
        try {

            Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());
            Optional<FamilyTree> juniorTreeOpt = centralServerNode.getFamilyTree(juniorID);

            if (userTreeOpt.isPresent() && juniorTreeOpt.isPresent()) {
                FamilyTree userTree = userTreeOpt.get();
                FamilyTree juniorTree = juniorTreeOpt.get();
                FamilyMember juniorMember = juniorTree.getMember(juniorID);
                Integer parentId = juniorMember.getParentId();
                if (parentId != null && !parentId.equals(user.getCharacterId())) {
                    userResultPacket = FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0);
                }
                else {
                    // unregister junior and extract subtree
                    FamilyTree juniorSubTree = userTree.extractAndRemoveSubTree(juniorID);
                    centralServerNode.addFamilyTree(juniorSubTree);

                    userResultPacket = FamilyPacket.unregisterJunior(juniorID);
                    juniorUser = centralServerNode.getUserByCharacterId(juniorID);
                }
            } else {  // something went wrong
                userResultPacket = FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0);
            }
        }
        finally {
            lock.unlock();
        }

        user.write(userResultPacket);
        // update the entitlements the user sees.
        user.write(FamilyPacket.loadFamilyEntitlements(!user.getFamilyInfo().hasFamily()));

        // Update Family Pedigrees and Information to the user and ex-junior client (if they are online).
        updateFamilyDisplay(user);

        juniorUser.ifPresent(targetUser -> {
            // let the user know they are an orphan 😢
            targetUser.systemMessage("You have been kicked out of your family by %s.", user.getCharacterName());
            targetUser.write(FamilyPacket.loadFamilyEntitlements(!targetUser.getFamilyInfo().hasFamily()));
            updateFamilyDisplay(targetUser);  // not necessary, but is smoother if they have the dialog open.
        });
    }

    /**
     * Handles a request from a user to unregister themselves from their parent (senior) in the family system.
     *
     * The client triggers this handler using the {@link InHeader#FamilyUnregisterParent} packet.
     * This request removes the user from their parent's family tree and creates a separate family tree
     * for the user if necessary.
     *
     * Thread safety is ensured using the global family lock while validating the user and parent,
     * and while modifying the family trees.
     *
     * Key behaviors:
     *  - If the user has no parent or their parent tree cannot be found, the user receives an
     *    {@link FamilyResultType#IncorrectOrOffline} response.
     *  - If successful, the user's subtree is extracted from the parent's tree and added as a new tree.
     *  - The user receives a success packet notifying them they have been removed from the parent's family.
     *  - If the parent is online, they are notified via a system message that the user has left their family.
     *
     * Note: The incoming packet contains no additional data beyond the header. The server
     * determines the parent to unregister by looking up the user's current family tree.
     *
     * @param user the user requesting to unregister from their parent
     * @param inPacket the incoming packet (header only; no payload is used)
     */
    @Handler({InHeader.FamilyUnregisterParent})
    public static void handleFamilyUnregisterParent(User user, InPacket inPacket) {
        CentralServerNode centralServerNode = Server.getCentralServerNode();

        OutPacket userResultPacket;
        Optional<User> parentUser;

        Integer parentId = null;
        ReentrantLock lock = centralServerNode.getGlobalFamilyLock();
        lock.lock();
        try {
            Optional<FamilyTree> userTreeOpt = centralServerNode.getFamilyTree(user.getCharacterId());

            if (userTreeOpt.isPresent()) {
                FamilyTree userTree = userTreeOpt.get();
                FamilyMember userMember = userTree.getMember(user.getCharacterId());
                parentId = userMember.getParentId();

                if (parentId == null) {
                    userResultPacket = FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0);
                }
                else {
                    Optional<FamilyTree> parentTreeOpt = centralServerNode.getFamilyTree(parentId);

                    if (parentTreeOpt.isPresent()) {
                        FamilyTree parentTree = parentTreeOpt.get();

                        if (parentTree != userTree){
                            // Should never occur if coded correctly, identical to DumbDeveloperFoundException.
                            log.error(
                                    "Family tree mismatch detected: user [{}] (ID: {}) parent (ID: {}) " +
                                            "trees do not match. UserTree={}, ParentTree={}",
                                    user.getCharacterName(),
                                    user.getCharacterId(),
                                    parentId,
                                    userTree.getLeaderId(),
                                    parentTree.getLeaderId()
                            );
                            userResultPacket = FamilyPacket.of(FamilyResultType.DifferentFamily, 0);
                        }
                        else {
                            // the junior's current tree becomes a new, separate family tree
                            FamilyTree userNewTree = parentTree.extractAndRemoveSubTree(user.getCharacterId());
                            centralServerNode.addFamilyTree(userNewTree);

                            // notify the user of the success
                            userResultPacket = FamilyPacket.unregisterJunior(user.getCharacterId());
                        }
                    } else {
                        // data inconsistency, parent's tree not found
                        userResultPacket = FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0);
                    }
                }
            } else { // user's own tree not found, something is wrong
                userResultPacket = FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0);
            }
        } finally {
            lock.unlock();
        }

        user.write(userResultPacket);
        // update the entitlements the user sees.
        user.write(FamilyPacket.loadFamilyEntitlements(!user.getFamilyInfo().hasFamily()));

        // Update the family pedigree and info for the user who just left.
        updateFamilyDisplay(user);

        if (parentId != null) {  // let the senior know.
            parentUser = centralServerNode.getUserByCharacterId(parentId);

            parentUser.ifPresent(targetUser -> {
                targetUser.systemMessage("%s has left your family.", user.getCharacterName());
                updateFamilyDisplay(targetUser);  // not necessary, but is smoother if they have the dialog open.
            });
        }
    }

    /**
     * Handles a user's request to use a Family Entitlement.
     *
     * - Checks if the user is in a family; resets entitlements if not.
     * - Attempts to use the entitlement via `tryUseEntitlementWithRollback`,
     *   which handles cooldowns, invalid targets, and rollback automatically.
     * - Applies the entitlement effects depending on its type:
     *   - Type 1: target-based (e.g., FAMILY_REUNION, SUMMON_FAMILY)
     *   - Type 2: self or family-wide effects (e.g., FAMILY_HASTE, SELF_EXP_1_5)
     *
     * All exceptions related to spam or invalid input are handled inside
     * `tryUseEntitlementWithRollback`.
     *
     * @param user the user using the entitlement
     * @param inPacket packet containing entitlement usage info
     */
    @Handler({InHeader.FamilyUsePrivilege})
    public static void handleFamilyUsePrivilege(User user, InPacket inPacket) {
        int entitlementId = inPacket.decodeInt();
        FamilyEntitlement entitlement = FamilyEntitlement.values()[entitlementId];

        FamilyMember userMember = user.getFamilyInfo();
        if (!userMember.hasFamily()){  // A user should not see entitlements if they are not in a family.
            user.write(FamilyPacket.of(FamilyResultType.DifferentFamily, 0));  // User has no family.
            user.write(FamilyPacket.loadFamilyEntitlements(true)); // reset the entitlements they can see.
            user.systemMessage("To use an entitlement, you must have a family!");
            return;
        }

        if (!userMember.canUse(entitlement)){  // protect entitlements from spammers.
            // Sending a fake popup box, so they can't spam it as much if they have a high latency to the server.
            user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
            return;
        };

        // use the entitlement and roll it back later if failed, this helps prevents spamming and hackers
        boolean success = userMember.tryUseEntitlementWithRollback(entitlement, () -> {
            handleFamilyInfoRequest(user, null);  // will disable the button for the user.

            User targetUser = null;

            if (entitlement.getType() == 1) {  // this entitlement requires a character name input
                String targetName = inPacket.decodeString();
                targetUser = Server.getCentralServerNode()
                        .getUserByCharacterName(targetName)
                        .orElse(null);
                if (targetUser == null) {
                    user.write(FamilyPacket.of(FamilyResultType.IncorrectOrOffline, 0));
                    throw new InvalidInputException("Entitlement use blocked by invalid target user");
                }

                // Type 1 Cases
                switch (entitlement) {
                    case FAMILY_REUNION:
                        // TODO: Restrict player from teleporting to maps that aren't allowed.

                        // if the user is in the cash shop, the user will be teleported to the map they are in.
                        user.warpTo(targetUser);
                        user.systemMessage("Teleported to %s's location.", targetName);
                        break;
                    case SUMMON_FAMILY:
                        // TODO: Restrict player from teleporting to maps that aren't allowed.
                        // Not a popup, "The summons has failed. Your current location or state does not allow a summons."
                        // user.write(FamilyPacket.of(FamilyResultType.SummonFailed, 0));
                        // throw new InvalidInputException("Entitlement use blocked by restricted map.");
                        targetUser.write(FamilyPacket.createSummonRequest(user));
                        break;
                    default:
                        break;

                }
            }
            else {  // Type 2 cases
                switch (entitlement) {
                    case FAMILY_HASTE:
                        System.out.println("Applying FAMILY_HASTE to all family members");
                        // TODO: apply haste buff
                        break;

                    case FAMILY_EXP:
                        System.out.println("Applying FAMILY_EXP to all family members");
                        // TODO: apply exp boost
                        break;

                    case FAMILY_DROP:
                        System.out.println("Applying FAMILY_DROP to all family members");
                        // TODO: apply drop boost
                        break;

                    case SELF_DROP_1_5:
                        System.out.println("Applying SELF_DROP_1_5 to user");
                        // TODO: apply self drop boost
                        break;

                    case SELF_EXP_1_5:
                        System.out.println("Applying SELF_EXP_1_5 to user");
                        // TODO: apply self exp boost
                        break;
                    default:
                        System.out.println("Unhandled FamilyEntitlement: " + entitlement);
                        break;
                }
            }
        });

//        System.out.println("Handled FamilyUsePrivilege: " + entitlement + ", target: " + targetUser);
    }

    /**
     * Sends the latest family chart and family information to the specified user.
     *
     * This is a convenience method to avoid repeatedly calling
     * handleFamilyChartRequest() and handleFamilyInfoRequest() together.
     *
     * @param user the user whose family data should be refreshed
     */
    private static void updateFamilyDisplay(User user) {
        handleFamilyChartRequest(user, null);
        handleFamilyInfoRequest(user, null);
    }
}
