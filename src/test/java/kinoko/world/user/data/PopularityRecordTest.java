package kinoko.world.user.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


/**
 * Unit tests for PopularityRecord, validating fame/popularity interaction timing rules.
 */
public class PopularityRecordTest {
    /**
     * Tests whether PopularityRecord correctly detects if popularity was given today.
     * <p>
     * Verifies:
     * - No record returns false initially.
     * - Adding a record with today's date makes it return true.
     */
    @Test
    public void testHasGivenPopularityToday() {
        final PopularityRecord pr = new PopularityRecord();

        Assertions.assertFalse(pr.hasGivenPopularityToday(), "Expected no popularity given today at first.");

        pr.addRecord(1, Instant.now());  // Simulate giving popularity to character ID 1 today
        Assertions.assertTrue(pr.hasGivenPopularityToday(), "Expected true after adding today's popularity record.");
    }

    /**
     * Tests if PopularityRecord correctly tracks whether popularity has been given
     * to a target within the current month.
     * <p>
     * Verifies:
     * - Popularity given in the previous month is not detected.
     * - Popularity given in the current month is detected.
     * - No match for a different character ID.
     */
    @Test
    public void testHasGivenPopularityTarget() {
        final PopularityRecord pr = new PopularityRecord();
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime previousMonth = now.minusMonths(1);

        pr.addRecord(1, previousMonth.toInstant());
        Assertions.assertFalse(pr.hasGivenPopularityTarget(1), "Expected no match for previous month.");

        pr.addRecord(1, now.toInstant());
        Assertions.assertTrue(pr.hasGivenPopularityTarget(1), "Expected match for current month.");

        Assertions.assertFalse(pr.hasGivenPopularityTarget(2), "Expected no match for different character ID.");
    }
}
