package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import kinoko.database.GiftAccessor;
import kinoko.server.cashshop.Gift;

import java.util.List;

public class CassandraGiftAccessor extends CassandraAccessor implements GiftAccessor {
    public CassandraGiftAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    @Override
    public List<Gift> getGiftsByAccountId(int accountId) {
        return null;
    }

    @Override
    public boolean newGift(Gift gift, int receiverId) {
        return false;
    }

    @Override
    public boolean deleteGift(Gift gift) {
        return false;
    }
}
