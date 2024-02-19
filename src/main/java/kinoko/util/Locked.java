package kinoko.util;

public final class Locked<T extends Lockable<T>> implements AutoCloseable {
    private final T lockable;

    Locked(T lockable) {
        this.lockable = lockable;
    }

    public T get() {
        return this.lockable;
    }

    @Override
    public void close() {
        this.lockable.unlock();
    }
}
