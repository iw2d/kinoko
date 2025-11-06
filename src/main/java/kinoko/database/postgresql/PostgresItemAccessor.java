package kinoko.database.postgresql;

import com.zaxxer.hikari.HikariDataSource;
import kinoko.database.ItemAccessor;
import kinoko.database.postgresql.type.ItemDao;
import kinoko.world.item.Item;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

public final class PostgresItemAccessor extends PostgresAccessor implements ItemAccessor {
    public PostgresItemAccessor(HikariDataSource dataSource) {
        super(dataSource);

        try (Connection conn = getConnection()){
            ItemDao.cleanupInvalidItems(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a single item to the database within a transaction.
     * If the item does not already exist, it will be created.
     * This method delegates to ItemDao.saveItemsBatch for consistency with batch operations.
     *
     * @param item the item to be saved or created
     * @return true if the transaction completes successfully; false if the transaction fails
     */
    @Override
    public boolean saveItem(Item item) {
        return withTransaction(conn -> {
                // will also create any items that don't exist.
                ItemDao.saveItemsBatch(conn, Collections.singletonList(item));
                return true;
        });
    }
}
