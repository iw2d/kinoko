package kinoko.packet.world.message;

public enum DropPickUpMessageType {
    CANNOT_ACQUIRE_ANY_ITEMS(-3),
    UNAVAILABLE_FOR_PICK_UP(-2),
    CANNOT_GET_ANYMORE_ITEMS(-1),
    ITEM_BUNDLE(0),
    MONEY(1),
    ITEM_SINGLE(2);

    private final byte value;

    DropPickUpMessageType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }
}
