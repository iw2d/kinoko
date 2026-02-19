package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kinoko.database.GuildAccessor;
import kinoko.database.json.GuildSerializer;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;
import kinoko.server.guild.GuildRanking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static kinoko.database.schema.GuildSchema.*;

public final class CassandraGuildAccessor extends CassandraAccessor implements GuildAccessor {
    private static final String tableName = "guild_table";
    private static final String guildNameIndex = "guild_name_index";
    private final GuildSerializer guildSerializer = new GuildSerializer();

    public CassandraGuildAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Guild loadGuild(Row row) {
        final int guildId = row.getInt(GUILD_ID);
        final String guildName = row.getString(GUILD_NAME);
        final Guild guild = new Guild(guildId, guildName);
        guild.setGradeNames(guildSerializer.deserializeGradeNames(getJsonArray(row, GRADE_NAMES)));
        for (GuildMember member : guildSerializer.deserializeGuildMembers(getJsonArray(row, MEMBERS))) {
            guild.addMember(member);
        }
        guild.setMemberMax(row.getInt(MEMBER_MAX));
        guild.setMarkBg(row.getShort(MARK_BG));
        guild.setMarkBgColor(row.getByte(MARK_BG_COLOR));
        guild.setMark(row.getShort(MARK));
        guild.setMarkColor(row.getByte(MARK_COLOR));
        guild.setNotice(row.getString(NOTICE));
        guild.setPoints(row.getInt(POINTS));
        guild.setLevel(row.getByte(LEVEL));

        guild.getBoardEntries().addAll(guildSerializer.deserializeBoardEntryList(getJsonArray(row, BOARD_ENTRY_LIST)));
        guild.setBoardNoticeEntry(guildSerializer.deserializeBoardEntryNotice(getJsonObject(row, BOARD_ENTRY_NOTICE)));
        guild.setBoardEntryCounter(new AtomicInteger(row.getInt(BOARD_ENTRY_COUNTER)));
        return guild;
    }

    @Override
    public Optional<Guild> getGuildById(int guildId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(GUILD_ID).isEqualTo(literal(guildId))
                        .build()
        );
        for (Row row : selectResult) {
            return Optional.of(loadGuild(row));
        }
        return Optional.empty();
    }

    @Override
    public boolean checkGuildNameAvailable(String name) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName).all()
                        .whereColumn(guildNameIndex).isEqualTo(literal(lowerName(name)))
                        .build()
        );
        for (Row row : selectResult) {
            final String existingName = row.getString(guildNameIndex);
            if (existingName != null && existingName.equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized boolean newGuild(Guild guild) {
        if (!checkGuildNameAvailable(guild.getGuildName())) {
            return false;
        }
        return saveGuild(guild);
    }

    @Override
    public boolean saveGuild(Guild guild) {
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), tableName)
                        .setColumn(GUILD_NAME, literal(guild.getGuildName()))
                        .setColumn(guildNameIndex, literal(lowerName(guild.getGuildName())))
                        .setColumn(GRADE_NAMES, literalJsonArray(guildSerializer.serializeGradeNames(guild.getGradeNames())))
                        .setColumn(MEMBERS, literalJsonArray(guildSerializer.serializeGuildMembers(guild.getGuildMembers())))
                        .setColumn(MEMBER_MAX, literal(guild.getMemberMax()))
                        .setColumn(MARK_BG, literal(guild.getMarkBg()))
                        .setColumn(MARK_BG_COLOR, literal(guild.getMarkBgColor()))
                        .setColumn(MARK, literal(guild.getMark()))
                        .setColumn(MARK_COLOR, literal(guild.getMarkColor()))
                        .setColumn(NOTICE, literal(guild.getNotice()))
                        .setColumn(POINTS, literal(guild.getPoints()))
                        .setColumn(LEVEL, literal(guild.getLevel()))
                        .setColumn(BOARD_ENTRY_LIST, literalJsonArray(guildSerializer.serializeBoardEntryList(guild.getBoardEntries())))
                        .setColumn(BOARD_ENTRY_NOTICE, literalJsonObject(guildSerializer.serializeBoardEntryNotice(guild.getBoardNoticeEntry())))
                        .setColumn(BOARD_ENTRY_COUNTER, literal(guild.getBoardEntryCounter().get()))
                        .whereColumn(GUILD_ID).isEqualTo(literal(guild.getGuildId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteGuild(int guildId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), tableName)
                        .whereColumn(GUILD_ID).isEqualTo(literal(guildId))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public List<GuildRanking> getGuildRankings() {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), tableName)
                        .columns(
                                GUILD_NAME,
                                POINTS,
                                MARK,
                                MARK_COLOR,
                                MARK_BG,
                                MARK_BG_COLOR
                        )
                        .build()
                        .setExecutionProfileName(CassandraConnector.PROFILE_ONE)
        );
        final List<GuildRanking> guildRankings = new ArrayList<>();
        for (Row row : selectResult) {
            guildRankings.add(new GuildRanking(
                    row.getString(GUILD_NAME),
                    row.getInt(POINTS),
                    row.getShort(MARK),
                    row.getByte(MARK_COLOR),
                    row.getShort(MARK_BG),
                    row.getByte(MARK_BG_COLOR)
            ));
        }
        return guildRankings.stream()
                .sorted(Comparator.comparing(GuildRanking::getPoints).reversed())
                .toList();
    }


    public static void createTable(CqlSession session, String keyspace) {
        session.execute(
                SchemaBuilder.createTable(keyspace, tableName)
                        .ifNotExists()
                        .withPartitionKey(GUILD_ID, DataTypes.INT)
                        .withColumn(GUILD_NAME, DataTypes.TEXT)
                        .withColumn(guildNameIndex, DataTypes.TEXT)
                        .withColumn(GRADE_NAMES, JSON_TYPE)
                        .withColumn(MEMBERS, JSON_TYPE)
                        .withColumn(MEMBER_MAX, DataTypes.INT)
                        .withColumn(MARK_BG, DataTypes.SMALLINT)
                        .withColumn(MARK_BG_COLOR, DataTypes.TINYINT)
                        .withColumn(MARK, DataTypes.SMALLINT)
                        .withColumn(MARK_COLOR, DataTypes.TINYINT)
                        .withColumn(NOTICE, DataTypes.TEXT)
                        .withColumn(POINTS, DataTypes.INT)
                        .withColumn(LEVEL, DataTypes.TINYINT)
                        .withColumn(BOARD_ENTRY_LIST, JSON_TYPE)
                        .withColumn(BOARD_ENTRY_NOTICE, JSON_TYPE)
                        .withColumn(BOARD_ENTRY_COUNTER, DataTypes.INT)
                        .build()
        );
        session.execute(
                SchemaBuilder.createIndex()
                        .ifNotExists()
                        .onTable(keyspace, tableName)
                        .andColumn(guildNameIndex)
                        .build()
        );
    }
}
