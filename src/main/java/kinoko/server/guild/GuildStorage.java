package kinoko.server.guild;

import kinoko.database.DatabaseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GuildStorage {
    private final Lock lock = new ReentrantLock();
    private final Map<Integer, Guild> mapByGuildId = new HashMap<>();
    private final Map<String, Guild> mapByGuildName = new HashMap<>();

    public boolean addGuild(Guild guild) {
        lock.lock();
        try {
            if (mapByGuildId.containsKey(guild.getGuildId())) {
                return false;
            }
            if (mapByGuildName.containsKey(guild.getGuildName())) {
                return false;
            }
            if (!DatabaseManager.guildAccessor().newGuild(guild)) {
                return false;
            }
            mapByGuildId.put(guild.getGuildId(), guild);
            mapByGuildName.put(guild.getGuildName(), guild);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeGuild(Guild guild) {
        lock.lock();
        try {
            if (!DatabaseManager.guildAccessor().deleteGuild(guild.getGuildId())) {
                return false;
            }
            mapByGuildId.remove(guild.getGuildId());
            mapByGuildName.remove(guild.getGuildName());
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Guild> getGuildById(int guildId) {
        if (guildId == 0) {
            return Optional.empty();
        }
        lock.lock();
        try {
            if (mapByGuildId.containsKey(guildId)) {
                return Optional.of(mapByGuildId.get(guildId));
            }
            final Optional<Guild> guildResult = DatabaseManager.guildAccessor().getGuildById(guildId);
            if (guildResult.isPresent()) {
                final Guild guild = guildResult.get();
                mapByGuildId.put(guild.getGuildId(), guild);
                mapByGuildName.put(guild.getGuildName(), guild);
            }
            return guildResult;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Guild> getGuildByName(String guildName) {
        lock.lock();
        try {
            if (mapByGuildName.containsKey(guildName)) {
                return Optional.of(mapByGuildId.get(guildName));
            }
            final Optional<Guild> guildResult = DatabaseManager.guildAccessor().getGuildByName(guildName);
            if (guildResult.isPresent()) {
                final Guild guild = guildResult.get();
                mapByGuildId.put(guild.getGuildId(), guild);
                mapByGuildName.put(guild.getGuildName(), guild);
            }
            return guildResult;
        } finally {
            lock.unlock();
        }
    }
}
