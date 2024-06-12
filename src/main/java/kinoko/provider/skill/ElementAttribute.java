package kinoko.provider.skill;

import kinoko.provider.ProviderError;

public enum ElementAttribute {
    // ELEMENT
    PHYSICAL(0),
    ICE(1),
    FIRE(2),
    LIGHT(3),
    POISON(4),
    HOLY(5),
    DARK(6),
    UNDEAD(7),
    COUNT(8);

    private final int value;

    ElementAttribute(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static ElementAttribute getByValue(char value) throws ProviderError {
        // get_element_attribute
        return switch (value) {
            case 'P', 'p' -> PHYSICAL;
            case 'D', 'd' -> DARK;
            case 'F', 'f' -> FIRE;
            case 'H', 'h' -> HOLY;
            case 'I', 'i' -> ICE;
            case 'L', 'l' -> LIGHT;
            case 'S', 's' -> POISON;
            case 'U', 'u' -> UNDEAD;
            default -> throw new ProviderError("Unknown element attribute value %c", value);
        };
    }
}
