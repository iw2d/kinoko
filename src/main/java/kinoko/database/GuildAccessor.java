package kinoko.database;

import kinoko.server.guild.Guild;
import kinoko.server.guild.GuildBoardEntry;
import kinoko.server.guild.GuildRanking;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GuildAccessor {
    Optional<Guild> getGuildById(int guildId);

    boolean checkGuildNameAvailable(String name);

    boolean newGuild(Guild guild);

    boolean saveGuild(Guild guild);

    boolean deleteGuild(int guildId);

    List<GuildRanking> getGuildRankings();

    default void saveAll(Collection<Guild> guilds){
        if (guilds == null || guilds.isEmpty()) return;

        for (Guild guild : guilds) {
            saveGuild(guild);
        }
    }
}