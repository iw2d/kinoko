package kinoko.script.common;

public enum ScriptMessageType {
    // SM
    SAY(0),
    SAYIMAGE(1),
    ASKYESNO(2),
    ASKTEXT(3),
    ASKNUMBER(4),
    ASKMENU(5),
    ASKQUIZ(6),
    ASKSPEEDQUIZ(7),
    ASKAVATAR(8),
    ASKMEMBERSHOPAVATAR(9),
    ASKPET(10),
    ASKPETALL(11),
    SCRIPT(12),
    ASKACCEPT(13),
    ASKBOXTEXT(14),
    ASKSLIDEMENU(15),
    ASKCENTER(16);

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
