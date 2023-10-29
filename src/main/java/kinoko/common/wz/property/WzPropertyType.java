package kinoko.common.wz.property;

public enum WzPropertyType {
    LIST("Property"),
    CANVAS("Canvas"),
    VECTOR("Shape2D#Vector2D"),
    CONVEX("Shape2D#Convex2D"),
    SOUND("Sound_DX8"),
    UOL("UOL");

    private final String id;

    WzPropertyType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
