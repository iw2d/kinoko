package kinoko.common.provider;

import kinoko.common.map.*;
import kinoko.common.wz.*;
import kinoko.common.wz.property.WzListProperty;
import kinoko.common.wz.property.WzProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapProvider extends Provider<MapInfo> {
    public MapProvider(WzCrypto crypto) {
        super(crypto);
    }

    public static MapProvider fromReader(WzReader reader) {
        return new MapProvider(reader.getCrypto());
    }

    public List<Foothold> resolveFoothold(WzProperty property) throws ProviderError {
        if (property == null) {
            return List.of();
        }
        if (!(property instanceof WzListProperty layerList)) {
            throw new ProviderError("Failed to resolve foothold property");
        }
        List<Foothold> foothold = new ArrayList<>();
        for (var layerEntry : layerList.getItems().entrySet()) {
            final int layerId = decInteger(layerEntry.getKey());
            if (!(layerEntry.getValue() instanceof WzListProperty groupList)) {
                throw new ProviderError("Failed to resolve foothold property");
            }
            for (var groupEntry : groupList.getItems().entrySet()) {
                final int groupId = decInteger(groupEntry.getKey());
                if (!(groupEntry.getValue() instanceof WzListProperty footholdList)) {
                    throw new ProviderError("Failed to resolve foothold property");
                }
                for (var footholdEntry : footholdList.getItems().entrySet()) {
                    final int footholdId = decInteger(footholdEntry.getKey());
                    if (!(footholdEntry.getValue() instanceof WzListProperty footholdProperty)) {
                        throw new ProviderError("Failed to resolve foothold property");
                    }
                    final Map<WzString, Object> propItems = footholdProperty.getItems();
                    foothold.add(new Foothold(
                            layerId,
                            groupId,
                            footholdId,
                            (int) propItems.getOrDefault(encString("x1"), 0),
                            (int) propItems.getOrDefault(encString("y1"), 0),
                            (int) propItems.getOrDefault(encString("x2"), 0),
                            (int) propItems.getOrDefault(encString("y2"), 0)
                    ));
                }
            }
        }
        return foothold;
    }

    public List<LifeInfo> resolveLife(WzProperty property) throws ProviderError {
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
            final Map<WzString, Object> propItems = lifeProperty.getItems();
            final String lifeTypeName = decString((WzString) propItems.get(encString("type")));
            final LifeType lifeType = LifeType.fromString(lifeTypeName);
            if (lifeType == null) {
                throw new ProviderError("Unknown life type : %s", lifeTypeName);
            }
            life.add(new LifeInfo(
                    lifeType,
                    Integer.parseInt(decString((WzString) propItems.get(encString("id")))),
                    (int) propItems.get(encString("x")),
                    (int) propItems.get(encString("y")),
                    (int) propItems.get(encString("rx0")),
                    (int) propItems.get(encString("rx1")),
                    (int) propItems.get(encString("cy")),
                    (int) propItems.get(encString("fh")),
                    ((int) propItems.getOrDefault(encString("f"), 0) != 0),
                    ((int) propItems.getOrDefault(encString("hide"), 0) != 0),
                    (int) propItems.getOrDefault(encString("mobTime"), 0)
            ));
        }
        return life;
    }

    public List<PortalInfo> resolvePortal(WzProperty property) throws ProviderError {
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
            final Map<WzString, Object> propItems = portalProperty.getItems();
            final PortalType portalType = PortalType.fromInt((int) propItems.get(encString("pt")));
            if (portalType == null) {
                throw new ProviderError("Unknown portal type : %d", propItems.get(encString("pt")));
            }
            final WzString portalScript = (WzString) propItems.get(encString("script"));
            portal.add(new PortalInfo(
                    portalType,
                    decString((WzString) propItems.get(encString("pn"))),
                    (int) propItems.get(encString("tm")),
                    decString((WzString) propItems.get(encString("tn"))),
                    (int) propItems.get(encString("x")),
                    (int) propItems.get(encString("y")),
                    portalScript != null ? decString(portalScript) : null
            ));
        }
        return portal;
    }

    public List<ReactorInfo> resolveReactor(WzProperty property) throws ProviderError {
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
            final Map<WzString, Object> propItems = reactorProperty.getItems();
            final WzString reactorName = (WzString) propItems.get(encString("name"));
            reactor.add(new ReactorInfo(
                    Integer.parseInt(decString((WzString) propItems.get(encString("id")))),
                    reactorName != null ? decString(reactorName) : null,
                    (int) propItems.get(encString("x")),
                    (int) propItems.get(encString("y")),
                    ((int) propItems.getOrDefault(encString("f"), 0) != 0),
                    (int) propItems.get(encString("reactorTime"))
            ));
        }
        return reactor;
    }

    public MapInfo resolveMapInfo(int mapId, WzImage image) throws ProviderError {
        final Map<WzString, Object> props = image.getProperty().getItems();

        List<Foothold> foothold = resolveFoothold((WzProperty) props.get(encString("foothold")));
        List<LifeInfo> life = resolveLife((WzProperty) props.get(encString("life")));
        List<PortalInfo> portal = resolvePortal((WzProperty) props.get(encString("portal")));
        List<ReactorInfo> reactor = resolveReactor((WzProperty) props.get(encString("reactor")));
        if (!(props.get(encString("info")) instanceof WzListProperty infoProp)) {
            throw new ProviderError("Failed to resolve info property");
        }
        final Map<WzString, Object> propItems = infoProp.getItems();
        final WzString onFirstUserEnter = (WzString) propItems.get(encString("onFirstUserEnter"));
        final WzString onUserEnter = (WzString) propItems.get(encString("onUserEnter"));
        return new MapInfo(
                mapId,
                foothold,
                life,
                portal,
                reactor,
                decString((WzString) propItems.get(encString("bgm"))),
                (int) propItems.get(encString("version")),
                ((int) propItems.get(encString("town")) != 0),
                ((int) propItems.getOrDefault(encString("swim"), 0) != 0),
                ((int) propItems.getOrDefault(encString("fly"), 0) != 0),
                (int) propItems.get(encString("returnMap")),
                (int) propItems.get(encString("forcedReturn")),
                (int) propItems.getOrDefault(encString("fieldLimit"), 0),
                (float) propItems.get(encString("mobRate")),
                onFirstUserEnter != null ? decString(onFirstUserEnter) : null,
                onUserEnter != null ? decString(onUserEnter) : null,
                (int) propItems.getOrDefault(encString("VRTop"), 0),
                (int) propItems.getOrDefault(encString("VRLeft"), 0),
                (int) propItems.getOrDefault(encString("VRBottom"), 0),
                (int) propItems.getOrDefault(encString("VRRight"), 0)
        );
    }

    @Override
    public Map<Integer, MapInfo> resolve(WzPackage source) throws ProviderError {
        final Map<Integer, MapInfo> mapInfos = new HashMap<>();
        final WzDirectory mapDirectory = source.getDirectory().getDirectories().get(encString("Map"));
        if (mapDirectory == null) {
            throw new ProviderError("Could not resolve Map.wz/Map");
        }
        for (var dirEntry : mapDirectory.getDirectories().entrySet()) {
            final String directoryName = decString(dirEntry.getKey());
            if (!directoryName.matches("Map[0-9]")) {
                continue;
            }
            for (var mapEntry : dirEntry.getValue().getImages().entrySet()) {
                final String imageName = decString(mapEntry.getKey());
                final int mapId = Integer.parseInt(imageName.replace(".img", ""));
                final MapInfo mapInfo = resolveMapInfo(mapId, mapEntry.getValue());
                mapInfos.put(mapId, mapInfo);
            }
        }
        return mapInfos;
    }
}
