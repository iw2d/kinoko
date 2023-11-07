package kinoko.provider.map;

public enum PortalType {
    START_POINT(0),
    PORTAL_INVISIBLE(1),
    PORTAL_VISIBLE(2),
    PORTAL_COLLISION(3),
    PORTAL_CHANGEABLE(4),
    PORTAL_CHANGEABLE_INVISIBLE(5),
    TOWN_PORTAL_POINT(6),
    PORTAL_SCRIPT(7),
    PORTAL_SCRIPT_INVISIBLE(8),
    PORTAL_COLLISION_SCRIPT(9),
    PORTAL_HIDDEN(10),
    PORTAL_SCRIPT_HIDDEN(11),
    PORTAL_COLLISION_JUMP(12),
    PORTAL_COLLISION_CUSTOM(13),
    PORTAL_COLLISION_INVISIBLE_CHANGEABLE(14),
    PORTAL_UNK_15(15);

    private final int type;

    PortalType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static PortalType fromInt(int type) {
        for (PortalType pt : PortalType.values()) {
            if (pt.getType() == type) {
                return pt;
            }
        }
        return null;
    }
}
