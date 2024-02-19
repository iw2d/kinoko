package kinoko.util;

public interface Lockable<Self extends Lockable<Self>> {
    void lock();

    void unlock();

    @SuppressWarnings("unchecked")
    default Locked<Self> acquire() {
        lock();
        return new Locked<>((Self) this);
    }
}
