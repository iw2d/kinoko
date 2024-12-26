package kinoko.world.skill.maker;

public enum RecipeClass {
    // RECIPE_CLASS
    START(0),
    NORMAL(1),
    HIDDEN(2),
    MONSTER_CRYSTAL(3),
    EQUIP_DISASSEMBLE(4);

    private final int value;

    RecipeClass(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static RecipeClass getByValue(int value) {
        for (RecipeClass type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
