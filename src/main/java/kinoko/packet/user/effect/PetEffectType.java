package kinoko.packet.user.effect;

public enum PetEffectType {
    // PetEffect
    LEVEL_UP(0),
    TELEPORT(1),
    HANG_ON_BACK(2),
    EVOLUTION(3);

    private final int value;

    PetEffectType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
