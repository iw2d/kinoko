package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

import java.util.Collections;
import java.util.List;

public record MapInfo(
        int id,
        List<Foothold> foothold,
        List<LifeInfo> life,
        List<PortalInfo> portal,
        List<ReactorInfo> reactor,

        // info
        String bgm,
        int version,
        boolean town,
        boolean swim,
        boolean fly,
        int returnMap,
        int forcedReturn,
        int fieldLimit,
        float mobRate,
        String onFirstUserEnter,
        String onUserEnter,
        int VRTop,
        int VRLeft,
        int VRBottom,
        int VRRight
) {
    public static MapInfo from(int mapId, List<Foothold> foothold, List<LifeInfo> life, List<PortalInfo> portal, List<ReactorInfo> reactor, WzListProperty infoProp) {
        return new MapInfo(
                mapId,
                Collections.unmodifiableList(foothold),
                Collections.unmodifiableList(life),
                Collections.unmodifiableList(portal),
                Collections.unmodifiableList(reactor),
                infoProp.get("bgm"),
                infoProp.get("version"),
                infoProp.getOrDefault("town", 0) != 0,
                infoProp.getOrDefault("swim", 0) != 0,
                infoProp.getOrDefault("fly", 0) != 0,
                infoProp.get("returnMap"),
                infoProp.get("forcedReturn"),
                infoProp.getOrDefault("fieldLimit", 0),
                infoProp.get("mobRate"),
                infoProp.get("onFirstUserEnter"),
                infoProp.get("onUserEnter"),
                infoProp.getOrDefault("VRTop", 0),
                infoProp.getOrDefault("VRLeft", 0),
                infoProp.getOrDefault("VRBottom", 0),
                infoProp.getOrDefault("VRRight", 0)
        );
    }
}
