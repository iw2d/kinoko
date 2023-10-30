package kinoko.common.wz.property;

import java.util.Map;

public enum WzPropertyType {
    LIST("Property"),
    CANVAS("Canvas"),
    VECTOR("Shape2D#Vector2D"),
    CONVEX("Shape2D#Convex2D"),
    SOUND("Sound_DX8"),
    UOL("UOL");

    private static final Map<String, WzPropertyType> types = Map.of(
            LIST.getId(), LIST,
            CANVAS.getId(), CANVAS,
            VECTOR.getId(), VECTOR,
            CONVEX.getId(), CONVEX,
            SOUND.getId(), SOUND,
            UOL.getId(), UOL
    );
    private final String id;

    WzPropertyType(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

    public static WzPropertyType getById(String name) {
        return types.get(name);
    }
}
