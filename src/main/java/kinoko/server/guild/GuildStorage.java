package kinoko.server.guild;

import kinoko.database.DatabaseManager;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class GuildStorage {
    private final ConcurrentHashMap<Integer, Guild> guildMap = new ConcurrentHashMap<>();

    public boolean addGuild(Guild guild) {
        if (guildMap.containsKey(guild.getGuildId())) {
            return false;
        }
        if (!DatabaseManager.guildAccessor().newGuild(guild)) {
            return false;
        }
        guildMap.put(guild.getGuildId(), guild);
        return true;
    }

    public boolean removeGuild(Guild guild) {
        if (!DatabaseManager.guildAccessor().deleteGuild(guild.getGuildId())) {
            return false;
        }
        guildMap.remove(guild.getGuildId());
        return true;
    }

    public Optional<Guild> getGuildById(int guildId) {
        if (guildId == 0) {
            return Optional.empty();
        }
        if (guildMap.containsKey(guildId)) {
            return Optional.of(guildMap.get(guildId));
        }
        final Optional<Guild> guildResult = DatabaseManager.guildAccessor().getGuildById(guildId);
        guildResult.ifPresent(guild -> guildMap.put(guildId, guild));
        return guildResult;
    }

    /**
     * Retrieves all guilds currently stored in memory.
     *
     * @return a collection of all guilds in the cache
     */
    public Collection<Guild> getAllGuilds() {
        return guildMap.values();
    }

}
