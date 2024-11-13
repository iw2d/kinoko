package kinoko.util.tool;

import kinoko.provider.ProviderError;
import kinoko.provider.WzProvider;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import kinoko.util.Tuple;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public final class FieldTransferWhitelist {
    public static final Path MAP_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Map.wz");
    public static final Path EFFECT_WZ = Path.of(ServerConfig.WZ_DIRECTORY, "Effect.wz");

    public static void main(String[] args) throws IOException {
        try (final WzReader reader = WzReader.build(MAP_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            parseFieldObstacles(reader.readPackage()); // Map/Obj/%s.img/%s/%s/%d
        }
        try (final WzReader reader = WzReader.build(EFFECT_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            parseDirections(reader.readPackage());
        }
    }

    private static void parseFieldObstacles(WzPackage source) throws ProviderError {
        // Determine which objects are used in which maps
        final Map<Integer, Set<String>> objectMappings = new HashMap<>(); // mapId -> object identifiers ("oS/l0/l1/l2")
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
                final Set<String> objectIds = new HashSet<>();
                for (int layerId = 0; layerId <= 7; layerId++) {
                    if (!(mapEntry.getValue().getProperty().get(String.valueOf(layerId)) instanceof WzListProperty layerProp)) {
                        throw new ProviderError("Failed to resolve layer ID : %d", layerId);
                    }
                    if (!(layerProp.get("obj") instanceof WzListProperty objList)) {
                        continue;
                    }
                    for (var objEntry : objList.getItems().entrySet()) {
                        if (!(objEntry.getValue() instanceof WzListProperty objProp)) {
                            throw new ProviderError("Failed to resolve object property");
                        }
                        final String objectId = String.format("%s/%s/%s/%s",
                                WzProvider.getString(objProp.get("oS")),
                                WzProvider.getString(objProp.get("l0")),
                                WzProvider.getString(objProp.get("l1")),
                                WzProvider.getString(objProp.get("l2"))
                        );
                        objectIds.add(objectId);
                    }
                }
                objectMappings.put(mapId, Collections.unmodifiableSet(objectIds));
            }
        }
        // Process linked maps
//        for (var linkEntry : linkedMaps.entrySet()) {
//            final int mapId = linkEntry.getKey();
//            final int link = linkEntry.getValue().getLeft();
//            final Set<String> objectIds = objectMappings.get(link);
//            if (objectIds == null) {
//                throw new ProviderError("Failed to resolve linked map ID : %d, link : %d", mapId, link);
//            }
//            objectMappings.put(mapId, objectIds);
//        }
        // Parse Obj directory to find obstacle objects with targetField node
        final Map<String, Integer> obstacleWarps = new HashMap<>(); // object id -> targetField ID
        if (!(source.getDirectory().getDirectories().get("Obj") instanceof WzDirectory objDirectory)) {
            throw new ProviderError("Could not resolve Map.wz/Map");
        }
        for (var imageEntry : objDirectory.getImages().entrySet()) {
            final String oS = imageEntry.getKey().replace(".img", "");
            for (var l0Entry : imageEntry.getValue().getProperty().getItems().entrySet()) {
                final String l0 = l0Entry.getKey();
                if (!(l0Entry.getValue() instanceof WzListProperty l0Prop)) {
                    throw new ProviderError("Failed to resolve l0 property");
                }
                for (var l1Entry : l0Prop.getItems().entrySet()) {
                    final String l1 = l1Entry.getKey();
                    if (!(l1Entry.getValue() instanceof WzListProperty l1Prop)) {
                        // throw new ProviderError("Failed to resolve l1 property");
                        continue;
                    }
                    for (var l2Entry : l1Prop.getItems().entrySet()) {
                        final String l2 = l2Entry.getKey();
                        if (!(l2Entry.getValue() instanceof WzListProperty l2Prop)) {
                            // throw new ProviderError("Failed to resolve l2 property");
                            continue;
                        }
                        final int obstacle = WzProvider.getInteger(l2Prop.get("obstacle"), 0);
                        final int targetField = WzProvider.getInteger(l2Prop.get("targetField"), 0);
                        if (obstacle == 0 || targetField == 0) {
                            continue;
                        }
                        final String objectId = String.format("%s/%s/%s/%s", oS, l0, l1, l2);
                        obstacleWarps.put(objectId, targetField);
                    }
                }
            }
        }
        // Match obstacle warps with field object mappings to report transfer field requests that should be whitelisted
        final Map<Integer, Set<Integer>> whitelist = new HashMap<>(); // targetField -> source map IDs
        for (var mapEntry : objectMappings.entrySet()) {
            final int mapId = mapEntry.getKey();
            for (var obstacleEntry : obstacleWarps.entrySet()) {
                if (!mapEntry.getValue().contains(obstacleEntry.getKey())) {
                    continue;
                }
                final int targetField = obstacleEntry.getValue();
                final Set<Integer> mapIds = whitelist.computeIfAbsent(targetField, (key) -> new HashSet<>());
                mapIds.add(mapId);
            }
        }
        // Report obstacle warps
        for (var entry : whitelist.entrySet()) {
            System.out.printf("case %s -> %d;%n", String.join(", ", entry.getValue().stream().map(String::valueOf).sorted().toList()), entry.getKey());
        }
    }

    private static void parseDirections(WzPackage source) throws ProviderError {
        // Parse Effect.wz/Direction images
        final Map<String, Integer> directionWarps = new HashMap<>(); // effectPath -> mapId
        for (var imageEntry : source.getDirectory().getImages().entrySet()) {
            final String imageName = imageEntry.getKey();
            if (!imageName.startsWith("Direction")) {
                continue;
            }
            for (var groupEntry : imageEntry.getValue().getProperty().getItems().entrySet()) {
                final String groupName = groupEntry.getKey();
                if (groupName.equals("effect") || groupName.equals("sound")) {
                    continue;
                }
                if (!(groupEntry.getValue() instanceof WzListProperty sceneList)) {
                    throw new ProviderError("Could not resolve scene list");
                }
                for (var sceneEntry : sceneList.getItems().entrySet()) {
                    final String sceneName = sceneEntry.getKey();
                    if (!(sceneEntry.getValue() instanceof WzListProperty nodeList)) {
                        throw new ProviderError("Could not resolve node list");
                    }
                    final String effectPath = String.format("Effect/%s/%s/%s", imageName, groupName, sceneName);
                    for (var nodeEntry : nodeList.getItems().entrySet()) {
                        if (!(nodeEntry.getValue() instanceof WzListProperty nodeProp)) {
                            throw new ProviderError("Could not resolve node prop");
                        }
                        final int type = WzProvider.getInteger(nodeProp.get("type"));
                        if (type != 2) {
                            continue;
                        }
                        final int field = WzProvider.getInteger(nodeProp.get("field"));
                        directionWarps.put(effectPath, field);
                    }
                }
            }
        }
        // Report direction warps
        for (var entry : directionWarps.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            System.out.printf("// %s -> %d%n", entry.getKey(), entry.getValue());
        }
    }
}
