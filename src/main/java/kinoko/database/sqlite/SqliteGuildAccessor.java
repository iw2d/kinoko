package kinoko.database.sqlite;

import kinoko.database.GuildAccessor;
import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildRanking;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public final class SqliteGuildAccessor extends SqliteAccessor implements GuildAccessor {
    public SqliteGuildAccessor(Connection connection) {
        super(connection);
    }

    @Override
    public Optional<Guild> getGuildById(int guildId) {
        return Optional.empty();
    }

    @Override
    public boolean checkGuildNameAvailable(String name) {
        return false;
    }

    @Override
    public boolean newGuild(Guild guild) {
        return false;
    }

    @Override
    public boolean saveGuild(Guild guild) {
        return false;
    }

    @Override
    public boolean deleteGuild(int guildId) {
        return false;
    }

    @Override
    public List<GuildRanking> getGuildRankings() {
        return List.of();
    }

    public static void createTable(Connection connection) {
        // TODO
    }
}
