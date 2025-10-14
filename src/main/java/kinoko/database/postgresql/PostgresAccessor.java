package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.postgresql.util.SQLAction;
import kinoko.database.postgresql.util.SQLBooleanAction;

import java.sql.Connection;
import java.sql.SQLException;



public abstract class PostgresAccessor {
    private final HikariDataSource dataSource;

    public PostgresAccessor(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a connection from the pool for a single operation.
     * Use try-with-resources when calling this!
     */
    protected final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Executes the given action within a database transaction using a connection from the instance's data source.
     * Sets auto-commit to false, runs the action, commits the transaction if successful, and rolls back if a SQLException occurs.
     * Finally, restores auto-commit to true and closes the connection.
     *
     * @param action The action to execute inside the transaction. Can throw SQLException.
     * @return true if the transaction committed successfully; false if an exception occurred and rollback was performed.
     */
    public boolean withTransaction(SQLAction action) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            action.apply(conn);

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                }
                catch (SQLException rollbackEx)
                {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch
                (SQLException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes the given action within a database transaction using the provided connection.
     * Sets auto-commit to false on the connection, executes the action, commits the transaction
     * if successful, and rolls back if a SQLException occurs. Restores auto-commit to true and closes
     * the connection in the finally block.
     *
     * @param conn   The database connection to use for the transaction. This connection will be closed by this method.
     * @param action The action to execute inside the transaction. Can throw SQLException.
     * @return true if the transaction committed successfully; false if an exception occurred and rollback was performed.
     */
    public static boolean withTransaction(Connection conn,
                                          SQLAction action) {
        try {
            conn.setAutoCommit(false);
            action.apply(conn);
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                }
                catch (SQLException rollbackEx)
                {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes a database operation within a managed transaction using a connection from the data source.
     * Auto-commit is disabled before execution and restored afterward.
     * The provided action determines logical success; if it returns false or throws an exception, the transaction is rolled back.
     * If successful, the transaction is committed before closing the connection.
     *
     * @param action the action to execute inside the transaction; may throw SQLException
     * @return true if the transaction was committed successfully, false if an exception occurred or rollback was performed
     */
    public boolean withTransaction(SQLBooleanAction action) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            boolean logicalSuccess = action.apply(conn);
            if (!logicalSuccess){
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                }
                catch (SQLException rollbackEx)
                {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch
                (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

