package kinoko.database.postgresql.util;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLAction {
    void apply(Connection conn) throws SQLException;
}
