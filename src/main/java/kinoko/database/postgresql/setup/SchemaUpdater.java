package kinoko.database.postgresql.setup;

import kinoko.server.Server;
import kinoko.util.Timing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.*;
import java.sql.*;
import java.io.IOException;

public class SchemaUpdater {
    private static final String UPDATES_DIR = "src/main/java/kinoko/database/postgresql/setup/updates";
    private static final Logger log = LogManager.getLogger(SchemaUpdater.class);


    public static void run(Connection connection) throws SQLException {
        Timing.logDurationThrowing("Schema updater", () -> {
            int currentVersion = getSchemaVersion(connection);  // get current version

            log.info("Current schema version: {}", currentVersion);

            while (true) {
                int nextVersion = currentVersion + 1;
                Path nextFile = Path.of(UPDATES_DIR, nextVersion + ".sql");

                if (!Files.exists(nextFile)) {
                    log.info("No update file found for version {}. Schema is up-to-date.", nextVersion);
                    break;
                }

                log.info("Applying schema update: {}", nextFile.getFileName());

                String sql;
                try {
                    sql = Files.readString(nextFile); // attempt to read the SQL update file from disk
                } catch (IOException e) {
                    // Wrap any IOException as a SQLException so it can be handled
                    // in the same catch block using logDurationThrowing.
                    throw new SQLException("Failed to read SQL update file: " + nextFile.getFileName(), e);
                }

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

                    log.info("Successfully applied version {}", currentVersion);
                } catch (SQLException e) {
                    connection.rollback();
                    log.error("Failed to apply schema update {}: {}", nextFile.getFileName(), e.getMessage());
                    break;
                } finally {
                    connection.setAutoCommit(true);
                }
            }
            log.info("Final schema version: {}", currentVersion);
        }, log);
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
