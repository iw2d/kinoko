package kinoko.database.postgresql.type;

import kinoko.world.user.FamilyMember;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class FamilyMemberDao {
    /**
     * Retrieves all family members from the database, including their associated character
     * information (name, level, job) and family relationships (parent ID, reputation).
     *
     * This method joins the `player.family` table with `player.characters` and `player.stats`
     * to build a complete list of `FamilyMember` objects representing every character that is
     * part of a family, regardless of which family they belong to.
     *
     * @param conn the active SQL connection to use for the query
     * @return a list of all `FamilyMember` objects in the database
     * @throws SQLException if a database access error occurs while executing the query
     */
    public static List<FamilyMember> getAllFamilyMembers(Connection conn) throws SQLException {
        String sql = """
            SELECT f.character_id, f.parent_id, f.reputation, f.total_reputation, f.reps_to_senior,
                   c.name, s.level, s.job
            FROM player.family f
            JOIN player.characters c ON f.character_id = c.id
            JOIN player.stats s ON s.character_id = c.id
        """;

        List<FamilyMember> members = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int charId = rs.getInt("character_id");
                Integer parentId = rs.getObject("parent_id") == null ? null : rs.getInt("parent_id");
                int reputation = rs.getInt("reputation");
                int totalReputation = rs.getInt("total_reputation");
                int repsToSenior = rs.getInt("reps_to_senior");
                String name = rs.getString("name");
                int level = rs.getInt("level");
                int job = rs.getInt("job");

                FamilyMember member = new FamilyMember(
                        charId,
                        name,
                        level,
                        job,
                        reputation,
                        totalReputation,
                        0,
                        repsToSenior,
                        parentId
                );

                members.add(member);
            }
        }

        return members;
    }

    /**
     * Saves a FamilyMember to the database. If the member already exists (based on `character_id`),
     * their record is updated with the latest information. Otherwise, a new record is inserted.
     *
     * @param conn   The active SQL Connection to use for executing the statement.
     * @param member The FamilyMember object containing the data to save.
     * @throws SQLException If a database access error occurs while executing the insert or update.
     */
    public static void saveFamilyMember(Connection conn, FamilyMember member) throws SQLException {
        String sql = """
        INSERT INTO player.family (character_id, parent_id, reputation, total_reputation, reps_to_senior)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT (character_id) DO UPDATE SET
            parent_id = EXCLUDED.parent_id,
            reputation = EXCLUDED.reputation,
            total_reputation = EXCLUDED.total_reputation,
            reps_to_senior = EXCLUDED.reps_to_senior
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, member.getCharacterId());
            if (member.getParentId() == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, member.getParentId());
            }
            stmt.setInt(3, member.getReputation());
            stmt.setInt(4, member.getTotalReputation());
            stmt.setInt(5, member.getReputationToSenior());

            stmt.executeUpdate();
        }
    }
}
