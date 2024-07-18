package kinoko.server.node;

import kinoko.server.field.InstanceFieldStorage;
import kinoko.world.field.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public final class ServerExecutor {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final List<ExecutorService> gameExecutors;
    private static final ExecutorService serviceExecutor;

    static {
        final List<ExecutorService> executors = new ArrayList<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executors.add(Executors.newSingleThreadExecutor());
        }
        gameExecutors = Collections.unmodifiableList(executors);
        serviceExecutor = Executors.newFixedThreadPool(gameExecutors.size());
    }

    public static void initialize() {
        // Run static initialization block
    }

    public static void shutdown() {
        gameExecutors.forEach(ExecutorService::shutdown);
        serviceExecutor.shutdown();
    }

    public static void submit(Client client, Runnable runnable) {
        if (client.getUser() == null || client.getUser().getField() == null) {
            serviceExecutor.submit(runnable);
        } else {
            submit(client.getUser().getField(), runnable);
        }
    }

    public static void submit(Field field, Runnable runnable) {
        if (field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage) {
            gameExecutors.get(instanceFieldStorage.getInstance().getInstanceId() % gameExecutors.size()).submit(runnable);
        } else {
            gameExecutors.get(Byte.toUnsignedInt(field.getFieldKey()) % gameExecutors.size()).submit(runnable);
        }
    }

    public static void submitService(Runnable runnable) {
        serviceExecutor.submit(runnable);
    }

    public static ScheduledFuture<?> schedule(Field field, Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submit(field, runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleService(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submitService(runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Field field, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleWithFixedDelay(() -> submit(field, runnable), initialDelay, delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleServiceWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleWithFixedDelay(() -> submitService(runnable), initialDelay, delay, timeUnit);
    }
}
