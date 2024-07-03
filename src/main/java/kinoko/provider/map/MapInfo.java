package kinoko.provider.map;

import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Rect;

import java.util.*;

public final class MapInfo {
    private final int mapId;
    private final boolean town;
    private final boolean swim;
    private final boolean fly;
    private final boolean clock;
    private final int timeLimit;
    private final int returnMap;
    private final int forcedReturn;
    private final Set<FieldOption> fieldOptions;
    private final FieldType fieldType;
    private final float mobRate;
    private final String onFirstUserEnter;
    private final String onUserEnter;
    private final int boundLeft;
    private final int boundRight;
    private final List<Foothold> footholds;
    private final List<LifeInfo> lifeInfos;
    private final List<PortalInfo> portalInfos;
    private final List<ReactorInfo> reactorInfos;
    private final FootholdNode footholdRoot = new FootholdNode();

    public MapInfo(int mapId, boolean town, boolean swim, boolean fly, boolean clock, int timeLimit, int returnMap, int forcedReturn, Set<FieldOption> fieldOptions, FieldType fieldType, float mobRate, String onFirstUserEnter, String onUserEnter, int boundLeft, int boundRight, List<Foothold> footholds, List<LifeInfo> lifeInfos, List<PortalInfo> portalInfos, List<ReactorInfo> reactorInfos) {
        this.mapId = mapId;
        this.town = town;
        this.swim = swim;
        this.fly = fly;
        this.clock = clock;
        this.timeLimit = timeLimit;
        this.returnMap = returnMap;
        this.forcedReturn = forcedReturn;
        this.fieldOptions = fieldOptions;
        this.fieldType = fieldType;
        this.mobRate = mobRate;
        this.onFirstUserEnter = onFirstUserEnter;
        this.onUserEnter = onUserEnter;
        this.boundLeft = boundLeft;
        this.boundRight = boundRight;
        this.footholds = footholds;
        this.lifeInfos = lifeInfos;
        this.portalInfos = portalInfos;
        this.reactorInfos = reactorInfos;

        // Initialize Footholds BST
        for (Foothold fh : footholds) {
            footholdRoot.insert(fh);
        }
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

    public boolean isClock() {
        return clock;
    }


    public int getTimeLimit() {
        return timeLimit;
    }

    public int getReturnMap() {
        return returnMap;
    }

    public int getForcedReturn() {
        return forcedReturn;
    }

    public Set<FieldOption> getFieldOptions() {
        return fieldOptions;
    }

    public boolean hasFieldOption(FieldOption fieldOption) {
        return fieldOptions.contains(fieldOption);
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public float getMobRate() {
        return mobRate;
    }

    public boolean hasOnFirstUserEnter() {
        return onFirstUserEnter != null && !onFirstUserEnter.isEmpty();
    }

    public String getOnFirstUserEnter() {
        return onFirstUserEnter;
    }

    public boolean hasOnUserEnter() {
        return onUserEnter != null && !onUserEnter.isEmpty();
    }

    public String getOnUserEnter() {
        return onUserEnter;
    }

    public int getBoundLeft() {
        return boundLeft;
    }

    public int getBoundRight() {
        return boundRight;
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

    public Optional<PortalInfo> getPortalByName(String portalName) {
        return portalInfos.stream()
                .filter(pi -> pi.getPortalName().equals(portalName))
                .findFirst();
    }

    public List<PortalInfo> getTownPortalPoints() {
        return portalInfos.stream()
                .filter(pi -> pi.getPortalType().equals(PortalType.TOWNPORTAL_POINT))
                .sorted(Comparator.comparingInt(PortalInfo::getPortalId))
                .toList();
    }

    public Optional<Foothold> getFootholdById(int footholdId) {
        return footholds.stream()
                .filter(fh -> fh.getFootholdId() == footholdId)
                .findFirst();
    }

    public Optional<Foothold> getFootholdBelow(int x, int y) {
        final FootholdNode.SearchResult result = new FootholdNode.SearchResult();
        footholdRoot.searchDown((match) -> {
            if (match.isWall()) {
                return;
            }
            if (x < match.getX1() || x > match.getX2()) {
                return;
            }
            final int my = match.getYFromX(x);
            if (my < y) {
                return;
            }
            result.setIf(res -> res.getYFromX(x) >= my, match);
        }, x, y);
        return Optional.ofNullable(result.get());
    }

    public Rect getRootBounds() {
        return footholdRoot.getRootBounds();
    }

    @Override
    public String toString() {
        return "MapInfo{" +
                "mapId=" + mapId +
                ", town=" + town +
                ", swim=" + swim +
                ", fly=" + fly +
                ", clock=" + clock +
                ", timeLimit=" + timeLimit +
                ", returnMap=" + returnMap +
                ", forcedReturn=" + forcedReturn +
                ", fieldOptions=" + fieldOptions +
                ", fieldType=" + fieldType +
                ", mobRate=" + mobRate +
                ", onFirstUserEnter='" + onFirstUserEnter + '\'' +
                ", onUserEnter='" + onUserEnter + '\'' +
                ", boundLeft=" + boundLeft +
                ", boundRight=" + boundRight +
                ", footholds=" + footholds +
                ", lifeInfos=" + lifeInfos +
                ", portalInfos=" + portalInfos +
                ", reactorInfos=" + reactorInfos +
                ", footholdRoot=" + footholdRoot +
                '}';
    }

    public static MapInfo from(int mapId, WzListProperty infoProp, List<Foothold> foothold, List<LifeInfo> life, List<PortalInfo> portal, List<ReactorInfo> reactor, boolean clock) {
        // Compute bounds
        int boundLeft = Integer.MAX_VALUE;
        int boundRight = Integer.MIN_VALUE;
        for (Foothold fh : foothold) {
            if (fh.getX1() < boundLeft) {
                boundLeft = fh.getX1();
            }
            if (fh.getX2() > boundRight) {
                boundRight = fh.getX2();
            }
        }
        return new MapInfo(
                mapId,
                infoProp.getOrDefault("town", 0) != 0,
                infoProp.getOrDefault("swim", 0) != 0,
                infoProp.getOrDefault("fly", 0) != 0,
                clock,
                infoProp.getOrDefault("timeLimit", 0),
                infoProp.get("returnMap"),
                infoProp.get("forcedReturn"),
                FieldOption.getByLimit(infoProp.getOrDefault("fieldLimit", 0)),
                FieldType.getByValue(WzProvider.getInteger(infoProp.getOrDefault("fieldType", 0))),
                infoProp.get("mobRate"),
                infoProp.get("onFirstUserEnter"),
                infoProp.get("onUserEnter"),
                boundLeft,
                boundRight,
                Collections.unmodifiableList(foothold),
                Collections.unmodifiableList(life),
                Collections.unmodifiableList(portal),
                Collections.unmodifiableList(reactor)
        );
    }

}
