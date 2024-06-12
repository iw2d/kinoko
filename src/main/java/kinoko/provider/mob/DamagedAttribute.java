package kinoko.provider.mob;

import kinoko.provider.ProviderError;

public enum DamagedAttribute {
    // ELEMENT_ATTR
    NONE(0),
    DAMAGE0(1),
    DAMAGE50(2),
    DAMAGE150(3);

    private final int value;

    DamagedAttribute(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static DamagedAttribute getByValue(char value) throws ProviderError {
        // get_element_attribute
        return switch (value) {
            case '1' -> DAMAGE0;
            case '2' -> DAMAGE50;
            case '3' -> DAMAGE150;
            default -> throw new ProviderError("Unknown damaged element attribute value %c", value);
        };
    }
}
