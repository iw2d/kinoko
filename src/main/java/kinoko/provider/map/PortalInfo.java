package kinoko.provider.map;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.world.GameConstants;

public final class PortalInfo {
    public static final PortalInfo EMPTY = new PortalInfo(PortalType.STARTPOINT, 0, "", GameConstants.UNDEFINED_FIELD_ID, "", 0, 0, 0, 0, 0, 0, 0, false, "");
    private final PortalType portalType;
    private final int portalId;
    private final String portalName;
    private final int destinationFieldId;
    private final String destinationPortalName;
    private final int x;
    private final int y;
    private final int delay;
    private final int hRange;
    private final int vRange;
    private final int hImpact;
    private final int vImpact;
    private final boolean onlyOnce;
    private final String script;

    public PortalInfo(PortalType portalType, int portalId, String portalName, int destinationFieldId, String destinationPortalName, int x, int y, int delay, int hRange, int vRange, int hImpact, int vImpact, boolean onlyOnce, String script) {
        this.portalType = portalType;
        this.portalId = portalId;
        this.portalName = portalName;
        this.destinationFieldId = destinationFieldId;
        this.destinationPortalName = destinationPortalName;
        this.x = x;
        this.y = y;
        this.delay = delay;
        this.hRange = hRange;
        this.vRange = vRange;
        this.hImpact = hImpact;
        this.vImpact = vImpact;
        this.onlyOnce = onlyOnce;
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

    public int getHRange() {
        return hRange;
    }

    public int getVRange() {
        return vRange;
    }

    public int getHImpact() {
        return hImpact;
    }

    public int getVImpact() {
        return vImpact;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isOnlyOnce() {
        return onlyOnce;
    }

    public String getScript() {
        return script;
    }

    @Override
    public String toString() {
        return "PortalInfo{" +
                "portalType=" + portalType +
                ", portalId=" + portalId +
                ", portalName='" + portalName + '\'' +
                ", destinationFieldId=" + destinationFieldId +
                ", destinationPortalName='" + destinationPortalName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", delay=" + delay +
                ", hRange=" + hRange +
                ", vRange=" + vRange +
                ", hImpact=" + hImpact +
                ", vImpact=" + vImpact +
                ", onlyOnce=" + onlyOnce +
                ", script='" + script + '\'' +
                '}';
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
                WzProvider.getInteger(portalProp.get("delay"), 0),
                WzProvider.getInteger(portalProp.get("hRange"), 100),
                WzProvider.getInteger(portalProp.get("vRange"), 100),
                WzProvider.getInteger(portalProp.get("horizontalImpact"), 0),
                WzProvider.getInteger(portalProp.get("verticalImpact"), 0),
                WzProvider.getInteger(portalProp.get("onlyOnce"), 0) != 0,
                portalProp.get("script")
        );
    }

}
