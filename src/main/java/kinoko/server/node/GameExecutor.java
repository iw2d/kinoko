package kinoko.server.node;

import kinoko.util.Lockable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GameExecutor implements Lockable<GameExecutor> {
    private static final Logger log = LogManager.getLogger(GameExecutor.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Lock lock = new ReentrantLock();

    public CompletableFuture<Void> submit(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try (var locked = acquire()) {
                runnable.run();
            } catch (Exception e) {
                log.error("Exception caught during execution : {}", e, e);
                e.printStackTrace();
            }
        }, executor);
    }

    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
