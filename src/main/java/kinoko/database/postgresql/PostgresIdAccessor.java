package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.IdAccessor;
import kinoko.database.postgresql.type.ItemDao;
import kinoko.world.item.Item;

import java.sql.SQLException;

public final class PostgresIdAccessor extends PostgresAccessor implements IdAccessor {

    public PostgresIdAccessor(HikariDataSource dataSource) {
        super(dataSource);
    }

    /**
     * Generates a new item SN for the given item if it does not already have one.
     * If the item already has a serial number, this method returns true immediately.
     * Otherwise, it creates a new item entry in the database and assigns the generated ID.
     *
     * @param item the item for which to generate an ID
     * @return true if the item already had an ID or was successfully assigned one, false if an error occurred
     */
    @Override
    public boolean generateItemSn(Item item) {
        if (!item.hasNoSN()){
            return true;
        }

        try {
            return withTransaction(getConnection(), c -> ItemDao.createNewItem(c, item));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
