package kinoko.provider.map;

import kinoko.provider.MapProvider;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.util.Crc32;
import kinoko.util.Rect;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class MapInfo {
    private final int mapId;
    private final boolean town;
    private final boolean swim;
    private final boolean fly;
    private final boolean shop;
    private final boolean clock;
    private final int phase;
    private final int returnMap;
    private final int forcedReturn;
    private final Set<FieldOption> fieldOptions;
    private final FieldType fieldType;
    private final float mobRate;
    private final String onFirstUserEnter;
    private final String onUserEnter;
    private final List<Rect> areas;
    private final List<Foothold> footholds;
    private final List<LadderRope> ladderRope;
    private final List<LifeInfo> lifeInfos;
    private final List<PortalInfo> portalInfos;
    private final List<ReactorInfo> reactorInfos;

    private final FootholdNode footholdRoot;
    private final int fieldCrc;

    public MapInfo(int mapId, boolean town, boolean swim, boolean fly, boolean shop, boolean clock, int phase, int returnMap, int forcedReturn, Set<FieldOption> fieldOptions, FieldType fieldType, float mobRate, String onFirstUserEnter, String onUserEnter, List<Rect> areas, List<Foothold> footholds, List<LadderRope> ladderRope, List<LifeInfo> lifeInfos, List<PortalInfo> portalInfos, List<ReactorInfo> reactorInfos) {
        this.mapId = mapId;
        this.town = town;
        this.swim = swim;
        this.fly = fly;
        this.shop = shop;
        this.clock = clock;
        this.phase = phase;
        this.returnMap = returnMap;
        this.forcedReturn = forcedReturn;
        this.fieldOptions = fieldOptions;
        this.fieldType = fieldType;
        this.mobRate = mobRate;
        this.onFirstUserEnter = onFirstUserEnter;
        this.onUserEnter = onUserEnter;
        this.areas = areas;
        this.footholds = footholds;
        this.ladderRope = ladderRope;
        this.lifeInfos = lifeInfos;
        this.portalInfos = portalInfos;
        this.reactorInfos = reactorInfos;

        // Initialize Footholds BST
        this.footholdRoot = new FootholdNode();
        for (Foothold fh : footholds) {
            this.footholdRoot.insert(fh);
        }

        // Compute Field CRC
        this.fieldCrc = Crc32.computeCrcField(MapProvider.getConstantCrc(), this);
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

    public boolean isShop() {
        return shop;
    }

    public boolean isClock() {
        return clock;
    }

    public int getPhase() {
        return phase;
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

    public List<Rect> getAreas() {
        return areas;
    }

    public List<Foothold> getFootholds() {
        return footholds;
    }

    public List<LadderRope> getLadderRopes() {
        return ladderRope;
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

    public int getFieldCrc() {
        return fieldCrc;
    }

    @Override
    public String toString() {
        return "MapInfo{" +
                "mapId=" + mapId +
                ", town=" + town +
                ", swim=" + swim +
                ", fly=" + fly +
                ", shop=" + shop +
                ", clock=" + clock +
                ", phase=" + phase +
                ", returnMap=" + returnMap +
                ", forcedReturn=" + forcedReturn +
                ", fieldOptions=" + fieldOptions +
                ", fieldType=" + fieldType +
                ", mobRate=" + mobRate +
                ", onFirstUserEnter='" + onFirstUserEnter + '\'' +
                ", onUserEnter='" + onUserEnter + '\'' +
                ", areas=" + areas +
                ", footholds=" + footholds +
                ", ladderRope=" + ladderRope +
                ", lifeInfos=" + lifeInfos +
                ", portalInfos=" + portalInfos +
                ", reactorInfos=" + reactorInfos +
                ", footholdRoot=" + footholdRoot +
                ", fieldCrc=" + fieldCrc +
                '}';
    }

    public static MapInfo from(int mapId, WzListProperty infoProp, List<Rect> area, List<Foothold> foothold, List<LadderRope> ladderRope, List<LifeInfo> life, List<PortalInfo> portal, List<ReactorInfo> reactor, boolean clock) {
        return new MapInfo(
                mapId,
                infoProp.getOrDefault("town", 0) != 0,
                infoProp.getOrDefault("swim", 0) != 0,
                infoProp.getOrDefault("fly", 0) != 0,
                infoProp.getOrDefault("personalShop", 0) != 0,
                clock,
                infoProp.getOrDefault("phase", 0),
                infoProp.get("returnMap"),
                infoProp.get("forcedReturn"),
                FieldOption.getByLimit(infoProp.getOrDefault("fieldLimit", 0)),
                FieldType.getByValue(WzProvider.getInteger(infoProp.getOrDefault("fieldType", 0))),
                infoProp.get("mobRate"),
                infoProp.get("onFirstUserEnter"),
                infoProp.get("onUserEnter"),
                area,
                foothold,
                ladderRope,
                life,
                portal,
                reactor
        );
    }

}
