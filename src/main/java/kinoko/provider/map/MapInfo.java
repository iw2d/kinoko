package kinoko.provider.map;

import kinoko.provider.wz.property.WzListProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MapInfo {
    private final int mapId;
    private final boolean town;
    private final boolean swim;
    private final boolean fly;
    private final int returnMap;
    private final int forcedReturn;
    private final int fieldLimit;
    private final float mobRate;
    private final String onFirstUserEnter;
    private final String onUserEnter;
    private final int vrTop;
    private final int vrLeft;
    private final int vrBottom;
    private final int vrRight;
    private final List<Foothold> footholds;
    private final List<LifeInfo> lifeInfos;
    private final List<PortalInfo> portalInfos;
    private final List<ReactorInfo> reactorInfos;

    public MapInfo(int mapId, boolean town, boolean swim, boolean fly, int returnMap, int forcedReturn, int fieldLimit,
                   float mobRate, String onFirstUserEnter, String onUserEnter, int vrTop, int vrLeft, int vrBottom,
                   int vrRight, List<Foothold> footholds, List<LifeInfo> lifeInfos, List<PortalInfo> portalInfos,
                   List<ReactorInfo> reactorInfos) {
        this.mapId = mapId;
        this.town = town;
        this.swim = swim;
        this.fly = fly;
        this.returnMap = returnMap;
        this.forcedReturn = forcedReturn;
        this.fieldLimit = fieldLimit;
        this.mobRate = mobRate;
        this.onFirstUserEnter = onFirstUserEnter;
        this.onUserEnter = onUserEnter;
        this.vrTop = vrTop;
        this.vrLeft = vrLeft;
        this.vrBottom = vrBottom;
        this.vrRight = vrRight;
        this.footholds = footholds;
        this.lifeInfos = lifeInfos;
        this.portalInfos = portalInfos;
        this.reactorInfos = reactorInfos;
    }

    public int getMapId() {
        return mapId;
    }

    public boolean isTown() {
        return town;
    }

    public boolean isSwim() {
        return swim;
    }

    public boolean isFly() {
        return fly;
    }

    public int getReturnMap() {
        return returnMap;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public int getFieldLimit() {
        return fieldLimit;
    }

    public float getMobRate() {
        return mobRate;
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public int getVrTop() {
        return vrTop;
    }

    public int getVrLeft() {
        return vrLeft;
    }

    public int getVrBottom() {
        return vrBottom;
    }

    public int getVrRight() {
        return vrRight;
    }

    public List<Foothold> getFootholds() {
        return footholds;
    }

    public List<LifeInfo> getLifeInfos() {
        return lifeInfos;
    }

    public List<PortalInfo> getPortalInfos() {
        return portalInfos;
    }

    public List<ReactorInfo> getReactorInfos() {
        return reactorInfos;
    }

    public Optional<PortalInfo> getPortalById(int portalId) {
        return portalInfos.stream()
                .filter(pi -> pi.getPortalId() == portalId)
                .findFirst();
    }

    public Optional<PortalInfo> getPortalByName(String name) {
        return portalInfos.stream()
                .filter(pi -> pi.getPortalName().equals(name))
                .findFirst();
    }

    @Override
    public String toString() {
        return "MapInfo[" +
                "id=" + mapId + ", " +
                "town=" + town + ", " +
                "swim=" + swim + ", " +
                "fly=" + fly + ", " +
                "returnMap=" + returnMap + ", " +
                "forcedReturn=" + forcedReturn + ", " +
                "fieldLimit=" + fieldLimit + ", " +
                "mobRate=" + mobRate + ", " +
                "onFirstUserEnter=" + onFirstUserEnter + ", " +
                "onUserEnter=" + onUserEnter + ", " +
                "VRTop=" + vrTop + ", " +
                "VRLeft=" + vrLeft + ", " +
                "VRBottom=" + vrBottom + ", " +
                "VRRight=" + vrRight + ", " +
                "foothold=" + footholds + ", " +
                "life=" + lifeInfos + ", " +
                "portal=" + portalInfos + ", " +
                "reactor=" + reactorInfos + ']';
    }

    public static MapInfo from(int mapId, WzListProperty infoProp, List<Foothold> foothold, List<LifeInfo> life, List<PortalInfo> portal, List<ReactorInfo> reactor) {
        return new MapInfo(
                mapId,
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
                infoProp.getOrDefault("VRRight", 0),
                Collections.unmodifiableList(foothold),
                Collections.unmodifiableList(life),
                Collections.unmodifiableList(portal),
                Collections.unmodifiableList(reactor)
        );
    }

}
