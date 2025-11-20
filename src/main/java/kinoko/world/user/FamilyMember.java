package kinoko.world.user;

import kinoko.server.Server;
import kinoko.server.family.FamilyTree;
import kinoko.server.packet.OutPacket;
import kinoko.util.Encodable;
import kinoko.util.exceptions.DumbDeveloperFound;
import kinoko.world.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            0, "", 0, 0, 0, 0, 0, 0, null,  Collections.emptyMap()
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
    private final Map<Integer, Integer> entitlements;
    private long lastSeenUnix; // unix timestamp in seconds

    public FamilyMember(int characterId, String name, int level, int job, int currentReputation, int totalReputation,
                        int todaysReputation, int reputationToSenior, Integer parentId, Map<Integer, Integer> entitlements) {
        this.characterId = characterId;
        this.name = name;
        this.level = level;
        this.job = job;

        this.currentReputation = currentReputation;
        this.totalReputation = totalReputation;
        this.todaysReputation = todaysReputation;
        this.reputationToSenior = reputationToSenior;

        this.parentId = parentId;
        this.entitlements = entitlements;
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
    public Map<Integer, Integer> getEntitlements() { return entitlements; }

    // ------------------------------------------------------------
    // Family tree operations
    // ------------------------------------------------------------

    public void addChild(int childId) throws DumbDeveloperFound{
        if (children.contains(childId)) {
            return; // Already a child, nothing to do
        }

        if (getChildrenCount() >= GameConstants.MAX_FAMILY_CHILDREN_COUNT) {
            // Cannot add more children, fail fast before modifying state
            throw new DumbDeveloperFound(
                    "FamilyMember " + getCharacterId() + " exceeded max juniors: "
                            + GameConstants.MAX_FAMILY_CHILDREN_COUNT + " (attempted to add child " + childId + ")"
            );
        }

        children.add(childId);
    }

    public void removeChild(int childId) {
        children.remove((Integer) childId);
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
        out.encodeString(isDefault() ? "You have no family :(" : "You have a family :D");

        out.encodeInt(entitlements.size());
        for (Map.Entry<Integer, Integer> entry : entitlements.entrySet()) {
            out.encodeInt(entry.getKey());
            out.encodeInt(entry.getValue());
        }
    }

    // ------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------
    public boolean isDefault() {
        return this == EMPTY;
    }
}
