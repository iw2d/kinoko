package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

public record PortalInfo(PortalType pt, int id, String pn, int tm, String tn, int x, int y, String script) {
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
