package kinoko.world.skill.maker;

public enum MakerResult {
    // ITEM_MAKER_RESULT
    SUCCESS(0),
    DESTROYED(1),
    // ITEM_MAKER_ERR
    UNKNOWN(2),
    EMPTYSLOT(3),
    EMPTYSLOT_EQUIP(4),
    EMPTYSLOT_CONSUME(5),
    EMPTYSLOT_INSTALL(6),
    EMPTYSLOT_ETC(7);

    private final int value;

    MakerResult(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }
}
