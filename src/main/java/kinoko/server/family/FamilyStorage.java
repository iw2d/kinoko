package kinoko.server.family;

import kinoko.world.user.FamilyMember;

import java.util.Collection;
import java.util.Collections;
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
     * Returns the global lock for the family storage.
     *
     * This lock should be used **only by external classes** (such as CentralServerNode or FamilyHandler)
     * when performing operations that involve multiple reads/writes across FamilyStorage
     * to ensure thread safety.
     *
     * Important guidelines for usage:
     * 1. **Use exclusively for family modifications** – acquire the lock only while
     *    reading or modifying family data. Do not hold it for network operations,
     *    database I/O, or other slow tasks, as this lock is highly contended.
     * 2. **Avoid combining with other locks** – never acquire this lock while holding
     *    another lock (or vice versa), as it can easily lead to deadlocks.
     * 3. **Keep lock duration short** – perform only the necessary operations inside
     *    the lock scope to minimize contention, since this lock protects all families
     *    globally and is used frequently.
     *
     * Example usage:
     *   ReentrantLock lock = Server.getCentralServerNode().getGlobalFamilyLock();
     *   lock.lock();
     *   try {
     *       // perform only family modifications or reads
     *       FamilyMember member = familyStorage.getFamilyMember(characterId).orElse(FamilyMember.EMPTY);
     *       FamilyTree tree = familyStorage.getTreeByMemberId(characterId).orElse(null);
     *   } finally {
     *       lock.unlock();
     *   }
     *
     * Internal methods of FamilyStorage do **not acquire this lock** themselves
     * and assume that the caller handles synchronization if needed.
     *
     * @return the ReentrantLock protecting access to the family storage
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

    /**
     * Returns a collection of all FamilyTree instances stored.
     *
     * Modifications to the returned collection do not affect the underlying map.
     *
     * @return an unmodifiable collection of all FamilyTree instances
     */
    public Collection<FamilyTree> getAllFamilyTrees() {
        return Collections.unmodifiableCollection(families.values());
    }
}
