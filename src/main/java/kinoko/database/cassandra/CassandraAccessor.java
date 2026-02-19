package kinoko.database.cassandra;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.Literal;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public abstract class CassandraAccessor {
    protected static final DataType JSON_TYPE = DataTypes.TEXT;

    private final CqlSession session;
    private final String keyspace;

    public CassandraAccessor(CqlSession session, String keyspace) {
        this.session = session;
        this.keyspace = keyspace;
    }

    public final CqlSession getSession() {
        return session;
    }

    public final String getKeyspace() {
        return keyspace;
    }

    protected final String lowerName(String name) {
        return name.toLowerCase();
    }

    protected static JSONObject getJsonObject(Row row, String key) {
        return JSON.parseObject(row.getString(key));
    }

    protected static Literal literalJsonObject(JSONObject object) {
        return literal(JSON.toJSONString(object));
    }

    protected static JSONArray getJsonArray(Row row, String key) {
        return JSON.parseArray(row.getString(key));
    }

    protected static Literal literalJsonArray(JSONArray array) {
        return literal(JSON.toJSONString(array));
    }
}
