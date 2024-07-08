package kinoko.util.tool;

import kinoko.provider.*;
import kinoko.provider.item.ItemInfo;
import kinoko.provider.map.MapInfo;
import kinoko.provider.map.PortalInfo;
import kinoko.provider.npc.NpcTemplate;
import kinoko.provider.reactor.ReactorTemplate;
import kinoko.provider.wz.*;
import kinoko.provider.wz.property.WzListProperty;
import kinoko.server.ServerConfig;
import kinoko.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

final class ScriptHelper {
    private static final Logger log = LogManager.getLogger(ScriptHelper.class);
    private static final Map<Path, List<String>> processedScripts = new HashMap<>();

    public static void main(String[] args) throws IOException {
        StringProvider.initialize();
        MapProvider.initialize();
        NpcProvider.initialize();
        ItemProvider.initialize();
        QuestProvider.initialize();
        ReactorProvider.initialize();

        // Load scripts
        final Map<String, Path> scriptFiles = new HashMap<>();
        try (final Stream<Path> stream = Files.walk(Path.of(ServerConfig.SCRIPT_DIRECTORY))) {
            for (Path path : stream.toList()) {
                if (!Files.isRegularFile(path) || !path.toString().endsWith(".py")) {
                    continue;
                }
                final String scriptName = path.getFileName().toString().replace(".py", "");
                if (scriptFiles.containsKey(scriptName)) {
                    throw new RuntimeException("Duplicate script : " + path);
                }
                scriptFiles.put(scriptName, path);
            }
        }


        // Collect field enter script comments
        final List<MapInfo> mapInfoList = MapProvider.getMapInfos().stream()
                .sorted(Comparator.comparingInt(MapInfo::getMapId)).toList();
        final Map<String, Map<Integer, List<String>>> mapComments = new HashMap<>();
        final Function<Integer, List<String>> mapperFunction = (mapId) -> {
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("# %s (%d)", resolveMapName(mapId), mapId));
            return comments;
        };
        for (MapInfo mapInfo : mapInfoList) {
            final int mapId = mapInfo.getMapId();
            final String firstUserEnter = mapInfo.getOnFirstUserEnter();
            if (firstUserEnter != null && !firstUserEnter.isEmpty() && scriptFiles.containsKey(firstUserEnter)) {
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(firstUserEnter, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                // commentList.add("#   onFirstUserEnter");
            }
            final String userEnter = mapInfo.getOnUserEnter();
            if (userEnter != null && !userEnter.isEmpty() && scriptFiles.containsKey(userEnter)) {
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(userEnter, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                // commentList.add("#   onUserEnter");
            }
        }
        // Collect portal script comments
        for (MapInfo mapInfo : mapInfoList) {
            final int mapId = mapInfo.getMapId();
            if (MapProvider.getMapLink(mapId).isPresent()) {
                // skip linked maps
                continue;
            }
            for (PortalInfo portalInfo : mapInfo.getPortalInfos().stream().sorted(Comparator.comparing(PortalInfo::getPortalName)).toList()) {
                final String scriptName = portalInfo.getScript();
                if (scriptName == null || scriptName.isEmpty() || !scriptFiles.containsKey(scriptName)) {
                    continue;
                }
                final Map<Integer, List<String>> commentMap = mapComments.computeIfAbsent(scriptName, (key) -> new HashMap<>());
                final List<String> commentList = commentMap.computeIfAbsent(mapId, mapperFunction);
                commentList.add(String.format("#   %s (%d, %d)", portalInfo.getPortalName(), portalInfo.getX(), portalInfo.getY()));
            }
        }
        // Process map comments
        for (var scriptEntry : mapComments.entrySet()) {
            final String scriptName = scriptEntry.getKey();
            final List<String> comments = new ArrayList<>();
            for (var mapEntry : scriptEntry.getValue().entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).toList()) {
                comments.addAll(mapEntry.getValue());
            }
            processComments(scriptFiles.get(scriptName), comments);
        }


        // Process npc scripts
        for (NpcTemplate npcTemplate : NpcProvider.getNpcTemplates().stream().sorted(Comparator.comparingInt(NpcTemplate::getId)).toList()) {
            final int npcId = npcTemplate.getId();
            final String scriptName = npcTemplate.getScript();
            if (scriptName == null || scriptName.isEmpty() || !scriptFiles.containsKey(scriptName)) {
                continue;
            }
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("# %s (%d)", StringProvider.getNpcName(npcId), npcId));
            final List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getLifeInfos().stream().anyMatch((lifeInfo) -> lifeInfo.getTemplateId() == npcId))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            for (MapInfo mapInfo : npcFields) {
                final int mapId = mapInfo.getMapId();
                comments.add(String.format("#   %s (%d)", resolveMapName(mapId), mapId));
            }
            processComments(scriptFiles.get(scriptName), comments);
        }


        // Process item scripts
        for (ItemInfo itemInfo : ItemProvider.getItemInfos()) {
            final int itemId = itemInfo.getItemId();
            final String scriptName = itemInfo.getScript();
            if (scriptName == null || scriptName.isEmpty() || !scriptFiles.containsKey(scriptName)) {
                continue;
            }
            processComments(scriptFiles.get(scriptName), List.of(
                    String.format("# %s (%d)", StringProvider.getItemName(itemId), itemId)
            ));
        }


        // Process quest scripts
        try (final WzReader reader = WzReader.build(QuestProvider.QUEST_WZ, new WzReaderConfig(WzConstants.WZ_GMS_IV, ServerConstants.GAME_VERSION))) {
            final WzPackage wzPackage = reader.readPackage();
            final WzImage infoImage = wzPackage.getDirectory().getImages().get("QuestInfo.img");
            final WzImage checkImage = wzPackage.getDirectory().getImages().get("Check.img");
            for (var entry : checkImage.getProperty().getItems().entrySet()) {
                final int questId = WzProvider.getInteger(entry.getKey());
                if (!(entry.getValue() instanceof WzListProperty questCheck) ||
                        !(questCheck.get("0") instanceof WzListProperty startCheck) ||
                        !(questCheck.get("1") instanceof WzListProperty endCheck)) {
                    throw new ProviderError("Could not resolve quest check");
                }
                if (!(infoImage.getProperty().get(entry.getKey()) instanceof WzListProperty questInfo)) {
                    throw new ProviderError("Could not resolve quest check");
                }
                final String questName = WzProvider.getString(questInfo.get("name"));
                // Start script
                final String startScript = WzProvider.getString(startCheck.get("startscript"), "");
                final int startNpc = WzProvider.getInteger(startCheck.get("npc"), 0);
                if (startScript != null && !startScript.isEmpty() && scriptFiles.containsKey(startScript)) {
                    final List<String> comments = new ArrayList<>();
                    comments.add(String.format("# %s (%d)", questName, questId));
                    if (startNpc != 0) {
                        // comments.add(String.format("#   %s (%d)", StringProvider.getNpcName(startNpc), startNpc));
                    }
                    processComments(scriptFiles.get(startScript), comments);
                }
                // End script
                final String endScript = WzProvider.getString(endCheck.get("endscript"), "");
                final int endNpc = WzProvider.getInteger(endCheck.get("npc"), 0);
                if (endScript != null && !endScript.isEmpty() && scriptFiles.containsKey(endScript)) {
                    final List<String> comments = new ArrayList<>();
                    comments.add(String.format("# %s (%d)", questName, questId));
                    if (endNpc != 0) {
                        // comments.add(String.format("#   %s (%d)", StringProvider.getNpcName(endNpc), endNpc));
                    }
                    processComments(scriptFiles.get(endScript), comments);
                }
            }
        } catch (IOException | ProviderError e) {
            throw new IllegalArgumentException("Exception caught while loading Quest.wz", e);
        }


        // Process reactor scripts
        for (ReactorTemplate reactorTemplate : ReactorProvider.getReactorTemplates().stream().sorted(Comparator.comparingInt(ReactorTemplate::getId)).toList()) {
            final int reactorId = reactorTemplate.getId();
            final String scriptName = reactorTemplate.getAction();
            if (scriptName == null || scriptName.isEmpty() || !scriptFiles.containsKey(scriptName)) {
                continue;
            }
            final List<String> comments = new ArrayList<>();
            comments.add(String.format("# %s (%d)", scriptName, reactorId));
            final List<MapInfo> npcFields = MapProvider.getMapInfos().stream()
                    .filter((mapInfo) -> mapInfo.getReactorInfos().stream().anyMatch((reactorInfo) -> reactorInfo.getTemplateId() == reactorId))
                    .sorted(Comparator.comparingInt(MapInfo::getMapId))
                    .toList();
            for (MapInfo mapInfo : npcFields) {
                final int mapId = mapInfo.getMapId();
                comments.add(String.format("#   %s (%d)", resolveMapName(mapId), mapId));
            }
            processComments(scriptFiles.get(scriptName), comments);
        }
    }

    private static void processComments(Path path, List<String> comments) throws IOException {
        // Check if script has been processed
        final List<String> combinedComments = new ArrayList<>();
        if (processedScripts.containsKey(path)) {
            final List<String> existingComments = processedScripts.get(path);
            if (existingComments.contains(comments.get(0))) {
                return;
            }
            // Add existing comments
            // log.warn("Existing comments for path : {}", path);
            combinedComments.addAll(existingComments);
        }
        combinedComments.addAll(comments);
        processedScripts.put(path, combinedComments);
        // Collect existing lines
        boolean start = false;
        final List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(path)) {
            if (!start && (line.trim().startsWith("#") || line.trim().isEmpty())) {
                continue;
            }
            start = true;
            lines.add(line);
        }
        // Write comments
        Files.write(path, combinedComments, StandardOpenOption.TRUNCATE_EXISTING);
        if (lines.isEmpty()) {
            return;
        }
        // Append existing lines
        lines.addFirst("");
        Files.write(path, lines, StandardOpenOption.APPEND);
    }

    private static String resolveMapName(int mapId) {
        final String name = StringProvider.getMapName(mapId);
        if (name != null) {
            return name;
        }
        final Optional<Integer> linkResult = MapProvider.getMapLink(mapId);
        if (linkResult.isEmpty()) {
            // log.warn("Could not resolve name for map ID : {}", mapId);
            return null;
        }
        return StringProvider.getMapName(linkResult.get());
    }
}
