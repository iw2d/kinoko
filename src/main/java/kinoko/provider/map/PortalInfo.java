package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.GameConstants;

public final class PortalInfo {
    public static final PortalInfo EMPTY = new PortalInfo(PortalType.START_POINT, 0, "", GameConstants.UNDEFINED_FIELD_ID, "", 0, 0, "");
    private final PortalType portalType;
    private final int portalId;
    private final String portalName;
    private final int destinationFieldId;
    private final String destinationPortalName;
    private final int x;
    private final int y;
    private final String script;

    public PortalInfo(PortalType portalType, int portalId, String portalName, int destinationFieldId, String destinationPortalName, int x, int y, String script) {
        this.portalType = portalType;
        this.portalId = portalId;
        this.portalName = portalName;
        this.destinationFieldId = destinationFieldId;
        this.destinationPortalName = destinationPortalName;
        this.x = x;
        this.y = y;
        this.script = script;
    }

    public PortalType getPortalType() {
        return portalType;
    }

    public int getPortalId() {
        return portalId;
    }

    public String getPortalName() {
        return portalName;
    }

    public boolean hasDestinationField() {
        return destinationFieldId != GameConstants.UNDEFINED_FIELD_ID;
    }

    public int getDestinationFieldId() {
        return destinationFieldId;
    }

    public String getDestinationPortalName() {
        return destinationPortalName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "PortalInfo[" +
                "pt=" + portalType + ", " +
                "id=" + portalId + ", " +
                "pn=" + portalName + ", " +
                "tm=" + destinationFieldId + ", " +
                "tn=" + destinationPortalName + ", " +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "script=" + script + ']';
    }

    public static PortalInfo from(PortalType portalType, int portalId, WzListProperty portalProp) {
        return new PortalInfo(
                portalType,
                portalId,
                portalProp.get("pn"),
                portalProp.get("tm"),
                portalProp.get("tn"),
                portalProp.get("x"),
                portalProp.get("y"),
                portalProp.get("script")
        );
    }

}
