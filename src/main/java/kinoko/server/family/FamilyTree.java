package kinoko.server.family;

import kinoko.server.packet.OutPacket;
import kinoko.util.Timing;
import kinoko.util.exceptions.DumbDeveloperFoundException;
import kinoko.world.user.FamilyMember;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents an in-memory family tree rooted at a single leader, managing
 * parent-child relationships. Supports adding and removing members, building
 * pedigrees, performing DFS traversal, and encoding family data for client packets.
 *
 * Thread-safety: Individual FamilyTree instances do not require locks, as all
 * modifications are protected by the global family lock in FamilyStorage.
 */
public final class FamilyTree {
    /** characterId → FamilyMember map */
    private final Map<Integer, FamilyMember> members = new HashMap<>();

    private final Map<FamilyEntitlement, Long> activeEntitlements = new HashMap<>();

    private String familyMessage;

    /** Root leader (parentId = null) */
    private int leaderId;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public FamilyTree(FamilyMember leader) {
        members.put(leader.getCharacterId(), leader);
        this.leaderId = leader.getCharacterId();
    }

    // -------------------------------------------------------------------------
    // Member access
    // -------------------------------------------------------------------------

    public boolean hasMember(int characterId) {
        return members.containsKey(characterId);
    }

    public FamilyMember getMember(int characterId) {
        return members.get(characterId);
    }

    /**
     * Returns an unmodifiable collection of all members in this FamilyTree.
     *
     * The returned collection contains every FamilyMember in the tree,
     * including the leader, seniors, juniors, and super-juniors.
     * Attempts to modify the returned collection will throw
     * an UnsupportedOperationException.
     *
     * @return an unmodifiable Collection of all FamilyMember instances in the tree
     */
    public Collection<FamilyMember> getAllMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    public int getLeaderId() {
        return leaderId;
    }

    public FamilyMember getLeader() {
        return members.get(leaderId);
    }

    public String getFamilyMessage() {
        return familyMessage;
    }

    /**
     * Sets the family tree's message and broadcasts it to all members.
     *
     * Updates the tree-level message (`familyMessage`) and then propagates
     * it to each member in the tree so everyone sees the updated message.
     *
     * @param message the new family message to set
     */
    public void setFamilyMessage(String message){
        familyMessage = message;
        broadcastFamilyMessage();
    }

    /**
     * Sets the family message for the entire family tree.
     *
     * Iterates over all members and updates each member's local message
     * as well as the tree's global family message.
     *
     */
    private void broadcastFamilyMessage() {
        forEach(member -> member.setFamilyMessage(familyMessage)); // Update each member
    }

    /**
     * Activates a given family entitlement for the user.
     *
     * The entitlement will be active for its defined duration in minutes, converted to seconds.
     * The expiration time is stored in the activeEntitlements map.
     *
     * @param ent the FamilyEntitlement to activate
     */
    public void activateEntitlement(FamilyEntitlement ent) {
        long expiresMinutes = ent.getExpiresAfterMinutes();
        long expireAt = Timing.nowSeconds() + expiresMinutes * 60;

        activeEntitlements.put(ent, expireAt);
    }

    /**
     * Checks if a given family entitlement is currently active for the user.
     *
     * If the entitlement has expired (based on seconds), it is automatically removed from
     * the activeEntitlements map.
     *
     * @param ent the FamilyEntitlement to check
     * @return true if the entitlement is active and not expired, false otherwise
     */
    public boolean isEntitlementActive(FamilyEntitlement ent) {
        Long expireAt = activeEntitlements.get(ent);
        if (expireAt == null) return false;

        if (expireAt < Timing.nowSeconds()) {
            activeEntitlements.remove(ent);  // expired
            return false;
        }

        return true;
    }

    /**
     * Retrieves the active family experience (EXP) modifier for this user.
     *
     * Checks if the FAMILY_EXP entitlement is active. If it is, returns its modifier (e.g., 1.2),
     * otherwise returns the default value of 1.0.
     *
     * @return the current active family EXP modifier, with 1.0 as the default
     */
    public double getExpModifier() {
        return isEntitlementActive(FamilyEntitlement.FAMILY_EXP)
                ? FamilyEntitlement.FAMILY_EXP.getModifier()
                : 1.0;
    }

