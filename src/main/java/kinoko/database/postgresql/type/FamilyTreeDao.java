package kinoko.database.postgresql.type;

import kinoko.server.family.FamilyTree;
import kinoko.world.user.FamilyMember;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;


public final class FamilyTreeDao {
    /**
     * Retrieves all FamilyTree objects from the database by loading all family members
     * and assembling them into in-memory tree structures.
     *
     * Each FamilyTree represents one family, with the root member being the leader
     * (i.e., the member with no parent). Children are properly attached under their
     * respective parents, regardless of the order in which they are loaded from the database.
     *
     * @param conn Active SQL connection
     * @return Collection of fully built FamilyTree objects
     * @throws SQLException if a database access error occurs during retrieval
     */
    public static Collection<FamilyTree> getAllFamilies(Connection conn) throws SQLException {
        // Load all members using FamilyMemberDao
        List<FamilyMember> members = FamilyMemberDao.getAllFamilyMembers(conn);

        // Map characterId -> FamilyMember
        Map<Integer, FamilyMember> allMembersMap = new HashMap<>();
        // Map parentId -> list of children
        Map<Integer, List<FamilyMember>> childrenMap = new HashMap<>();
        // Map of root members (parentId == null)
        Map<Integer, FamilyMember> roots = new HashMap<>();

        for (FamilyMember member : members) {
            allMembersMap.put(member.getCharacterId(), member);

            Integer parentId = member.getParentId();
            if (parentId == null) {
                // Root member (leader of their own family)
                roots.put(member.getCharacterId(), member);
            } else {
                // Add to parent -> children map
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(member);
            }
        }

        // Helper: recursively attach children to parents
        Consumer<FamilyTree> addChildren = tree -> {
            Queue<FamilyMember> queue = new ArrayDeque<>();
            queue.add(tree.getMember(tree.getLeaderId()));

            while (!queue.isEmpty()) {
                FamilyMember parent = queue.poll();
                List<FamilyMember> children = childrenMap.get(parent.getCharacterId());
                if (children != null) {
                    for (FamilyMember child : children) {
                        tree.addMember(child, parent.getCharacterId());
                        queue.add(child);
                    }
                }
            }
        };

        // Build FamilyTree objects
        List<FamilyTree> familyTrees = new ArrayList<>();
        for (FamilyMember root : roots.values()) {
            FamilyTree tree = new FamilyTree(root);
            addChildren.accept(tree);
            familyTrees.add(tree);
        }

        return familyTrees;
    }
}
