package kinoko.provider;

import kinoko.provider.map.*;
import kinoko.provider.wz.WzDirectory;
import kinoko.provider.wz.WzImage;
import kinoko.provider.wz.WzPackage;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.provider.wz.property.WzProperty;

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
                    if (!(footholdEntry.getValue() instanceof WzListProperty footholdProp)) {
                        throw new ProviderError("Failed to resolve foothold property");
                    }
                    foothold.add(new Foothold(
                            layerId,
                            groupId,
                            footholdId,
                            footholdProp.getOrDefault("x1", 0),
                            footholdProp.getOrDefault("y1", 0),
                            footholdProp.getOrDefault("x2", 0),
                            footholdProp.getOrDefault("y2", 0)
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
            if (!(lifeEntry.getValue() instanceof WzListProperty lifeProp)) {
                throw new ProviderError("Failed to resolve life property");
            }
            final LifeType lifeType = LifeType.fromString(lifeProp.get("type"));
            if (lifeType == null) {
                throw new ProviderError("Unknown life type : %s", lifeProp.get("type"));
            }
            life.add(new LifeInfo(
                    lifeType,
                    Integer.parseInt(lifeProp.get("id")),
                    lifeProp.get("x"),
                    lifeProp.get("y"),
                    lifeProp.get("rx0"),
                    lifeProp.get("rx1"),
                    lifeProp.get("cy"),
                    lifeProp.get("fh"),
                    lifeProp.getOrDefault("f", 0) != 0,
                    lifeProp.getOrDefault("hide", 0) != 0,
                    lifeProp.getOrDefault("mobTime", 0)
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
            final int portalId = Integer.parseInt(portalEntry.getKey());
            if (!(portalEntry.getValue() instanceof WzListProperty portalProp)) {
                throw new ProviderError("Failed to resolve portal property");
            }
            final PortalType portalType = PortalType.fromInt(portalProp.get("pt"));
            if (portalType == null) {
                throw new ProviderError("Unknown portal type : %d", portalProp.get("pt"));
            }
            portal.add(new PortalInfo(
                    portalType,
                    portalId,
                    portalProp.get("pn"),
                    portalProp.get("tm"),
                    portalProp.get("tn"),
                    portalProp.get("x"),
                    portalProp.get("y"),
                    portalProp.get("script")
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
            if (!(reactorEntry.getValue() instanceof WzListProperty reactorProp)) {
                throw new ProviderError("Failed to resolve reactor property");
            }
            reactor.add(new ReactorInfo(
                    Integer.parseInt(reactorProp.get("id")),
                    reactorProp.get("name"),
                    reactorProp.get("x"),
                    reactorProp.get("y"),
                    reactorProp.getOrDefault("f", 0) != 0,
                    reactorProp.get("reactorTime")
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
        return new MapInfo(
                mapId,
                foothold,
                life,
                portal,
                reactor,
                infoProp.get("bgm"),
                infoProp.get("version"),
                ((int) infoProp.get("town")) != 0,
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
