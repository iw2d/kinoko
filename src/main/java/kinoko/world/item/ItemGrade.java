package kinoko.world.item;

public enum ItemGrade {
    // GRADE
    NORMAL(0),
    RARE(1),
    EPIC(2),
    UNIQUE(3),

    RELEASED(4);

    private final int value;

    ItemGrade(int value) {
        this.value = value;
    }

    public final int getValue() {
        return this.value;
    }

    public final ItemGrade getLowerGrade() {
        return switch (this) {
            case EPIC -> RARE;
            case UNIQUE -> EPIC;
            default -> NORMAL;
        };
    }
}
