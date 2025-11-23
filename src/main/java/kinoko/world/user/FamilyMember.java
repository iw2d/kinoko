package kinoko.world.user;

import kinoko.server.Server;
import kinoko.server.family.FamilyEntitlement;
import kinoko.server.family.FamilyTree;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.Timing;
import kinoko.util.exceptions.DumbDeveloperFoundException;
import kinoko.util.exceptions.InvalidInputException;
import kinoko.world.GameConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a single character's family information.
 * Combines persistent family tree data (characterId, parentId, children, etc.)
 * with ephemeral user-specific data (todaysRep, entitlement usage).
 */
public final class FamilyMember implements Encodable {
    private static final Logger log = LogManager.getLogger(FamilyMember.class);
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
    private final Map<FamilyEntitlement, Long> activeEntitlements = new HashMap<>();
    private String familyMessage;


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
    public String getFamilyMessage(){
        if (this.familyMessage != null) {
            return this.familyMessage;
        }
        return hasFamily() ? "Welcome!" : "You have no family :(";
    }

    public List<Integer> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public int getTodaysRep() { return todaysReputation; }

    public void setFamilyMessage(String message){
        this.familyMessage = message;
    }

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
        // TODO: use rep points
        long now = Timing.nowSeconds();
        entitlementUsageLog.computeIfAbsent(entitlement, k -> new ArrayList<>()).add(now);
        usedEntitlements.put(entitlement, now);  // mark as used
    }

    /**
     * Attempts to use a Family Entitlement optimistically, running the given action,
     * and rolls back the usage if the action fails.
     *
     * Behavior:
     * - Marks the entitlement as used immediately to prevent concurrent spamming.
     * - Executes the provided action (the effect of using the entitlement).
     * - If the action fails (throws or indicates failure), removes the usage mark
     *   to allow the entitlement to be retried later.
     *
     * This ensures cooldown enforcement while allowing safe rollback in case of
     * invalid or unsuccessful entitlement usage, and helps prevent multiple threads
     * from concurrently abusing the same entitlement.
     *
     * @param entitlement The Family Entitlement to attempt using.
     * @param action A Runnable representing the operation to perform for this entitlement.
     *               If this action fails, the entitlement usage will be rolled back.
     * @return true if the action successfully ran and the entitlement remains marked as used,
     *         false if the action failed and the usage was rolled back.
     */
    public boolean tryUseEntitlementWithRollback(FamilyEntitlement entitlement, Runnable action) {
        // TODO: use rep points
        long now = Timing.nowSeconds();
        usedEntitlements.put(entitlement, now);
        entitlementUsageLog.computeIfAbsent(entitlement, k -> new ArrayList<>()).add(now);

        boolean success = false;
        try {
            action.run(); // whatever the entitlement is supposed to do
            success = true;
        }
        catch (InvalidInputException ignored) {}
        finally {
            if (!success) {
                // undo usage
                rollbackEntitlementUsage(entitlement, now);
                usedEntitlements.remove(entitlement);
                List<Long> list = entitlementUsageLog.get(entitlement);
                if (list != null) list.remove(now);
            }
        }
        return success;
    }

    /**
     * Rolls back a previously recorded entitlement usage.
     * Removes the usage timestamp from both the active usage map
     * and the usage history list.
     *
     * @param entitlement The entitlement to undo usage for.
     * @param timestamp   The timestamp that was originally recorded.
     */
    public void rollbackEntitlementUsage(FamilyEntitlement entitlement, long timestamp) {
        // TODO: refund REP Points
        usedEntitlements.remove(entitlement);

        List<Long> history = entitlementUsageLog.get(entitlement);
        if (history != null) {
            history.remove(timestamp);
            if (history.isEmpty()) {
                entitlementUsageLog.remove(entitlement);
            }
        }
    }

    /**
     * Rolls back the most recent usage of the given entitlement.
     * Removes the latest timestamp from both the active usage map
     * and the logged usage history.
     *
     * This is useful when the caller does not know or care about
     * the exact timestamp that was recorded.
     *
     * @param entitlement The entitlement to roll back.
     */
    public void rollbackEntitlementUsage(FamilyEntitlement entitlement) {
        // TODO: refund REP POINTS
        // remove from active usage map
        usedEntitlements.remove(entitlement);

        // Remove the most recent timestamp from history
        List<Long> history = entitlementUsageLog.get(entitlement);
        if (history != null && !history.isEmpty()) {
            history.removeLast();
            if (history.isEmpty()) {
                entitlementUsageLog.remove(entitlement);
            }
        }
    }

    /**
     * Checks if the given entitlement can be used by this family member.
     *
     * @param entitlement The entitlement to check
     * @return true if the entitlement is usable (not currently active/expired), false otherwise
     */
    public synchronized boolean canUse(FamilyEntitlement entitlement) {
        Long lastUsed = usedEntitlements.get(entitlement);
        if (lastUsed == null) {
            return true;  // never used, so it's available
        }

        long now = Timing.nowSeconds();
        long expirySeconds = entitlement.getUsageResetAfterMinutes() * 60L;

        // Usable if enough time has passed since last usage
        return now - lastUsed > expirySeconds;
    }

    /**
     * Activates a given family entitlement for the user.
     *
     * Calculates the expiration time based on the entitlement's duration (in minutes)
     * and stores it in the activeEntitlements map.
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
     * If the entitlement has expired, it is automatically removed from the activeEntitlements map.
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

    // ------------------------------------------------------------
    // Online operations
    // ------------------------------------------------------------


    public boolean isOnline() {
        return Server.getCentralServerNode()
                .getUserByCharacterId(characterId)
                .isPresent();
    }

    public int getMinutesOnline() {
        return Timing.secondsToMinutes(Timing.nowSeconds() - lastSeenUnix);
    }

    public void updateLastLogin() {
        lastSeenUnix = Timing.nowSeconds();
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
        out.encodeShort(GameConstants.MAX_FAMILY_CHILDREN_COUNT); // max juniors
        out.encodeShort(0); // unknown, wTotalChildCount
        out.encodeInt(parentId == null ? getCharacterId() : parentId);
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
        out.encodeString(getFamilyMessage());
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
                long expirySeconds = entitlement.getUsageResetAfterMinutes() * 60L;
                if (now - lastUsed > expirySeconds) {
                    usedEntitlements.remove(entitlement);
                } else {
                    isUsed = true;
                }
            }

            // count usages in the last 24 hours
            List<Long> usageList = entitlementUsageLog.getOrDefault(entitlement, Collections.emptyList());
            long usageCount = usageList.stream().filter(ts -> now - ts <= Timing.DAY_SECONDS).count();

            usageList.removeIf(ts -> now - ts > Timing.DAY_SECONDS);  // clean up old timestamps

            out.encodeInt(entitlement.ordinal());
            out.encodeInt(forChart ? (int) usageCount : (isUsed ? 1 : 0));
        }
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    /**
     * Checks if this family instance represents the default/empty family.
     *
     * @return true if this is the EMPTY family instance, false otherwise
     */
    public boolean isDefault() {
        return this == EMPTY;
    }

    /**
     * Determines if the user belongs to a family with either children or a parent.
     *
     * @return true if the user has a family, false if they are in the default family
     *         or have no family members
     */
    public boolean hasFamily() {
        return !isDefault() && (getChildrenCount() > 0 || getParentId() != null);
    }

    /**
     * Checks if the user is the leader of their family.
     *
     * A leader is defined as a user in a family without a parent.
     *
     * @return true if the user is a family leader, false otherwise
     */
    public boolean isLeader(){
        return !isDefault() && getParentId() == null;
    }

    /**
     * Retrieves the personal experience (EXP) modifier for this user.
     *
     * If the SELF_EXP_1_5 entitlement is active, returns its modifier (e.g., 1.5),
     * otherwise returns 1.0.
     *
     * @return the active personal EXP modifier, with 1.0 as the default
     */
    public double getExpModifier() {
        return isEntitlementActive(FamilyEntitlement.SELF_EXP_1_5)
                ? FamilyEntitlement.SELF_EXP_1_5.getModifier()
                : GameConstants.DEFAULT_FAMILY_PERSONAL_EXP_MODIFIER;
    }


    /**
     * Retrieves the personal drop rate modifier for this user.
     *
     * If the SELF_DROP_1_5 entitlement is active, returns its modifier (e.g., 1.5),
     * otherwise returns 1.0.
     *
     * @return the active personal drop modifier, with 1.0 as the default
     */
    public double getDropModifier() {
        return isEntitlementActive(FamilyEntitlement.SELF_DROP_1_5)
                ? FamilyEntitlement.SELF_DROP_1_5.getModifier()
                : GameConstants.DEFAULT_FAMILY_PERSONAL_DROP_MODIFIER;
    }
}
