package kinoko.server.family;

import kinoko.server.packet.OutPacket;
import kinoko.util.Lockable;
import kinoko.world.user.FamilyMember;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * In-memory family tree for a single leader, managing parent-child relationships.
 * Supports adding/removing members, pedigree building, DFS traversal, and encoding
 * family data for client packets.
 */
public final class FamilyTree implements Lockable<FamilyTree> {

    private final Lock lock = new ReentrantLock();

    /** characterId → FamilyMember map */
    private final Map<Integer, FamilyMember> members = new HashMap<>();

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

    public int getLeaderId() {
        return leaderId;
    }

    public FamilyMember getLeader() {
        return members.get(leaderId);
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
     * the method returns false. The member is added to the parent's list of children
     * (duplicates are ignored) and to this tree's internal members map.
     *
     * Thread-safety: The method acquires a lock to prevent concurrent modifications.
     *
     * @param junior the FamilyMember to add
     * @param parentId the character ID of the parent under whom the member should be added
     * @return true if the member was successfully added, false otherwise
     */
    public boolean addMember(FamilyMember junior, int parentId) {
        lock();
        try {
            if (!members.containsKey(parentId)) return false;
            if (members.containsKey(junior.getCharacterId())) return false;

            FamilyMember parent = members.get(parentId);
            parent.addChild(junior.getCharacterId());  // duplicates ignored.
            members.put(junior.getCharacterId(), junior);
            return true;
        } finally {
            unlock();
        }
    }

    /**
     * Removes a member and their entire subtree from this FamilyTree.
     *
     * If the member does not exist or is the leader of the tree, the method returns false.
     * The member is first removed from their parent's list of children (if any),
     * then the member and all their descendants are removed from the tree.
     *
     * Thread-safety: The method acquires a lock to prevent concurrent modifications.
     *
     * @param characterId the character ID of the member to remove
     * @return true if the member was successfully removed, false otherwise
     */
    public boolean removeMember(int characterId) {
        lock();
        try {
            if (!members.containsKey(characterId)) return false;
            if (characterId == leaderId) return false;

            FamilyMember member = members.get(characterId);
            FamilyMember parent = members.get(member.getParentId());

            if (parent != null) {
                parent.removeChild(characterId);
            }

            member.setParentId(null);

            removeSubtree(characterId);
            return true;
        } finally {
            unlock();
        }
    }


    /**
     * Adds an entire subtree from another FamilyTree under a specified parent in this tree.
     *
     * The subtree is merged into this tree, preserving all parent-child relationships.
     * The root of the subtree will become a child of the specified parent, and all
     * descendants are added recursively. The member map of this tree is updated to
     * include all members from the subtree.
     *
     * Thread-safety: This method locks the tree during the operation to prevent
     * concurrent modifications.
     *
     * @param tree the FamilyTree containing the subtree to add
     * @param parentId the character ID of the parent in this tree under which the subtree
     *                 should be attached
     * @throws IllegalArgumentException if the specified parent does not exist in this tree
     */
    public void addSubTree(FamilyTree tree, int parentId) {
        lock();
        try {
            FamilyMember parent = getMember(parentId);
            if (parent == null) {
                throw new IllegalArgumentException("Parent not found in this tree: " + parentId);
            }

            FamilyMember subtreeRoot = tree.getLeader();
            addMember(subtreeRoot, parentId);

            // marge all members of the subtree into this tree's member map
            tree.forEach(member -> {
                System.out.println(member.getCharacterId());
                if (member != subtreeRoot) {
                    addMember(member, member.getParentId());
                }
            });
        }
        finally {
            unlock();
        }
    }

    /**
     * Recursively removes a subtree from this FamilyTree starting at the specified member.
     *
     * All descendants of the given member are removed first, then the member itself
     * is removed from the members map. This method does not update any external lookups;
     * it only affects this tree's internal member mapping.
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
     * This is useful for isolating a subtree before removing it or moving it elsewhere.
     * If moving it somewhere else, please reference extractAndRemoveSubTree to be concurrency safe.
     *
     * @param rootId the character ID of the new subtree root
     * @return a new FamilyTree containing the root member and all descendants
     * @throws IllegalArgumentException if rootId does not exist in this tree
     */
    public FamilyTree createSubTree(int rootId) {
        lock();
        try {
            FamilyMember rootMember = getMember(rootId);
            if (rootMember == null) {
                throw new IllegalArgumentException("Character ID not found in this tree: " + rootId);
            }

            FamilyTree subTree = new FamilyTree(rootMember);
            addChildrenToSubTree(rootMember, subTree);
            return subTree;
        } finally {
            unlock();
        }
    }

    /**
     * Atomically extracts and removes an entire subtree rooted at the given member ID.
     *
     * This operation is fully thread-safe and performed under a single tree-wide lock.
     * Unlike calling createSubTree() and removeMember() separately, this method ensures
     * both actions occur without any other modifications in between.
     *
     * Behavior details:
     *  - Validates that the rootId exists (via createSubTree(), which throws if not).
     *  - Builds a new FamilyTree containing the root member and all of its descendants.
     *  - Removes the root member—and thus its entire hierarchy—from this tree.
     *  - Returns the detached subtree, which can be moved or modified independently.
     *
     * Concurrency note:
     *  Although createSubTree() has its own locking, this method acquires the lock first,
     *  ensuring that both subtree creation and removal occur under the same lock scope.
     *  This prevents races where other threads could mutate the tree between the two steps.
     *
     * @param rootId the character ID at the root of the subtree to extract
     * @return a new FamilyTree containing the extracted subtree
     * @throws IllegalArgumentException if rootId does not exist in this tree
     */
    public FamilyTree extractAndRemoveSubTree(int rootId) {
        lock(); // lock the whole tree
        try {
            FamilyTree subTree = createSubTree(rootId);
            removeMember(rootId);
            return subTree;
        } finally {
            unlock();
        }
    }

    /**
     * Recursively adds children of a member to the given subtree.
     *
     * @param member the parent member whose children should be added
     * @param subTree the FamilyTree to populate
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
     * Builds a pedigree list for the given viewee, representing their family hierarchy
     * and immediate relatives in the format expected by the client.
     *
     * The returned list contains members in the following order:
     * 1. The family leader (root of the family tree)
     * 2. All senior members (ancestors) of the viewee, from the leader down to the
     *    direct parent
     * 3. The viewee themselves
     * 4. The viewee's siblings (other children of the same parent)
     * 5. The viewee's juniors (direct children) and super-juniors (grandchildren),
     *    up to two layers deep
     *
     * This list is primarily used for encoding the family chart for the client.
     *
     * @param vieweeId the character ID of the member whose pedigree is being built
     * @return a list of FamilyMember objects representing the viewee's family hierarchy,
     *         including the viewee, ancestors, siblings, and descendants up to two levels
     */
    public List<FamilyMember> buildPedigree(int vieweeId) {
        List<FamilyMember> pedigree = new ArrayList<>();
        FamilyMember viewee = getMember(vieweeId);
        if (viewee == null) return pedigree;

        // 1. Leader
        FamilyMember leader = getMember(getLeaderId());
        pedigree.add(leader);

        // 2. Seniors (walk up)
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

        // 3. Viewee
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


        // 5. Juniors / super-juniors (two layers deep max)
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
     * Returns the total number of seniors (ancestors) for a given viewee.
     *
     * @param vieweeId The character ID of the viewee.
     * @return The total number of seniors above the viewee.
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
     * Returns ordered children (left/right) by rule:
     * lower character ID = left child
     */
    public List<Integer> getOrderedChildren(int parentId) {
        FamilyMember parent = members.get(parentId);
        List<Integer> result = new ArrayList<>(parent.getChildren());
        result.sort(Integer::compareTo);
        return result;
    }

    /**
     * Performs a depth-first traversal of the family tree, starting from the leader,
     * and applies the given Consumer to each FamilyMember.
     *
     * Traversal order:
     * - Starts at the root (leader)
     * - Visits each member before recursively visiting their children
     * - Children are visited in the order returned by getOrderedChildren()
     *
     * This is useful for iterating over all members of the tree in a predictable
     * hierarchical order (root → seniors → juniors → super-juniors).
     *
     * @param consumer a Consumer function that will be called for each FamilyMember
     */
    public void forEach(Consumer<FamilyMember> consumer) {
        traverse(leaderId, consumer);
    }


    /**
     * Recursive helper for depth-first traversal.
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
     * 2. The list of family members in the pedigree (leader, seniors, siblings, juniors, and super-juniors),
     *    with each member encoded via `addPedigreeEntry`.
     * 3. The statistics block (v83-like) containing key-value pairs such as total members, total seniors,
     *    and junior counts for super-juniors.
     * 4. The privilege/entitlements block from the viewee's FamilyMember (key-value pairs of entitlements).
     * 5. A final short indicating whether the "Add Junior" button should be enabled
     *    (enabled if the viewee has fewer than 2 children, disabled otherwise).
     *
     * Note: This method assumes the FamilyTree structure is thread-safe or externally synchronized.
     *
     * @param out the OutPacket to write the encoded family chart into
     * @param vieweeId the character ID of the player viewing the chart
     */
    public void encodeChart(OutPacket out, int vieweeId) {
        List<FamilyMember> pedigree = buildPedigree(vieweeId);

        // Part 1: Viewee ID
        out.encodeInt(vieweeId);

        // Part 2: The list of all family members in the chart
        out.encodeInt(pedigree.size());
        for (FamilyMember member : pedigree) {
            addPedigreeEntry(out, member); // Call the new, correct method
        }

        // Part 3: The "Statistics" block. This is the v83-like block.
        // Based on the disassembly, it reads a count, then key-value pairs.
        // Let's replicate the common v83 structure for this.
        Map<Integer, Integer> stats = buildStatsMap(vieweeId, pedigree);
        out.encodeInt(stats.size());
        for (Map.Entry<Integer, Integer> entry : stats.entrySet()) {
            out.encodeInt(entry.getKey());
            out.encodeInt(entry.getValue());
        }

        // Part 4: The "Privilege" block (Entitlements).
        // Assuming you have an entitlements map on the viewee's FamilyMember object.
        FamilyMember viewee = getMember(vieweeId);
        Map<Integer, Integer> entitlements = viewee != null ? viewee.getEntitlements() : Collections.emptyMap();
        out.encodeInt(entitlements.size());
        for (Map.Entry<Integer, Integer> entry : entitlements.entrySet()) {
            out.encodeInt(entry.getKey());
            out.encodeInt(entry.getValue());
        }

        // Part 5: The final short for enabling the "Add Junior" button.
        if (viewee != null) {
            out.encodeShort(viewee.getChildren().size() >= 2 ? 0 : 2);
        } else {
            out.encodeShort(0);
        }
    }

    /**
     * Encodes a single FamilyMember's information into the given OutPacket,
     * ensuring it matches the client's expected Decode format.
     *
     * The encoded data includes:
     * 1. Character ID
     * 2. Parent ID (0 if none)
     * 3. Job (short)
     * 4. Level (byte)
     * 5. Online status (1 = online, 0 = offline, byte)
     * 6. Reputation
     * 7. Total reputation
     * 8. Reputation needed to become a senior
     * 9. Today's grandparent points (currently sent as 0)
     * 10. Channel ID (-1 if offline)
     * 11. Minutes online
     * 12. Character name (String)
     *
     * @param out the OutPacket to write the encoded character data into
     * @param member the FamilyMember whose data is being encoded
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
     * Builds a statistics map for a given viewee to be sent in the family chart packet.
     *
     * The map contains key-value pairs representing various family statistics:
     *  - Key -1: Total number of members in the family.
     *  - Key 0: Total number of seniors (ancestors) above the viewee.
     *  - Keys of super-juniors (grandchildren of the viewee): Number of children each has.
     *
     * This map is used in the packet encoding to provide the client with summary
     * statistics about the viewee's position and relationships within the family.
     *
     * @param vieweeId the character ID of the viewee
     * @param pedigree the pre-built pedigree list of FamilyMember objects
     * @return a LinkedHashMap preserving insertion order with the computed statistics
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

    // -------------------------------------------------------------------------
    // Locking
    // -------------------------------------------------------------------------

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
