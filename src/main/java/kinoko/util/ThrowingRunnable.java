package kinoko.util;

/**
 * A functional interface similar to Runnable that allows checked exceptions to be thrown.
 *
 * This interface can be used in contexts where a block of code needs to be executed
 * (such as a lambda) and may throw a checked exception. It is especially useful in
 * utility methods like Timing.logDurationThrowing where you want to measure execution time
 * while still allowing exceptions to propagate.
 *
 * @param <E> the type of checked exception that may be thrown
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
    void run() throws E;
}