package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.FamilyAccessor;
import kinoko.database.postgresql.type.FamilyTreeDao;
import kinoko.server.family.FamilyTree;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

public final class PostgresFamilyAccessor extends PostgresAccessor implements FamilyAccessor {

    public PostgresFamilyAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }


    @Override
    public Collection<FamilyTree> getAllFamilies(){
        try (Connection conn = getConnection()) {
            return FamilyTreeDao.getAllFamilies(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    };
}
