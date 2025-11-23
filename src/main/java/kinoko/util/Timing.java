package kinoko.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;


/**
 * Utility class for time operations and measuring durations.
 *
 * Provides methods for current time, unit conversions, and timing/logging code execution.
 * Helps remove magic numbers across the codebase. All methods are static; the class cannot be instantiated.
 */
public final class Timing {
    private static final Logger log = LogManager.getLogger(Timing.class);
    public static final int DAY_SECONDS = 86400;
    public static final int DAY_MINUTES = 1440;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final long NANOS_IN_MILLI = 1_000_000L;

    private Timing() {
        // prevent instantiation
    }

    /**
     * Returns the current time as a Unix timestamp in seconds.
     *
     * @return the current Unix timestamp in seconds
     */
    public static long nowSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return current time in milliseconds
     */
    public static long nowMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Executes the given action and logs the time it took to complete.
     * Uses Timing's internal logger.
     *
     * @param taskName a descriptive name for the task being measured
     * @param action a Runnable representing the code block whose duration is to be measured
     */
    public static void logDuration(String taskName, Runnable action) {
        logDuration(taskName, action, log);
    }

    /**
     * Executes the given action and logs the time it took to complete using the provided logger.
     *
     * @param taskName a descriptive name for the task being measured
     * @param action a Runnable representing the code block whose duration is to be measured
     * @param logger the Logger to use for logging
     */
    public static void logDuration(String taskName, Runnable action, Logger logger) {
        long start = System.nanoTime();
        action.run();
        long elapsedMillis = (System.nanoTime() - start) / NANOS_IN_MILLI; // simpler conversion
        logger.info("{} completed in {} milliseconds", taskName, elapsedMillis);
    }

    /**
     * Executes the given action and logs the time it took to complete using the provided logger.
     *
     * This method measures the elapsed time of the action in milliseconds and logs a message
     * indicating the task name and duration. Unlike the standard logDuration method, this
     * version logDurationThrowing allows the action to throw checked exceptions, which will propagate to the caller.
     *
     * @param taskName a descriptive name for the task being measured
     * @param action a ThrowingRunnable representing the code block whose duration is to be measured
     * @param logger the Logger to use for logging the duration
     * @param <E> the type of checked exception that the action may throw
     * @throws E if the action throws a checked exception
     */
    public static <E extends Exception> void logDurationThrowing(String taskName, ThrowingRunnable<E> action, Logger logger) throws E {
        long start = System.nanoTime();
        action.run(); // can throw E
        long elapsedMillis = (System.nanoTime() - start) / NANOS_IN_MILLI;
        logger.info("{} completed in {} milliseconds", taskName, elapsedMillis);
    }

    /**
     * Converts a duration in seconds to whole minutes, rounding down.
     *
     * Example:
     *   90 seconds -> 1 minute
     *   59 seconds -> 0 minutes
     *
     * @param seconds the duration in seconds
     * @return the equivalent number of minutes
     */
    public static int secondsToMinutes(long seconds) {
        return (int) (seconds / SECONDS_IN_MINUTE);
    }
}