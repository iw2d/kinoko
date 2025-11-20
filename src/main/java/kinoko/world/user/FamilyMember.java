package kinoko.world.user;

import kinoko.server.Server;
import kinoko.server.family.FamilyEntitlement;
import kinoko.server.family.FamilyTree;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Timing;
import kinoko.util.exceptions.DumbDeveloperFoundException;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a single character's family information.
 * Combines persistent family tree data (characterId, parentId, children, etc.)
 * with ephemeral user-specific data (todaysRep, entitlement usage).
 */
public final class FamilyMember implements Encodable {

    /**
     * Default empty instance for characters not in a family.
     */
    public static final FamilyMember EMPTY = new FamilyMember(
            0, "", 0, 0, 0,
            0, 0, 0, null
    );

    // -----------------------------
    // Persistent family data
    // -----------------------------
    private final int characterId;

    // -----------------------------
    // Ephemeral/user-specific data
    // -----------------------------
    private Integer parentId;
    private String name;
    private int level;
    private int job;
    private List<Integer> children = new ArrayList<>();
    private int currentReputation;
    private int totalReputation;
    private int todaysReputation;
    private int reputationToSenior;
    private long lastSeenUnix; // unix timestamp in seconds
    private final Map<FamilyEntitlement, Long> usedEntitlements = new ConcurrentHashMap<>();
    private final Map<FamilyEntitlement, List<Long>> entitlementUsageLog = new ConcurrentHashMap<>();


    public FamilyMember(int characterId, String name, int level, int job, int currentReputation, int totalReputation,
                        int todaysReputation, int reputationToSenior, Integer parentId) {
        this.characterId = characterId;
        this.name = name;
        this.level = level;
        this.job = job;

        this.currentReputation = currentReputation;
        this.totalReputation = totalReputation;
        this.todaysReputation = todaysReputation;
        this.reputationToSenior = reputationToSenior;

        this.parentId = parentId;
    }


    public void updateUser(User user){
        this.name = user.getCharacterName();
        this.level = user.getLevel();
        this.job = user.getJob();
    }

    // ------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------

    public int getCharacterId() { return characterId; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getJob() { return job; }
    public int getReputation() { return currentReputation; }
    public int getTotalReputation() { return totalReputation; }
    public int getReputationToSenior() { return reputationToSenior; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<Integer> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public int getTodaysRep() { return todaysReputation; }

    // ------------------------------------------------------------
    // Family tree operations
    // ------------------------------------------------------------

    public void addChild(int childId) throws DumbDeveloperFoundException {
        if (children.contains(childId)) {
            return; // Already a child, nothing to do
        }

        if (getChildrenCount() >= GameConstants.MAX_FAMILY_CHILDREN_COUNT) {
            // Cannot add more children, fail fast before modifying state
            throw new DumbDeveloperFoundException(
                    "FamilyMember " + getCharacterId() + " exceeded max juniors: "
                            + GameConstants.MAX_FAMILY_CHILDREN_COUNT + " (attempted to add child " + childId + ")"
            );
        }

        children.add(childId);
    }

    public void removeChild(int childId) {
        children.remove((Integer) childId);
    }

    /**
     * Attempt to use a Family Entitlement.
     * @param entitlement The entitlement to use
     */
    public void useEntitlement(FamilyEntitlement entitlement) {
        long now = Timing.nowSeconds();
        entitlementUsageLog.computeIfAbsent(entitlement, k -> new ArrayList<>()).add(now);
        usedEntitlements.put(entitlement, now);  // mark as used
    }

    // ------------------------------------------------------------
    // Online operations
    // ------------------------------------------------------------


    public boolean isOnline() {
        return Server.getCentralServerNode()
                .getUserByCharacterId(characterId)
                .isPresent();
    }

    public int getMinutesOnline() {
        return (int) ((System.currentTimeMillis() / 1000L - lastSeenUnix) / 60);
    }

    public void updateLastLogin() {
        lastSeenUnix = System.currentTimeMillis() / 1000L;
    }

    public int getChannelID() {
        return Server.getCentralServerNode()
                .getUserByCharacterId(characterId)
                .map(User::getChannelId)
                .orElse(-1);                // -1 if offline / not found
    }

    // ------------------------------------------------------------
    // Packet encoding
    // ------------------------------------------------------------

    @Override
    public void encode(OutPacket out) {
        out.encodeInt(currentReputation);
        out.encodeInt(totalReputation);
        out.encodeInt(todaysReputation);
        out.encodeShort((short) getChildrenCount());
        out.encodeShort(2); // max juniors
        out.encodeShort(0); // unknown
        out.encodeInt(parentId == null ? 0 : parentId);
        out.encodeString(
                isDefault()
                        ? null
                        : Server.getCentralServerNode()
                        .getFamilyTree(characterId)
                        .map(FamilyTree::getLeader)   // get the leader member
                        .map(FamilyMember::getName)  // get the leader's name
                        .map(name -> name + "'s")        // append 's
                        .orElse(null)   // fallback if anything is missing
        );

        // scrolling family message, set to null to be blank
        // we can let the family leader modify this in the future.
        out.encodeString(hasFamily() ? "You have a family :D" : "You have no family :(");
        encodeEntitlements(out, false);
    }

    /**
     * Encodes all family entitlements into the given packet and cleans up expired ones.
     *
     * Behavior depends on the `forChart` flag:
     * - If `forChart` is false: encodes whether the entitlement is currently active (1) or inactive (0),
     *   based on the last usage and the entitlement's expiration time.
     * - If `forChart` is true: encodes how many times the entitlement was used in the last 24 hours,
     *   for purposes like pedigree charts.
     *
     * This method also removes expired entitlements from the usedEntitlements map,
     * and cleans up old timestamps in the usage log to avoid unbounded growth.
     *
     * @param out the OutPacket to write the entitlement data into
     * @param forChart whether to encode usage count (true) or active/inactive status (false)
     */
    public void encodeEntitlements(OutPacket out, boolean forChart) {
        out.encodeInt(FamilyEntitlement.values().length);

        long now = Timing.nowSeconds();

        for (FamilyEntitlement entitlement : FamilyEntitlement.values()) {
            Long lastUsed = usedEntitlements.get(entitlement);
            boolean isUsed = false;

            // expiry check
            if (lastUsed != null) {
                long expirySeconds = entitlement.getExpiresAfterMinutes() * 60L;
                if (now - lastUsed > expirySeconds) {
                    usedEntitlements.remove(entitlement);
                } else {
                    isUsed = true;
                }
            }

            // count usages in the last 24 hours
            List<Long> usageList = entitlementUsageLog.getOrDefault(entitlement, Collections.emptyList());
            long usageCount = usageList.stream().filter(ts -> now - ts <= Timing.daySeconds()).count();

            usageList.removeIf(ts -> now - ts > Timing.daySeconds());  // clean up old timestamps

            out.encodeInt(entitlement.ordinal());
            out.encodeInt(forChart ? (int) usageCount : (isUsed ? 1 : 0));
        }
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    public boolean isDefault() {
        return this == EMPTY;
    }

    public boolean hasFamily() {
        return !isDefault() && (getChildrenCount() > 0 || getParentId() != null);
    }
}
