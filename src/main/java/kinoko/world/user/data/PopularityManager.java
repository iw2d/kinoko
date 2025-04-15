package kinoko.world.user.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import kinoko.world.user.User;

public final class PopularityManager {

    public enum PopularityResult {
        OK_TARGET,            // 0 - '%s' have raised '%s''s level of fame.
        ERROR_USERNAME,       // 1 = The user name is incorrectly entered.
        ERROR_LEVEL,          // 2 = Users under level 15 are unable to toggle with fame.
        ERROR_DAILY_LIMIT,    // 3 = You can't raise or drop a level of fame anymore for today.
        ERROR_MONTHLY_LIMIT,  // 4 = You can't raise or drop a level of fame of that character anymore for this month.
        OK_PROVIDER           // 5 - You have raised/dropped '%s''s level of fame.
    }

    private final HashMap<Instant, Integer> popularityRecords = new HashMap<>();

    /**
     * Checks if the user has already given fame today.
     * Uses `Instant` for precise timestamp comparison.
     *
     * @return {@code true} if fame has already been given today; {@code false} otherwise.
     */
    public boolean hasGivenPopularityToday() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS); 
        
        return popularityRecords.keySet().stream()
            .anyMatch(time -> time.truncatedTo(ChronoUnit.DAYS).equals(now));
    }

    /**
     * Checks if the user has given popularity to a target within the last specified number of days.
     *
     * @param characterId the ID of the character to check fame for.
     * @param days the number of days to check back for fame interactions.
     * @return {@code true} if fame was given to the target within the last 'days' days; {@code false} otherwise.
     */
    public boolean hasGivenPopularityTargetDays(int characterId, int days) {
        Instant threshold = Instant.now().minus(days, ChronoUnit.DAYS);

        return popularityRecords.entrySet().stream()
            .filter(entry -> entry.getValue() == characterId)
            .anyMatch(entry -> entry.getKey().isAfter(threshold));
    }

    public HashMap<Instant, Integer> getRecords() {
        return popularityRecords;
    }

    /**
     * Records a new fame interaction with the current time.
     *
     * @param characterId the ID of the character receiving fame.
     */
    public void record(int characterId) {
        popularityRecords.put(Instant.now(), characterId);
    }

    /**
     * Determines the result of a fame request, checking various conditions such as username validity,
     * level restrictions, daily and monthly limits.
     *
     * @param provider the user who is giving fame.
     * @param target the user who is receiving fame.
     * @return the {@link PopularityResult} indicating the result of the fame request.
     */
    public PopularityResult popularityResult(User provider, User target) {
        if (target == null) {
            return PopularityResult.ERROR_USERNAME;
        }

        if (provider.getLevel() < 15 || target.getLevel() < 15) {
            return PopularityResult.ERROR_LEVEL;
        }

       if (hasGivenPopularityToday()) {
           return PopularityResult.ERROR_DAILY_LIMIT;
       }

        if (hasGivenPopularityTargetDays(target.getCharacterId(), 30)) {
            return PopularityResult.ERROR_MONTHLY_LIMIT;
        }

        return null;
    }
}
