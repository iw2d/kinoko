package kinoko.world.user.effect;

public enum PetEffectType {
    // PetEffect
    LevelUp(0),
    Teleport(1),
    HangOnBack(2),
    Evolution(3);

    private final int value;

    PetEffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
