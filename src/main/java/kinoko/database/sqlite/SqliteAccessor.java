package kinoko.database.sqlite;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public abstract class SqliteAccessor {
    public static final String INSTANT_TYPE = "BIGINT";
    public static final String JSON_TYPE = "TEXT";

    private final Connection connection;

    public SqliteAccessor(Connection connection) {
        this.connection = connection;
    }

    public final Connection getConnection() {
        return connection;
    }

    protected static Instant getInstant(ResultSet rs, String key) throws SQLException {
        final long value = rs.getLong(key);
        return value != 0 ? Instant.ofEpochMilli(value) : null;
    }

    protected static void setInstant(PreparedStatement ps, int index, Instant value) throws SQLException {
        ps.setLong(index, value != null ? value.toEpochMilli() : 0);
    }

    protected static JSONObject getJsonObject(ResultSet rs, String key) throws SQLException {
        return JSON.parseObject(rs.getString(key));
    }

    protected static void setJsonObject(PreparedStatement ps, int index, JSONObject object) throws SQLException {
        ps.setString(index, JSON.toJSONString(object));
    }

    protected static JSONArray getJsonArray(ResultSet rs, String key) throws SQLException {
        return JSON.parseArray(rs.getString(key));
    }

    protected static void setJsonArray(PreparedStatement ps, int index, JSONArray array) throws SQLException {
        ps.setString(index, JSON.toJSONString(array));
    }
}
