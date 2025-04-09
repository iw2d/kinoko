package kinoko.server.node;

import kinoko.server.field.InstanceFieldStorage;
import kinoko.world.field.Field;
import kinoko.world.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public final class ServerExecutor {
    private static final Logger log = LogManager.getLogger(ServerExecutor.class);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final List<GameExecutor> gameExecutors;
    private static final ExecutorService serviceExecutor;

    static {
        final int executorCount = Runtime.getRuntime().availableProcessors();
        final List<GameExecutor> executors = new ArrayList<>();
        for (int i = 0; i < executorCount; i++) {
            executors.add(new GameExecutor());
        }
        gameExecutors = Collections.unmodifiableList(executors);
        serviceExecutor = Executors.newFixedThreadPool(executorCount);
    }

    public static void initialize() {
        // Run static initialization block
    }

    public static void shutdown() {
        gameExecutors.forEach(GameExecutor::shutdown);
        serviceExecutor.shutdown();
    }


    // GAME EXECUTOR METHODS -------------------------------------------------------------------------------------------

    public static void submit(Client client, Runnable runnable) {
        if (client.getUser() == null) {
            submitService(runnable);
        } else {
            submit(client.getUser(), runnable);
        }
    }

    public static void submit(User user, Runnable runnable) {
        if (user.getField() == null) {
            submitService(runnable);
        } else {
            submit(user.getField(), runnable);
        }
    }

    public static void submit(Field field, Runnable runnable) {
        getExecutor(field).submit(runnable);
    }

    public static ScheduledFuture<?> schedule(User user, Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submit(user, runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> schedule(Field field, Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submit(field, runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Field field, Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(() -> submit(field, runnable), initialDelay, delay, timeUnit);
    }


    // SERVICE EXECUTOR METHODS ----------------------------------------------------------------------------------------

    public static void submitService(Runnable runnable) {
        serviceExecutor.submit(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Exception caught during service execution : {}", e, e);
                e.printStackTrace();
            }
        });
    }

    public static ScheduledFuture<?> scheduleService(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduler.schedule(() -> submitService(runnable), delay, timeUnit);
    }

    public static ScheduledFuture<?> scheduleServiceAtFixedRate(Runnable runnable, long initialDelay, long delay, TimeUnit timeUnit) {
        return scheduler.scheduleAtFixedRate(() -> submitService(runnable), initialDelay, delay, timeUnit);
    }


    // HELPER METHODS --------------------------------------------------------------------------------------------------

    public static GameExecutor getExecutor(Field field) {
        if (field.getFieldStorage() instanceof InstanceFieldStorage instanceFieldStorage) {
            return gameExecutors.get(instanceFieldStorage.getInstance().getInstanceId() % gameExecutors.size());
        } else {
            return gameExecutors.get(field.getExecutorIndex() % gameExecutors.size());
        }
    }

    public static void lockExecutor(Field field) {
        getExecutor(field).lock();
    }

    public static void unlockExecutor(Field field) {
        getExecutor(field).unlock();
    }
}
