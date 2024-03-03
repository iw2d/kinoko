package kinoko.packet.user;

public enum PetActionType {
    // PET_ACTTYPE
    INTERACT(0),
    FEED(1),
    CHAT(2),
    RANDOM(3);

    private final int value;

    PetActionType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
