package kinoko.server.node;

import kinoko.server.event.Event;
import kinoko.server.field.InstanceFieldStorage;
import kinoko.world.field.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public final class ServerExecutor {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final List<ExecutorService> executorArray;

    static {
        final List<ExecutorService> executors = new ArrayList<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executors.add(Executors.newSingleThreadExecutor());
        }
        executorArray = Collections.unmodifiableList(executors);
    }

    public static void initialize() {
        // Run static initialization block
    }

    public static void shutdown() {
        executorArray.forEach(ExecutorService::shutdown);
    }

    public static void submit(Client client, Runnable runnable) {
        if (client.getUser() == null || client.getUser().getField() == null) {
            executorArray.get(client.getClientId() % executorArray.size()).submit(runnable);
        } else {
            submit(client.getUser().getField(), runnable);
        }
    }

    public static void submit(Field field, Runnable runnable) {
        if (field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage) {
            executorArray.get(instanceFieldStorage.getInstance().getInstanceId() % executorArray.size()).submit(runnable);
        } else {
            executorArray.get(Byte.toUnsignedInt(field.getFieldKey()) % executorArray.size()).submit(runnable);
        }
    }

    public static ScheduledFuture<?> schedule(Field field, Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submit(field, runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Field field, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleWithFixedDelay(() -> submit(field, runnable), initialDelay, delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Event event, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        final ExecutorService eventExecutor = executorArray.get(event.getType().ordinal() % executorArray.size());
        return scheduler.scheduleWithFixedDelay(() -> eventExecutor.submit(runnable), initialDelay, delay, timeUnit);
    }
}
