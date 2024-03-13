package kinoko.server.event;

import kinoko.world.skill.SkillProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

public final class EventScheduler {
    private static final Logger log = LogManager.getLogger(SkillProcessor.class);
    private static ScheduledExecutorService scheduler;
    private static ExecutorService executor;

    public static void initialize() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public static ScheduledFuture<?> addEvent(Runnable runnable, long delay) {
        return scheduler.schedule(() -> wrapAndSubmit(runnable), delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> addEvent(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> wrapAndSubmit(runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> addFixedDelayEvent(Runnable runnable, long initialDelay, long delay) {
        return scheduler.scheduleWithFixedDelay(() -> wrapAndSubmit(runnable), initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> addFixedDelayEvent(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleWithFixedDelay(() -> wrapAndSubmit(runnable), initialDelay, delay, timeUnit);
    }

    public static void submit(Runnable runnable) {
        executor.submit(runnable);
    }

    private static void wrapAndSubmit(Runnable runnable) {
        submit(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Exception caught while executing scheduled event", e);
                e.printStackTrace();
            }
        });
    }
}
