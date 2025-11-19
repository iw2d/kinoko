package kinoko.server.family;

import kinoko.world.user.FamilyMember;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Stores all FamilyTree instances in memory.
 * Keyed by leaderId (root character of the family).
 */
public final class FamilyStorage {

    private final ConcurrentHashMap<Integer, FamilyTree> families = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, FamilyTree> memberLookup = new ConcurrentHashMap<>();

    private final ReentrantLock globalFamilyLock = new ReentrantLock();

    /**
     * Returns the global family lock.
     */
    public ReentrantLock getGlobalLock() {
        return globalFamilyLock;
    }

    /**
     * Adds a FamilyTree to storage.
     */
    public void addFamily(FamilyTree family) {
        families.put(family.getLeaderId(), family);
        family.forEach(member -> memberLookup.put(member.getCharacterId(), family));
    }

    /**
     * Removes a FamilyTree from storage.
     */
    public boolean removeFamily(FamilyTree family) {
        if (!families.remove(family.getLeaderId(), family)) return false;
        family.forEach(member -> memberLookup.remove(member.getCharacterId()));
        return true;
    }

    /**
     * Retrieves a FamilyTree by its leader/root ID.
     */
    public Optional<FamilyTree> getFamilyByLeaderId(int leaderId) {
        return Optional.ofNullable(families.get(leaderId));
    }

    /**
     * Retrieves a FamilyTree by a member's character ID (fast O(1) lookup).
     */
    public Optional<FamilyTree> getTreeByMemberId(int characterId) {
        return Optional.ofNullable(memberLookup.get(characterId));
    }

    /**
     * Retrieves a FamilyMember by their character ID.
     *
     * @param characterId The character ID to search for
     * @return Optional containing the FamilyMember if found, empty otherwise
     */
    public Optional<FamilyMember> getFamilyMember(int characterId) {
        return getTreeByMemberId(characterId)
                .map(tree -> tree.getMember(characterId));
    }

    /**
     * Updates the lookup table so each member in the given family tree
     * is associated with that FamilyTree instance. This ensures quick
     * reverse lookup from a character ID to the FamilyTree it belongs to.
     *
     * @param family the FamilyTree whose members should be registered
     */
    public void updateFamilyTree(FamilyTree family){
        family.forEach(member -> memberLookup.put(member.getCharacterId(), family));
    }

    /**
     * Removes a member from their family tree and cleans up the member lookup.
     *
     * If the member exists in a family, they will be removed from the
     * corresponding FamilyTree and their entry in {@code memberLookup} will
     * also be deleted. If the member is not found in any family, this method
     * does nothing.
     *
     * @param characterId the ID of the member to remove
     */
    public void removeMemberFromFamily(int characterId) {
        FamilyTree tree = memberLookup.get(characterId);
        if (tree == null) return;

        FamilyMember member = tree.getMember(characterId);
        if (member == null) return;

        // Remove member from the family tree & lookup
        tree.removeMember(characterId);
        memberLookup.remove(characterId);
    }

    /**
     * Returns all FamilyTrees stored.
     */
    public ConcurrentHashMap<Integer, FamilyTree> getAllFamilies() {
        return families;
    }
}
