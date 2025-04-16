package kinoko.world.user.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for PopularityManager, validating fame/popularity interaction timing rules.
 */
public class PopularityManagerTest {

    // Set up a new PopularityManager instance for each test
    PopularityManager pm = new PopularityManager();

    /**
     * Tests whether PopularityManager correctly detects if popularity was given today.
     * 
     * Verifies:
     * - No record returns false initially.
     * - Adding a record with today's date makes it return true.
     */
    @Test
    public void testHasGivenPopularityToday() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS); // Get current time in UTC truncated to date

        Assertions.assertFalse(pm.hasGivenPopularityToday(), "Expected no popularity given today at first.");

        pm.getRecords().put(1, now);  // Simulate giving popularity to character ID 1 today

        Assertions.assertTrue(pm.hasGivenPopularityToday(), "Expected true after adding today's popularity record.");
    }

    /**
     * Tests if PopularityManager correctly tracks whether popularity has been given
     * to a target within a specified number of days.
     * 
     * Verifies:
     * - Popularity given 25 days ago is detected within a 30-day window.
     * - Same record is not detected within a 5-day window.
     * - No match for a different character ID.
     */
    @Test
    public void testHasGivenPopularityDays() {
        Instant now = Instant.now(); // Current time
        Instant past = now.minus(25, ChronoUnit.DAYS); // 25 days ago

        pm.getRecords().put(1, past);  // Popularity given to character ID 1, 25 days ago

        Assertions.assertTrue(pm.hasGivenPopularityTargetDays(1, 30), "Expected match within 30 days.");
        Assertions.assertFalse(pm.hasGivenPopularityTargetDays(1, 5), "Expected no match within 5 days.");
        Assertions.assertFalse(pm.hasGivenPopularityTargetDays(2, 30), "Expected no match for different character ID.");
    }
}