    /**
     * Retrieves the active family drop rate modifier for this user.
     *
     * Checks if the FAMILY_DROP entitlement is active. If it is, returns its modifier (e.g., 1.2),
     * otherwise returns the default value of 1.0.
     *
     * @return the current active family drop modifier, with 1.0 as the default
     */
    public double getDropModifier() {
        return isEntitlementActive(FamilyEntitlement.FAMILY_DROP)
                ? FamilyEntitlement.FAMILY_DROP.getModifier()
                : 1.0;
    }

    // -------------------------------------------------------------------------
    // Add / remove members
    // -------------------------------------------------------------------------
    public void changeLeader(FamilyMember newLeader){
        this.leaderId = newLeader.getCharacterId();
    };

    /**
     * Adds a new member as a child of the specified parent in this FamilyTree.
     *
     * If the parent does not exist in the tree or the member is already present,
     * the method returns false. Otherwise, the member is added to the parent's
     * list of children (duplicates are ignored) and to this tree's internal members map.
     *
     * Note: If adding the child would exceed the maximum allowed children for the parent,
     * a {@link DumbDeveloperFoundException} runtime exception is thrown.
     *
     * @param junior the FamilyMember to add
     * @param parentId the character ID of the parent under whom the member should be added
     * @return true if the member was successfully added, false otherwise
     * @throws DumbDeveloperFoundException if internal family constraints are violated
     */
    public boolean addMember(FamilyMember junior, int parentId) throws DumbDeveloperFoundException {
        if (!members.containsKey(parentId)) return false;
        if (members.containsKey(junior.getCharacterId())) return false;

        FamilyMember parent = members.get(parentId);
        parent.addChild(junior.getCharacterId());  // duplicates ignored, may throw DumbDeveloperFound
        members.put(junior.getCharacterId(), junior);

        if (familyMessage != null){
            junior.setFamilyMessage(familyMessage);
        }
        return true;
    }

    /**
     * Removes a member and their entire subtree from this FamilyTree.
     *
     * If the member does not exist or is the leader of the tree, the method returns false.
     * The member is first removed from their parent's list of children (if any),
     * then the member and all their descendants are removed from the tree.
     *
     * @param characterId the character ID of the member to remove
     * @return true if the member was successfully removed, false otherwise
     */
    public boolean removeMember(int characterId) {
        if (!members.containsKey(characterId)) return false;
        if (characterId == leaderId) return false;

        FamilyMember member = members.get(characterId);
        FamilyMember parent = members.get(member.getParentId());

        if (parent != null) {
            parent.removeChild(characterId);
        }

        member.setFamilyMessage(null);
        member.setParentId(null);

        removeSubtree(characterId);
        return true;
    }


    /**
     * Adds an entire subtree from another FamilyTree under a specified parent in this tree.
     *
     * The subtree is merged into this tree, preserving all parent-child relationships.
     * The root of the subtree will become a child of the specified parent, and all
     * descendants are added recursively. The member map of this tree is updated to
     * include all members from the subtree.
     *
     * @param tree the FamilyTree containing the subtree to add
     * @param parentId the character ID of the parent in this tree under which the subtree
     *                 should be attached
     * @throws IllegalArgumentException if the specified parent does not exist in this tree
     */
    public void addSubTree(FamilyTree tree, int parentId) throws IllegalArgumentException{
        FamilyMember parent = getMember(parentId);
        if (parent == null) {
            throw new IllegalArgumentException("Parent not found in this tree: " + parentId);
        }

        FamilyMember subtreeRoot = tree.getLeader();
        addMember(subtreeRoot, parentId);

        // marge all members of the subtree into this tree's member map
        tree.forEach(member -> {
            if (member != subtreeRoot) {
                addMember(member, member.getParentId());
            }
        });
    }

