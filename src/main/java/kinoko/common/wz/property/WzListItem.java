package kinoko.common.wz.property;

public final class WzListItem<T> {
    private final T value;

    public WzListItem(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
