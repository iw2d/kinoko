package kinoko.provider.item;

public enum ItemOptionType {
    ANY_EQUIP(0),
    WEAPON(10),
    EXCEPT_WEAPON(11),
    ANY_ARMOR(20),
    ACCESSORY(40),
    CAP(51),
    COAT(52),
    PANTS(53),
    GLOVE(54),
    SHOES(55),
    UNK_90(90); // incRewardProp?

    private final int value;

    ItemOptionType(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public static ItemOptionType getByValue(int value) {
        for (ItemOptionType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