    /**
     * Recursively removes a subtree from this FamilyTree starting at the specified member.
     *
     * All descendants of the given member are removed first, then the member itself
     * is removed from the members map. This method only affects this tree's internal
     * member mapping and does not update any external lookups.
     *
     * @param characterId the character ID of the root member of the subtree to remove
     */
    private void removeSubtree(int characterId) {
        FamilyMember member = members.get(characterId);
        for (int childId : member.getChildren()) {
            removeSubtree(childId);
        }
        members.remove(characterId);
    }

    /**
     * Creates a new FamilyTree rooted at the given character ID from this tree,
     * including the member and all of its descendants.
     *
     * Useful for isolating a subtree before removing it or moving it elsewhere.
     * This method does not modify this tree; it only constructs a new FamilyTree
     * containing the specified root and its descendants.
     *
     * @param rootId the character ID of the new subtree root
     * @return a new FamilyTree containing the root member and all descendants
     * @throws IllegalArgumentException if rootId does not exist in this tree
     */
    public FamilyTree createSubTree(int rootId) {
        FamilyMember rootMember = getMember(rootId);
        if (rootMember == null) {
            throw new IllegalArgumentException("Character ID not found in this tree: " + rootId);
        }

        FamilyTree subTree = new FamilyTree(rootMember);
        addChildrenToSubTree(rootMember, subTree);
        return subTree;
    }

    /**
     * Extracts and removes an entire subtree rooted at the given member ID.
     *
     * This method creates a new FamilyTree containing the root member and all
     * of its descendants, and then removes the root member (and its entire
     * hierarchy) from this tree. The detached subtree can then be moved or
     * modified independently.
     *
     * @param rootId the character ID at the root of the subtree to extract
     * @return a new FamilyTree containing the extracted subtree
     * @throws IllegalArgumentException if rootId does not exist in this tree
     */
    public FamilyTree extractAndRemoveSubTree(int rootId) {
        FamilyTree subTree = createSubTree(rootId);
        removeMember(rootId);
        return subTree;
    }

