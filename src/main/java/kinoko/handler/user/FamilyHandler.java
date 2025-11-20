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
import kinoko.world.GameConstants;
import kinoko.world.user.FamilyMember;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
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
                        inviterID,
                        Collections.emptyMap()
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
                        null,
                        Collections.emptyMap()
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
        seniorUser.write(outToSenior);

        updateFamilyDisplay(user);
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
                targetPacket = FamilyPacket.createFamilyInvite(user, targetUser);  // can invite
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
        System.out.println("Handled FamilySetPrecept");
    }


    @Handler(InHeader.FamilySummonResult)
    public static void handleFamilySummonResult(User user, InPacket inPacket) {
        System.out.println("Handled FamilySummonResult");
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

        // Update Family Pedigrees and Information to the user and ex-junior client (if they are online).
        updateFamilyDisplay(user);

        juniorUser.ifPresent(targetUser -> {
            // let the user know they are no longer a junior 😢
            targetUser.systemMessage("You have been kicked out of your family by %s.", user.getCharacterName());
            updateFamilyDisplay(targetUser);  // not necessary, but is smoother if they have the dialog open.
        });
    }

    @Handler({InHeader.FamilyUnregisterParent})
    public static void handleFamilyUnregisterParent(User user, InPacket inPacket) {
        System.out.println("Handled FamilyUnregisterParent");
    }

    @Handler({InHeader.FamilyUsePrivilege})
    public static void handleFamilyUsePrivilege(User user, InPacket inPacket) {
        System.out.println("Handled FamilyUsePrivilege");
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
