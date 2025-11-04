package kinoko.database.postgresql.setup;

import java.nio.file.*;
import java.sql.*;
import java.io.IOException;

public class SchemaUpdater {
    private static final String UPDATES_DIR = "src/main/java/kinoko/database/postgresql/setup/updates";

    public static void run(Connection connection) throws SQLException, IOException {
        long startTime = System.nanoTime(); // start timing

        // get current version
        int currentVersion = getSchemaVersion(connection);

        System.out.println("Current schema version: " + currentVersion);

        while (true) {
            int nextVersion = currentVersion + 1;
            Path nextFile = Path.of(UPDATES_DIR, nextVersion + ".sql");

            if (!Files.exists(nextFile)) {
                System.out.println("No update file found for version " + nextVersion + ". Schema is up-to-date.");
                break;
            }

            System.out.println("Applying schema update: " + nextFile.getFileName());

            String sql = Files.readString(nextFile);

            try {
                // execute migration
                connection.setAutoCommit(false);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(sql);
                }

                // increment schema version (inside same transaction)
                try (PreparedStatement ps = connection.prepareStatement(
                        "SELECT versioning.increment_schema_version(?)")) {
                    ps.setInt(1, currentVersion);
                    ps.executeQuery();
                }

                connection.commit();
                currentVersion = nextVersion;

                System.out.println("✅ Successfully applied version " + currentVersion);
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("❌ Failed to apply schema update " + nextFile.getFileName() + ": " + e.getMessage());
                break;
            } finally {
                connection.setAutoCommit(true);
            }
        }

        long endTime = System.nanoTime(); // end timing
        long durationMs = (endTime - startTime) / 1_000_000; // convert to milliseconds
        System.out.println("Final schema version: " + currentVersion);
        System.out.println("Schema updater completed in " + durationMs + " ms");
    }

    private static int getSchemaVersion(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT versioning.get_schema_version()")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0; // fallback default
    }
}
