package kinoko.server.event;

import java.util.concurrent.*;

public final class EventScheduler {
    private static ScheduledExecutorService scheduler;
    private static ExecutorService executor;

    public static void initialize() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static <V> ScheduledFuture<V> addEvent(Callable<V> callable, long delay) {
        return scheduler.schedule(callable, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> addEvent(Runnable runnable, long delay) {
        return scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static <V> ScheduledFuture<V> addEvent(Callable<V> callable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(callable, delay, timeUnit);
    }

    public static ScheduledFuture<?> addEvent(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(runnable, delay, timeUnit);
    }
}
