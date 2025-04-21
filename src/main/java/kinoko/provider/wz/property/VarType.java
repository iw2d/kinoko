package kinoko.provider.wz.property;

import java.util.HashMap;
import java.util.Map;

public enum VarType {
    EMPTY       (0x0000),
    NULL        (0x0001),
    I2          (0x0002),
    I4          (0x0003),
    R4          (0x0004),
    R8          (0x0005),
    CY          (0x0006),
    DATE        (0x0007),
    BSTR        (0x0008),
    DISPATCH    (0x0009),
    ERROR       (0x000A),
    BOOL        (0x000B),
    VARIANT     (0x000C),
    UNKNOWN     (0x000D),
    DECIMAL     (0x000E),
    I1          (0x0010),
    UI1         (0x0011),
    UI2         (0x0012),
    UI4         (0x0013),
    I8          (0x0014),
    UI8         (0x0015),
    INT         (0x0016),
    UINT        (0x0017),
    VOID        (0x0018),
    HRESULT     (0x0019),
    PTR         (0x001A),
    SAFEARRAY   (0x001B),
    CARRAY      (0x001C),
    USERDEFINED (0x001D),
    LPSTR       (0x001E),
    LPWSTR      (0x001F),
    RECORD      (0x0024),
    INT_PTR     (0x0025),
    UINT_PTR    (0x0026),
    ARRAY       (0x2000),
    BYREF       (0x4000);

    private final int code;
    private static final Map<Integer, VarType> LOOKUP = new HashMap<>();

    static {
        for (VarType v : VarType.values()) {
            LOOKUP.put(v.code, v);
        }
    }

    VarType(int code) {
        this.code = code;
    }

    /** Returns the integer (hex) code of this enum constant. */
    public int getCode() {
        return code;
    }

    /**
     * Converts an integer code back into the corresponding VARENUM.
     * @throws IllegalArgumentException if the code is not valid.
     */
    public static VarType fromCode(int code) {
        VarType v = LOOKUP.get(code);
        if (v == null) {
            throw new IllegalArgumentException("Unknown VARENUM code: 0x"
                    + Integer.toHexString(code).toUpperCase());
        }
        return v;
    }
}