    /**
     * Recursively adds the children of a given member to the specified subtree.
     *
     * This method traverses the hierarchy starting from the given parent member,
     * adding each child (and their descendants) to the provided FamilyTree.
     * It preserves parent-child relationships within the new subtree.
     *
     * @param member the parent member whose children (and their descendants) should be added
     * @param subTree the FamilyTree to populate with the subtree members
     */
    private void addChildrenToSubTree(FamilyMember member, FamilyTree subTree) {
        for (int childId : member.getChildren()) {
            FamilyMember child = getMember(childId);
            if (child != null) {
                subTree.addMember(child, child.getParentId());
                addChildrenToSubTree(child, subTree);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Charts / Pedigrees
    // -------------------------------------------------------------------------
    /**
     * Builds a pedigree list for the given member, representing their family hierarchy
     * and immediate relatives in the format expected by the client.
     *
     * The returned list contains members in the following order:
     * 1. The family leader (root of the family tree)
     * 2. All senior members (ancestors) of the viewee, from the leader down to the direct parent
     * 3. The viewee themselves
     * 4. The viewee's siblings (other children of the same parent)
     * 5. The viewee's juniors (direct children) and super-juniors (grandchildren),
     *    up to two layers deep
     *
     * This is used primarily for encoding the family chart for the client.
     *
     * @param vieweeId the character ID of the member whose pedigree is being built
     * @return a list of FamilyMember objects representing the viewee's hierarchy,
     *         including ancestors, siblings, and descendants up to two levels
     */
    public List<FamilyMember> buildPedigree(int vieweeId) {
        List<FamilyMember> pedigree = new ArrayList<>();
        FamilyMember viewee = getMember(vieweeId);
        if (viewee == null) return pedigree;

        // Leader
        FamilyMember leader = getMember(getLeaderId());
        pedigree.add(leader);

        // Seniors (walk up)
        List<FamilyMember> seniors = new ArrayList<>();
        FamilyMember current = viewee;
        while (current.getParentId() != null) {
            FamilyMember parent = getMember(current.getParentId());
            if (parent == null) {
                break;
            }

            seniors.add(0, parent); // prepend
            current = parent;
        }
        pedigree.addAll(seniors);

        // Viewee
        pedigree.add(viewee);

        Integer parentId = viewee.getParentId();
        if (parentId != null) {
            FamilyMember parent = getMember(parentId);
            if (parent != null) {
                for (int siblingId : parent.getChildren()) {
                    if (siblingId != vieweeId) {
                        FamilyMember sibling = getMember(siblingId);
                        if (sibling != null) pedigree.add(sibling);
                    }
                }
            }
        }


        // Juniors / super-juniors (two layers deep max)
        List<FamilyMember> superJuniors = new ArrayList<>();
        for (int childId : viewee.getChildren()) {
            FamilyMember junior = getMember(childId);
            if (junior != null) {
                pedigree.add(junior);
                for (int grandChildId : junior.getChildren()) {
                    FamilyMember superJunior = getMember(grandChildId);
                    if (superJunior != null) superJuniors.add(superJunior);
                }
            }
        }
        pedigree.addAll(superJuniors);

        return pedigree;
    }

    /**
     * Returns the total number of senior members (ancestors) above the specified member.
     *
     * @param vieweeId the character ID of the member
     * @return the number of ancestors above the member in the family tree
     */
    public int getTotalSeniors(int vieweeId) {
        int count = 0;
        FamilyMember current = getMember(vieweeId);
        if (current == null) return 0;

        while (current.getParentId() != null) {
            FamilyMember parent = getMember(current.getParentId());
            if (parent == null) break;
            count++;
            current = parent;
        }

        return count;
    }

    // -------------------------------------------------------------------------
    // Traversal / ordering
    // -------------------------------------------------------------------------

    /**
     * Returns the children of the specified parent in a deterministic order.
     * Ordering rule: lower character ID comes first (left child), higher ID comes second (right child).
     *
     * @param parentId the character ID of the parent
     * @return a list of child character IDs, sorted by ID
     */
    public List<Integer> getOrderedChildren(int parentId) {
        FamilyMember parent = members.get(parentId);
        List<Integer> result = new ArrayList<>(parent.getChildren());
        result.sort(Integer::compareTo);
        return result;
    }

    /**
     * Performs a depth-first traversal of the family tree, starting from the leader,
     * applying the given Consumer to each FamilyMember in hierarchical order.
     *
     * Traversal order:
     * 1. Leader (root)
     * 2. Seniors → viewee → juniors → super-juniors
     * 3. Children are visited in the order returned by getOrderedChildren()
     *
     * Useful for iterating over all members in a predictable tree order.
     *
     * @param consumer a Consumer function applied to each FamilyMember
     */
    public void forEach(Consumer<FamilyMember> consumer) {
        traverse(leaderId, consumer);
    }


    /**
     * Helper method for recursive depth-first traversal.
     *
     * @param characterId the ID of the current member being visited
     * @param c the Consumer to apply to each FamilyMember
     */
    private void traverse(int characterId, Consumer<FamilyMember> c) {
        FamilyMember m = members.get(characterId);
        c.accept(m);
        for (int childId : getOrderedChildren(characterId)) {
            traverse(childId, c);
        }
    }

    // -------------------------------------------------------------------------
    // Packet encoding
    // -------------------------------------------------------------------------

    /**
     * Encodes the family chart for a given viewee into the provided OutPacket.
     *
     * This method serializes the following components in order:
     * 1. The viewee's character ID.
     * 2. The list of family members in the pedigree (leader, seniors, siblings, juniors, super-juniors),
     *    with each member encoded via `addPedigreeEntry`.
     * 3. The statistics block containing total members, total seniors, and grandchild counts.
     * 4. The privilege/entitlements block from the viewee's FamilyMember.
     * 5. A final short indicating whether the "Add Junior" button should be enabled
     *    (enabled if the viewee has fewer than 2 children, disabled otherwise).
     *
     * Note: This method assumes that the FamilyTree is externally synchronized if
     * concurrent access is possible.
     *
     * @param out the OutPacket to write the encoded family chart into
     * @param vieweeId the character ID of the player viewing the chart
     */
    public void encodeChart(OutPacket out, int vieweeId) {
        List<FamilyMember> pedigree = buildPedigree(vieweeId);

        // Viewee ID
        out.encodeInt(vieweeId);

        // The list of all family members in the chart
        out.encodeInt(pedigree.size());
        for (FamilyMember member : pedigree) {
            addPedigreeEntry(out, member); // Call the new, correct method
        }

        // The "Statistics" block. This is the v83-like block.
        // Based on the v95 disassembly, it reads a count, then key-value pairs.
        // Let's replicate the common v83 structure for this from HeavenMS.
        Map<Integer, Integer> stats = buildStatsMap(vieweeId, pedigree);
        out.encodeInt(stats.size());
        for (Map.Entry<Integer, Integer> entry : stats.entrySet()) {
            out.encodeInt(entry.getKey());
            out.encodeInt(entry.getValue());
        }

        // The "Privilege" block (Entitlements).
        FamilyMember viewee = getMember(vieweeId);
        if (viewee != null) {
            viewee.encodeEntitlements(out, true);
        } else {
            out.encodeInt(0);  // If viewee is null, encode 0 entitlements
        }

        // The final short for enabling the "Add Junior" button.
        if (viewee != null) {
            out.encodeShort(viewee.getChildren().size() >= 2 ? 0 : 2);
        } else {
            out.encodeShort(0);
        }
    }

    /**
     * Encodes a single FamilyMember into the packet in the format expected by the client.
     *
     * Fields include character ID, parent ID, job, level, online status, reputation,
     * total reputation, reputation to senior, grandparent points, channel, minutes online,
     * and character name.
     *
     * @param out the OutPacket to write into
     * @param member the FamilyMember to encode
     */
    private void addPedigreeEntry(OutPacket out, FamilyMember member) {
        out.encodeInt(member.getCharacterId());
        out.encodeInt(member.getParentId() == null ? 0 : member.getParentId());
        out.encodeShort(member.getJob());
        out.encodeByte(member.getLevel());
        out.encodeByte(member.isOnline() ? 1 : 0);
        out.encodeInt(member.getReputation());
        out.encodeInt(member.getTotalReputation());
        out.encodeInt(member.getReputationToSenior());
        out.encodeInt(0); // For nTodayGrandParentPoint, send 0 for now.
        out.encodeInt(member.getChannelID());
        out.encodeInt(member.getMinutesOnline());
        out.encodeString(member.getName());
    }

    /**
     * Builds a statistics map for a viewee, containing:
     *  - Key -1: Total members in the family
     *  - Key 0: Total seniors above the viewee
     *  - Super-juniors (grandchildren): Key = characterId, Value = number of children
     *
     * This map is used in the family chart packet to provide summary info about
     * the viewee's position in the family.
     *
     * @param vieweeId the character ID of the viewee
     * @param pedigree pre-built list of FamilyMember objects in the pedigree
     * @return LinkedHashMap preserving insertion order with computed statistics
     */
    private Map<Integer, Integer> buildStatsMap(int vieweeId, List<FamilyMember> pedigree) {
        Map<Integer, Integer> stats = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order
        stats.put(-1, members.size()); // Total members
        stats.put(0, getTotalSeniors(vieweeId)); // Total seniors for viewee

        // Add junior counts for super-juniors (grandchildren of the viewee)
        FamilyMember viewee = getMember(vieweeId);
        if (viewee != null) {
            for (int childId : viewee.getChildren()) {
                FamilyMember junior = getMember(childId);
                if (junior != null) {
                    for (int grandChildId : junior.getChildren()) {
                        FamilyMember superJunior = getMember(grandChildId);
                        if (superJunior != null) {
                            stats.put(superJunior.getCharacterId(), superJunior.getChildren().size());
                        }
                    }
                }
            }
        }
        return stats;
    }
}
