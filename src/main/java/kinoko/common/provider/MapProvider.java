package kinoko.common.provider;

import kinoko.common.map.*;
import kinoko.common.wz.*;
import kinoko.common.wz.property.WzListProperty;
import kinoko.common.wz.property.WzProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapProvider {
    public static List<Foothold> resolveFoothold(WzProperty property) throws ProviderError {
        if (property == null) {
            return List.of();
        }
        if (!(property instanceof WzListProperty layerList)) {
            throw new ProviderError("Failed to resolve foothold property");
        }
        List<Foothold> foothold = new ArrayList<>();
        for (var layerEntry : layerList.getItems().entrySet()) {
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
                    if (!(footholdEntry.getValue() instanceof WzListProperty footholdProperty)) {
                        throw new ProviderError("Failed to resolve foothold property");
                    }
                    final Map<String, Object> propItems = footholdProperty.getItems();
                    foothold.add(new Foothold(
                            layerId,
                            groupId,
                            footholdId,
                            (int) propItems.getOrDefault("x1", 0),
                            (int) propItems.getOrDefault("y1", 0),
                            (int) propItems.getOrDefault("x2", 0),
                            (int) propItems.getOrDefault("y2", 0)
                    ));
                }
            }
        }
        return foothold;
    }

    public static List<LifeInfo> resolveLife(WzProperty property) throws ProviderError {
        if (property == null) {
            return List.of();
        }
        if (!(property instanceof WzListProperty layerList)) {
            throw new ProviderError("Failed to resolve life property");
        }
        List<LifeInfo> life = new ArrayList<>();
        for (var lifeEntry : layerList.getItems().entrySet()) {
            if (!(lifeEntry.getValue() instanceof WzListProperty lifeProperty)) {
                throw new ProviderError("Failed to resolve life property");
            }
            final Map<String, Object> propItems = lifeProperty.getItems();
            final LifeType lifeType = LifeType.fromString((String) propItems.get("type"));
            if (lifeType == null) {
                throw new ProviderError("Unknown life type : %s", propItems.get("type"));
            }
            life.add(new LifeInfo(
                    lifeType,
                    Integer.parseInt((String) propItems.get("id")),
                    (int) propItems.get("x"),
                    (int) propItems.get("y"),
                    (int) propItems.get("rx0"),
                    (int) propItems.get("rx1"),
                    (int) propItems.get("cy"),
                    (int) propItems.get("fh"),
                    ((int) propItems.getOrDefault("f", 0) != 0),
                    ((int) propItems.getOrDefault("hide", 0) != 0),
                    (int) propItems.getOrDefault("mobTime", 0)
            ));
        }
        return life;
    }

    public static List<PortalInfo> resolvePortal(WzProperty property) throws ProviderError {
        if (property == null) {
            return List.of();
        }
        if (!(property instanceof WzListProperty layerList)) {
            throw new ProviderError("Failed to resolve portal property");
        }
        List<PortalInfo> portal = new ArrayList<>();
        for (var portalEntry : layerList.getItems().entrySet()) {
            if (!(portalEntry.getValue() instanceof WzListProperty portalProperty)) {
                throw new ProviderError("Failed to resolve portal property");
            }
            final Map<String, Object> propItems = portalProperty.getItems();
            final PortalType portalType = PortalType.fromInt((int) propItems.get("pt"));
            if (portalType == null) {
                throw new ProviderError("Unknown portal type : %d", propItems.get("pt"));
            }
            portal.add(new PortalInfo(
                    portalType,
                    (String) propItems.get("pn"),
                    (int) propItems.get("tm"),
                    (String) propItems.get("tn"),
                    (int) propItems.get("x"),
                    (int) propItems.get("y"),
                    (String) propItems.get("script")
            ));
        }
        return portal;
    }

    public static List<ReactorInfo> resolveReactor(WzProperty property) throws ProviderError {
        if (property == null) {
            return List.of();
        }
        if (!(property instanceof WzListProperty layerList)) {
            throw new ProviderError("Failed to resolve reactor property");
        }
        List<ReactorInfo> reactor = new ArrayList<>();
        for (var reactorEntry : layerList.getItems().entrySet()) {
            if (!(reactorEntry.getValue() instanceof WzListProperty reactorProperty)) {
                throw new ProviderError("Failed to resolve reactor property");
            }
            final Map<String, Object> propItems = reactorProperty.getItems();
            reactor.add(new ReactorInfo(
                    Integer.parseInt((String) propItems.get("id")),
                    (String) propItems.get("name"),
                    (int) propItems.get("x"),
                    (int) propItems.get("y"),
                    ((int) propItems.getOrDefault("f", 0) != 0),
                    (int) propItems.get("reactorTime")
            ));
        }
        return reactor;
    }

    public static MapInfo resolveMapInfo(int mapId, WzImage image) throws ProviderError {
        final Map<String, Object> props = image.getProperty().getItems();

        final List<Foothold> foothold = resolveFoothold((WzProperty) props.get("foothold"));
        final List<LifeInfo> life = resolveLife((WzProperty) props.get("life"));
        final List<PortalInfo> portal = resolvePortal((WzProperty) props.get("portal"));
        final List<ReactorInfo> reactor = resolveReactor((WzProperty) props.get("reactor"));

        if (!(props.get("info") instanceof WzListProperty infoProp)) {
            throw new ProviderError("Failed to resolve info property");
        }
        final Map<String, Object> propItems = infoProp.getItems();
        return new MapInfo(
                mapId,
                foothold,
                life,
                portal,
                reactor,
                (String) propItems.get("bgm"),
                (int) propItems.get("version"),
                ((int) propItems.get("town") != 0),
                ((int) propItems.getOrDefault("swim", 0) != 0),
                ((int) propItems.getOrDefault("fly", 0) != 0),
                (int) propItems.get("returnMap"),
                (int) propItems.get("forcedReturn"),
                (int) propItems.getOrDefault("fieldLimit", 0),
                (float) propItems.get("mobRate"),
                (String) propItems.get("onFirstUserEnter"),
                (String) propItems.get("onUserEnter"),
                (int) propItems.getOrDefault("VRTop", 0),
                (int) propItems.getOrDefault("VRLeft", 0),
                (int) propItems.getOrDefault("VRBottom", 0),
                (int) propItems.getOrDefault("VRRight", 0)
        );
    }

    public static Map<Integer, MapInfo> resolveMapInfos(WzPackage source) throws ProviderError {
        final Map<Integer, MapInfo> mapInfos = new HashMap<>();
        final WzDirectory mapDirectory = source.getDirectory().getDirectories().get("Map");
        if (mapDirectory == null) {
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
                final MapInfo mapInfo = resolveMapInfo(mapId, mapEntry.getValue());
                mapInfos.put(mapId, mapInfo);
            }
        }
        return mapInfos;
    }
}
