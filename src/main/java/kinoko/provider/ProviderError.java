package kinoko.provider;

public final class ProviderError extends Error {
    public ProviderError(String message) {
        super(message);
    }

    public ProviderError(String format, Object... args) {
        super(String.format(format, args));
    }
}
