package kinoko.database;

import kinoko.server.guild.Guild;

import java.util.Optional;

public interface GuildAccessor {
    Optional<Guild> getGuildById(int guildId);

    boolean checkGuildNameAvailable(String name);

    boolean newGuild(Guild guild);

    boolean saveGuild(Guild guild);

    boolean deleteGuild(int guildId);
}