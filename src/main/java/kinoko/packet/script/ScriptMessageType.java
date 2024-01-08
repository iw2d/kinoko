package kinoko.packet.script;

public enum ScriptMessageType {
    SAY(0),
    SAY_IMAGE(1),
    ASK_YES_NO(2),
    ASK_TEXT(3),
    ASK_NUMBER(4),
    ASK_MENU(5),
    ASK_QUIZ(6),
    ASK_SPEED_QUIZ(7),
    ASK_AVATAR(8),
    ASK_MEMBER_SHOP_AVATAR(9),
    ASK_PET(10),
    ASK_PET_ALL(11),
    ASK_YES_NO_QUEST(13),
    ASK_BOX_TEXT(14),
    ASK_SLIDE_MENU(15);

    private final byte value;

    ScriptMessageType(int value) {
        this.value = (byte) value;
    }

    public final byte getValue() {
        return value;
    }

    public static ScriptMessageType getByValue(int value) {
        for (ScriptMessageType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
