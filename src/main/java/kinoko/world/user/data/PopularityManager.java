package kinoko.world.user.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;

import kinoko.world.user.User;

public final class PopularityManager {

    private final HashMap<Integer, Instant> popularityRecords = new HashMap<>();

    public boolean hasGivenPopularityToday() {
    Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS); 

    return popularityRecords.values().stream()
        .anyMatch(time -> time.truncatedTo(ChronoUnit.DAYS).equals(today));
    }

   public boolean hasGivenPopularityTargetDays(int characterId, int days) {
    Instant threshold = Instant.now().minus(days, ChronoUnit.DAYS);
    return Optional.ofNullable(popularityRecords.get(characterId))
        .map(record -> record.isAfter(threshold))
        .orElse(false);
}

    public HashMap<Integer, Instant> getRecords() {
        return popularityRecords;
    }

    public void record(int characterId) {
        popularityRecords.put(characterId, Instant.now());
    }

    public PopularityResult popularityResult(User provider, User target) {
        if (target == null) {
            return PopularityResult.InvalidCharacterID;
        }

        if (provider.getCharacterId() == target.getCharacterId()) {
            return PopularityResult.InvalidCharacterID;
        }

        if (provider.getLevel() < 15 || target.getLevel() < 15) {
            return PopularityResult.LevelLow;
        }

       if (hasGivenPopularityToday()) {
           return PopularityResult.AlreadyDoneToday;
       }

        if (hasGivenPopularityTargetDays(target.getCharacterId(), 30)) {
            return PopularityResult.AlreadyDoneTarget;
        }

        return null;
    }
}
