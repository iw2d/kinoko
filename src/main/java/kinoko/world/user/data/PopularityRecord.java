package kinoko.world.user.data;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class PopularityRecord {
    private final Map<Integer, Instant> popularityRecords = new HashMap<>();

    public Map<Integer, Instant> getRecords() {
        return popularityRecords;
    }

    public void addRecord(int characterId, Instant timestamp) {
        popularityRecords.put(characterId, timestamp);
    }

    public boolean hasGivenPopularityToday() {
        final Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        return popularityRecords.values().stream()
                .anyMatch((timestamp) -> timestamp.isAfter(today));
    }

    public boolean hasGivenPopularityTarget(int characterId) {
        final ZonedDateTime month = ZonedDateTime.now(ZoneId.of("UTC")).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        return popularityRecords.getOrDefault(characterId, Instant.MIN).isAfter(month.toInstant());
    }
}
