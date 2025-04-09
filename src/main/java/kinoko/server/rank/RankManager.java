package kinoko.server.rank;

import kinoko.database.DatabaseManager;
import kinoko.server.guild.GuildRanking;
import kinoko.server.node.ServerExecutor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class RankManager {
    private static Map<Integer, CharacterRank> originalCharacterRanks;
    private static Map<Integer, CharacterRank> currentCharacterRanks;
    private static List<GuildRanking> guildRankings;
    private static ScheduledFuture<?> refreshSchedule;

    public static void initialize() {
        originalCharacterRanks = DatabaseManager.characterAccessor().getCharacterRanks();
        currentCharacterRanks = originalCharacterRanks;
        guildRankings = DatabaseManager.guildAccessor().getGuildRankings();
        // Schedule refresh every 10 minutes
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime nextStateTime = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(10 - (now.getMinute() % 10));
        refreshSchedule = ServerExecutor.scheduleServiceAtFixedRate(RankManager::refresh, now.until(nextStateTime, ChronoUnit.MILLIS), 10 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public static void refresh() {
        if (getNearestHour() == 0 && getNearestMinute() == 0) {
            // Refresh original character ranks
            originalCharacterRanks = DatabaseManager.characterAccessor().getCharacterRanks();
            currentCharacterRanks = originalCharacterRanks;
        } else {
            // Compute character rank gaps
            final Map<Integer, CharacterRank> newCharacterRanks = DatabaseManager.characterAccessor().getCharacterRanks();
            for (var entry : newCharacterRanks.entrySet()) {
                final CharacterRank newRank = entry.getValue();
                final CharacterRank oldRank = originalCharacterRanks.get(entry.getKey());
                if (oldRank != null) {
                    newRank.setWorldRankGap(oldRank.getWorldRank() - newRank.getWorldRank());
                    newRank.setJobRankGap(oldRank.getJobRank() - newRank.getJobRank());
                }
            }
            currentCharacterRanks = newCharacterRanks;
        }
        // Refresh guild rankings
        guildRankings = DatabaseManager.guildAccessor().getGuildRankings();
    }

    public static void shutdown() {
        refreshSchedule.cancel(true);
    }

    public static Optional<CharacterRank> getCharacterRank(int characterId) {
        return Optional.ofNullable(currentCharacterRanks.get(characterId));
    }

    public static List<GuildRanking> getGuildRankings() {
        return guildRankings.subList(0, Math.min(guildRankings.size(), 100));
    }

    private static int getNearestHour() {
        long minutes = (System.currentTimeMillis() / 60000) % (60 * 24);
        return (int) Math.round(minutes / 60.0) % 24;
    }

    private static int getNearestMinute() {
        long seconds = (System.currentTimeMillis() / 1000) % 3600;
        return (int) Math.round(seconds / 60.0) % 60;
    }
}
