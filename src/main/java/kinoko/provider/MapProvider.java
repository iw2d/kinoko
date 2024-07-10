package kinoko.provider;

import kinoko.provider.map.*;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Tuple;
import kinoko.world.GameConstants;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class MapProvider implements WzProvider {
    public static final Path MAP_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Map.wz");
    private static final Map<Integer, MapInfo> mapInfos = new HashMap<>();
    private static final Map<Integer, Integer> mapLinks = new HashMap<>();
    private static final Map<Integer, Integer> areaCodes = new HashMap<>(); // key -> category

    public static void initialize() {
        try (final WzReader reader = WzReader.build(MAP_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            loadMapInfos(wzPackage);
            loadAreaCodes(wzPackage);
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Map.wz", e);
        }
    }

    public static List<MapInfo> getMapInfos() {
        return mapInfos.values().stream().toList();
    }

    public static Optional<MapInfo> getMapInfo(int mapId) {
        return Optional.ofNullable(mapInfos.get(mapId));
    }

    public static Optional<Integer> getMapLink(int mapId) {
        return Optional.ofNullable(mapLinks.get(mapId));
    }

    public static boolean isConnected(int fromFieldId, int toFieldId) {
        // CWvsContext::IsConnected
        if (GameConstants.isEventMap(fromFieldId) || GameConstants.isEventMap(toFieldId)) {
            return false;
        }
        if (fromFieldId / 10000 == 20009 || toFieldId / 10000 == 20009) {
            return false;
        }
        final Integer fromCategory = areaCodes.get(fromFieldId);
        if (fromCategory == null) {
            return false;
        }
        return fromCategory.equals(areaCodes.get(toFieldId));
    }

    private static void loadAreaCodes(WzPackage source) throws ProviderError {
        if (!(source.getDirectory().getDirectories().get("Map") instanceof WzDirectory mapDirectory)) {
            throw new ProviderError("Could not resolve Map.wz/Map");
        }
        for (var areaEntry : mapDirectory.getImages().get("AreaCode.img").getProperty().getItems().entrySet()) {
            final int key = Integer.parseInt(areaEntry.getKey());
            final int category = WzProvider.getInteger(areaEntry.getValue());
            areaCodes.put(key, category);
        }
    }

    private static void loadMapInfos(WzPackage source) throws ProviderError {
        final Map<Integer, Tuple<Integer, WzListProperty>> linkedMaps = new HashMap<>(); // mapId -> link, info
        if (!(source.getDirectory().getDirectories().get("Map") instanceof WzDirectory mapDirectory)) {
            throw new ProviderError("Could not resolve Map.wz/Map");
        }
        for (var dirEntry : mapDirectory.getDirectories().entrySet()) {
            final String directoryName = dirEntry.getKey();
            if (!directoryName.matches("Map[0-9]")) {
                continue;
            }
            for (var mapEntry : dirEntry.getValue().getImages().entrySet()) {
                final String imageName = mapEntry.getKey();
                final int mapId = Integer.parseInt(imageName.replace(".img", ""));
                if (!(mapEntry.getValue().getProperty().get("info") instanceof WzListProperty infoProp)) {
                    throw new ProviderError("Failed to resolve info property");
                }
                if (infoProp.getItems().containsKey("link")) {
                    linkedMaps.put(mapId, Tuple.of(WzProvider.getInteger(infoProp.get("link")), infoProp));
                    continue;
                }
                final boolean clock = mapEntry.getValue().getProperty().getItems().containsKey("clock");
                final MapInfo mapInfo = resolveMapInfo(mapId, mapEntry.getValue(), infoProp, clock);
                mapInfos.put(mapId, mapInfo);
            }
        }
        // Process linked maps
        for (var linkEntry : linkedMaps.entrySet()) {
            final int mapId = linkEntry.getKey();
            final int link = linkEntry.getValue().getLeft();
            final MapInfo linkInfo = mapInfos.get(link);
            if (linkInfo == null) {
                throw new ProviderError("Failed to resolve linked map ID : %d, link : %d", mapId, link);
            }
            mapInfos.put(mapId, MapInfo.from(
                    mapId,
                    linkEntry.getValue().getRight(),
                    linkInfo.getFootholds(),
                    linkInfo.getLifeInfos(),
                    linkInfo.getPortalInfos(),
                    linkInfo.getReactorInfos(),
                    linkInfo.isClock()
            ));
            mapLinks.put(mapId, link);
        }
    }

    private static MapInfo resolveMapInfo(int mapId, WzImage image, WzListProperty infoProp, boolean clock) throws ProviderError {
        final List<Foothold> foothold = resolveFoothold(image.getProperty());
        final List<LifeInfo> life = resolveLife(image.getProperty());
        final List<PortalInfo> portal = resolvePortal(image.getProperty());
        final List<ReactorInfo> reactor = resolveReactor(image.getProperty());
        return MapInfo.from(mapId, infoProp, foothold, life, portal, reactor, clock);
    }

    private static List<Foothold> resolveFoothold(WzListProperty imageProp) throws ProviderError {
        if (!(imageProp.get("foothold") instanceof WzListProperty listProp)) {
            return List.of();
        }
        final List<Foothold> foothold = new ArrayList<>();
        for (var layerEntry : listProp.getItems().entrySet()) {
            final int layerId = Integer.parseInt(layerEntry.getKey());
            if (!(layerEntry.getValue() instanceof WzListProperty groupList)) {
                throw new ProviderError("Failed to resolve foothold property");
            }
            for (var groupEntry : groupList.getItems().entrySet()) {
                final int groupId = Integer.parseInt(groupEntry.getKey());
                if (!(groupEntry.getValue() instanceof WzListProperty footholdList)) {
                    throw new ProviderError("Failed to resolve foothold property");
                }
                for (var footholdEntry : footholdList.getItems().entrySet()) {
                    final int footholdId = Integer.parseInt(footholdEntry.getKey());
                    if (!(footholdEntry.getValue() instanceof WzListProperty footholdProp)) {
                        throw new ProviderError("Failed to resolve foothold property");
                    }
                    foothold.add(Foothold.from(layerId, groupId, footholdId, footholdProp));
                }
            }
        }
        return foothold;
    }

    private static List<LifeInfo> resolveLife(WzListProperty imageProp) throws ProviderError {
        if (!(imageProp.get("life") instanceof WzListProperty listProp)) {
            return List.of();
        }
        final List<LifeInfo> life = new ArrayList<>();
        for (var lifeEntry : listProp.getItems().entrySet()) {
            if (!(lifeEntry.getValue() instanceof WzListProperty lifeProp)) {
                throw new ProviderError("Failed to resolve life property");
            }
            final LifeType lifeType = LifeType.fromString(lifeProp.get("type"));
            life.add(LifeInfo.from(lifeType, lifeProp));
        }
        return life;
    }

    private static List<PortalInfo> resolvePortal(WzListProperty imageProp) throws ProviderError {
        if (!(imageProp.get("portal") instanceof WzListProperty listProp)) {
            return List.of();
        }
        final List<PortalInfo> portal = new ArrayList<>();
        for (var portalEntry : listProp.getItems().entrySet()) {
            final int portalId = Integer.parseInt(portalEntry.getKey());
            if (!(portalEntry.getValue() instanceof WzListProperty portalProp)) {
                throw new ProviderError("Failed to resolve portal property");
            }
            final PortalType portalType = PortalType.getByValue(portalProp.get("pt"));
            if (portalType == null) {
                throw new ProviderError("Failed to resolve portal type");
            }
            portal.add(PortalInfo.from(portalType, portalId, portalProp));
        }
        return portal;
    }

    private static List<ReactorInfo> resolveReactor(WzListProperty imageProp) throws ProviderError {
        if (!(imageProp.get("reactor") instanceof WzListProperty listProp)) {
            return List.of();
        }
        final List<ReactorInfo> reactor = new ArrayList<>();
        for (var reactorEntry : listProp.getItems().entrySet()) {
            if (!(reactorEntry.getValue() instanceof WzListProperty reactorProp)) {
                throw new ProviderError("Failed to resolve reactor property");
            }
            reactor.add(ReactorInfo.from(reactorProp));
        }
        return reactor;
    }
}
