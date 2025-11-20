package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.FamilyAccessor;
import kinoko.database.postgresql.type.FamilyTreeDao;
import kinoko.server.family.FamilyTree;
import kinoko.server.guild.Guild;

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

    @Override
    public void saveFamily(FamilyTree family){
        try (Connection conn = getConnection()) {
            FamilyTreeDao.saveFamilyTree(conn, family);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveFamilies(Collection<FamilyTree> families){
        try (Connection conn = getConnection()) {
            FamilyTreeDao.saveAllFamilies(conn, families);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
