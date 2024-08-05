package kinoko.database.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import kinoko.database.GuildAccessor;
import kinoko.database.cassandra.table.GuildTable;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildMember;

import java.util.List;
import java.util.Optional;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;

public final class CassandraGuildAccessor extends CassandraAccessor implements GuildAccessor {
    public CassandraGuildAccessor(CqlSession session, String keyspace) {
        super(session, keyspace);
    }

    private Guild loadGuild(Row row) {
        final int guildId = row.getInt(GuildTable.GUILD_ID);
        final String guildName = row.getString(GuildTable.GUILD_NAME);
        final Guild guild = new Guild(guildId, guildName);
        final List<String> gradeNames = row.getList(GuildTable.GRADE_NAMES, String.class);
        if (gradeNames != null) {
            guild.setGradeNames(gradeNames);
        }
        final List<GuildMember> members = row.getList(GuildTable.MEMBERS, GuildMember.class);
        if (members != null) {
            for (GuildMember member : members) {
                guild.addMember(member);
            }
        }
        guild.setMemberMax(row.getInt(GuildTable.MEMBER_MAX));
        guild.setMarkBg(row.getShort(GuildTable.MARK_BG));
        guild.setMarkBgColor(row.getByte(GuildTable.MARK_BG_COLOR));
        guild.setMark(row.getShort(GuildTable.MARK));
        guild.setMarkColor(row.getByte(GuildTable.MARK_COLOR));
        guild.setNotice(row.getString(GuildTable.NOTICE));
        guild.setPoints(row.getInt(GuildTable.POINTS));
        guild.setLevel(row.getByte(GuildTable.LEVEL));
        return guild;
    }

    @Override
    public Optional<Guild> getGuildById(int guildId) {
        final ResultSet selectResult = getSession().execute(
                selectFrom(getKeyspace(), GuildTable.getTableName()).all()
                        .whereColumn(GuildTable.GUILD_ID).isEqualTo(literal(guildId))
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
                selectFrom(getKeyspace(), GuildTable.getTableName()).all()
                        .whereColumn(GuildTable.GUILD_NAME_INDEX).isEqualTo(literal(lowerName(name)))
                        .build()
        );
        for (Row row : selectResult) {
            final String existingName = row.getString(GuildTable.GUILD_NAME_INDEX);
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
        final CodecRegistry registry = getSession().getContext().getCodecRegistry();
        final ResultSet updateResult = getSession().execute(
                update(getKeyspace(), GuildTable.getTableName())
                        .setColumn(GuildTable.GUILD_NAME, literal(guild.getGuildName()))
                        .setColumn(GuildTable.GUILD_NAME_INDEX, literal(lowerName(guild.getGuildName())))
                        .setColumn(GuildTable.GRADE_NAMES, literal(guild.getGradeNames()))
                        .setColumn(GuildTable.MEMBERS, literal(guild.getGuildMembers(), registry))
                        .setColumn(GuildTable.MEMBER_MAX, literal(guild.getMemberMax()))
                        .setColumn(GuildTable.MARK_BG, literal(guild.getMarkBg()))
                        .setColumn(GuildTable.MARK_BG_COLOR, literal(guild.getMarkBgColor()))
                        .setColumn(GuildTable.MARK, literal(guild.getMark()))
                        .setColumn(GuildTable.MARK_COLOR, literal(guild.getMarkColor()))
                        .setColumn(GuildTable.NOTICE, literal(guild.getNotice()))
                        .setColumn(GuildTable.POINTS, literal(guild.getPoints()))
                        .setColumn(GuildTable.LEVEL, literal(guild.getLevel()))
                        .whereColumn(GuildTable.GUILD_ID).isEqualTo(literal(guild.getGuildId()))
                        .build()
        );
        return updateResult.wasApplied();
    }

    @Override
    public boolean deleteGuild(int guildId) {
        final ResultSet updateResult = getSession().execute(
                deleteFrom(getKeyspace(), GuildTable.getTableName())
                        .whereColumn(GuildTable.GUILD_ID).isEqualTo(literal(guildId))
                        .build()
        );
        return updateResult.wasApplied();
    }
}
